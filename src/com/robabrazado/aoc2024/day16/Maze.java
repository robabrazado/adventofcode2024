package com.robabrazado.aoc2024.day16;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Stream;

import com.robabrazado.aoc2024.grid.Coords;
import com.robabrazado.aoc2024.grid.Dir;

public class Maze {
	private static final Dir[] CARDINALS = Dir.cardinals();
	
	private final Set<Coords> pathCoords = new HashSet<Coords>();
	private final int height;
	private final int width;
	private final Coords start;
	private final Coords end;
	
	public Maze(Stream<String> puzzleInput) {
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
			
			for (int col = 0; col < len; col++) {
				char c = chars[col];
				switch (c) {
				case '.':
				case 'S':
				case 'E':
					Coords coords = new Coords(col, row);
					this.pathCoords.add(coords);
					if (c == 'S') {
						if (tempStart == null) {
							tempStart = coords;
						} else {
							throw new RuntimeException("Maze map has multiple start coordinates");
						}
					} else if (c == 'E') {
						if (tempEnd == null) {
							tempEnd = coords;
						} else {
							throw new RuntimeException("Maze map has multiple end coordinates");
						}
					}
					break;
				case '#':
					// do nothing (wall)
					break;
				default:
					throw new RuntimeException("Unrecognized maze map character: " + c);
				}
			}
			row++;
		}
		
		this.width = tempWidth;
		this.height = row;
		if (tempStart == null) {
			throw new RuntimeException("Maze map has no start coordinates");
		}
		this.start = tempStart;
		if (tempEnd == null) {
			throw new RuntimeException("Maze map has no end coordinates");
		}
		this.end = tempEnd;
		return;
	}
	
	// Plain ol' Dijkstra's
	public int bestPathScore() {
		Map<PathNode, PathNodeWithDistance> nodeMap = new HashMap<PathNode, PathNodeWithDistance>();
		PriorityQueue<PathNodeWithDistance> unseen = new PriorityQueue<PathNodeWithDistance>(
				Comparator.comparing(PathNodeWithDistance::distance, Comparator.nullsLast(Comparator.naturalOrder())));
		for (Coords c : this.pathCoords) {
			for (Dir d : CARDINALS) {
				PathNode node = new PathNode(c, d);
				PathNodeWithDistance pnwd = new PathNodeWithDistance(node);
				if (c.equals(this.start) && d.equals(Dir.E)) {
					pnwd.distance = 0;
				}
				unseen.add(pnwd);
				nodeMap.put(node, pnwd);
			}
		}
		
		while (!unseen.isEmpty() &&
				!unseen.peek().node.position.equals(this.end) &&
				unseen.peek().distance != null) {
			PathNodeWithDistance me = unseen.poll();
			List<PathNodeWithDistance> neighborEdges = this.getAccessibleNeighbors(me.node);
			for (PathNodeWithDistance neighborEdge : neighborEdges) {
				PathNodeWithDistance neighbor = nodeMap.get(neighborEdge.node);
				int newDistance = me.distance + neighborEdge.distance;
				if (neighbor.distance == null || newDistance < neighbor.distance) {
					unseen.remove(neighbor);
					neighbor.distance = newDistance;
					unseen.add(neighbor);
				}
			}
		}
		
		if (!unseen.peek().node.position.equals(this.end)) {
			throw new RuntimeException("Pathfinding couldn't find the end?!");
		}
		
		return unseen.peek().distance;
	}
	
	@Override
	public String toString() {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		
		for (int row = 0; row < this.width; row++) {
			for (int col = 0; col < this.height; col++) {
				Coords coords = new Coords(col, row);
				if (this.pathCoords.contains(coords)) {
					if (this.start.equals(coords)) {
						pw.print('S');
					} else if (this.end.equals(coords)) {
						pw.print('E');
					} else {
						pw.print('.');
					}
				} else {
					pw.print('#');
				}
			}
			pw.println();
		}
		
		return sw.toString();
	}
	
	private List<PathNodeWithDistance> getAccessibleNeighbors(PathNode node) {
		List<PathNodeWithDistance> result = new ArrayList<PathNodeWithDistance>();
		
		// Check forward, right, and left; not backward
		Coords myCoords = node.position;
		Dir myFacing = node.facing;
		
		// Forward
		Coords neighbor = myCoords.applyOffset(myFacing);
		this.addIfValid(result, neighbor, myFacing, 1);
		
		// Right
		Dir newFacing = myFacing.turnClockwise(2);
		neighbor = myCoords.applyOffset(newFacing);
		this.addIfValid(result, neighbor, newFacing, 1001);
		
		// Left
		newFacing = myFacing.turnClockwise(-2);
		neighbor = myCoords.applyOffset(newFacing);
		this.addIfValid(result, neighbor, newFacing, 1001);
		
		return result;
	}
	
	// Destructive to collection
	private void addIfValid(Collection<PathNodeWithDistance> collection, Coords position, Dir direction, int distance) {
		if (this.isInBounds(position) && this.pathCoords.contains(position)) {
			collection.add(new PathNodeWithDistance(new PathNode(position, direction), distance));
		}
	}
	
	private boolean isInBounds(Coords c) {
		int col = c.getCol();
		int row = c.getRow();
		return col >= 0 && col < this.width &&
				row >= 0 && row < this.height;
	}
	
	private record PathNode(Coords position, Dir facing) {}
	
	private class PathNodeWithDistance {
		final PathNode node;
		Integer distance = null; // null is infinity
		
		PathNodeWithDistance(PathNode node) {
			this.node = node;
			return;
		}
		
		PathNodeWithDistance(PathNode node, int distance) {
			this.node = node;
			this.distance = distance;
			return;
		}
		
		Integer distance() {
			return distance;
		}
		
		@Override
		public boolean equals(Object o) {
			boolean equal = false;
			
			if (o instanceof PathNodeWithDistance) {
				PathNodeWithDistance other = (PathNodeWithDistance) o;
				equal = this.node.equals(other.node) &&
						this.distance == other.distance;
			}
			
			return equal;
		}
		
		@Override
		public int hashCode() {
			return this.node.hashCode() ^ this.distance;
		}
	}
}
