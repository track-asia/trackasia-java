package com.trackasia.geojson.model

import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.Test
import com.trackasia.geojson.TestUtils.DELTA
import com.trackasia.geojson.TestUtils.compareJson

class GeometryCollectionTest {

    @Test
    fun sanity() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0)
        )

        val lineString = LineString(points)
        val geometries = listOf(points[0], lineString)

        val geometryCollection = GeometryCollection(geometries)
        assertNotNull(geometryCollection)
    }

    @Test
    fun bbox_nullWhenNotSet() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0)
        )

        val lineString = LineString(points)
        val geometries = listOf(points[0], lineString)

        val geometryCollection = GeometryCollection(geometries)
        assertNull(geometryCollection.bbox)
    }

    @Test
    fun bbox_doesNotSerializeWhenNotPresent() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0)
        )

        val lineString = LineString(points)
        val geometries = listOf(points[0], lineString)

        val geometryCollection = GeometryCollection(geometries)

        val actualGeometryCollection = GeometryCollection.fromJson(geometryCollection.toJson())
            val expectedGeometryCollection = GeometryCollection.fromJson("{\"type\":\"GeometryCollection\",\"geometries\":[" + "{\"type\":\"Point\","
                    + "\"coordinates\":[1.0,2.0]},{\"type\":\"LineString\",\"coordinates\":[[1.0,2.0],[2.0,3.0]]}]}")
        assertEquals(expectedGeometryCollection, actualGeometryCollection)
    }

    @Test
    fun bbox_returnsCorrectBbox() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0)
        )

        val lineString = LineString(points)
        val geometries = listOf(points[0], lineString)

        val bbox = BoundingBox(1.0, 2.0, 3.0, 4.0)
        val geometryCollection = GeometryCollection(geometries, bbox)
        assertNotNull(geometryCollection.bbox)
        assertEquals(1.0, geometryCollection.bbox!!.west, DELTA)
        assertEquals(2.0, geometryCollection.bbox!!.south, DELTA)
        assertEquals(3.0, geometryCollection.bbox!!.east, DELTA)
        assertEquals(4.0, geometryCollection.bbox!!.north, DELTA)
    }

    @Test
    fun passingInSingleGeometry_doesHandleCorrectly() {
        val geometry = Point(1.0, 2.0)
        val collection = GeometryCollection(geometry)
        assertNotNull(collection)
        assertEquals(1, collection.geometries.size)
        assertEquals(2.0, (collection.geometries.first() as Point).latitude, DELTA)
    }

    @Test
    fun bbox_doesSerializeWhenPresent() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0)
        )

        val lineString = LineString(points)
        val geometries = listOf(points[0], lineString)

        val bbox = BoundingBox(1.0, 2.0, 3.0, 4.0)
        val geometryCollection = GeometryCollection(geometries, bbox)

        val actualGeometryCollection = GeometryCollection.fromJson(geometryCollection.toJson())
        val expectedGeometryCollection = GeometryCollection.fromJson("{\"type\":\"GeometryCollection\",\"bbox\":[1.0,2.0,3.0,4.0],"
                + "\"geometries\":[{\"type\":\"Point\",\"coordinates\":[1.0,2.0]},"
                + "{\"type\":\"LineString\",\"coordinates\":[[1.0,2.0],[2.0,3.0]]}]}")
        assertEquals(expectedGeometryCollection, actualGeometryCollection)
    }

    @Test
    fun fromJson() {
        val json =
            "    { \"type\": \"GeometryCollection\"," +
                    "            \"bbox\": [120, 40, -120, -40]," +
                    "      \"geometries\": [" +
                    "      { \"type\": \"Point\"," +
                    "              \"bbox\": [110, 30, -110, -30]," +
                    "        \"coordinates\": [100, 0]}," +
                    "      { \"type\": \"LineString\"," +
                    "              \"bbox\": [110, 30, -110, -30]," +
                    "        \"coordinates\": [[101, 0], [102, 1]]}]}"
        val geo = GeometryCollection.fromJson(json)
        assertTrue(geo.geometries.first() is Point)
        assertTrue(geo.geometries[1] is LineString)
    }

    @Test
    fun toJson() {
        val jsonOriginal =
            "    { \"type\": \"GeometryCollection\"," +
                    "            \"bbox\": [-120.0, -40.0, 120.0, 40.0]," +
                    "      \"geometries\": [" +
                    "      { \"type\": \"Point\"," +
                    "              \"bbox\": [-110.0, -30.0, 110.0, 30.0]," +
                    "        \"coordinates\": [100.0, 0.0]}," +
                    "      { \"type\": \"LineString\"," +
                    "              \"bbox\": [-110.0, -30.0, 110.0, 30.0]," +
                    "        \"coordinates\": [[101.0, 0.0], [102.0, 1.0]]}]}"

        val geometries = listOf(
            Point(
                100.0, 0.0,
                bbox = BoundingBox(-110.0, -30.0, 110.0, 30.0)
            ),
            LineString(
                listOf(
                    Point(101.0, 0.0),
                    Point(102.0, 1.0)
                ),
                BoundingBox(-110.0, -30.0, 110.0, 30.0)
            )
        )

        val geometryCollection = GeometryCollection(
            geometries,
            BoundingBox(-120.0, -40.0, 120.0, 40.0)
        )

        val actualGeometryCollection = GeometryCollection.fromJson(geometryCollection.toJson())
        val expectedGeometryCollection = GeometryCollection.fromJson(jsonOriginal)
        assertEquals(expectedGeometryCollection, actualGeometryCollection)
    }
}
