package com.robabrazado.aoc2024.day16;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.robabrazado.aoc2024.grid.Coords;
import com.robabrazado.aoc2024.grid.Dir;

public class Maze {
	 private final CellType[][] grid;
	 private final int height;
	 private final int width;
	 private final Coords start;
//	 private final Coords end;
	
	public Maze(Stream<String> puzzleInput) {
		List<CellType[]> tempGrid = new ArrayList<CellType[]>();
		Iterator<String> it = puzzleInput.iterator();
		Integer tempWidth = null;
		Coords tempStart = null;
		Coords tempEnd = null;
		int row = 0;
		while (it.hasNext()) {
			char[] chars = it.next().toCharArray();
			int len = chars.length;
			if (tempWidth == null) {
				tempWidth = len;
			} else if (tempWidth != len) {
				throw new RuntimeException("Maze map input is not rectangular");
			}
			CellType[] cells = new CellType[len];
			for (int col = 0; col < len; col++) {
				char c = chars[col];
				switch (c) {
				case '.':
					cells[col] = CellType.SPACE;
					break;
				case '#':
					cells[col] = CellType.WALL;
					break;
				case 'S':
					cells[col] = CellType.START;
					if (tempStart == null) {
						tempStart = new Coords(col, row);
					} else {
						throw new RuntimeException("Maze map input has more than one starting point");
					}
					break;
				case 'E':
					cells[col] = CellType.END;
					if (tempEnd == null) {
						tempEnd = new Coords(col, row);
					} else {
						throw new RuntimeException("Maze map input has more than one ending point");
					}
					break;
				default:
					throw new RuntimeException("Unrecognized maze map character: " + c);
				}
			}
			tempGrid.add(cells);
			row++;
		}
		
		this.grid = tempGrid.toArray(new CellType[0][0]);
		this.width = tempWidth;
		this.height = row;
		this.start = tempStart;
//		this.end = tempEnd;
		return;
	}
	
	@Override
	public String toString() {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		
		for (int row = 0; row < this.width; row++) {
			for (int col = 0; col < this.height; col++) {
				pw.print(this.grid[row][col].getChar());
			}
			pw.println();
		}
		
		return sw.toString();
	}
	
	// Returns null if end unreachable...for some reason?!
	public Integer lowestScoreToEnd() {
		return this.findLowestScoreToEnd(this.start, Dir.E, 0, new HashMap<PathStep, Integer>());
	}
	
	// Returns null if end unreachable or search abandoned
	private Integer findLowestScoreToEnd(Coords fromPosition, Dir fromFacing, int scoreSoFar, Map<PathStep, Integer> seen) {
		Integer result = null;
		PathStep thisStep = new PathStep(fromPosition, fromFacing);
		
		if (!seen.containsKey(thisStep) || seen.get(thisStep) > scoreSoFar) {
			seen.put(thisStep, scoreSoFar);
			CellType cell = this.cellAtCoords(fromPosition);
			
			if (cell == CellType.END) {
				result = scoreSoFar;
			} else {
				Integer neighborScore = null;
				// Try forward first, since it's the "closest" neighbor.
				Coords checking = fromPosition.applyOffset(fromFacing);
				if (this.cellAtCoords(checking) != CellType.WALL) {
					result = this.findLowestScoreToEnd(checking, fromFacing, scoreSoFar + 1, seen);
				}
				
				// Try turning right
				Dir newFacing = fromFacing.turnClockwise(2);
				checking = fromPosition.applyOffset(newFacing);
				if (this.cellAtCoords(checking) != CellType.WALL) {
					neighborScore = this.findLowestScoreToEnd(checking, newFacing, scoreSoFar + 1001, seen);
				}
				if (result != null && neighborScore != null) {
					result = Math.min(result, neighborScore);
				} else if (result == null) {
					result = neighborScore;
				}
				
				// Try turning left
				newFacing = fromFacing.turnClockwise(-2);
				checking = fromPosition.applyOffset(newFacing);
				if (this.cellAtCoords(checking) != CellType.WALL) {
					neighborScore = this.findLowestScoreToEnd(checking, newFacing, scoreSoFar + 1001, seen);
				}
				if (result != null && neighborScore != null) {
					result = Math.min(result, neighborScore);
				} else if (result == null) {
					result = neighborScore;
				}
				
				// Don't bother trying the way you came, since that will never be the shortest route.
			}
		}
		return result;
	}
	
	private CellType cellAtCoords(Coords c) {
		return this.grid[c.getRow()][c.getCol()];
	}
	
	public enum CellType {
		SPACE		('.'),
		WALL		('#'),
		START		('S'),
		END			('E');
		
		private final char c;
		
		CellType(char c) {
			this.c = c;
		}
		
		public char getChar() {
			return this.c;
		}
	}
	
	private record PathStep(Coords position, Dir direction) {}
}
