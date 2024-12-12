package com.robabrazado.aoc2024.day08;

import java.util.stream.Stream;

import com.robabrazado.aoc2024.Solver;

// --- Day 8: Resonant Collinearity ---
public class Day08Solver extends Solver {
	
	public Day08Solver() {
		super(8);
		return;
	}
	
	@Override public String solve(Stream<String> puzzleInput, boolean partOne) {
		FrequencyCity city = new FrequencyCity(puzzleInput);
		return String.valueOf(city.countAntinodes(partOne));
	}

}
