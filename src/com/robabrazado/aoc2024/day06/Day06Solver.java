package com.robabrazado.aoc2024.day06;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import com.robabrazado.aoc2024.Solver;
import com.robabrazado.aoc2024.grid.Coords;

// --- Day 6: Guard Gallivant ---
public class Day06Solver extends Solver {
	
	public Day06Solver() {
		super(6);
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
		Lab lab = new Lab(puzzleInput);
		lab.patrolUntilGone();
		return String.valueOf(lab.getGuardVisitedCount());
	}
	
	private String solve2(Stream<String> puzzleInput) {
		Lab originalLab = new Lab(puzzleInput);
		Set<Coords> obstacleCandidates = originalLab.getNewObstacleCandidates();
		Set<Coords> loopingObstacles = new HashSet<Coords>();
		
		// I'm aware this is inelegant brute force, but I'll tough it out.
		for (Coords newObstacle : obstacleCandidates) {
			Lab testLab = originalLab.copy();
			testLab.addObstacle(newObstacle);
			System.out.print("Trying new obstacle at " + newObstacle + "...");
			if (!testLab.patrolUntilGone()) {
				loopingObstacles.add(newObstacle);
				System.out.println("looped!");
			} else {
				System.out.println("escaped");
			}
		}
		
		return String.valueOf(loopingObstacles.size());
	}
}
