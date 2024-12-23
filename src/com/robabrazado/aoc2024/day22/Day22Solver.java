package com.robabrazado.aoc2024.day22;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.stream.Stream;

import com.robabrazado.aoc2024.Solver;

// --- Day 22: Monkey Market ---
public class Day22Solver extends Solver {
	
	public Day22Solver() {
		super(22);
		return;
	}
	
	@Override
	public String solve(Stream<String> puzzleInput, boolean partOne, boolean isTest) {
		BigInteger result = BigInteger.ZERO;
		
		Iterator<String> it = puzzleInput.iterator();
		while (it.hasNext()) {
			SecretNumber sn = new SecretNumber(it.next());
			for (int i = 1; i <= 2000; i++) {
				sn.next();
			}
			result = result.add(sn.getValue());
		}
		
		return result.toString();
	}
	
}
