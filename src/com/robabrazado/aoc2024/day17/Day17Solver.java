package com.robabrazado.aoc2024.day17;

import java.util.stream.Stream;

import com.robabrazado.aoc2024.Solver;

// --- Day 17: Chronospatial Computer ---
public class Day17Solver extends Solver {
	
	public Day17Solver() {
		super(17);
		return;
	}
	
	@Override
	public String solve(Stream<String> puzzleInput, boolean partOne, boolean isTest) {
		if (partOne) {
			return Computer.part1(puzzleInput);
		} else {
			return Computer.part2(puzzleInput);
		}
	}
	
}
