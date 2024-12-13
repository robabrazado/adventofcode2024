package com.robabrazado.aoc2024.day04;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import com.robabrazado.aoc2024.grid.Coords;
import com.robabrazado.aoc2024.grid.Dir;

/*
 * I originally (on day 4) had these grand designs about making a general-use
 * Grid class that I could wrap around various puzzle-specific data types
 * and just use as a, like...grid-navigating framework. The vestiges of this
 * are the <code>com.robabrazado.aoc2024.grid</code> package, but at the time,
 * I abandoned the general use class and just never got back around to the
 * Day 4 puzzle.
 * 
 * It is now day 12, and I have some time, so I'm taking another stab at the
 * Day 4 puzzle.
 */
public class WordSearch {
	private final char[][] letters;
	private final int height;
	private final int width;
	
	public WordSearch(Stream<String> puzzleInput) {
		List<char[]> tempLetters = new ArrayList<char[]>();
		Integer width = null;
		
		Iterator<String> it = puzzleInput.iterator();
		while (it.hasNext()) {
			String line = it.next();
			tempLetters.add(line.toCharArray());
			
			if (width == null) {
				width = Integer.valueOf(line.length());
				if (width == 0) {
					throw new IllegalArgumentException("Input grid has no width");
				}
			} else if (width != line.length()) {
				throw new IllegalArgumentException("Input grid is non-rectangular");
			}
		}
		this.width = width;
		this.height = tempLetters.size();
		if (this.height == 0) {
			throw new IllegalArgumentException("Input grid has no height");
		}
		this.letters = tempLetters.toArray(new char[][] {});
	}
	
	public int countXmas() {
		String word = "XMAS";
		int total = 0;
		
		
		Coords cell = new Coords(0, 0);
		while (cell != null) {
			total += this.countWordFrom(word, cell);
			cell = this.nextCell(cell);
		}
		
		return total;
	}
	
	public int countMasX() {
		int total = 0;
		
		Coords cell = new Coords(0, 0);
		while (cell != null) {
			if (this.isMasX(cell)) {
				total++;
			}
			cell = this.nextCell(cell);
		}
		
		return total;
	}
	
	private boolean isMasX(Coords cell) {
		String word = "MAS";
		char centerChar = word.charAt(1);
		boolean found = false;
		
		// First just see if this is a center
		if (this.charAt(cell) == centerChar) {
			// One leg of the X
			Dir testDir = Dir.SE; // Arbitrary starting direction
			Dir oppDir = testDir.oppositeDirection();
			
			// Check first leg
			if (this.isWordInDir(word, cell.applyOffset(oppDir), testDir) ||
					this.isWordInDir(word, cell.applyOffset(testDir), oppDir)) {
				// Other leg of the X
				testDir = testDir.turnClockwise(2);
				oppDir = testDir.oppositeDirection();
				
				// Check the second leg
				found = this.isWordInDir(word, cell.applyOffset(oppDir), testDir) ||
						this.isWordInDir(word, cell.applyOffset(testDir), oppDir);
			}
		}
		
		return found;
	}
	
	private int countWordFrom(String word, Coords cell) {
		int total = 0;
		if (word.length() > 0 && this.charAt(cell) == word.charAt(0)) {
			for (Dir d : Dir.values()) {
				if (this.isWordInDir(word, cell, d)) {
					total++;
				}
			}
		}
		return total;
	}
	
	private boolean isWordInDir(String word, Coords cell, Dir d) {
		boolean found = false;
		
		if (this.isInBounds(cell) && word.length() > 0 &&
				this.charAt(cell) == word.charAt(0)) {
			if (word.length() == 1) {
				found = true;
			} else {
				Coords newCell = cell.applyOffset(d);
				if (this.isInBounds(newCell)) {
					found = isWordInDir(word.substring(1), newCell, d);
				}
			}
		}
		
		return found;
	}
	
	// Returns null if none
	private Coords nextCell(Coords oldCoords) {
		Coords result = null;
		int row = oldCoords.getRow();
		int col = oldCoords.getCol();
		
		if (col < this.width - 1) {
			col++;
		} else {
			row++;
			col = 0;
		}
		
		if (row < this.height) {
			result = new Coords(col, row);
		}
		return result;
	}
	
	private boolean isInBounds(Coords cell) {
		int col = cell.getCol();
		int row = cell.getRow();
		return col >= 0 && col < this.width &&
				row >= 0 && row < this.height;
	}
	
	public char charAt(Coords cell) {
		return this.letters[cell.getRow()][cell.getCol()];
	}
	
	@Override
	public String toString() {
		StringBuilder strb = new StringBuilder();
		for (char[] chars : this.letters) {
			strb.append(chars).append('\n');
		}
		return strb.toString();
	}
}
