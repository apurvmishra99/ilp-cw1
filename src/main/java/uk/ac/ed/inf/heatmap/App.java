package uk.ac.ed.inf.heatmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class App {
	
	private static void writeToFile(String fileName, String toWrite) {
		try (FileWriter file = new FileWriter(fileName)) {
			file.write(toWrite);
			file.flush();
			System.out.println("Output successfully written to file " + fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		String inputFilePath = args[0];
		
		// height and width of the grid
		int height = 0;
		int width = 0;
		
		// Nested array list to parse the predictions text file
		var predictionsGrid = new ArrayList<ArrayList<Integer>>();
		
		Scanner input = null;
		try {
			input = new Scanner(new File(inputFilePath));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		while (input.hasNextLine()) {
			height++;
			String[] line = input.nextLine().split(", ");
			width = line.length;
			var row = new ArrayList<Integer>();
			for (int j = 0; j < width; j++) {
				int num = Integer.parseInt(line[j]); // parse the int value
				row.add(num);
			}
			var currHeight = predictionsGrid.size();
			if (currHeight > 1) {
				if (row.size() != predictionsGrid.get(currHeight - 1).size()) 
					throw new IllegalArgumentException("All the rows must have the same size.");
			}
			predictionsGrid.add(row);
		}
		
		int[][] predictionsGridArray = predictionsGrid.stream()
				.map(l -> l.stream()
				.mapToInt(Integer::intValue).toArray())    
				.toArray(int[][]::new);

		Heatmap heatmap = new Heatmap(height, width);
		heatmap.getFeatureCollection(predictionsGridArray);
		
		String heatmapGeoJSON = null;
		try {
			heatmapGeoJSON = heatmap.getFeatureCollectionJSON();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		writeToFile("heatmap.geojson", heatmapGeoJSON);
		
		System.out.println("Program successfully executed.");
		
	}
}
