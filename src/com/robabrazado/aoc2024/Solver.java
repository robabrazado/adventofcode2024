package com.robabrazado.aoc2024;

import java.util.stream.Stream;

public abstract class Solver {
	private final int day;
	
	protected Solver(int dayNumber) {
		this.day = dayNumber;
		return;
	}
	
	/**
	 * Run the solver on either part one or part two.
	 * Errors will generally be elevated as runtime exceptions, but this method returning
	 * a value of null should also be considered an error.
	 * 
	 * @param puzzleInput a Stream of String lines of puzzle input
	 * @param isPartOne {@code true} for part one; {@code false} for part two
	 * @return the result of the solution
	 */
	public abstract String solve(Stream<String> puzzleInput, boolean isPartOne);
	
	public int getDay() {
		return this.day;
	}
	
}
