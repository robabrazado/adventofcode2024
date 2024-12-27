package com.robabrazado.aoc2024.day19;

import java.util.stream.Stream;

import com.robabrazado.aoc2024.Solver;

// --- Day 19: Linen Layout ---
public class Day19Solver extends Solver {
	
	public Day19Solver() {
		super(19);
		return;
	}
	
	@Override
	public String solve(Stream<String> puzzleInput, boolean partOne, boolean isTest) {
		TowelArranger arranger = TowelArranger.arrangerFromPuzzle(puzzleInput);
		System.out.println(arranger);
		
		return String.valueOf(arranger.possibleDesignCount());
	}
	
}
