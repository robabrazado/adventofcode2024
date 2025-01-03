package com.robabrazado.aoc2024.day21;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.robabrazado.aoc2024.grid.Coords;
import com.robabrazado.aoc2024.grid.Dir;

public class Keypad {
	private static final MetadataArrangement[] ARRANGEMENTS = MetadataArrangement.values();
	
	private final KeypadType type;
	private final String name;
	private final Set<Key> keys;
	private final int width;
	private final int height;
	
	private Map<Coords, Key> positionMap = null;
	private Map<Character, Key> charMap = null;
	
	private static final Map<KeypadType, Map<CharPair, Collection<List<Dir>>>> pathMap =
			new HashMap<KeypadType, Map<CharPair, Collection<List<Dir>>>>();
	
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
	
	public boolean hasKeyAt(Coords coords) {
		return this.positionMap().containsKey(coords);
	}
	
	public char charAt(Coords coords) {
		return this.keyAt(coords).c;
	}
	
	private Key keyAt(Coords coords) {
		if (this.positionMap().containsKey(coords)) {
			return this.positionMap().get(coords);
		} else {
			throw new RuntimeException(String.format("%s is not a valid location on %s", coords, this.name));
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
	
	private Coords keyPosition(char c) {
		if (this.charMap().containsKey(c)) {
			return this.charMap().get(c).position;
		} else {
			throw new RuntimeException(String.format("%s does not have a '%c' key", this.name, c));
		}
	}
	
	public boolean isInBounds(Coords coords) {
		int col = coords.getCol();
		int row = coords.getRow();
		return col >= 0 && col < this.width &&
				row >=0 && row < this.height;
	}
	
	// The "metadata," which originally was WAY more complicated, is really just an offset, when you get down to it.
	public Coords getKeystrokeMetadata(char from, char to) {
		return this.keyPosition(from).getOffsetTo(this.keyPosition(to));
	}
	
	// Returns the shortest paths that connect the specified keys that do not pass over an empty space
	// This seems like a good thing to memoize and share between objects
	public Collection<List<Dir>> getShortestValidPaths(char from, char to) {
		CharPair pair = new CharPair(from, to);
		if (!this.pathMap().containsKey(pair)) {
			List<List<Dir>> paths = new ArrayList<List<Dir>>();
			
			// Find the two shortest paths (or one if one offset is 0), and add each valid one to the results
			Coords metadata = this.getKeystrokeMetadata(from, to);
			int col = metadata.getCol();
			int row = metadata.getRow();
			
			if (col != 0 || row != 0) {
				List<Dir> tempPath;
			
				if (row != 0) {
					tempPath = this.generatePath(metadata, MetadataArrangement.COL_FIRST);
					if (this.isValidPath(from, to, tempPath)) {
						paths.add(tempPath);
					}
				}
				
				if (col != 0) {
					tempPath = this.generatePath(metadata, MetadataArrangement.ROW_FIRST);
					if (this.isValidPath(from, to, tempPath)) {
						paths.add(tempPath);
					}
				}
				
			} else {
				// No offsets; from and to are same key
				paths.add(new ArrayList<Dir>());
			}
			this.pathMap().put(pair, Collections.unmodifiableList(paths));
		}
		return this.pathMap().get(pair);
	}
	
	private List<Dir> generatePath(Coords metadata, MetadataArrangement arrangement) {
		List<Dir> path = new ArrayList<Dir>();
		int col = metadata.getCol();
		int row = metadata.getRow();
		int[] counts = new int[2];
		Dir[] dirs = new Dir[2];
		
		int colIdx;
		switch (arrangement) {
		case COL_FIRST:
			colIdx = 0;
			break;
		case ROW_FIRST:
			colIdx = 1;
			break;
		default:
			throw new RuntimeException("Unsupported metadata arrangement: " + arrangement.name());
		}
		int rowIdx = colIdx ^ 1;
		
		counts[colIdx] = Math.abs(col);
		counts[rowIdx] = Math.abs(row);
		dirs[colIdx] = col > 0 ? Dir.E : Dir.W;
		dirs[rowIdx] = row > 0 ? Dir.S : Dir.N;
		
		for (int i = 0; i < 2; i++) {
			for (int j = 1; j <= counts[i]; j++) {
				path.add(dirs[i]);
			}
		}
		
		return path;
	}
	
	private boolean isValidPath(char from, char to, List<Dir> path) {
		boolean valid = true;
		
		Coords checking = this.charMap().get(from).position;
		Iterator<Dir> it = path.iterator();
		while (it.hasNext() && valid) {
			checking = checking.applyOffset(it.next());
			valid = this.isInBounds(checking) && this.positionMap().containsKey(checking);
		}
		return valid;
	}
	
	private Map<CharPair, Collection<List<Dir>>> pathMap() {
		if (!Keypad.pathMap.containsKey(this.type)) {
			Keypad.pathMap.put(this.type, new HashMap<CharPair, Collection<List<Dir>>>());
		}
		return Keypad.pathMap.get(this.type);
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
					pw.print(this.charAt(coords));
				} else {
					pw.print(' ');
				}
			}
			pw.println();
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
}
