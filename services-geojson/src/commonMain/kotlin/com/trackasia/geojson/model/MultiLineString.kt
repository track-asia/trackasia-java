package com.trackasia.geojson.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import com.trackasia.geojson.serializer.PointDoubleArraySerializer
import com.trackasia.geojson.utils.json
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * A multilinestring is an array of LineString coordinate arrays.
 *
 *
 * This adheres to the RFC 7946 internet standard when serialized into JSON. When deserialized, this
 * class becomes an immutable object which should be initiated using its static factory methods.
 *
 *
 * When representing a LineString that crosses the antimeridian, interoperability is improved by
 * modifying their geometry. Any geometry that crosses the antimeridian SHOULD be represented by
 * cutting it in two such that neither part's representation crosses the antimeridian.
 *
 *
 * For example, a line extending from 45 degrees N, 170 degrees E across the antimeridian to 45
 * degrees N, 170 degrees W should be cut in two and represented as a MultiLineString.
 *
 *
 * A sample GeoJson MultiLineString's provided below (in it's serialized state).
 * ```json
 * {
 *   "type": "MultiLineString",
 *   "coordinates": [
 *     [
 *       [100.0, 0.0],
 *       [101.0, 1.0]
 *     ],
 *     [
 *       [102.0, 2.0],
 *       [103.0, 3.0]
 *     ]
 *   ]
 * }
 * ```
 *
 * Look over the [LineString] documentation to get more information about
 * formatting your list of linestring objects correctly.
 *
 * @param coordinates a list of {@link Point}s which make up the MultiLineString geometry
 * @param bbox   optionally include a bbox definition
 * @since 1.0.0
 */
@Serializable
@SerialName("MultiLineString")
open class MultiLineString
@JvmOverloads
constructor(
    override val coordinates: List<List<@Serializable(with = PointDoubleArraySerializer::class) Point>>,
    override val bbox: BoundingBox? = null,
) : CoordinateContainer<List<List<Point>>> {

    /**
     * Returns a list of LineStrings which are currently making up this MultiLineString.
     *
     * @return a list of [LineString]s
     * @since 3.0.0
     */
    val lineStrings: List<LineString>
        get() = coordinates.map { points -> LineString(points) }

    /**
     * This takes the currently defined values found inside this instance and converts it to a GeoJson
     * string.
     *
     * @return a JSON string which represents this MultiLineString geometry
     * @since 1.0.0
     */
    override fun toJson() = json.encodeToString(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as MultiLineString

        if (coordinates != other.coordinates) return false
        if (bbox != other.bbox) return false

        return true
    }

    override fun hashCode(): Int {
        var result = coordinates.hashCode()
        result = 31 * result + (bbox?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "MultiLineString(coordinates=$coordinates, bbox=$bbox)"
    }

    companion object {

        /**
         * Create a new instance of this class by defining a list of [LineString] objects and
         * passing that list in as a parameter in this method. The LineStrings should comply with the
         * GeoJson specifications described in the documentation. Optionally, pass in an instance of a
         * [BoundingBox] which better describes this MultiLineString.
         *
         * @param lineStrings a list of LineStrings which make up this MultiLineString
         * @param bbox        optionally include a bbox definition
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        @JvmStatic
        @JvmOverloads
        fun fromLineStrings(
            lineStrings: List<LineString>,
            bbox: BoundingBox? = null
        ) = MultiLineString(lineStrings.map { lineString -> lineString.coordinates }, bbox)

        /**
         * Create a new instance of this class by passing in a single [LineString] object. The
         * LineStrings should comply with the GeoJson specifications described in the documentation.
         *
         * @param lineString a single LineString which make up this MultiLineString
         * @param bbox       optionally include a bbox definition
         * @return a new instance of this class defined by the values passed inside this static factory
         * method
         * @since 3.0.0
         */
        @JvmStatic
        @JvmOverloads
        fun fromLineString(
            lineString: LineString,
            bbox: BoundingBox? = null
        ) = MultiLineString(listOf(lineString.coordinates), bbox)

        /**
         * Create a new instance of this class by passing in a formatted valid JSON String. If you are
         * creating a MultiLineString object from scratch it is better to use the constructor.
         *
         * @param jsonString a formatted valid JSON string defining a GeoJson MultiLineString
         * @return a new instance of this class defined by the values in the JSON string
         * @since 1.0.0
         */
        @JvmStatic
        fun fromJson(jsonString: String): MultiLineString = json.decodeFromString(jsonString)
    }
}
