package com.robabrazado.aoc2024.day11;

import java.util.stream.Stream;

import com.robabrazado.aoc2024.Solver;

// --- Day 11: Plutonian Pebbles ---
public class Day11Solver extends Solver {
	
	public Day11Solver() {
		super(11);
		return;
	}
	
	@Override
	public String solve(Stream<String> puzzleInput, boolean partOne, boolean isTest) {
		StoneBlinker blinker = new StoneBlinker(puzzleInput);
		System.out.println(blinker);
		System.out.println();
		
		int blinkCount = partOne ? 25 : 75;
		return String.valueOf(blinker.stoneCountAfterBlinks(blinkCount));
	}
	
}
