package com.robabrazado.aoc2024.day03;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.robabrazado.aoc2024.Solver;

// --- Day 3: Mull It Over ---
public class Day03Solver extends Solver {
	
	public Day03Solver() {
		super(3);
		return;
	}

	@Override
	protected String solvePart1(Stream<String> puzzleInput, boolean isTest) {
		Pattern p = Pattern.compile("mul\\((\\d{1,3}),(\\d{1,3})\\)");
		int runningTotal = 0;
		
		Iterator<String> it = puzzleInput.iterator();
		while (it.hasNext()) {
			String line = it.next();
			Matcher m = p.matcher(line);
			
			while (m.find()) {
				runningTotal += (Integer.parseInt(m.group(1)) * Integer.parseInt(m.group(2)));
			}
		}
		
		return String.valueOf(runningTotal);
	}
	
	@Override
	protected String solvePart2(Stream<String> puzzleInput, boolean isTest) {
		/*
		 * This is my fourth attempt writing this. Originally I had left the code for the previous attempts in 
		 * as commented code, but they make even less sense after the Day 7 refactoring I did, so now I just
		 * deleted them. If you're really curious, the history is in commits and the saga is summarized in the
		 * README, but suffice it to say...it took me quite a while to get to this point.
		 */
		Pattern p = Pattern.compile("(?:mul\\((?<mul1>\\d{1,3}),(?<mul2>\\d{1,3})\\))|(?:do\\(\\))|(?:don't\\(\\))");
		int runningTotal = 0;
		boolean enabled = true;
		Iterator<String> it = puzzleInput.iterator();
		
		while (it.hasNext()) {
			String line = it.next();
			Matcher m = p.matcher(line);
			while (m.find()) {
				if ("do()".equals(m.group())) {
					enabled = true;
				} else if ("don't()".equals(m.group())) {
					enabled = false;
				} else if (enabled) {
					runningTotal += (Integer.parseInt(m.group("mul1")) * Integer.parseInt(m.group("mul2")));
				} // else do nothing
			}
		}
		
		return String.valueOf(runningTotal);
	}

}
