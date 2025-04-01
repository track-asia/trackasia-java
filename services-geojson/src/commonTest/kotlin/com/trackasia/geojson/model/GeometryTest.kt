package com.trackasia.geojson.model

import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.Test
import com.trackasia.geojson.TestUtils.DELTA
import com.trackasia.geojson.TestUtils.compareJson

class GeometryTest {

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

        val geometry = Geometry.fromJson(json)
        assertTrue(geometry is GeometryCollection)
    }

    @Test
    fun pointFromJson() {
        val geometry = Geometry.fromJson(
            "{\"coordinates\": [2,3],"
                    + "\"type\":\"Point\",\"bbox\":[1.0,2.0,3.0,4.0]}"
        )

        assertNotNull(geometry)
        assertNotNull(geometry.bbox)
        assertEquals(1.0, geometry.bbox!!.southwest.longitude, DELTA)
        assertEquals(2.0, geometry.bbox!!.southwest.latitude, DELTA)
        assertEquals(3.0, geometry.bbox!!.northeast.longitude, DELTA)
        assertEquals(4.0, geometry.bbox!!.northeast.latitude, DELTA)
        assertNotNull((geometry as Point).coordinates)
        assertEquals(2.0, geometry.longitude, DELTA)
        assertEquals(3.0, geometry.latitude, DELTA)
    }

    @Test
    fun pointToJson() {
        val geometry: Geometry = Point(
            2.0, 3.0, bbox = BoundingBox(1.0, 2.0, 3.0, 4.0)
        )

        val actualPoint = Point.fromJson(geometry.toJson())
        val expectedPoint = Point.fromJson("{\"coordinates\": [2.0,3.0],"
                + "\"type\":\"Point\",\"bbox\":[1.0,2.0,3.0,4.0]}")
        assertEquals(expectedPoint, actualPoint)
    }

    @Test
    fun lineStringFromJson() {
        val lineString = Geometry.fromJson(
            "{\"coordinates\":[[1,2],[2,3],[3,4]],"
                    + "\"type\":\"LineString\",\"bbox\":[1.0,2.0,3.0,4.0]}"
        )

        assertNotNull(lineString)
        assertNotNull(lineString.bbox)
        assertEquals(1.0, lineString.bbox!!.southwest.longitude, DELTA)
        assertEquals(2.0, lineString.bbox!!.southwest.latitude, DELTA)
        assertEquals(3.0, lineString.bbox!!.northeast.longitude, DELTA)
        assertEquals(4.0, lineString.bbox!!.northeast.latitude, DELTA)
        assertNotNull((lineString as LineString).coordinates)
        assertEquals(1.0, lineString.coordinates[0].longitude, DELTA)
        assertEquals(2.0, lineString.coordinates[0].latitude, DELTA)
        assertEquals(2.0, lineString.coordinates[1].longitude, DELTA)
        assertEquals(3.0, lineString.coordinates[1].latitude, DELTA)
        assertEquals(3.0, lineString.coordinates[2].longitude, DELTA)
        assertEquals(4.0, lineString.coordinates[2].latitude, DELTA)
    }

    @Test
    fun lineStringToJson() {
        val geometry: Geometry = LineString(
            listOf(
                Point(1.0, 2.0),
                Point(2.0, 3.0),
                Point(3.0, 4.0)
            ),
            BoundingBox(1.0, 2.0, 3.0, 4.0)
        )

        val actualLineString = LineString.fromJson(geometry.toJson())
        val expectedLineString = LineString.fromJson("{\"coordinates\":[[1.0,2.0],[2.0,3.0],[3.0,4.0]],"
                + "\"type\":\"LineString\",\"bbox\":[1.0,2.0,3.0,4.0]}")

        assertEquals(expectedLineString, actualLineString)
    }
}
