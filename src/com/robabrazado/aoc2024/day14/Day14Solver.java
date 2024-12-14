package com.robabrazado.aoc2024.day14;

import java.util.stream.Stream;

import com.robabrazado.aoc2024.Solver;

// --- Day 14: Restroom Redoubt ---
public class Day14Solver extends Solver {
	
	public Day14Solver() {
		super(9);
		return;
	}
	
	@Override
	public String solve(Stream<String> puzzleInput, boolean partOne, boolean isTest) {
		TileArea area = new TileArea(puzzleInput, isTest);
		area.advance(100);
		return String.valueOf(area.countByQuadrant());
	}
	
}
