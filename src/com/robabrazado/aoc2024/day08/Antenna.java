package com.robabrazado.aoc2024.day08;

import com.robabrazado.aoc2024.grid.Coords;

public class Antenna {
	private final char freq;
	private final Coords coords;
	
	public Antenna(char frequency, Coords coordinates) {
		this.freq = frequency;
		this.coords = coordinates;
		return;
	}
	
	public char getFrequency() {
		return this.freq;
	}
	
	public Coords getCoordinates() {
		return this.coords;
	}
}
