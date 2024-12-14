package com.robabrazado.aoc2024.day14;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.robabrazado.aoc2024.grid.Coords;

public class Robot {
	private static final Pattern p = Pattern.compile("p=(\\d+),(\\d+) v=(-?\\d+),(-?\\d+)");
	
	private Coords position = null;
	private final Coords velocity;
	
	public Robot(String puzzleInputLine) {
		Matcher m = p.matcher(puzzleInputLine);
		if (!m.find()) {
			throw new IllegalArgumentException("Robot did not understand " + puzzleInputLine);
		}
		this.position = new Coords(Integer.valueOf(m.group(1)), Integer.valueOf(m.group(2)));
		this.velocity = new Coords(Integer.valueOf(m.group(3)), Integer.valueOf(m.group(4)));
		return;
	}
	
	public Coords getPosition() {
		return this.position;
	}
	
	public Coords advance(int steps, int width, int height) {
		Coords newPosition = null;
		for (int i = 1; i <= steps; i++) {
			newPosition = this.position.applyOffset(this.velocity);
			
			if (newPosition.getCol() < 0) {
				newPosition = newPosition.applyOffset(new Coords(width, 0));
			} else if (newPosition.getCol() >= width) {
				newPosition = newPosition.applyOffset(new Coords(-width, 0));
			}
			
			if (newPosition.getRow() < 0) {
				newPosition = newPosition.applyOffset(new Coords(0, height));
			} else if (newPosition.getRow() >= height) {
				newPosition = newPosition.applyOffset(new Coords(0, -height));
			}
			
			this.position = newPosition;
		}
		return this.position;
	}
}
