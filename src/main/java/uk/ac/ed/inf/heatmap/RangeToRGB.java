package uk.ac.ed.inf.heatmap;

import java.util.NavigableMap;
import java.util.TreeMap;

public class RangeToRGB {
	private static final NavigableMap<Integer, String> RANGE_TO_RGB;

	static {
		RANGE_TO_RGB = new TreeMap<Integer, String>();
		RANGE_TO_RGB.put(0, "#00ff00");
		RANGE_TO_RGB.put(32, "#40ff00");
		RANGE_TO_RGB.put(64, "#80ff00");
		RANGE_TO_RGB.put(96, "#c0ff00");
		RANGE_TO_RGB.put(128, "#ffc000");
		RANGE_TO_RGB.put(160, "#ff8000");
		RANGE_TO_RGB.put(192, "#ff4000");
		RANGE_TO_RGB.put(224, "#ff0000");
	}

	public static String getRGBString(int num) throws IllegalArgumentException {
		if (num < 0 || num > 255) {
			throw new IllegalArgumentException("The key should be between 0 to 255.");
		} else {
			return RANGE_TO_RGB.floorEntry(num).getValue();
		}
	}
}