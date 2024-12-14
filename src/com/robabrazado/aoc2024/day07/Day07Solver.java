package com.robabrazado.aoc2024.day07;

import java.util.stream.Stream;

import com.robabrazado.aoc2024.Solver;

// --- Day 7: Bridge Repair ---
public class Day07Solver extends Solver {
	public Day07Solver() {
		super(7);
		return;
	}
	
	@Override
	public String solve(Stream<String> puzzleInput, boolean isPartOne, boolean isTest) {
		Equationator equationator = new Equationator(puzzleInput);
		return equationator.solve(isPartOne);
	}
}
