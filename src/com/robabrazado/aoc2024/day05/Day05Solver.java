package com.robabrazado.aoc2024.day05;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.robabrazado.aoc2024.Solver;

// --- Day 5: Print Queue ---
public class Day05Solver extends Solver {
	
	public Day05Solver() {
		super(5);
		return;
	}
	
	@Override
	public String solve(Stream<String> puzzleInput, boolean partOne, boolean isTest) {
		// I guess I just...use regex for everything now
		Pattern pRule = Pattern.compile("(\\d+)\\|(\\d+)");
		Pattern pOrder = Pattern.compile("(\\d+)");
		
		RuleSet rs = new RuleSet();
		int runningTotal = 0;
		
		Iterator<String> it = puzzleInput.iterator();
		
		// Reading the first "chunk" of input up until the first empty line
		String line = null;
		while (it.hasNext() && line == null) {
			line = it.next();
			if (!line.isEmpty()) {
				Matcher mRule = pRule.matcher(line);
				if (mRule.find()) {
					rs.addRule(Integer.parseInt(mRule.group(1)), Integer.parseInt(mRule.group(2)));
				} else {
					throw new RuntimeException("Didn't understand " + line);
				}
				line = null; // Keep looping
			} // else do nothing (loop will exit)
		}
		
		while (it.hasNext()) {
			line = it.next();
			List<Integer> order = new ArrayList<Integer>();
			Matcher mOrder = pOrder.matcher(line);
			while (mOrder.find()) {
				order.add(Integer.valueOf(mOrder.group(1)));
			}
			
			if (partOne) {
				if (rs.orderPasses(order)) {
					runningTotal += order.get(order.size() / 2);
				}
			} else {
				if (!rs.orderPasses(order)) {
					List<Integer> newOrder = rs.correctOrder(order);
					runningTotal += newOrder.get(newOrder.size() / 2);
				}
			}
		}
		
		return String.valueOf(runningTotal);
	}

	@Override
	protected String solvePart1(Stream<String> puzzleInput, boolean isTest) {
		return this.solve(puzzleInput, true, isTest);
	}

	@Override
	protected String solvePart2(Stream<String> puzzleInput, boolean isTest) {
		return this.solve(puzzleInput, false, isTest);
	}
	
}
