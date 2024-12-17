package com.robabrazado.aoc2024.grid;

import java.util.Arrays;

/**
 * Orientation directions for navigating a 2D space. Made to be used with Grid, so assumes a 2D matrix with 0,0 is the upper left
 * space with col increasing to the east (right) and row increasing downward (south). Iterating order starts NW and proceeds clockwise.
 */
public enum Dir {
	NE	(1, -1),
	E	(1, 0),
	SE	(1, 1),
	S	(0, 1),
	SW	(-1, 1),
	W	(-1, 0),
	NW	(-1, -1),
	N	(0, -1);
	
	private static final Dir[] CARDINALS = new Dir[] {Dir.E, Dir.S, Dir.W, Dir.N};
	private static final Dir[] DIAGONALS = new Dir[] {Dir.NE, Dir.SE, Dir.SW, Dir.NW};
	
	private final int colOffset;
	private final int rowOffset;
	private final Coords offset;
	
	Dir(int colOffset, int rowOffset) {
		this.colOffset = colOffset;
		this.rowOffset = rowOffset;
		this.offset = new Coords(colOffset, rowOffset);
	}
	
	public int getColOffset() {
		return this.colOffset;
	}
	
	public int rowOffset() {
		return this.rowOffset;
	}
	
	public Coords getOffset() {
		return this.offset;
	}
	
	public Dir turnClockwise() {
		return this.turnClockwise(1);
	}
	
	public Dir turnClockwise(int steps) {
		int newOrdinal = (this.ordinal() + steps) % 8;
		if (newOrdinal < 0) {
			newOrdinal += 8;
		}
		return Dir.values()[newOrdinal];
	}
	
	public Dir oppositeDirection() {
		return this.turnClockwise(4);
	}
	
	public static Dir[] cardinals() {
		return Arrays.copyOf(Dir.CARDINALS, 4);
	}
	
	public static Dir[] diagonals() {
		return Arrays.copyOf(Dir.DIAGONALS, 4);
	}
}
