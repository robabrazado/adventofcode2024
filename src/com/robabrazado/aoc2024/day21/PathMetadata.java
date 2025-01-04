package com.robabrazado.aoc2024.day21;

import com.robabrazado.aoc2024.grid.Coords;
import com.robabrazado.aoc2024.grid.Dir;

/*
 * The first metadata class was way complicated. Then the class was removed
 * altogether because when you got down to it, it was just the same
 * information as Coords. This is the new metadata object which IS a Coords
 * object (representing the offset in a path), but with some handy wrapped
 * functionality plus from/to information. You can still use it for Coords
 * (offset) purposes.
 * 
 * Of particular note is that path metadata has no keypad awareness! This
 * object does not imply anything about the validity of the path or even
 * the existence of the specified keys.
 */
public class PathMetadata extends Coords {
	private final char from;
	private final char to;
	
	public PathMetadata(char from, char to, int colOffset, int rowOffset) {
		super(colOffset, rowOffset);
		this.from = from;
		this.to = to;
		return;
	}
	
	public PathMetadata(char from, char to, Coords offset) {
		this(from, to, offset.getCol(), offset.getRow());
		return;
	}
	
	public int getColCount() {
		return Math.abs(super.getCol());
	}
	
	public int getRowCount() {
		return Math.abs(super.getRow());
	}
	
	public Dir getColDir() {
		return super.getCol() > 0 ? Dir.E : Dir.W;
	}
	
	public Dir getRowDir() {
		return super.getRow() > 0 ? Dir.S : Dir.N;
	}
	
	public char getFrom() {
		return this.from;
	}
	
	public char getTo() {
		return this.to;
	}
	
	public int getTaxicabDistance() {
		return this.getColCount() + this.getRowCount();
	}
	
	@Override
	public String toString() {
		return String.format("'%c' to '%c' (%s)", this.from, this.to, super.toString());
	}
}
