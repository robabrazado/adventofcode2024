package com.robabrazado.aoc2024.day12;

import java.util.List;
import java.util.stream.Stream;

import com.robabrazado.aoc2024.Solver;

// --- Day 12: Garden Groups ---
public class Day12Solver extends Solver {
	
	public Day12Solver() {
		super(12);
		return;
	}
	
	@Override
	public String solve(Stream<String> puzzleInput, boolean partOne, boolean isTest) {
		Garden garden = new Garden(puzzleInput);
		System.out.println(garden);
		List<Garden.Region> regions = garden.getRegions();
		int total = 0;
		for (Garden.Region region : regions) {
			System.out.println(region);
			total += (region.getArea() * region.getPerimeter());
		}
		return String.valueOf(total);
	}
	
}
