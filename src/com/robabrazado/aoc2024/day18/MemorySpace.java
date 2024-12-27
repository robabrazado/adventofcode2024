package com.robabrazado.aoc2024.day18;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.robabrazado.aoc2024.grid.Coords;
import com.robabrazado.aoc2024.grid.Dir;

public class MemorySpace {
	private static final Pattern COORDINATE_PATTERN = Pattern.compile("^(\\d+),(\\d+)$");
	
	private final int size;
	private final Coords start = new Coords(0, 0);
	private final Coords exit;
	private final List<Coords> fallingBytes = new ArrayList<Coords>();
	private final Set<Coords> corrupted = new HashSet<Coords>();
	private int currentTime = 0;
	
	public MemorySpace(int size) {
		if (size >= 0) {
			this.size = size;
			int lastIndex = size - 1;
			this.exit = new Coords(lastIndex, lastIndex);
			return;
		} else {
			throw new IllegalArgumentException("Negative size not allowed");
		}
	}
	
	public void setFallingBytes(List<Coords> locations) {
		this.clearFallingBytes();
		if (locations != null) {
			this.fallingBytes.addAll(locations);
		}
		return;
	}
	
	public void clearFallingBytes() {
		this.fallingBytes.clear();
		return;
	}
	
	public void addFallingByte(Coords c) {
		this.fallingBytes.add(c);
		return;
	}
	
	public void advanceTime(int nanos) {
		if (nanos > 0) {
			List<Coords> bytesToFall = new ArrayList<Coords>(this.fallingBytes.subList(this.currentTime, this.currentTime + nanos));
			for (int i = 1; i <= nanos; i++) {
				if (bytesToFall.size() > 0) {
					this.corrupted.add(bytesToFall.remove(0));
				}
			}
			this.currentTime += nanos;
		} else if (nanos < 0) {
			throw new IllegalArgumentException("Cannot go back in time");
		} // else advance 0 does nothing
		return;
	}
	
	public void resetTime() {
		this.currentTime = 0;
		this.corrupted.clear();
		return;
	}
	
	public int minStepsToExit() {
		// Gonna try this with a PriorityQueue and see how it goes
		PriorityQueue<CoordsWithDistance> unseen = new PriorityQueue<CoordsWithDistance>();
		Map<Coords, CoordsWithDistance> distanceMap = new HashMap<Coords, CoordsWithDistance>();
		
		// Initialize unseen set and distance list
		for (int col = 0; col < this.size; col++) {
			for (int row = 0; row < this.size; row++) {
				Coords c = new Coords(col, row);
				if (!this.corrupted.contains(c)) {
					CoordsWithDistance cwd = new CoordsWithDistance(c);
					unseen.add(cwd);
					distanceMap.put(c, cwd);
				}
			}
		}
		
		// Initialize start node
		distanceMap.get(this.start).distance = 0;
		
		// Pathfind!
		Dir[] cardinals = Dir.cardinals();
		while (unseen.size() > 0) {
			CoordsWithDistance myCwd = unseen.poll();
			if (!this.exit.equals(myCwd.coords)) {
				// Check and measure neighbors
				int myNeighborDistance = myCwd.distance + 1;
				for (Dir d : cardinals) {
					Coords neighbor = myCwd.coords.applyOffset(d);
					if (this.isInBounds(neighbor) && !this.corrupted.contains(neighbor)) {
						CoordsWithDistance neighborCwd = distanceMap.get(neighbor);
						if (neighborCwd.distance > myNeighborDistance) {
							unseen.remove(neighborCwd);
							neighborCwd.distance = myNeighborDistance;
							unseen.add(neighborCwd);
						}
					}
				}
			} else {
				// Found the exit; we're done here
				break;
			}
		}
		
		return distanceMap.get(this.exit).distance;
	}
	
	public boolean isInBounds(Coords c) {
		int col = c.getCol();
		int row = c.getRow();
		
		return col >= 0 && col < this.size &&
				row >= 0 && row < this.size;
	}
	
	@Override
	public String toString() {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		
		for (int row = 0; row < this.size; row++) {
			for (int col = 0; col < this.size; col++) {
				Coords c = new Coords(col, row);
				if (this.corrupted.contains(c)) {
					pw.print('#');
				} else {
					pw.print('.');
				}
			}
			pw.println();
		}
		return sw.toString();
	}
	
	public static MemorySpace getMemorySpaceFromPuzzle(Stream<String> puzzleInput, boolean isTest) {
		MemorySpace result = new MemorySpace(isTest ? 7 : 71);
		
		Iterator<String> it = puzzleInput.iterator();
		while (it.hasNext()) {
			String line = it.next();
			Matcher m = COORDINATE_PATTERN.matcher(line);
			if (m.find()) {
				Coords c = new Coords(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
				result.addFallingByte(c);
			} else {
				throw new RuntimeException("Malformed puzzle input: " + line);
			}
		}
		
		return result;
	}
	
	private class CoordsWithDistance
			implements Comparable<CoordsWithDistance> {
		private final Coords coords;
		private int distance = Integer.MAX_VALUE;
		
		CoordsWithDistance(Coords c) {
			this.coords = c;
			return;
		}
		
		@Override
		public int compareTo(CoordsWithDistance o) {
			return this.distance - o.distance;
		}
		
		@Override
		public boolean equals(Object o) {
			boolean equal = false;
			if (o instanceof CoordsWithDistance) {
				CoordsWithDistance other = (CoordsWithDistance) o;
				equal = this.coords.equals(other.coords) &&
						this.distance == other.distance;
			}
			return equal;
		}
		
		@Override
		public int hashCode() {
			return this.coords.hashCode() ^ this.distance;
		}
	}

}
