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
	protected String solvePart1(Stream<String> puzzleInput, boolean isTest) {
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

	@Override
	protected String solvePart2(Stream<String> puzzleInput, boolean isTest) {
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
