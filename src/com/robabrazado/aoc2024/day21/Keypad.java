package com.robabrazado.aoc2024.day21;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.robabrazado.aoc2024.grid.Coords;
import com.robabrazado.aoc2024.grid.Dir;

public class Keypad {
	private static final Map<KeypadType, Map<CharPair, String>> COMMAND_MAPS =
			new HashMap<KeypadType, Map<CharPair, String>>();
	
	private final KeypadType type;
	private final String name;
	private final Set<Key> keys;
	private final int width;
	private final int height;
	private final Keypad controller;
	private Keypad worker;
	
	private Map<Coords, Key> positionMap = null;
	private Map<Character, Key> charMap = null;
	
	public Keypad(String name, KeypadType type) {
		this(name, type, (Keypad) null);
		
	}
	
	public Keypad(String name, KeypadType type, Keypad controller) {
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
		if (controller != null) {
			controller.worker = this;
		}
		this.controller = controller;
		return;
	}
	
	public KeypadType getType() {
		return this.type;
	}
	
	public String getName() {
		return this.name;
	}
	
	public boolean hasController() {
		return this.controller != null;
	}
	
	public Keypad getController() {
		return this.controller;
	}
	
	public boolean hasWorker() {
		return this.worker != null;
	}
	
	public Keypad getWorker() {
		return this.worker;
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
	
	public int getTailCostForKeypresses(String code) {
		int result = 0;
		
		if (code != null) {
			char from = 'A';
			char[] chars = code.toCharArray();
			for (char to : chars) {
				result += this.getTailCostForKeystroke(from, to);
				from = to;
			}
		}
		
		return result;
	}
	
	public int getTailCostForKeystroke(char from, char to) {
		int result;
		if (this.controller != null) {
			String myCommand = this.getCommandForKeystroke(from, to);
			result = this.controller.getTailCostForKeypresses(myCommand);
		} else {
			result = 1;
		}
		return result;
	}
	
	public String getCommandForKeystroke(char from, char to) {
		CharPair pair = new CharPair(from, to);
		if (!this.commandMap().containsKey(pair)) {
			StringBuilder strb = new StringBuilder();
			List<Dir> path = this.getShortestPathForKeystroke(from, to);
			for (Dir d : path) {
				strb.append(Command.getCommandByDir(d).c);
			}
			this.commandMap().put(pair, strb.append(Command.ACT.c).toString());
		}
		return this.commandMap().get(pair);
	}
	
	private List<Dir> getShortestPathForKeystroke(char from, char to) {
		/*
		 * Start with the premise that the shortest paths between keys all
		 * consist of some number of column offsets and some number of row
		 * offsets. We're only considering two candidates: the two with the
		 * fewest number of key changes (on the theory that repeated
		 * keypresses are less costly than keypresses that need navigation
		 * between them. Sometimes there will only be one candidate (when
		 * there is only one type of offset involved, column or row). When
		 * there are two candidates, one might be eliminated for being
		 * "invalid," that is, it passes over an emtpy space on the keypad.
		 * If both are valid, both are assumed equally costly.
		 * 
		 * The two potential candidate paths can be described by a number
		 * of column offsets (in one direction), a number of row offsets
		 * (in another direction), and an "arrangement" of column-first or
		 * row-first.
		 */
		List<Dir> result = null;
		Coords fromPosition = this.charMap().get(from).position;
		Coords toPosition = this.charMap().get(to).position;
		PathMetadata metadata = new PathMetadata(from, to, fromPosition.getOffsetTo(toPosition));
		int colOffset = metadata.getColCount();
		int rowOffset = metadata.getRowCount();
		
		if (colOffset == 0 && rowOffset == 0) {
			// This must be the same key
			result = new ArrayList<Dir>();
		} else if (colOffset != 0) {
			result = this.getValidPath(metadata, true);
		}
		
		if (result == null) {
			result = this.getValidPath(metadata, false);
		}
		
		if (result == null) {
			throw new RuntimeException(String.format("%s unable to find valid path with %s", this.name, metadata.toString()));
		}
		return result;
	}
	
	// Returns null if path would be invalid
	private List<Dir> getValidPath(PathMetadata metadata, boolean colFirst) {
		List<Dir> result = null;
		List<Dir> temp = new ArrayList<Dir>();
		int colIdx = colFirst ? 0 : 1;
		int rowIdx = colIdx ^ 1;
		CountDir[] countDirs = new CountDir[2];
		countDirs[colIdx] = new CountDir(metadata.getColCount(), metadata.getColDir());
		countDirs[rowIdx] = new CountDir(metadata.getRowCount(), metadata.getRowDir());
		
		Coords checking = this.charMap.get(metadata.getFrom()).position;
		boolean valid = true;
		for (int idx = 0; idx < 2 && valid; idx++) {
			for (int i = 1; i <= countDirs[idx].count && valid; i++) {
				Dir d = countDirs[idx].dir;
				checking = checking.applyOffset(d);
				temp.add(d);
				valid = this.positionMap().containsKey(checking);
			}
		}
		
		if (valid) {
			result = temp;
		}
		return result;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
	public String status() {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		
		pw.println("Keypad: " + this.name);
		
		pw.print("Controlled by: ");
		if (this.controller != null) {
			pw.print(this.controller.name);
		} else {
			pw.print("N/A");
		}
		pw.println();
		
		pw.print("Controlling: ");
		if (this.worker != null) {
			pw.print(this.worker.name);
		} else {
			pw.print("N/A");
		}
		pw.println();
		
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
	
	private Map<CharPair, String> commandMap() {
		if (!COMMAND_MAPS.containsKey(this.type)) {
			COMMAND_MAPS.put(this.type, new HashMap<CharPair, String>());
		}
		return COMMAND_MAPS.get(this.type);
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
	
	private record CountDir(int count, Dir dir) {}
}
