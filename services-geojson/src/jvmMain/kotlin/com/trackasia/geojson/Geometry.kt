package com.trackasia.geojson

import com.google.gson.GsonBuilder
import com.trackasia.geojson.common.toJvm
import com.trackasia.geojson.model.GeometryCollection
import com.trackasia.geojson.model.LineString
import com.trackasia.geojson.model.MultiLineString
import com.trackasia.geojson.model.MultiPoint
import com.trackasia.geojson.model.MultiPolygon
import com.trackasia.geojson.model.Polygon
import com.trackasia.geojson.model.Point as CommonPoint
import com.trackasia.geojson.model.Geometry as CommonGeometry



/**
 * Each of the six geometries and [GeometryCollection]
 * which make up GeoJson implement this interface.
 *
 * @since 1.0.0
 */
@Deprecated(
    message = "Use new common models instead.",
    replaceWith = ReplaceWith("Geometry", "com.trackasia.geojson.model.Geometry"),
)
interface Geometry : GeoJson {

    companion object {

        /**
         * Create a new instance of Geometry class by passing in a formatted valid JSON String.
         *
         * @param json a formatted valid JSON string defining a GeoJson Geometry
         * @return a new instance of Geometry class defined by the values passed inside
         * this static factory method
         * @since 4.0.0
         */
        @JvmStatic
        fun fromJson(json: String): Geometry {
            return when (val commonGeometry = CommonGeometry.fromJson(json)) {
                is CommonPoint -> commonGeometry.toJvm()
                is LineString -> commonGeometry.toJvm()
                is MultiLineString -> commonGeometry.toJvm()
                is MultiPoint -> commonGeometry.toJvm()
                is MultiPolygon -> commonGeometry.toJvm()
                is Polygon -> commonGeometry.toJvm()
                is GeometryCollection -> commonGeometry.toJvm()
            }
        }
    }
}