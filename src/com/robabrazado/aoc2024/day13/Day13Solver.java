package com.robabrazado.aoc2024.day13;

import java.util.List;
import java.util.stream.Stream;

import com.robabrazado.aoc2024.Solver;

// --- Day 13: Claw Contraption ---
public class Day13Solver extends Solver {
	
	public Day13Solver() {
		super(13);
		return;
	}
	
	@Override
	public String solve(Stream<String> puzzleInput, boolean partOne, boolean isTest) {
		int result = 0;
		List<ClawMachine> clawMachines = ClawMachine.parseClawMachines(puzzleInput);
		for (ClawMachine machine : clawMachines) {
			System.out.println(machine);
			int prizeCost = machine.getLowestPrizeCost();
			System.out.println("Lowest cost prize costs " + String.valueOf(prizeCost));
			if (prizeCost > 0) {
				result += prizeCost;
			}
		}
		return String.valueOf(result);
	}
	
}
