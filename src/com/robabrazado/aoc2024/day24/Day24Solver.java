package com.robabrazado.aoc2024.day24;

import java.math.BigInteger;
import java.util.stream.Stream;

import com.robabrazado.aoc2024.Solver;

// --- Day 24: Crossed Wires ---
public class Day24Solver extends Solver {
	
	public Day24Solver() {
		super(24);
		return;
	}
	
	@Override
	public String solve(Stream<String> puzzleInput, boolean partOne, boolean isTest) {
		Board board = new Board(puzzleInput);
		System.out.println(board);
		BigInteger result = board.getZOutputValue();
		System.out.println(result.toString(2));
		return result.toString();
	}
	
}
