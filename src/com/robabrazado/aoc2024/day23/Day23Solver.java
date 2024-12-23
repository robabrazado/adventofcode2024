package com.robabrazado.aoc2024.day23;

import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;

import com.robabrazado.aoc2024.Solver;

// --- Day 23: LAN Party ---
public class Day23Solver extends Solver {
	
	public Day23Solver() {
		super(23);
		return;
	}
	
	@Override
	public String solve(Stream<String> puzzleInput, boolean partOne, boolean isTest) {
		int result = 0;
		LanGraph lanGraph = new LanGraph(puzzleInput);
		System.out.println(lanGraph);
		Set<Set<String>> trios = lanGraph.getTrios();
		for (Set<String> trio : trios) {
			boolean countThis = false;
			Iterator<String> it = trio.iterator();
			while (it.hasNext() && !countThis) {
				countThis = it.next().startsWith("t");
			}
			if (countThis) {
				result++;
			}
		}
		return String.valueOf(result);
	}
	
}
