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
		if (partOne) {
			return this.solve1(puzzleInput);
		} else {
			return this.solve2(puzzleInput);
		}
	}
	
	private String solve1(Stream<String> puzzleInput) {
		Warehouse warehouse = new Warehouse(puzzleInput);
		System.out.println(warehouse);
		warehouse.executeMoves();
		System.out.println(warehouse);
		return String.valueOf(warehouse.getGpsSum());
	}
	
	private String solve2(Stream<String> puzzleInput) {
		DoubleWideWarehouse warehouse = new DoubleWideWarehouse(puzzleInput);
		System.out.println(warehouse);
		warehouse.executeMoves();
		System.out.println(warehouse);
		return String.valueOf(warehouse.getGpsSum());
	}
}
