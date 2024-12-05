package com.robabrazado.aoc2024.day05;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.robabrazado.aoc2024.Solver;

// --- Day 5: Print Queue ---
public class Day05Solver extends Solver {
	
	public Day05Solver() {
		super(5);
		return;
	}

	@Override
	protected void solve(PrintWriter out, PrintWriter err, boolean isPartOne, boolean testData) throws IOException {
		if (isPartOne) {
			this.solve1(out, err, testData);
		} else {
			this.solve2(out, err, testData);
		}
	}
	
	protected void solve1(PrintWriter out, PrintWriter err, boolean testData) throws IOException {
		BufferedReader in = null;
		
		// I guess I just...use regex for everything now
		Pattern pRule = Pattern.compile("(\\d+)\\|(\\d+)");
		Pattern pOrder = Pattern.compile("(\\d+)");
		
		RuleSet rs = new RuleSet();
		int runningTotal = 0;
		
		try {
			in = super.getPuzzleInputReader(testData);
			
			String line = in.readLine();
			while (!line.isEmpty()) {
				Matcher mRule = pRule.matcher(line);
				if (mRule.find()) {
					rs.addRule(Integer.parseInt(mRule.group(1)), Integer.parseInt(mRule.group(2)));
				} else {
					throw new RuntimeException("Didn't understand " + line);
				}
				
				line = in.readLine();
			}
			
			line = in.readLine();
			while (line != null) {
				List<Integer> order = new ArrayList<Integer>();
				Matcher mOrder = pOrder.matcher(line);
				while (mOrder.find()) {
					order.add(Integer.valueOf(mOrder.group(1)));
				}
				
				if (rs.orderPasses(order)) {
					runningTotal += order.get(order.size() / 2);
				}
				
				line = in.readLine();
			}
			
		} finally {
			if (in != null) {
				in.close();
			}
		}
		
		out.println(runningTotal);
		return;
	}
	
	protected void solve2(PrintWriter out, PrintWriter err, boolean testData) throws IOException {
		BufferedReader in = null;
		
		Pattern pRule = Pattern.compile("(\\d+)\\|(\\d+)");
		Pattern pOrder = Pattern.compile("(\\d+)");
		
		RuleSet rs = new RuleSet();
		int runningTotal = 0;
		
		try {
			in = super.getPuzzleInputReader(testData);
			
			String line = in.readLine();
			while (!line.isEmpty()) {
				Matcher mRule = pRule.matcher(line);
				if (mRule.find()) {
					rs.addRule(Integer.parseInt(mRule.group(1)), Integer.parseInt(mRule.group(2)));
				} else {
					throw new RuntimeException("Didn't understand " + line);
				}
				
				line = in.readLine();
			}
			
			line = in.readLine();
			while (line != null) {
				List<Integer> order = new ArrayList<Integer>();
				Matcher mOrder = pOrder.matcher(line);
				while (mOrder.find()) {
					order.add(Integer.valueOf(mOrder.group(1)));
				}
				
				if (!rs.orderPasses(order)) {
					List<Integer> newOrder = rs.reorderOrder(order);

					runningTotal += newOrder.get(newOrder.size() / 2);
				}
				
				line = in.readLine();
			}
			
		} finally {
			if (in != null) {
				in.close();
			}
		}
		
		out.println(runningTotal);
		return;
	}
	
}
