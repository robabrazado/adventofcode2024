package com.robabrazado.aoc2024.day16;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collectors;
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
	
	public int bestPathScore() {
		Map<PathNodeKey, PathNode> pathMap = this.pathfind();
		return this.bestEndNodes(pathMap).get(0).shortestDistanceToStart;
	}

	public int bestSeatCount() {
		return this.bestSeats().size();
	}
	
	private Set<Coords> bestSeats() {
		Set<Coords> result = new HashSet<Coords>();
		Map<PathNodeKey, PathNode> pathMap = this.pathfind();
		List<PathNode> nodesToCheck = this.bestEndNodes(pathMap);

		// I don't actually need to walk the paths, like, correctly; I just need to know what positions they cover
		while (!nodesToCheck.isEmpty()) {
			PathNode node = nodesToCheck.remove(0);
			nodesToCheck.addAll(node.previousHops.stream().map(key -> pathMap.get(key)).collect(Collectors.toList()));
			result.add(node.key.position);
		}
		return result;
	}
	
	@Override
	public String toString() {
		return this.toString((Set<Coords>) null);
	}
	
	public String toStringWithBestSeats() {
		return this.toString(this.bestSeats());
	}
	
	private String toString(Set<Coords> highlights) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		
		if (highlights == null) {
			highlights = new HashSet<Coords>();
		}
		for (int row = 0; row < this.width; row++) {
			for (int col = 0; col < this.height; col++) {
				Coords coords = new Coords(col, row);
				if (this.pathCoords.contains(coords)) {
					if (this.start.equals(coords)) {
						pw.print('S');
					} else if (this.end.equals(coords)) {
						pw.print('E');
					} else if (highlights.contains(coords)) {
						pw.print('O');
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
	
	// Good ol' Dijkstra's
	private Map<PathNodeKey, PathNode> pathfind() {
		Map<PathNodeKey, PathNode> result = new HashMap<PathNodeKey, PathNode>();
		for (Coords c : this.pathCoords) {
			for (Dir d : CARDINALS) {
				PathNodeKey key = new PathNodeKey(c, d);
				boolean isStartNode = c.equals(this.start) && d.equals(Dir.E);
				
				if (isStartNode || this.isNodeAccessible(key)) {
					PathNode node = new PathNode(key);
					if (isStartNode) {
						node.shortestDistanceToStart = 0;
					}
					result.put(key, node);
				}
			}
		}
		
		PriorityQueue<PathNode> unseen = new PriorityQueue<PathNode>(PathNode.getMinDistanceComparator());
		unseen.addAll(result.values());
		
		while (!unseen.isEmpty() &&
				unseen.peek().shortestDistanceToStart != null) {
			PathNode myNode = unseen.poll();
			List<PathEdge> neighborEdges = this.getAdjacentEdges(myNode.key);
			for (PathEdge neighborEdge : neighborEdges) {
				PathNode neighbor = result.get(neighborEdge.key);
				int newDistance = myNode.shortestDistanceToStart + neighborEdge.distance;
				if (neighbor.shortestDistanceToStart == null || newDistance <= neighbor.shortestDistanceToStart) {
					unseen.remove(neighbor);
					if (neighbor.shortestDistanceToStart == null || newDistance < neighbor.shortestDistanceToStart) {
						neighbor.previousHops.clear();
					} // else same distance
					neighbor.previousHops.add(myNode.key);
					neighbor.shortestDistanceToStart = newDistance;
					unseen.add(neighbor);
				}
			}
		}
		
		return result;
	}
	
	private List<PathNode> bestEndNodes(Map<PathNodeKey, PathNode> pathMap) {
		List<PathNode> endNodes = pathMap.values().stream().filter(node -> node.key.position.equals(this.end)).collect(Collectors.toList());
		if (endNodes.isEmpty()) {
			throw new RuntimeException("No path to end found");
		}
		Collections.sort(endNodes, PathNode.getMinDistanceComparator());
		int bestScore = endNodes.get(0).shortestDistanceToStart;
		endNodes.removeIf(node -> node.shortestDistanceToStart == null || node.shortestDistanceToStart > bestScore);
		return endNodes;
	}
	
	private boolean isNodeAccessible(PathNodeKey key) {
		Coords myCoords = key.position;
		Coords accessorCoords = myCoords.applyOffset(key.facing.oppositeDirection());
		return this.isInBounds(accessorCoords) && this.pathCoords.contains(accessorCoords);
	}
	
	private List<PathEdge> getAdjacentEdges(PathNodeKey node) {
		List<PathEdge> result = new ArrayList<PathEdge>();
		
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
	private void addIfValid(Collection<PathEdge> collection, Coords position, Dir direction, int distance) {
		if (this.isInBounds(position) && this.pathCoords.contains(position)) {
			collection.add(new PathEdge(new PathNodeKey(position, direction), distance));
		}
	}
	
	private boolean isInBounds(Coords c) {
		int col = c.getCol();
		int row = c.getRow();
		return col >= 0 && col < this.width &&
				row >= 0 && row < this.height;
	}
	
	private record PathNodeKey(Coords position, Dir facing) {}
	
	private record PathEdge(PathNodeKey key, int distance) {}
	
	private class PathNode {
		final PathNodeKey key;
		Integer shortestDistanceToStart = null; // null is infinity
		List<PathNodeKey> previousHops = new ArrayList<PathNodeKey>();
		
		PathNode(PathNodeKey key) {
			this.key = key;
			return;
		}
		
		Integer shortestDistanceToStart() {
			return this.shortestDistanceToStart;
		}
		
		static Comparator<PathNode> getMinDistanceComparator() {
			return Comparator.comparing(PathNode::shortestDistanceToStart, Comparator.nullsLast(Comparator.naturalOrder()));
		}
	}
}
