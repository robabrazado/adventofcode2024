package com.robabrazado.aoc2024.day25;

import java.util.stream.Stream;

import com.robabrazado.aoc2024.Solver;

// --- Day 25: Code Chronicle ---
public class Day25Solver extends Solver {
	
	public Day25Solver() {
		super(25);
		return;
	}
	
	@Override
	public String solve(Stream<String> puzzleInput, boolean partOne, boolean isTest) {
		LocksAndKeys lak = new LocksAndKeys(puzzleInput);
		System.out.println(lak);
		
		return String.valueOf(lak.possibleFitCount());
	}
	
}
