package com.trackasia.geojson.common

import com.google.gson.JsonParser
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.JsonElement as KtxJsonElement
import com.google.gson.JsonObject as GsonJsonObject
import com.trackasia.geojson.model.BoundingBox
import com.trackasia.geojson.model.Point
import com.trackasia.geojson.model.MultiPoint
import com.trackasia.geojson.model.LineString
import com.trackasia.geojson.model.MultiLineString
import com.trackasia.geojson.model.Polygon
import com.trackasia.geojson.model.MultiPolygon
import com.trackasia.geojson.model.Feature
import com.trackasia.geojson.model.FeatureCollection
import com.trackasia.geojson.model.Geometry
import com.trackasia.geojson.model.GeometryCollection
import com.trackasia.geojson.Feature as JvmFeature
import com.trackasia.geojson.FeatureCollection as JvmFeatureCollection
import com.trackasia.geojson.Point as JvmPoint
import com.trackasia.geojson.MultiPoint as JvmMultiPoint
import com.trackasia.geojson.LineString as JvmLineString
import com.trackasia.geojson.MultiLineString as JvmMultiLineString
import com.trackasia.geojson.Polygon as JvmPolygon
import com.trackasia.geojson.MultiPolygon as JvmMultiPolygon
import com.trackasia.geojson.BoundingBox as JvmBoundingBox
import com.trackasia.geojson.Geometry as JvmGeometry
import com.trackasia.geojson.GeometryCollection as JvmGeometryCollection

fun Geometry.toJvm(): JvmGeometry {
    return JvmGeometry.fromJson(toJson())
}

fun JvmGeometry.toCommon(): Geometry {
    return Geometry.fromJson(toJson())
}

fun GeometryCollection.toJvm(): JvmGeometryCollection {
    return JvmGeometryCollection(
        "GeometryCollection",
        bbox?.toJvm(),
        geometries.map { geom -> geom.toJvm() }
    )
}

fun Feature.toJvm(): JvmFeature {
    return JvmFeature(
        "Feature",
        bbox?.toJvm(),
        id,
        geometry?.toJvm(),
        properties?.let { props -> JsonParser.parseString(props.toString()).asJsonObject }?.takeIf { it.size() > 0 }
    )
}

fun JvmFeature.toCommon(): Feature {
    return Feature(
        geometry = geometry()?.toCommon(),
        bbox = bbox(),
        id = id(),
        properties = properties()?.toKtxJsonMap()?.toMutableMap()
    )
}

fun FeatureCollection.toJvm(): JvmFeatureCollection {
    return JvmFeatureCollection(
        "FeatureCollection",
        bbox?.toJvm(),
        features.map { feat -> feat.toJvm() }
    )
}

fun Point.toJvm(): JvmPoint {
    return JvmPoint(
        "Point",
        bbox?.toJvm(),
        coordinates
    )
}

fun MultiPoint.toJvm(): JvmMultiPoint {
    return JvmMultiPoint(
        "MultiPoint",
        bbox?.toJvm(),
        coordinates.map { coord -> coord.toJvm() }
    )
}

fun LineString.toJvm(): JvmLineString {
    return JvmLineString(
        "LineString",
        bbox?.toJvm(),
        coordinates.map { coord -> coord.toJvm() }
    )
}

fun MultiLineString.toJvm(): JvmMultiLineString {
    return JvmMultiLineString(
        "MultiLineString",
        bbox?.toJvm(),
        coordinates.map { line -> line.map { coord -> coord.toJvm() } }
    )
}

fun Polygon.toJvm(): JvmPolygon {
    return JvmPolygon(
        "Polygon",
        bbox?.toJvm(),
        coordinates.map { line -> line.map { coord -> coord.toJvm() } }
    )
}

fun MultiPolygon.toJvm(): JvmMultiPolygon {
    return JvmMultiPolygon(
        "MultiPolygon",
        bbox?.toJvm(),
        coordinates.map { poly -> poly.map { line -> line.map { coord -> coord.toJvm() } } }
    )
}

fun BoundingBox.toJvm(): JvmBoundingBox {
    return JvmBoundingBox(
        southwest.toJvm(),
        northeast.toJvm()
    )
}

fun GsonJsonObject.toKtxJsonMap(): Map<String, KtxJsonElement> {
    return Json.parseToJsonElement(toString()).jsonObject
}