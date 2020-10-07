package uk.ac.ed.inf.heatmap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.gson.JsonObject;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;

public class Heatmap {
	private static final double NORTH_LAT = 55.946233;
	private static final double SOUTH_LAT = 55.942617;
	private static final double EAST_LNG = -3.184319;
	private static final double WEST_LNG = -3.192473;
	private final int GRID_HEIGHT;
	private final int GRID_WIDTH;
	private FeatureCollection featureCollection;

	public Heatmap(int height, int width) {
		this.GRID_HEIGHT = height;
		this.GRID_WIDTH = width;
	}

	private Point[][] getGridCoords() {
		Point[][] gridCoords = new Point[GRID_HEIGHT + 1][GRID_WIDTH + 1];
		double latDelta = (NORTH_LAT - SOUTH_LAT) / GRID_HEIGHT;
		double lngDelta = (EAST_LNG - WEST_LNG) / GRID_WIDTH;
		double startLat = NORTH_LAT;
		double startLng = WEST_LNG;
		for (int i = 0; i <= GRID_HEIGHT; i++) {
			for (int j = 0; j <= GRID_WIDTH; j++) {
				Point p = Point.fromLngLat(startLng + j * lngDelta, startLat - i * latDelta);
				gridCoords[i][j] = p;
			}
		}
		return gridCoords;
	}

	private List<Feature> buildPolygonFeatures(int[][] predictions) {
		var gridCoords = getGridCoords();
		var features = new ArrayList<Feature>();
		for (int i = 0; i < GRID_HEIGHT; i++) {
			for (int j = 0; j < GRID_WIDTH; j++) {
				List<Point> points = Arrays.asList(gridCoords[i][j], gridCoords[i + 1][j], gridCoords[i + 1][j + 1],
						gridCoords[i][j + 1]);
				String rgbString = RangeToRGB.getRGBString(predictions[i][j]);
				// Make a JSON object for properties of the polygon
				JsonObject properties = new JsonObject();
				properties.addProperty("fill-opacity", 0.75);
				properties.addProperty("fill", rgbString);
				properties.addProperty("rgb-string", rgbString);
				// Construct the polygon feature from the list of points and assign it the properties
				Feature polygon = Feature.fromGeometry(Polygon.fromLngLats(Collections.singletonList(points)), properties);
				// Add the constructed polygon to the features list
				features.add(polygon);
			}
		}
		return features;
	}

	public void getFeatureCollection(int[][] predictions) throws IllegalArgumentException {
		if (predictions.length != GRID_HEIGHT || predictions[0].length != GRID_WIDTH) {
			throw new IllegalArgumentException("The predictions grid does not meet the required boundary conditions.");
		}
		var features = buildPolygonFeatures(predictions);
		this.featureCollection = FeatureCollection.fromFeatures(features);
	}

	public String getFeatureCollectionJSON() throws Exception {
		if (featureCollection == null) {
			throw new Exception("Feature collection not constructed yet use getFeatureCollection first.");
		}
		return featureCollection.toJson();
	}

}
