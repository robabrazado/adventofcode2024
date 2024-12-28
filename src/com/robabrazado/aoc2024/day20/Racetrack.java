package com.robabrazado.aoc2024.day20;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Stream;

import com.robabrazado.aoc2024.grid.Coords;
import com.robabrazado.aoc2024.grid.Dir;

public class Racetrack {
	private static final Dir[] CARDINALS = Dir.cardinals();
	private static final Dir[] DIAGONALS = Dir.diagonals();
	private static final char[] DISTANCE_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
		
	private final Map<Coords, Cell> trackMap = new HashMap<Coords, Cell>();
	private final int width;
	private final int height;
	private final Cell start;
	private final Cell end;
	
	public Racetrack(Stream<String> puzzleInput) {
		Integer tempWidth = null;
		Iterator<String> it = puzzleInput.iterator();
		Cell tempStart = null;
		Cell tempEnd = null;
		
		int row = 0;
		while (it.hasNext()) {
			char[] chars = it.next().toCharArray();
			if (tempWidth == null) {
				tempWidth = chars.length;
			} else if (tempWidth != chars.length) {
				throw new RuntimeException("Racetrack map input is not rectangular");
			}
			
			for (int col = 0; col < tempWidth; col++) {
				char c = chars[col];
				switch (c) {
				case '.':
				case 'S':
				case 'E':
					Coords coords = new Coords(col, row);
					Cell cell = new Cell(coords);
					this.trackMap.put(coords, cell);
					if (c == 'S') {
						if (tempStart == null) {
							cell.setStartDistance(0);
							tempStart = cell;
						} else {
							throw new RuntimeException("Racetrack map has more than one start");
						}
					} else if (c == 'E') {
						if (tempEnd == null) {
							cell.setEndDistance(0);
							tempEnd = cell;
						} else {
							throw new RuntimeException("Racetrack map has more than one end");
						}
					}
					break;
				case '#':
					// Do nothing
					break;
				default:
					throw new RuntimeException("Unrecognized map symbol: " + c);
				}
			}
			row++;
		}
		
		if (tempStart == null) {
			throw new RuntimeException("Racetrack map has no start");
		} else if (tempEnd == null) {
			throw new RuntimeException("Racetrack map has no end");
		}
		
		this.width = tempWidth;
		this.height = row;
		this.start = tempStart;
		this.end = tempEnd;
		
		this.pathfind(); // Might as well do this now
		
		return;
	}
	
	// Only lists cheats that save time
	public List<Cheat> getWorthwhileCheats() {
		List<Cheat> result = new ArrayList<Cheat>();
		List<Cell> trackCellsRemaining = new ArrayList<Cell>(this.trackMap.values());
		
		while (!trackCellsRemaining.isEmpty()) {
			Cell me = trackCellsRemaining.remove(0);
			int myEndDistance = me.getEndDistance();
			List<Cell> cheatNeighbors = this.getCheatNeighbors(me);
//System.out.println(me.getCoords() + " has " + String.valueOf(cheatNeighbors.size()) + " cheat neighbors");
			for (Cell cheatNeighbor : cheatNeighbors) {
				// If cheating to this neighbor would save time, log it
				int neighborEndDistance = cheatNeighbor.getEndDistance();
				if (neighborEndDistance < myEndDistance - 2) {
					result.add(new Cheat(me.getCoords(), cheatNeighbor.getCoords(), myEndDistance - 2 - neighborEndDistance));
				}
			}
		}
		
		return result;
	}
	
	
	
	@Override
	public String toString() {
		return this.toString((Cell.DistanceType) null);
	}
	
	public String toString(Cell.DistanceType type) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		
		for (int row = 0; row < this.height; row++) {
			for (int col = 0; col < this.width; col++) {
				Coords coords = new Coords(col, row);
				char c;
				if (this.trackMap.containsKey(coords)) {
					Cell cell = this.trackMap.get(coords);
					if (cell == this.start) {
						c = 'S';
					} else if (cell == this.end) {
						c = 'E';
					} else {
						if (type != null) {
							Integer distance = cell.getDistance(type);
							if (distance == null) {
								c = '*';
							} else if (distance < DISTANCE_CHARS.length) {
								c = DISTANCE_CHARS[distance];
							} else {
								c = '+';
							}
						} else {
							c = '.';
						}
					}
				} else {
					c = '#';
				}
				pw.print(c);
			}
			pw.println();
		}
		
		return sw.toString();
	}
	
	// Load each track cell with its distance to start and distance to end; the ol' Double Dijkstra!
	private void pathfind() {
		for (Cell.DistanceType type : Cell.DistanceType.values()) { // One for start, once for end
			PriorityQueue<Cell> unseen = new PriorityQueue<Cell>(Cell.CellComparatorFactory.getComparator(type));
			unseen.addAll(this.trackMap.values()); // No constructor that takes both initial collection and comparator?
			
			while (!unseen.isEmpty()) { // No early exit for finding goal; searching all (reachable) nodes
				Cell me = unseen.poll();
				Integer newDistance = me.getDistance(type);
				if (newDistance != null) {
					newDistance++; // Distance from self to neighbor is always 1
					List<Cell> neighbors = this.getNeighbors(me);
					for (Cell neighbor : neighbors) {
						if (unseen.contains(neighbor)) {
							Integer oldDistance = neighbor.getDistance(type);
							if (oldDistance == null || oldDistance < newDistance) {
								unseen.remove(neighbor);
								neighbor.setDistance(newDistance, type);
								unseen.add(neighbor);
							}
						}
					}
				} else {
					// No more reachable nodes
					break;
				}
			}
		}
		return;
	}
	
	// Returns track cells within 1 step of specified cell
	private List<Cell> getNeighbors(Cell c) {
		List<Cell> results = new ArrayList<Cell>();
		Coords me = c.getCoords();
		for (Dir d : CARDINALS) {
			Coords checking = me.applyOffset(d);
			this.addIfTrackCell(results, checking);
		}
		return results;
	}
	
	// Returns track cells (exactly) 2 steps away from specified cell, even through a wall
	// Does NOT include regular neighbors (reachable in 1 step without cheating)
	private List<Cell> getCheatNeighbors(Cell c) {
		List<Cell> results = new ArrayList<Cell>();
		Coords me = c.getCoords();
		for (Dir d : CARDINALS) {
			Coords checking = me.applyOffset(d, 2);
			this.addIfTrackCell(results, checking);
		}
		for (Dir d : DIAGONALS) {
			Coords checking = me.applyOffset(d);
			this.addIfTrackCell(results, checking);
		}
		return results;
	}
	
	private boolean isInBounds(Coords c) {
		int col = c.getCol();
		int row = c.getRow();
		
		return col >= 0 && col < this.width &&
				row >= 0 && row < this.height;
	}
	
	// Destructive to list!
	private void addIfTrackCell(List<Cell> list, Coords candidate) {
		if (this.isInBounds(candidate) && this.trackMap.containsKey(candidate)) {
			list.add(this.trackMap.get(candidate));
		}
		return;
	}
	
	public record Cheat(Coords start, Coords end, int timeSaved) {}
}
