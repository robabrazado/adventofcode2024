package com.robabrazado.aoc2024.day06;

import com.robabrazado.aoc2024.grid.Coords;
import com.robabrazado.aoc2024.grid.Dir;

public class Guard {
	private Coords position;
	private Dir facing;
	
	public Guard(Coords position, Dir facing) {
		this.position = position;
		this.facing = facing;
		return;
	}
	
	public Coords getPosition() {
		return this.position;
	}
	
	public Dir getFacing() {
		return this.facing;
	}
	
	public void turnRight() {
		this.facing = facing.turnClockwise(2);
	}
	
	public void goForward() {
		this.goForward(1);
	}
	
	public void goForward(int steps) {
		this.position = this.position.applyOffset(this.facing, steps);
	}
	
	public Coords getLookAhead() {
		return this.position.applyOffset(this.facing);
	}
}
