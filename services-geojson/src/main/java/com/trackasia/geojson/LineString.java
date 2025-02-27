package com.trackasia.geojson;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.trackasia.geojson.gson.GeoJsonAdapterFactory;
import com.trackasia.geojson.utils.PolylineUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A linestring represents two or more geographic points that share a relationship and is one of the
 * seven geometries found in the GeoJson spec.
 * <p>
 * This adheres to the RFC 7946 internet standard when serialized into JSON. When deserialized, this
 * class becomes an immutable object which should be initiated using its static factory methods.
 * </p><p>
 * The list of points must be equal to or greater than 2. A LineString has non-zero length and
 * zero area. It may approximate a curve and need not be straight. Unlike a LinearRing, a LineString
 * is not closed.
 * </p><p>
 * When representing a LineString that crosses the antimeridian, interoperability is improved by
 * modifying their geometry. Any geometry that crosses the antimeridian SHOULD be represented by
 * cutting it in two such that neither part's representation crosses the antimeridian.
 * </p><p>
 * For example, a line extending from 45 degrees N, 170 degrees E across the antimeridian to 45
 * degrees N, 170 degrees W should be cut in two and represented as a MultiLineString.
 * </p><p>
 * A sample GeoJson LineString's provided below (in it's serialized state).
 * <pre>
 * {
 *   "TYPE": "LineString",
 *   "coordinates": [
 *     [100.0, 0.0],
 *     [101.0, 1.0]
 *   ]
 * }
 * </pre>
 * Look over the {@link Point} documentation to get more
 * information about formatting your list of point objects correctly.
 *
 * @since 1.0.0
 */
@Keep
public final class LineString implements CoordinateContainer<List<Point>> {

  private static final String TYPE = "LineString";

  private final String type;

  private final BoundingBox bbox;

  private final List<Point> coordinates;

  /**
   * Create a new instance of this class by passing in a formatted valid JSON String. If you are
   * creating a LineString object from scratch it is better to use one of the other provided static
   * factory methods such as {@link #fromLngLats(List)}. For a valid lineString to exist, it must
   * have at least 2 coordinate entries. The LineString should also have non-zero distance and zero
   * area.
   *
   * @param json a formatted valid JSON string defining a GeoJson LineString
   * @return a new instance of this class defined by the values passed inside this static factory
   *   method
   * @since 1.0.0
   */
  public static LineString fromJson(String json) {
    GsonBuilder gson = new GsonBuilder();
    gson.registerTypeAdapterFactory(GeoJsonAdapterFactory.create());
    return gson.create().fromJson(json, LineString.class);
  }

  /**
   * Create a new instance of this class by defining a {@link MultiPoint} object and passing. The
   * multipoint object should comply with the GeoJson specifications described in the documentation.
   *
   * @param multiPoint which will make up the LineString geometry
   * @return a new instance of this class defined by the values passed inside this static factory
   *   method
   * @since 3.0.0
   */
  public static LineString fromLngLats(@NonNull MultiPoint multiPoint) {
    return new LineString(TYPE, null, multiPoint.coordinates());
  }

  /**
   * Create a new instance of this class by defining a list of {@link Point}s which follow the
   * correct specifications described in the Point documentation. Note that there should not be any
   * duplicate points inside the list and the points combined should create a LineString with a
   * distance greater than 0.
   * <p>
   * Note that if less than 2 points are passed in, a runtime exception will occur.
   * </p>
   *
   * @param points a list of {@link Point}s which make up the LineString geometry
   * @return a new instance of this class defined by the values passed inside this static factory
   *   method
   * @since 3.0.0
   */
  public static LineString fromLngLats(@NonNull List<Point> points) {
    return new LineString(TYPE, null, points);
  }

  /**
   * Create a new instance of this class by defining a list of {@link Point}s which follow the
   * correct specifications described in the Point documentation. Note that there should not be any
   * duplicate points inside the list and the points combined should create a LineString with a
   * distance greater than 0.
   * <p>
   * Note that if less than 2 points are passed in, a runtime exception will occur.
   * </p>
   *
   * @param points a list of {@link Point}s which make up the LineString geometry
   * @param bbox   optionally include a bbox definition as a double array
   * @return a new instance of this class defined by the values passed inside this static factory
   *   method
   * @since 3.0.0
   */
  public static LineString fromLngLats(@NonNull List<Point> points, @Nullable BoundingBox bbox) {
    return new LineString(TYPE, bbox, points);
  }

  /**
   * Create a new instance of this class by defining a {@link MultiPoint} object and passing. The
   * multipoint object should comply with the GeoJson specifications described in the documentation.
   *
   * @param multiPoint which will make up the LineString geometry
   * @param bbox       optionally include a bbox definition as a double array
   * @return a new instance of this class defined by the values passed inside this static factory
   *   method
   * @since 3.0.0
   */
  public static LineString fromLngLats(@NonNull MultiPoint multiPoint, @Nullable BoundingBox bbox) {
    return new LineString(TYPE, bbox, multiPoint.coordinates());
  }

  LineString(String type, @Nullable BoundingBox bbox, List<Point> coordinates) {
    if (type == null) {
      throw new NullPointerException("Null type");
    }
    this.type = type;
    this.bbox = bbox;
    if (coordinates == null) {
      throw new NullPointerException("Null coordinates");
    }
    this.coordinates = coordinates;
  }

  static LineString fromLngLats(double[][] coordinates) {
    ArrayList<Point> converted = new ArrayList<>(coordinates.length);
    for (double[] coordinate : coordinates) {
      converted.add(Point.fromLngLat(coordinate));
    }
    return LineString.fromLngLats(converted);
  }

  /**
   * Create a new instance of this class by convert a polyline string into a lineString. This is
   * handy when an API provides you with an encoded string representing the line geometry and you'd
   * like to convert it to a useful LineString object. Note that the precision that the string
   * geometry was encoded with needs to be known and passed into this method using the precision
   * parameter.
   *
   * @param polyline  encoded string geometry to decode into a new LineString instance
   * @param precision The encoded precision which must match the same precision used when the string
   *                  was first encoded
   * @return a new instance of this class defined by the values passed inside this static factory
   *   method
   * @since 1.0.0
   */
  public static LineString fromPolyline(@NonNull String polyline, int precision) {
    return LineString.fromLngLats(PolylineUtils.decode(polyline, precision), null);
  }

  /**
   * This describes the TYPE of GeoJson geometry this object is, thus this will always return
   * {@link LineString}.
   *
   * @return a String which describes the TYPE of geometry, for this object it will always return
   *   {@code LineString}
   * @since 1.0.0
   */
  @NonNull
  @Override
  public String type()  {
    return type;
  }

  /**
   * A Feature Collection might have a member named {@code bbox} to include information on the
   * coordinate range for it's {@link Feature}s. The value of the bbox member MUST be a list of
   * size 2*n where n is the number of dimensions represented in the contained feature geometries,
   * with all axes of the most southwesterly point followed by all axes of the more northeasterly
   * point. The axes order of a bbox follows the axes order of geometries.
   *
   * @return a list of double coordinate values describing a bounding box
   * @since 3.0.0
   */
  @Nullable
  @Override
  public BoundingBox bbox()  {
    return bbox;
  }

  /**
   * Provides the list of {@link Point}s that make up the LineString geometry.
   *
   * @return a list of points
   * @since 3.0.0
   */
  @NonNull
  @Override
  public List<Point> coordinates()  {
    return coordinates;
  }

  /**
   * This takes the currently defined values found inside this instance and converts it to a GeoJson
   * string.
   *
   * @return a JSON string which represents this LineString geometry
   * @since 1.0.0
   */
  @Override
  public String toJson() {
    GsonBuilder gson = new GsonBuilder();
    gson.registerTypeAdapterFactory(GeoJsonAdapterFactory.create());
    return gson.create().toJson(this);
  }

  /**
   * Encode this LineString into a Polyline string for easier serializing. When passing geometry
   * information over a mobile network connection, encoding the geometry first will generally result
   * in less bandwidth usage.
   *
   * @param precision the encoded precision which fits your best use-case
   * @return a string describing the geometry of this LineString
   * @since 1.0.0
   */
  public String toPolyline(int precision) {
    return PolylineUtils.encode(coordinates(), precision);
  }

  /**
   * Gson TYPE adapter for parsing Gson to this class.
   *
   * @param gson the built {@link Gson} object
   * @return the TYPE adapter for this class
   * @since 3.0.0
   */
  public static TypeAdapter<LineString> typeAdapter(Gson gson) {
    return new LineString.GsonTypeAdapter(gson);
  }

  @Override
  public String toString() {
    return "LineString{"
            + "type=" + type + ", "
            + "bbox=" + bbox + ", "
            + "coordinates=" + coordinates
            + "}";
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof LineString) {
      LineString that = (LineString) obj;
      return (this.type.equals(that.type()))
              && ((this.bbox == null) ? (that.bbox() == null) : this.bbox.equals(that.bbox()))
              && (this.coordinates.equals(that.coordinates()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;
    hashCode *= 1000003;
    hashCode ^= type.hashCode();
    hashCode *= 1000003;
    hashCode ^= (bbox == null) ? 0 : bbox.hashCode();
    hashCode *= 1000003;
    hashCode ^= coordinates.hashCode();
    return hashCode;
  }

  /**
   * TypeAdapter for LineString geometry.
   *
   * @since 4.6.0
   */
  static final class GsonTypeAdapter extends BaseGeometryTypeAdapter<LineString, List<Point>> {

    GsonTypeAdapter(Gson gson) {
      super(gson, new ListOfPointCoordinatesTypeAdapter());
    }

    @Override
    public void write(JsonWriter jsonWriter, LineString object) throws IOException {
      writeCoordinateContainer(jsonWriter, object);
    }

    @Override
    public LineString read(JsonReader jsonReader) throws IOException {
      return (LineString) readCoordinateContainer(jsonReader);
    }

    @Override
    CoordinateContainer<List<Point>> createCoordinateContainer(String type,
                                                               BoundingBox bbox,
                                                               List<Point> coordinates) {
      return new LineString(type == null ? "LineString" : type, bbox, coordinates);
    }
  }
}
