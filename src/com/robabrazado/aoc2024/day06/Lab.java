package com.robabrazado.aoc2024.day06;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import com.robabrazado.aoc2024.grid.Coords;
import com.robabrazado.aoc2024.grid.Dir;

public class Lab {
	private final Set<Coords> obstacles = new HashSet<Coords>();
	private final int width;
	private final int height;
	private final Guard guard;
	private final Map<Coords, Set<Dir>> guardVisited = new HashMap<Coords, Set<Dir>>();
	
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
	
	private Lab(Set<Coords> obstacles, int width, int height, Guard guard, Map<Coords, Set<Dir>> guardVisisted) {
		this.width = width;
		this.height = height;
		this.guard = guard;
		this.obstacles.addAll(obstacles);
		this.guardVisited.putAll(guardVisisted);
		return;
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
	
	// Returns true if guard left the lab; false if guard stuck in a loop
	public boolean patrolUntilGone() {
		boolean leaving = true;
		Coords position = this.guard.getPosition();
		while (this.isInBounds(position) && leaving) {
			if (!this.guardVisited.containsKey(position)) {
				this.guardVisited.put(position, new HashSet<Dir>());
			}
			// If the guard has previously been in this position in this direction, guard is in a loop
			if (this.guardVisited.get(position).contains(this.guard.getFacing())) {
				leaving = false;
			} else {
				this.guardVisited.get(position).add(this.guard.getFacing());
			}
			if (leaving) {
				Coords lookAhead = this.guard.getLookAhead();
				if (this.obstacles.contains(lookAhead)) {
					this.guard.turnRight();
				} else {
					this.guard.goForward();
				}
				position = this.guard.getPosition();
			}
		}
		return leaving;
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

	public void addObstacle(Coords obstacle) {
		this.obstacles.add(obstacle);
		return;
	}
	
	public Set<Coords> getNewObstacleCandidates() {
		Set<Coords> result = new HashSet<Coords>();
		for (int col = 0; col < this.width; col++) {
			for (int row = 0; row < this.height; row++) {
				Coords c = new Coords(col, row);
				if (!this.obstacles.contains(c) && !this.guard.getPosition().equals(c)) {
					result.add(c);
				}
			}
		}
		return result;
	}
	
	public Lab copy() {
		return new Lab(this.obstacles, this.width, this.height,
				new Guard(this.guard.getPosition(), this.guard.getFacing()), this.guardVisited);
	}
}
