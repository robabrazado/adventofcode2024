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
		int advanceTime = isTest ? 12 : 1024;
		space.advanceTime(advanceTime);
		System.out.println(space);
		
		if (partOne) {
			return String.valueOf(space.minStepsToExit());
		} else {
			return space.nextBlockingByteCoords().toString();
		}
	}
	
}
