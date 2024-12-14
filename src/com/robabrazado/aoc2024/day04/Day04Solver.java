package com.robabrazado.aoc2024.day04;

import java.util.stream.Stream;

import com.robabrazado.aoc2024.Solver;

// --- Day 4: Ceres Search ---
public class Day04Solver extends Solver {
	
	public Day04Solver() {
		super(4);
		return;
	}
	
	@Override
	public String solve(Stream<String> puzzleInput, boolean isPartOne, boolean isTest) {
		WordSearch wordSearch = new WordSearch(puzzleInput);
		if (isPartOne) {
			return String.valueOf(wordSearch.countXmas());
		} else {
			return String.valueOf(wordSearch.countMasX());
		}
	}
	
}
