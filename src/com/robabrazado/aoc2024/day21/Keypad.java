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
	private static final Map<KeypadType, Map<CharPair, PathGenerator>> PATH_GENERATOR_MAPS =
			new HashMap<KeypadType, Map<CharPair, PathGenerator>>();
	
	private final KeypadType type;
	private final String name;
	private final Set<Key> keys;
	private final int width;
	private final int height;
	
	private Map<Coords, Key> positionMap = null;
	private Map<Character, Key> charMap = null;
	
	public Keypad(String name, KeypadType type) {
		if (name == null || name.isEmpty()) {
			throw new RuntimeException("Keypad must have a name");
		}
		this.name = name;
		this.type = type;
		
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
		
		return;
	}
	
	public KeypadType getType() {
		return this.type;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Map<Coords, Key> getPositionMap() {
		return Collections.unmodifiableMap(this.positionMap());
	}
	
	public Map<Character, Key> getCharMap() {
		return Collections.unmodifiableMap(this.charMap());
	}
	
	public boolean isInBounds(Coords coords) {
		int col = coords.getCol();
		int row = coords.getRow();
		return col >= 0 && col < this.width &&
				row >=0 && row < this.height;
	}
	
	public PathMetadata getKeystrokeMetadata(char from, char to) {
		return new PathMetadata(from, to, this.keyPosition(from).getOffsetTo(this.keyPosition(to)));
	}
	
	public PathGenerator getKeystrokePathGenerator(char from, char to) {
		CharPair pair = new CharPair(from, to);
		if (!this.pathGeneratorMap().containsKey(pair)) {
			this.pathGeneratorMap().put(pair, new PathGenerator(this.type, this.getKeystrokeMetadata(from, to)));
		}
		return this.pathGeneratorMap().get(pair);
	}
	
	@Override
	public String toString() {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		
		pw.println(this.name);
		for (int row = 0; row < this.height; row++) {
			for (int col = 0; col < this.width; col++) {
				Coords coords = new Coords(col, row);
				if (this.positionMap().containsKey(coords)) {
					pw.print(this.positionMap().get(coords).c);
				} else {
					pw.print(' ');
				}
			}
			pw.println();
		}
		return sw.toString();
	}

	// Returns valid or throws
	private Coords keyPosition(char c) {
		if (this.charMap().containsKey(c)) {
			return this.charMap().get(c).position;
		} else {
			throw new RuntimeException(String.format("%s does not have a '%c' key", this.name, c));
		}
	}
	
	private Map<Coords, Key> positionMap() {
		if (this.positionMap == null) {
			this.positionMap = new HashMap<Coords, Key>();
			this.keys.stream().forEach(key -> this.positionMap.put(key.position, key));
		}
		return this.positionMap;
	}
	
	private Map<Character, Key> charMap() {
		if (this.charMap == null) {
			this.charMap = new HashMap<Character, Key>();
			this.keys.stream().forEach(key -> this.charMap.put(key.c, key));
		}
		return this.charMap;
	}
	
	private Map<CharPair, PathGenerator> pathGeneratorMap() {
		if (!PATH_GENERATOR_MAPS.containsKey(this.type)) {
			PATH_GENERATOR_MAPS.put(this.type, new HashMap<CharPair, PathGenerator>());
		}
		return PATH_GENERATOR_MAPS.get(this.type);
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
}
