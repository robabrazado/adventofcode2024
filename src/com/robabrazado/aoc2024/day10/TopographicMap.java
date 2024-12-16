package com.robabrazado.aoc2024.day10;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import com.robabrazado.aoc2024.grid.Coords;
import com.robabrazado.aoc2024.grid.Dir;

public class TopographicMap {
	private final Cell[][] grid;
	private final int width;
	private final int height;
	
	public TopographicMap(Stream<String> puzzleInput) {
		Iterator<String> it = puzzleInput.iterator();
		Integer tempWidth = null;
		List<Cell[]> tempGrid = new ArrayList<Cell[]>();
		
		while (it.hasNext()) {
			char[] chars = it.next().toCharArray();
			int len = chars.length;
			if (tempWidth == null) {
				tempWidth = len;
			} else {
				if (tempWidth != len) {
					throw new RuntimeException("Map input is not rectangular");
				}
			}
			Cell[] cells = new Cell[len];
			for (int i = 0; i < len; i++) {
				cells[i] = new Cell(chars[i] - '0');
			}
			tempGrid.add(cells);
		}
		
		this.grid = tempGrid.toArray(new Cell[0][0]);
		this.width = tempWidth;
		this.height = tempGrid.size();
		return;
	}
	
	public int sumTrailheadScores() {
		int result = 0;
		Dir[] dirs = Dir.cardinals();
		
		// First log which 8s can reach 9s
		// Then start descending; each location can reach the peaks its eligible neighbors can reach
		// Finally, log the trailhead scores at the lowest height
		// Eh, fuck it, I'm putting this all in one loop, performance be damned.
		for (int height = 9; height >= 0; height--) {
			Set<Coords> cs = this.getLocationsWithHeight(height);
			for (Coords c : cs) {
				Cell me = this.getCell(c);
				if (me.height == 9) {
					me.canReachPeak.add(c);
				} else {
					for (Dir d : dirs) {
						Coords checking = c.applyOffset(d);
						if (this.isInBounds(checking)) {
							Cell other = this.getCell(checking);
							if (other.height == me.height + 1) {
								me.canReachPeak.addAll(other.canReachPeak);
							}
						}
					}
					if (me.height == 0) {
						result += me.canReachPeak.size();
					}
				}
			}
		}
		
		return result;
	}
	
	private Cell getCell(Coords c) {
		return this.grid[c.getRow()][c.getCol()];
	}
	
	@Override
	public String toString() {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		
		for (Cell[] row : this.grid) {
			for (Cell c : row) {
				pw.print(c.height);
			}
			pw.println();
		}
		
		return sw.toString();
	}
	
	private Set<Coords> getLocationsWithHeight(int height) {
		Set<Coords> locations = new HashSet<Coords>();
		for (int col = 0; col < this.width; col++) {
			for (int row = 0; row < this.height; row++) {
				if (this.grid[row][col].height == height) {
					locations.add(new Coords(col, row));
				}
			}
		}
		return locations;
	}
	
	private boolean isInBounds(Coords c) {
		int col = c.getCol();
		int row = c.getRow();
		return col >= 0 && col < this.width &&
				row >= 0 && row < this.height;
	}
	
	private class Cell {
		final int height;
		final Set<Coords> canReachPeak = new HashSet<Coords>();
		
		Cell(int height) {
			this.height = height;
			return;
		}
	}
}
