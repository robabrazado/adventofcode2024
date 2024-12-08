package com.robabrazado.aoc2024.grid;

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
}
