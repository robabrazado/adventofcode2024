package com.robabrazado.aoc2024.day06;

import java.util.stream.Stream;

import com.robabrazado.aoc2024.Solver;

// --- Day 6: Guard Gallivant ---
public class Day06Solver extends Solver {
	
	public Day06Solver() {
		super(6);
		return;
	}
	
	@Override
	public String solve(Stream<String> puzzleInput, boolean partOne, boolean isTest) {
		if (partOne) {
			return this.solve1(puzzleInput);
		} else {
			return this.solve2(puzzleInput);
		}
	}
	
	private String solve1(Stream<String> puzzleInput) {
		Lab lab = new Lab(puzzleInput);
		lab.patrol();
		return String.valueOf(lab.getGuardVisitedCount());
	}
	
	private String solve2(Stream<String> puzzleInput) {
		return null;
	}
}
