package com.robabrazado.aoc2024.day15;

import java.util.stream.Stream;

import com.robabrazado.aoc2024.Solver;

// --- Day 15: Warehouse Woes ---
public class Day15Solver extends Solver {
	
	public Day15Solver() {
		super(15);
		return;
	}
	
	@Override
	public String solve(Stream<String> puzzleInput, boolean partOne, boolean isTest) {
		Warehouse warehouse = new Warehouse(puzzleInput);
		System.out.println(warehouse);
		warehouse.executeMoves();
		System.out.println(warehouse);
		return String.valueOf(warehouse.getGpsSum());
	}
	
}
