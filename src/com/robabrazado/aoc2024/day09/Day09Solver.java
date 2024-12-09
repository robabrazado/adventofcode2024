package com.robabrazado.aoc2024.day09;

import java.util.stream.Stream;

import com.robabrazado.aoc2024.Solver;

// --- Day 9: Disk Fragmenter ---
public class Day09Solver extends Solver {
	
	public Day09Solver() {
		super(9);
		return;
	}
	
	@Override public String solve(Stream<String> puzzleInput, boolean partOne, boolean isTest) {
		AmphipodDisk disk = new AmphipodDisk(puzzleInput.iterator().next());
		disk.defragment();
		return disk.checksum();
	}

	@Override
	protected String solvePart1(Stream<String> puzzleInput, boolean isTest) {
		return this.solve(puzzleInput, true, isTest);
	}

	@Override
	protected String solvePart2(Stream<String> puzzleInput, boolean isTest) {
		return this.solve(puzzleInput, false, isTest);
	}

}
