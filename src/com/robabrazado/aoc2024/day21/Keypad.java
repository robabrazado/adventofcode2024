package com.robabrazado.aoc2024.day21;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.robabrazado.aoc2024.grid.Coords;

public class Keypad {
	private final KeypadType type;
	private final String name;
	private final Set<Key> keys;
	private final int width;
	private final int height;
	private final KeypadRobot worker;
	
	private Map<Coords, Key> positionMap = null;
	
	public Keypad(KeypadType type, String name) {
		this(type, name, (KeypadRobot) null);
	}
	
	public Keypad(KeypadType type, String name, KeypadRobot worker) {
		this.type = type;
		
		if (name == null || name.isEmpty()) {
			throw new RuntimeException("Keypad must have a name. Keypad is an individual.");
		}
		this.name = name;
		
		Set<Key> tempKeys = new HashSet<Key>();
		Set<Character> seenChars = new HashSet<Character>();
		Set<Coords> seenCoords = new HashSet<Coords>();
		this.height = type.keyGrid.length;
		if (this.height > 0) {
			this.width = type.keyGrid[0].length;
			if (this.width > 0) {
				for (int row = 0; row < this.height; row++) {
					for (int col = 0; col < this.width; col++) {
						char c = type.keyGrid[row][col];
						if (c != 0) {
							Coords coords = new Coords(col, row);
							if (!seenChars.add(c)) {
								throw new RuntimeException(String.format("Duplicate '%c' key found", c));
							}
							if (!seenCoords.add(coords)) {
								throw new RuntimeException(String.format("Duplicate position found at %s", coords));
							}
							tempKeys.add(new Key(c, coords));
						}
					}
				}
			} else {
				throw new RuntimeException("Grid has no width");
			}
		} else {
			throw new RuntimeException("Grid has no height");
		}
		this.keys = Collections.unmodifiableSet(tempKeys);
		
		this.worker = worker;
		
		return;
	}
	
	public KeypadType getType() {
		return this.type;
	}
	
	public String getName() {
		return this.name;
	}
	
	public KeypadRobot getWorker() {
		return this.worker;
	}
	
	public char charAt(Coords coords) {
		return this.keyAt(coords).c;
	}
	
	private Key keyAt(Coords coords) {
		if (this.positionMap().containsKey(coords)) {
			return this.positionMap().get(coords);
		} else {
			throw new RuntimeException(String.format("%s is not a valid location on this keypad", coords));
		}
	}
	
	private Map<Coords, Key> positionMap() {
		if (this.positionMap == null) {
			this.positionMap = new HashMap<Coords, Key>();
			this.keys.stream().forEach(key -> this.positionMap.put(key.position, key));
		}
		return this.positionMap;
	}
	
	public boolean isInBounds(Coords coords) {
		int col = coords.getCol();
		int row = coords.getRow();
		return col >= 0 && col < this.width &&
				row >=0 && row < this.height;
	}
	
	@Override
	public String toString() {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		
		pw.println("Keypad " + this.name);
		for (int row = 0; row < this.height; row++) {
			for (int col = 0; col < this.width; col++) {
				pw.print(this.charAt(new Coords(col, row)));
			}
			pw.println();
		}
		
		if (this.worker != null) {
			pw.println("Controlling " + this.worker.toString());
		}
		return sw.toString();
	}

	public enum KeypadType {
		NUMERIC			(new char[][] {{'7', '8', '9'}, {'4', '5', '6'}, {'1', '2', '3'}, {0, '0', 'A'}}),
		DIRECTIONAL		(new char[][] {{0, '^', 'A'}, {'<', 'v', '>'}});
		
		final char[][] keyGrid;
		
		KeypadType(char[][] keys) {
			this.keyGrid = keys;
			return;
		}
		
	}
	
	public class Key {
		public final char c;
		public final Coords position;
		
		private Key(char c, Coords position) {
			if (c == 0) {
				throw new RuntimeException("It is forbidden for Robot to gaze upon the null key");
			}
			this.c = c;
			this.position = position;
			return;
		}
		
		@Override
		public String toString() {
			return String.format("'%c' key at %s", this.c, this.position.toString());
		}
	}
	
	private record CharPair(char from, char to) {}
}
