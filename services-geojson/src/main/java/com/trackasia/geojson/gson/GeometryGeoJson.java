package com.trackasia.geojson.gson;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.gson.GsonBuilder;
import com.trackasia.geojson.Geometry;
import com.trackasia.geojson.GeometryAdapterFactory;

/**
 * This is a utility class that helps create a Geometry instance from a JSON string.
 * @since 4.0.0
 */
@Keep
public class GeometryGeoJson {

  /**
   * Create a new instance of Geometry class by passing in a formatted valid JSON String.
   *
   * @param json a formatted valid JSON string defining a GeoJson Geometry
   * @return a new instance of Geometry class defined by the values passed inside
   *   this static factory method
   * @since 4.0.0
   */
  public static Geometry fromJson(@NonNull String json) {

    GsonBuilder gson = new GsonBuilder();
    gson.registerTypeAdapterFactory(GeoJsonAdapterFactory.create());
    gson.registerTypeAdapterFactory(GeometryAdapterFactory.create());

    return gson.create().fromJson(json, Geometry.class);
  }
}
