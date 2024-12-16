package com.robabrazado.aoc2024.day10;

import java.util.stream.Stream;

import com.robabrazado.aoc2024.Solver;

// --- Day 10: Hoof It ---
public class Day10Solver extends Solver {
	
	public Day10Solver() {
		super(10);
		return;
	}
	
	@Override
	public String solve(Stream<String> puzzleInput, boolean partOne, boolean isTest) {
		TopographicMap map = new TopographicMap(puzzleInput);
		if (partOne) {
			return String.valueOf(map.sumTrailheadScores());
		} else {
			return String.valueOf(map.sumTrailheadRatings());
		}
	}
	
}
