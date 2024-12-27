package com.robabrazado.aoc2024.day18;

import java.util.stream.Stream;

import com.robabrazado.aoc2024.Solver;

// --- Day 18: RAM Run ---
public class Day18Solver extends Solver {
	
	public Day18Solver() {
		super(18);
		return;
	}
	
	@Override
	public String solve(Stream<String> puzzleInput, boolean partOne, boolean isTest) {
		MemorySpace space = MemorySpace.getMemorySpaceFromPuzzle(puzzleInput, isTest);
		space.advanceTime(isTest ? 12 : 1024);
		System.out.println(space);
		return String.valueOf(space.minStepsToExit());
	}
	
}
