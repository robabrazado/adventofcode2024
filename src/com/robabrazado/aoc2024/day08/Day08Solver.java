package com.robabrazado.aoc2024.day08;

import java.util.stream.Stream;

import com.robabrazado.aoc2024.Solver;

public class Day08Solver extends Solver {
	
	public Day08Solver() {
		super(8);
		return;
	}

	@Override
	protected String solvePart1(Stream<String> puzzleInput, boolean isTest) {
		FrequencyCity city = new FrequencyCity(puzzleInput);
		return String.valueOf(city.countAntinodes());
	}

	@Override
	protected String solvePart2(Stream<String> puzzleInput, boolean isTest) {
		// TODO Auto-generated method stub
		return null;
	}

}
