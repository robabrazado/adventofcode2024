package com.robabrazado.aoc2024.day06;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;

import com.robabrazado.aoc2024.grid.Coords;
import com.robabrazado.aoc2024.grid.Dir;

public class Lab {
	private final Set<Coords> obstacles = new HashSet<Coords>();
	private final int width;
	private final int height;
	private final Guard guard;
	private final Set<Coords> guardVisited = new HashSet<Coords>();
	
	public Lab(Stream<String> puzzleInput) {
		Iterator<String> it = puzzleInput.iterator();
		int row = 0;
		int col = 0;
		Integer tempWidth = null;
		Guard tempGuard = null;
		
		while (it.hasNext()) {
			char[] chars = it.next().toCharArray();
			if (tempWidth == null) {
				tempWidth = chars.length;
			} else {
				if (tempWidth != chars.length) {
					throw new RuntimeException("Lab layout input is not rectangular");
				}
			}
			
			col = 0;
			for (char c : chars) {
				switch (c) {
				case '.':
					// Do nothing
					break;
				case '#':
					// Log obstacle
					this.obstacles.add(new Coords(col, row));
					break;
				case '^': // I'm assuming this is the only guard icon (based on puzzle specs)
					if (tempGuard == null) {
						tempGuard = new Guard(new Coords(col, row), Dir.N);
					} else {
						throw new RuntimeException("Lab layout input has multiple guards");
					}
					break;
				default:
					throw new RuntimeException("Unrecognized layout character: " + c);
				}
				col++;
			}
			row++;
		}
		this.width = tempWidth;
		this.height = row;
		this.guard = tempGuard;
	}
	
	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public Guard getGuard() {
		return this.guard;
	}
	
	public void patrol() {
		while (this.isInBounds(this.guard.getPosition())) {
			this.guardVisited.add(this.guard.getPosition());
			Coords lookAhead = this.guard.getLookAhead();
			if (this.obstacles.contains(lookAhead)) {
				this.guard.turnRight();
			} else {
				this.guard.goForward();
			}
		}
		return;
	}
	
	public int getGuardVisitedCount() {
		return this.guardVisited.size();
	}
	
	private boolean isInBounds(Coords position) {
		int col = position.getCol();
		int row = position.getRow();
		
		return col >= 0 && col < this.width &&
				row >= 0 && row < this.height;
	}
}
