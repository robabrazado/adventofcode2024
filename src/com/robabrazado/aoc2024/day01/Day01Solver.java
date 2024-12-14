package com.robabrazado.aoc2024.day01;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.robabrazado.aoc2024.Solver;

// --- Day 1: Historian Hysteria ---
public class Day01Solver extends Solver {
	
	public Day01Solver() {
		super(1);
		return;
	}

	@Override
	public String solve(Stream<String> puzzleInput, boolean partOne, boolean isTest) {
		String result = null;
		List<Integer> leftList = new ArrayList<Integer>();
		List<Integer> rightList = new ArrayList<Integer>();
		Map<Integer, Integer> leftCounts = new HashMap<Integer, Integer>();
		Map<Integer, Integer> rightCounts = new HashMap<Integer, Integer>();
		Pattern p = Pattern.compile("(\\d+)\\s+(\\d+)");
		
		Iterator<String> it = puzzleInput.iterator();
		while (it.hasNext()) {
			String line = it.next();
			Matcher m = p.matcher(line);
			if (m.find()) {
				Integer leftInt = Integer.valueOf(m.group(1));
				Integer rightInt = Integer.valueOf(m.group(2));
				
				leftList.add(leftInt);
				Day01Solver.count(leftCounts, leftInt);
				
				rightList.add(rightInt);
				Day01Solver.count(rightCounts, rightInt);
			} else {
				throw new RuntimeException("Regex failed; this shouldn't happen");
			}
		}
		
		if (partOne) {
			Collections.sort(leftList);
			Collections.sort(rightList);
			
			int distanceTally = 0;
			int len = leftList.size();
			for (int i = 0; i < len; i++) {
				distanceTally += Math.abs(leftList.get(i).intValue() - rightList.get(i).intValue());
			}
			
			result = String.valueOf(distanceTally);
		} else {
			long similarityScore = 0;
			for (Integer i : leftList) {
				if (rightCounts.containsKey(i)) {
					similarityScore += i.intValue() * rightCounts.get(i).intValue();
				}
			}
			
			result = String.valueOf(similarityScore);
		}
		
		return result;
	}

	private static void count(Map<Integer, Integer> countMap, Integer num) {
		int oldCount = 0;
		if (countMap.containsKey(num)) {
			oldCount = countMap.get(num).intValue();
		}
		countMap.put(num, Integer.valueOf(++oldCount));
		return;
	}

}
