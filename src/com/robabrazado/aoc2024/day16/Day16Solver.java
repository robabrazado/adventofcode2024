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
		
		if (partOne) {
			return String.valueOf(maze.bestPathScore());
		} else {
//			System.out.println(maze.toStringWithBestSeats());
			return String.valueOf(maze.bestSeatCount());
		}
	}
	
}
