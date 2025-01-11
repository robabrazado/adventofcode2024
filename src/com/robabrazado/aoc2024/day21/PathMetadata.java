package com.robabrazado.aoc2024.day21;

import com.robabrazado.aoc2024.grid.Coords;
import com.robabrazado.aoc2024.grid.Dir;

/*
 * The first metadata class was way complicated. Then the class was removed
 * altogether because when you got down to it, it was just the same
 * information as Coords. This is the new metadata object which IS a Coords
 * object (representing the offset in a path), but with some handy wrapped
 * functionality plus start position. You can still use it for Coords
 * (offset) purposes.
 * 
 * Of particular note is that path metadata has no keypad awareness! This
 * object does not imply anything about the validity of any position or path.
 */
public class PathMetadata extends Coords {
	private final Coords startPosition;
	
	public PathMetadata(Coords startPosition, int colOffset, int rowOffset) {
		super(colOffset, rowOffset);
		if (startPosition == null) {
			throw new RuntimeException("Path metadata must have a start position");
		}
		this.startPosition = startPosition;
		return;
	}
	
	public PathMetadata(Coords startPosition, Coords offset) {
		this(startPosition, offset.getCol(), offset.getRow());
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
	
	public Coords getStartPosition() {
		return this.startPosition;
	}
	
	public Coords getEndPosition() {
		return this.startPosition.applyOffset(this);
	}
	
	public int getTaxicabDistance() {
		return this.getColCount() + this.getRowCount();
	}
	
	@Override
	public String toString() {
		return String.format("Start: %s; Offset: %s", this.startPosition.toString(), super.toString());
	}
}
