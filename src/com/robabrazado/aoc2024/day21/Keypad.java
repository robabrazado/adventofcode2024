package com.robabrazado.aoc2024.day21;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.robabrazado.aoc2024.grid.Coords;
import com.robabrazado.aoc2024.grid.Dir;

public class Keypad {
	private final KeypadType type;
	private final String name;
	private final Set<Key> keys;
	private final Coords emptySpacePosition;
	private final int width;
	private final int height;
	private final Keypad controller;
	private Keypad worker;
	
	private final Map<CharPair, BigInteger> lowestCostCache = new HashMap<CharPair, BigInteger>();
	
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
		Coords emptySpace = null;
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
						} else {
							if (emptySpace == null) {
								emptySpace = new Coords(col, row);
							} else {
								throw new RuntimeException("Duplicate emtpy space found");
							}
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
		this.emptySpacePosition = emptySpace;
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
	
	public BigInteger getLowestTailCost(String keypresses) {
		BigInteger result = BigInteger.ZERO;
		
		if (keypresses != null && !keypresses.isEmpty()) {
			if (this.controller != null) {
				char from = 'A'; // Assume always starting from 'A' position
				char[] chars = keypresses.toCharArray();
				for (char to : chars) {
					result = result.add(this.getLowestTailCostForKeypress(from, to));
					from = to;
				}
			} else {
				// I AM TAIL
				result = BigInteger.valueOf(keypresses.length());
			}
		}
			
		return result;
	}
	
	private BigInteger getLowestTailCostForKeypress(char from, char to) {
		CharPair pair = new CharPair(from, to);
		if (!this.lowestCostCache.containsKey(pair)) {
			BigInteger result = BigInteger.ONE;
			if (controller != null) {
				Coords fromPosition = this.charMap().get(from).position;
				Coords toPosition = this.charMap().get(to).position;
				Coords offset = fromPosition.getOffsetTo(toPosition);
				PathMetadata metadata = new PathMetadata(fromPosition, offset);
				result = this.getLowestTailCost(metadata);
			}
			
			this.lowestCostCache.put(pair, result);
		}
		return this.lowestCostCache.get(pair);
	}
	
	private BigInteger getLowestTailCost(PathMetadata metadata) {
		/*
		 * The metadata represents some collection of keypresses consisting
		 * of (a) zero or more column offset commands, (b) zero or more row
		 * offset commands, and (c) an ACT command. Parts (a) and (b) can be
		 * combined in any order, but part (c) must come at the end. We assume
		 * that the lowest cost will only be produced by either all part (a)
		 * followed by all part (b) or all part (b) followed by all part (a).
		 * This is because these are the two permutations with the fewest
		 * changes in cursor position between keypresses.
		 */
		BigInteger result;
		
		if (metadata.getCol() != 0 || metadata.getRow() != 0) {
			if (this.controller != null) {
				String colLeg = Keypad.buildCommandLeg(metadata.getColCount(), metadata.getColDir(), true);
				String rowLeg = Keypad.buildCommandLeg(metadata.getRowCount(), metadata.getRowDir(), true);
				
				BigInteger colFirstCost = null;
				if (metadata.getColCount() > 0 && this.isValidPath(metadata, true)) {
					colFirstCost = this.controller.getLowestTailCost(colLeg + rowLeg + Command.ACT.c);
				}
				
				BigInteger rowFirstCost = null;
				if (metadata.getRowCount() > 0 && this.isValidPath(metadata, false)) {
					rowFirstCost = this.controller.getLowestTailCost(rowLeg + colLeg + Command.ACT.c);
				}
				
				if (colFirstCost == null) {
					result = rowFirstCost;
				} else if (rowFirstCost == null) {
					result = colFirstCost;
				} else {
					result = colFirstCost.min(rowFirstCost);
				}
				if (result == null) {
					throw new RuntimeException(this.name + " found no valid paths: " + metadata);
				}
			} else {
				// This is the tail controller, so all that matters is the length of the command string.
				result = BigInteger.valueOf(metadata.getTaxicabDistance()).add(BigInteger.ONE);
			}
		} else {
			// Repeated keypress
			result = BigInteger.ONE;
		}
		
		return result;
	}
	
	private boolean isValidPath(PathMetadata metadata, boolean columnFirst) {
		if (!this.isInBounds(metadata.getStartPosition())) {
			throw new RuntimeException("Start position " + metadata.getStartPosition().toString() + " is out of bounds on " + this.name);
		}
		if (!this.isInBounds(metadata.getEndPosition())) {
			throw new RuntimeException("End position " + metadata.getEndPosition().toString() + " is out of bounds on " + this.name);
		}
		boolean valid = true;
		
		if (this.emptySpacePosition != null) {
			int emptyCol = this.emptySpacePosition.getCol();
			int emptyRow = this.emptySpacePosition.getRow();
			int startCol = metadata.getStartPosition().getCol();
			int colOffset = metadata.getCol();
			int startRow = metadata.getStartPosition().getRow();
			int rowOffset = metadata.getRow();
			
			int colRangeLo = colOffset < 0 ? startCol + colOffset : startCol;
			int colRangeHi = colOffset < 0 ? startCol : startCol + colOffset;
			int rowRangeLo = rowOffset < 0 ? startRow + rowOffset : startRow;
			int rowRangeHi = rowOffset < 0 ? startRow : startRow + rowOffset;
			
			if (columnFirst) {
				valid = !((emptyRow == startRow && colRangeLo <= emptyCol && colRangeHi >= emptyCol) ||
						(emptyCol == (startCol + colOffset) && rowRangeLo <= emptyRow && rowRangeHi >= emptyRow));
			} else {
				valid = !((emptyCol == startCol && rowRangeLo <= emptyRow && rowRangeHi >= emptyRow) ||
						(emptyRow == (startRow + rowOffset) && colRangeLo <= emptyCol && colRangeHi >= emptyCol));
			}
		}
		
		return valid;
	}
	
	private static String buildCommandLeg(int count, Dir d, boolean column) {
		StringBuilder strb = new StringBuilder();
		for (int i = 1; i <= count; i++) {
			strb.append(Command.getCommandByDir(d).c);
		}
		return strb.toString();
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
