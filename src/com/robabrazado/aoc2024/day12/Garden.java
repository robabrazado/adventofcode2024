package com.robabrazado.aoc2024.day12;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import com.robabrazado.aoc2024.grid.Coords;
import com.robabrazado.aoc2024.grid.Dir;

public class Garden {
	private final char[][] plots;
	private final int width;
	private final int height;
	
	public Garden(Stream<String> puzzleInput) {
		List<char[]> tempGrid = new ArrayList<char[]>();
		Integer tempWidth = null;
		Iterator<String> it = puzzleInput.iterator();
		while (it.hasNext()) {
			char[] chars = it.next().toCharArray();
			int len = chars.length;
			if (tempWidth == null) {
				tempWidth = len;
			} else if (tempWidth != len) {
				throw new RuntimeException("Garden plot map input is not rectangular");
			}
			tempGrid.add(chars);
		}
		this.width = tempWidth;
		this.height = tempGrid.size();
		this.plots = tempGrid.toArray(new char[0][0]);
		return;
	}
	
	@Override
	public String toString() {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		
		for (int row = 0; row < this.height; row++) {
			pw.println(this.plots[row]);
		}
		return sw.toString();
	}
	
	public List<Region> getRegions() {
		Dir[] cardinals = Dir.cardinals();
		List<Region> result = new ArrayList<Region>();
		
		// Assemble queue of cells to examine
		Deque<Coords> unregionedCells = new ArrayDeque<Coords>();
		for (int row = 0; row < this.height; row++) {
			for (int col = 0; col < this.width; col++) {
				unregionedCells.add(new Coords(col, row));
			}
		}
		
		// Go through cells and assemble regions
		while (!unregionedCells.isEmpty()) {
			Coords myCoords = unregionedCells.poll();
			char myPlant = this.getPlantAt(myCoords);
			
			// Start a new region
			Region thisRegion = new Region(myPlant);
			Deque<Coords> inRegion = new ArrayDeque<Coords>();
			result.add(thisRegion);
			inRegion.add(myCoords);
			
			while (!inRegion.isEmpty()) {
				myCoords = inRegion.poll();
				// I am always in my region
				thisRegion.area++;
				
				// Check if my neighbors are in my region
				for (Dir d : cardinals) {
					Coords neighborCoords = myCoords.applyOffset(d);
					if (this.isInBounds(neighborCoords) && myPlant == this.getPlantAt(neighborCoords)) {
						// Neighbor is in region; move neighbor from unregioned queue to this region's queue (if not already examined)
						if (unregionedCells.remove(neighborCoords)) {
							inRegion.add(neighborCoords);
						}
					} else {
						// Neighbor is not in region (or edge of garden); this is a perimeter side
						thisRegion.perimeter++;
					}
				}
			} // Once region queue is exhausted, this region is closed
		} // Continue checking unregioned cells
		
		return result;
	}
	
	private char getPlantAt(Coords c) {
		return this.plots[c.getRow()][c.getCol()];
	}
	
	private boolean isInBounds(Coords c) {
		int col = c.getCol();
		int row = c.getRow();
		return col >= 0 && col < this.width &&
				row >= 0 && row < this.height;
	}
	
	public class Region {
		private final char plant;
		private int area = 0;
		private int perimeter = 0;
		
		Region(char c) {
			this.plant = c;
			return;
		}
		
		public char getPlant() {
			return this.plant;
		}
		
		public int getArea() {
			return this.area;
		}
		
		public int getPerimeter() {
			return this.perimeter;
		}
		
		@Override
		public String toString() {
			return String.format("%c region area %d perimeter %d", this.plant, this.area, this.perimeter);
		}
	}
}
