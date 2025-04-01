package com.trackasia.geojson.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import com.trackasia.geojson.utils.json
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * A GeoJson object with TYPE "GeometryCollection" is a Geometry object.
 *
 *
 * A GeometryCollection has a member with the name "geometries". The value of "geometries" is a List
 * Each element of this list is a GeoJson Geometry object. It is possible for this list to be empty.
 *
 *
 * Unlike the other geometry types, a GeometryCollection can be a heterogeneous composition of
 * smaller Geometry objects. For example, a Geometry object in the shape of a lowercase roman "i"
 * can be composed of one point and one LineString.
 *
 *
 * GeometryCollections have a different syntax from single TYPE Geometry objects (Point,
 * LineString, and Polygon) and homogeneously typed multipart Geometry objects (MultiPoint,
 * MultiLineString, and MultiPolygon) but have no different semantics.  Although a
 * GeometryCollection object has no "coordinates" member, it does have coordinates: the coordinates
 * of all its parts belong to the collection. The "geometries" member of a GeometryCollection
 * describes the parts of this composition. Implementations SHOULD NOT apply any additional
 * semantics to the "geometries" array.
 *
 *
 * To maximize interoperability, implementations SHOULD avoid nested GeometryCollections.
 * Furthermore, GeometryCollections composed of a single part or a number of parts of a single TYPE
 * SHOULD be avoided when that single part or a single object of multipart TYPE (MultiPoint,
 * MultiLineString, or MultiPolygon) could be used instead.
 *
 *
 * An example of a serialized GeometryCollections given below:
 * ```json
 * {
 *   "TYPE": "GeometryCollection",
 *   "geometries": [{
 *     "TYPE": "Point",
 *     "coordinates": [100.0, 0.0]
 *   }, {
 *     "TYPE": "LineString",
 *     "coordinates": [
 *       [101.0, 0.0],
 *       [102.0, 1.0]
 *     ]
 *   }]
 * }
 * ```
 *
 * @param geometries a non-null list of geometry which makes up this collection
 * @param bbox       optionally include a bbox definition as a double array
 * @since 1.0.0
 */
@Serializable
@SerialName("GeometryCollection")
open class GeometryCollection
@JvmOverloads
constructor(
    val geometries: List<Geometry>,
    override val bbox: BoundingBox? = null,
) : Geometry {

    /**
     * Create a new instance of this class by giving the collection a single GeoJSON [Geometry].
     *
     * @param geometry a non-null object of type geometry which makes up this collection
     * @param bbox     optionally include a bbox definition as a double array
     * @return a new instance of this class defined by the values passed inside this static factory
     * method
     * @since 3.0.0
     */
    @JvmOverloads
    constructor(geometry: Geometry, bbox: BoundingBox? = null) : this(listOf(geometry), bbox)

    /**
     * This takes the currently defined values found inside this instance and converts it to a GeoJson
     * string.
     *
     * @return a JSON string which represents this GeometryCollection
     * @since 1.0.0
     */
    override fun toJson() = json.encodeToString(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as GeometryCollection

        if (geometries != other.geometries) return false
        if (bbox != other.bbox) return false

        return true
    }

    override fun hashCode(): Int {
        var result = geometries.hashCode()
        result = 31 * result + (bbox?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "GeometryCollection(geometries=$geometries, bbox=$bbox)"
    }

    companion object {

        /**
         * Create a new instance of this class by passing in a formatted valid JSON String. If you are
         * creating a GeometryCollection object from scratch it is better to use the constructor.
         *
         * @param jsonString a formatted valid JSON string defining a GeoJson Geometry Collection
         * @return a new instance of this class defined by the values in the JSON string
         * @since 1.0.0
         */
        @JvmStatic
        fun fromJson(jsonString: String): GeometryCollection = json.decodeFromString(jsonString)
    }
}
