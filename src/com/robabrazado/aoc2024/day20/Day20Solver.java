package com.robabrazado.aoc2024.day20;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import com.robabrazado.aoc2024.Solver;

// --- Day 20: Race Condition ---
public class Day20Solver extends Solver {
	
	public Day20Solver() {
		super(20);
		return;
	}
	
	@Override
	public String solve(Stream<String> puzzleInput, boolean partOne, boolean isTest) {
		Racetrack racetrack = new Racetrack(puzzleInput);
		System.out.println(racetrack);
//		System.out.println(racetrack.toString(Cell.DistanceType.START));
//		System.out.println(racetrack.toString(Cell.DistanceType.END));
		
		int cheatLength = partOne ? 2 : 20;
		List<Racetrack.CheatWithTimeSaved> cheats = racetrack.getWorthwhileCheats(cheatLength);
		// For info output
		Map<Integer, Integer> timeCounts = new HashMap<Integer, Integer>();
		for (Racetrack.CheatWithTimeSaved cheat : cheats) {
			int timeSaved = cheat.timeSaved();
			if (!timeCounts.containsKey(timeSaved)) {
				timeCounts.put(timeSaved, 0);
			}
			timeCounts.put(timeSaved, timeCounts.get(cheat.timeSaved()) + 1);
		}
		Set<Integer> times = new TreeSet<Integer>(timeCounts.keySet());
		for (int timeSaved : times) {
			System.out.println(String.valueOf(timeCounts.get(timeSaved)) + " cheats that save " + String.valueOf(timeSaved) + " picoseconds");
		}
		System.out.println();
		// End info output
		
		int result = 0;
		for (Racetrack.CheatWithTimeSaved cheat : cheats) {
			if (cheat.timeSaved() >= 100) {
				result++;
			}
		}
		
		
		return String.valueOf(result);
	}
	
}
