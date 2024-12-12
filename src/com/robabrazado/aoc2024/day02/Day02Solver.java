package com.robabrazado.aoc2024.day02;

import java.util.Iterator;
import java.util.stream.Stream;

import com.robabrazado.aoc2024.Solver;

// --- Day 2: Red-Nosed Reports ---
public class Day02Solver extends Solver {
	
	public Day02Solver() {
		super(2);
		return;
	}

	@Override
	public String solve(Stream<String> puzzleInput, boolean isPartOne) {
		if (isPartOne) {
			return this.solvePart1(puzzleInput);
		} else {
			return this.solvePart2(puzzleInput);
		}
	}

	private String solvePart1(Stream<String> puzzleInput) {
		int safeCounter = 0;
		
		Iterator<String> it = puzzleInput.iterator();
		while (it.hasNext()) {
			String line = it.next();
			Report r = new Report(line);
			if (r.isSafe()) {
				safeCounter++;
			}
		}
		
		return String.valueOf(safeCounter);
	}

	private String solvePart2(Stream<String> puzzleInput) {
		int safeCounter = 0;
		
		Iterator<String> it = puzzleInput.iterator();
		while (it.hasNext()) {
			String line = it.next();
			ReportWithProblemDampener r = new ReportWithProblemDampener(line);
			if (r.isSafe()) {
				safeCounter++;
			}
		}
		
		return String.valueOf(safeCounter);
	}

}
