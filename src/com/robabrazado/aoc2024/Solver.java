package com.robabrazado.aoc2024;

import java.util.stream.Stream;

public abstract class Solver {
	private final int day;
	
	public Solver(int dayNumber) {
		this.day = dayNumber;
		return;
	}
	
	/**
	 * Run the solver on either part one or part two and use either test data or not.
	 * Errors will generally be elevated as runtime exceptions, but this method returning
	 * a value of null should also be considered an error.
	 * 
	 * @param puzzleInput a Stream of String lines of puzzle input
	 * @param isPartOne {@code true} for part one; {@code false} for part two
	 * @param testData {@code true} to use test input; {@code false} to use real input
	 * @return the result of the solution
	 */
	public String solve(Stream<String> puzzleInput, boolean isPartOne, boolean isTest) {
		String result = null;
		if (isPartOne) {
			result = this.solvePart1(puzzleInput, isTest);
		} else {
			result = this.solvePart2(puzzleInput, isTest);
		}
		return result;
	}
	
	protected abstract String solvePart1(Stream<String> puzzleInput, boolean isTest);
	
	protected abstract String solvePart2(Stream<String> puzzleInput, boolean isTest);
	
	public int getDay() {
		return this.day;
	}
	
}
