package com.trackasia.geojson.turf

import kotlin.test.assertNotNull
import kotlin.test.assertEquals
import kotlin.test.Test
import com.trackasia.geojson.model.Feature
import com.trackasia.geojson.model.FeatureCollection
import com.trackasia.geojson.model.Point
import com.trackasia.geojson.turf.TestUtils.DELTA
import com.trackasia.geojson.turf.TestUtils.loadJsonFixture
import com.trackasia.geojson.turf.TurfClassification.nearestPoint

class TurfClassificationTest {

    @Test
    fun testLineDistanceWithGeometries() {
        val point = Feature.fromJson(loadJsonFixture(PT)).geometry as Point
        val points = FeatureCollection.fromJson(loadJsonFixture(PTS))

        val pointFeatures = points.features.map { feature ->
            feature.geometry as Point
        }

        val closestPt = nearestPoint(point, pointFeatures)

        assertNotNull(closestPt)
        assertEquals(closestPt.longitude, -75.33, DELTA)
        assertEquals(closestPt.latitude, 39.44, DELTA)
    }

    companion object {
        private const val PT = "turf-classification/pt.json"
        private const val PTS = "turf-classification/pts.json"
    }
}
