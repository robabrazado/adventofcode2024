package com.robabrazado.aoc2024.day21;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.robabrazado.aoc2024.grid.Coords;
import com.robabrazado.aoc2024.grid.Dir;

/*
 * Generates path strings from the specified path metadata. This object is
 * meant to represent one keystroke of input and can also be used to generate
 * command strings.
 * 
 * The indices are not guaranteed portable between instances of PathGenerator!
 * For this reason, it is recommended that the iterator be used to generate
 * paths. Indices can, however, be reliably used as a reference for the life
 * of a particular PathGenerator instance.
 */
public class PathGenerator {
	private final Keypad keypad;
	private final PathMetadata metadata;
	private final List<Integer> bitmaps = new ArrayList<Integer>();
	
	public PathGenerator(Keypad.KeypadType type, PathMetadata metadata) {
		this(new Keypad("Path generator keypad " + type.name(), type), metadata);
	}
	
	public PathGenerator(Keypad keypad, PathMetadata metadata) {
		if (keypad == null) {
			throw new IllegalArgumentException("Path generator requires a non-null associated Keypad");
		}
		this.keypad = keypad;
		
		if (metadata == null) {
			throw new IllegalArgumentException("Path generator requires non-null metadata");
		}
		this.metadata = metadata;
		
		// Populate bitmap list (0 is column, 1 is row)
		Map<Coords, Keypad.Key> positionMap = this.keypad.getPositionMap();
		Set<Coords> keyCoords = positionMap.keySet();
		int colCount = metadata.getColCount();
		int rowCount = metadata.getRowCount();
		int numBits = metadata.getTaxicabDistance();
		int numPossibleBitmaps = 1 << numBits; // 2 ^ numBits
		for (int potentialBitmap = 0; potentialBitmap < numPossibleBitmaps; potentialBitmap++) {
			// First check if this is even a viable bitmap for this metadata
			int numCols = 0;
			int numRows = 0;
			boolean viable = true;
			for (int i = 0; i < numBits && viable; i++) {
				if (((potentialBitmap >> i) & 1) == 0) {
					numCols++;
				} else {
					numRows++;
				}
				viable = numCols <= colCount && numRows <= rowCount;
			}
			if (viable) {
				// Then check if this path stays over keys
				List<Dir> dirs = this.getDirs(potentialBitmap);
				Coords checking = keypad.getCharMap().get(metadata.getFrom()).position; // Checked first step
				Iterator<Dir> dirIt = dirs.iterator();
				while (dirIt.hasNext() && viable) { // Check rest of steps (includes last step)
					checking = checking.applyOffset(dirIt.next());
					viable = keyCoords.contains(checking);
				}
				viable = viable && positionMap.get(checking).c == metadata.getTo();
			}
			if (viable) {
				this.bitmaps.add(potentialBitmap);
			}
		}
		
		return;
	}
	
	// This only generates the direction list! This does not guarantee a valid path.
	private List<Dir> getDirs(int bitmap) {
		// Remember path order is higher to lower bits
		List<Dir> result = new ArrayList<Dir>();
		Dir colDir = this.metadata.getColDir();
		Dir rowDir = this.metadata.getRowDir();
		int numSteps = this.metadata.getTaxicabDistance();
		int highMask = 1 << (numSteps - 1); // Highest bit
		
		
		for (int i = 0; i < numSteps; i++) {
			Dir d;
			if (((bitmap << i) & highMask) == 0) {
				d = colDir;
			} else {
				d = rowDir;
			}
			result.add(d);
		}
		return result;
	}
	
	public String generatePath(int index) {
		return this.generatePath(index, false);
	}
	
	public String generateCommand(int index) {
		return this.generatePath(index, true);
	}
	
	private String generatePath(int index, boolean asCommand) {
		if (index < 0 || index >= this.bitmaps.size()) {
			throw new IllegalArgumentException(String.valueOf(index) + " is not a valid index for this generator");
		}
		StringBuilder strb = new StringBuilder();
		List<Dir> dirs = this.getDirs(this.bitmaps.get(index));
		for (Dir d : dirs) {
			strb.append(Command.getCommandByDir(d).c);
		}
		if (asCommand) {
			strb.append(Command.ACT.c);
		}
		return strb.toString();
	}

	public int getIndexCount() {
		return this.bitmaps.size();
	}
	
	public Iterator<String> pathIterator() {
		return new PathIterator(this, false);
	}
	
	public Iterator<String> commandIterator() {
		return new PathIterator(this, true);
	}
	
	@Override
	public String toString() {
		return String.format("Path generator for %s %s; %d bitmap(s)",
				this.keypad.getName(), this.metadata.toString(), this.bitmaps.size());
	}
	
	private class PathIterator implements Iterator<String> {
		private final PathGenerator generator;
		private final int finalIndex;
		private final boolean isCommandGenerator;
		
		private int currentIdx = 0;
		
		private PathIterator(PathGenerator generator, boolean generateCommands) {
			if (generator == null) {
				throw new IllegalArgumentException("Null generator");
			}
			this.generator = generator;
			this.finalIndex = generator.getIndexCount();
			this.isCommandGenerator = generateCommands;
			return;
		}

		@Override
		public boolean hasNext() {
			return this.currentIdx < this.finalIndex;
		}

		@Override
		public String next() {
			StringBuilder strb = new StringBuilder(this.generator.generatePath(this.currentIdx, this.isCommandGenerator));
			this.currentIdx++;
			return strb.toString();
		}
		
	}
}
