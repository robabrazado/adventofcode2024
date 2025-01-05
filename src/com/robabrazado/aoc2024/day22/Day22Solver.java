package com.robabrazado.aoc2024.day22;

import java.util.stream.Stream;

import com.robabrazado.aoc2024.Solver;

// --- Day 22: Monkey Market ---
public class Day22Solver extends Solver {
	
	public Day22Solver() {
		super(22);
		return;
	}
	
	@Override
	public String solve(Stream<String> puzzleInput, boolean partOne, boolean isTest) {
		int numSteps = 2000;
		Marketplace marketplace = new Marketplace(puzzleInput);
		System.out.println(marketplace);
		
		if (partOne) {
			return String.valueOf(marketplace.sumAfter(numSteps));
		} else {
			return String.valueOf(marketplace.maxBananaCountAfter(numSteps));
		}
	}
	
}
