package com.robabrazado.aoc2024.day13;

import java.math.BigInteger;
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
		BigInteger result = BigInteger.ZERO;
		List<ClawMachine> clawMachines = ClawMachine.parseClawMachines(puzzleInput, partOne);
		for (ClawMachine machine : clawMachines) {
			System.out.println(machine);
			BigInteger prizeCost = machine.getLowestPrizeCost();
			System.out.println("Lowest cost prize costs " + prizeCost);
			if (prizeCost.compareTo(BigInteger.ZERO) > 0) {
				result = result.add(prizeCost);
			}
			System.out.println("New result: " + result);
		}
		return result.toString();
	}
	
}
