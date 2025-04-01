package com.trackasia.geojson.model

import kotlinx.serialization.SerializationException
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.Test
import com.trackasia.geojson.TestUtils.DELTA
import com.trackasia.geojson.TestUtils.compareJson
import kotlin.test.assertFailsWith

class MultiLineStringTest {

    @Test
    fun sanity() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0)
        )

        val lineStrings = listOf(
            LineString(points),
            LineString(points)
        )

        val multiLineString = MultiLineString.fromLineStrings(lineStrings)
        assertNotNull(multiLineString)
    }

    @Test
    fun bbox_nullWhenNotSet() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0)
        )

        val lineStrings = listOf(
            LineString(points),
            LineString(points)
        )

        val multiLineString = MultiLineString.fromLineStrings(lineStrings)
        assertNull(multiLineString.bbox)
    }

    @Test
    fun bbox_doesNotSerializeWhenNotPresent() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0)
        )

        val lineStrings = listOf(
            LineString(points),
            LineString(points)
        )

        val multiLineString: MultiLineString = MultiLineString.fromLineStrings(lineStrings)

        val actualMultiLineString = MultiLineString.fromJson(multiLineString.toJson())
        val expectedMultiLineString = MultiLineString.fromJson("{\"type\":\"MultiLineString\",\"coordinates\":[[[1.0,2.0],[2.0,3.0]],[[1.0,2.0],[2.0,3.0]]]}")
        assertEquals(expectedMultiLineString, actualMultiLineString)
    }

    @Test
    fun bbox_returnsCorrectBbox() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0)
        )

        val bbox = BoundingBox(1.0, 2.0, 3.0, 4.0)

        val lineStrings = listOf(
            LineString(points),
            LineString(points)
        )

        val multiLineString: MultiLineString = MultiLineString.fromLineStrings(lineStrings, bbox)
        assertNotNull(multiLineString.bbox)
        assertEquals(1.0, multiLineString.bbox!!.west, DELTA)
        assertEquals(2.0, multiLineString.bbox!!.south, DELTA)
        assertEquals(3.0, multiLineString.bbox!!.east, DELTA)
        assertEquals(4.0, multiLineString.bbox!!.north, DELTA)
    }

    @Test
    fun passingInSingleLineString_doesHandleCorrectly() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(3.0, 4.0)
        )

        val geometry = LineString(points)
        val multiLineString: MultiLineString = MultiLineString.fromLineString(geometry)

        assertNotNull(multiLineString)
        assertEquals(1, multiLineString.lineStrings.size)
        assertEquals(
            2.0,
            multiLineString.lineStrings[0].coordinates[0].latitude,
            DELTA
        )
    }

    @Test
    fun bbox_doesSerializeWhenPresent() {
        val points = listOf(
            Point(1.0, 2.0),
            Point(2.0, 3.0)
        )
        val bbox = BoundingBox(1.0, 2.0, 3.0, 4.0)

        val lineStrings = listOf(
            LineString(points),
            LineString(points)
        )

        val multiLineString: MultiLineString = MultiLineString.fromLineStrings(lineStrings, bbox)

        val actualMultiLineString = MultiLineString.fromJson(multiLineString.toJson())
        val expectedMultiLineString = MultiLineString.fromJson("{\"type\":\"MultiLineString\",\"bbox\":[1.0,2.0,3.0,4.0],"
                + "\"coordinates\":[[[1.0,2.0],[2.0,3.0]],[[1.0,2.0],[2.0,3.0]]]}")
        assertEquals(expectedMultiLineString, actualMultiLineString)
    }

    @Test
    fun fromJson() {
        val json = "{\"type\": \"MultiLineString\", " +
                "\"coordinates\": [[[100.0, 0.0],[101.0, 1.0]],[[102.0, 2.0],[103.0, 3.0]]] }"

        val geo: MultiLineString = MultiLineString.fromJson(json)
        assertEquals(geo.coordinates[0][0].longitude, 100.0, DELTA)
        assertEquals(geo.coordinates[0][0].latitude, 0.0, DELTA)
        assertNull(geo.coordinates[0][0].altitude)
    }

    @Test
    fun toJson() {
        val json = "{\"type\": \"MultiLineString\", " +
                "\"coordinates\": [[[100.0, 0.0],[101.0, 1.0]],[[102.0, 2.0],[103.0, 3.0]]] }"
        val geo = MultiLineString.fromJson(json)

        val actualMultiLineString = MultiLineString.fromJson(geo.toJson())
        val expectedMultiLineString = MultiLineString.fromJson(json)
        assertEquals(expectedMultiLineString, actualMultiLineString)
    }

    @Test
    fun fromJson_coordinatesPresent() {
        assertFailsWith(SerializationException::class) {
            MultiLineString.fromJson("{\"type\":\"MultiLineString\",\"coordinates\":null}")
        }
    }
}
