package com.trackasia.geojson

import com.trackasia.geojson.common.toCommon
import com.trackasia.geojson.common.toJvm
import com.trackasia.geojson.model.GeometryCollection as CommonGeometryCollection

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
 * @since 1.0.0
 */
@Deprecated(
    message = "Use new common models instead.",
    replaceWith = ReplaceWith("GeometryCollection", "com.trackasia.geojson.model.GeometryCollection"),
)
class GeometryCollection internal constructor(
    type: String,
    bbox: BoundingBox?,
    geometries: List<Geometry>
) : CommonGeometryCollection(
    geometries.map { geo -> geo.toCommon() },
    bbox
), Geometry {

    /**
     * This describes the TYPE of GeoJson this object is, thus this will always return
     * [GeometryCollection].
     *
     * @return a String which describes the TYPE of geometry, for this object it will always return
     * `GeometryCollection`
     * @since 1.0.0
     */
    override fun type(): String = TYPE

    /**
     * A Feature Collection might have a member named `bbox` to include information on the
     * coordinate range for it's [Feature]s. The value of the bbox member MUST be a list of
     * size 2*n where n is the number of dimensions represented in the contained feature geometries,
     * with all axes of the most southwesterly point followed by all axes of the more northeasterly
     * point. The axes order of a bbox follows the axes order of geometries.
     *
     * @return a list of double coordinate values describing a bounding box
     * @since 3.0.0
     */
    override fun bbox(): BoundingBox? = bbox?.toJvm()

    /**
     * This provides the list of geometry making up this Geometry Collection. Note that if the
     * Geometry Collection was created through [.fromJson] this list could be null.
     * Otherwise, the list can't be null but the size of the list can equal 0.
     *
     * @return a list of [Geometry] which make up this Geometry Collection
     * @since 1.0.0
     */
    fun geometries(): List<Geometry> = geometries.map { geo -> geo.toJvm() }

    companion object {
        private const val TYPE = "GeometryCollection"

        /**
         * Create a new instance of this class by passing in a formatted valid JSON String. If you are
         * creating a GeometryCollection object from scratch it is better to use one of the other provided
         * static factory methods such as [.fromGeometries].
         *
         * @param json a formatted valid JSON string defining a GeoJson Geometry Collection
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 1.0.0
         */
        @JvmStatic
        fun fromJson(json: String): GeometryCollection = CommonGeometryCollection.fromJson(json).toJvm()

        /**
         * Create a new instance of this class by giving the collection a list of [Geometry].
         *
         * @param geometries a non-null list of geometry which makes up this collection
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 1.0.0
         */
        @JvmStatic
        fun fromGeometries(geometries: List<Geometry>): GeometryCollection {
            return GeometryCollection(TYPE, null, geometries)
        }

        /**
         * Create a new instance of this class by giving the collection a list of [Geometry].
         *
         * @param geometries a non-null list of geometry which makes up this collection
         * @param bbox       optionally include a bbox definition as a double array
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 1.0.0
         */
        @JvmStatic
        fun fromGeometries(
            geometries: List<Geometry>,
            bbox: BoundingBox?
        ): GeometryCollection {
            return GeometryCollection(TYPE, bbox, geometries)
        }

        /**
         * Create a new instance of this class by giving the collection a single GeoJSON [Geometry].
         *
         * @param geometry a non-null object of type geometry which makes up this collection
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        @JvmStatic
        fun fromGeometry(geometry: Geometry): GeometryCollection {
            val geometries = listOf(geometry)
            return GeometryCollection(TYPE, null, geometries)
        }

        /**
         * Create a new instance of this class by giving the collection a single GeoJSON [Geometry].
         *
         * @param geometry a non-null object of type geometry which makes up this collection
         * @param bbox     optionally include a bbox definition as a double array
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        @JvmStatic
        fun fromGeometry(
            geometry: Geometry,
            bbox: BoundingBox?
        ): GeometryCollection {
            val geometries = listOf(geometry)
            return GeometryCollection(TYPE, bbox, geometries)
        }
    }
}