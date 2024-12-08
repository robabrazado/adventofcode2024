package com.robabrazado.aoc2024.grid;

/**
 * A simple int couplet styled as a row/column pair. Useful as both absolute coordinates and offsets. Immutable.
 */
public class Coords {
	private final int col;
	private final int row;
	
	public Coords (int col, int row) {
		this.col = col;
		this.row = row;
		return;
	}
	
	public int getCol() {
		return this.col;
	}
	
	public int getRow() {
		return this.row;
	}
	
	public Coords applyOffset(Coords offset) {
		return new Coords(this.col + offset.col, this.row + offset.row);
	}
	
	// One step in the specified direction
	public Coords applyOffset(Dir d) {
		return this.applyOffset(d, 1);
	}
	
	public Coords applyOffset(Dir d, int steps) {
		Coords dirOffset = d.getOffset();
		
		return new Coords(dirOffset.getCol() * steps, dirOffset.getRow() * steps);
	};
	
	public Coords getOffsetTo(Coords other) {
		return new Coords(other.col - this.col, other.row - this.row);
	}
	
	public Coords getOffsetFrom(Coords other) {
		return this.getOffsetTo(other).invert();
	}
	
	public Coords invert() {
		return new Coords(-this.col, -this.row);
	}
	
	@Override
	public String toString() {
		return "[" + String.valueOf(this.col) + "," + String.valueOf(this.row) + "]";  
	}
	
	@Override
	public boolean equals(Object o) {
		boolean equal = false;
		
		if (o != null && o instanceof Coords) {
			Coords other = (Coords) o;
			equal = (this.col == other.col) && (this.row == other.row);
		}
		
		return equal;
	}
	
	@Override
	public int hashCode() {
		return this.col ^ this.row;
	}
}