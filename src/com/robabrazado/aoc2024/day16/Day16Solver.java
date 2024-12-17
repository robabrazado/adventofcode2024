package com.robabrazado.aoc2024.day16;

import java.util.stream.Stream;

import com.robabrazado.aoc2024.Solver;

// --- Day 16: Reindeer Maze ---
public class Day16Solver extends Solver {
	
	public Day16Solver() {
		super(16);
		return;
	}
	
	@Override
	public String solve(Stream<String> puzzleInput, boolean partOne, boolean isTest) {
		Maze maze = new Maze(puzzleInput);
		System.out.println(maze);
		Integer score = maze.lowestScoreToEnd();
		if (score == null) {
			throw new RuntimeException("Couldn't find any path to the end; something has gone terribly awry");
		}
		return score.toString();
	}
	
}
