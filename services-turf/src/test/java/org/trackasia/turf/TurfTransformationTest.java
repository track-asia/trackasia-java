package org.trackasia.turf;

import org.trackasia.geojson.Feature;
import org.trackasia.geojson.FeatureCollection;
import org.trackasia.geojson.Polygon;
import org.trackasia.geojson.Point;

import org.junit.Ignore;
import org.junit.Test;

public class TurfTransformationTest extends TestUtils {

  private static final String CIRCLE_IN = "turf-transformation/circle_in.json";
  private static final String CIRCLE_OUT = "turf-transformation/circle_out.json";

  @Test
  @Ignore
  public void name() throws Exception {
    Feature feature = Feature.fromJson(loadJsonFixture(CIRCLE_IN));
    Polygon polygon = TurfTransformation.circle((Point) feature.geometry(),
      feature.getNumberProperty("radius").doubleValue());

    FeatureCollection featureCollection = FeatureCollection.fromJson(loadJsonFixture(CIRCLE_OUT));
    compareJson(featureCollection.features().get(1).geometry().toJson(), polygon.toJson());
  }
}
