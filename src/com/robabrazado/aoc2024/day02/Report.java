package com.robabrazado.aoc2024.day02;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Report {
	private final boolean isSafe;
	
	public Report (String inputLine) {
		boolean safeSoFar = true;
		
		Pattern p = Pattern.compile("(\\d+)");
		Matcher m = p.matcher(inputLine);
		Integer lastLevel = null;
		int lastDir = 0; // -1 asc; 1 desc
		
		while (m.find() && safeSoFar) {
			Integer thisLevel = Integer.valueOf(m.group());
			if (lastLevel != null) {
				int diff = lastLevel.intValue() - thisLevel.intValue();
				int absDiff = Math.abs(diff);
				if (absDiff >= 1 && absDiff <= 3) {
					// Change in safe range; check direction
					int thisDir = diff / absDiff;
					if (lastDir == 0) {
						lastDir = thisDir;
					} else {
						safeSoFar = lastDir == thisDir;
					}
				} else {
					// Level change outside safe range
					safeSoFar = false;
				}
			}
			lastLevel = thisLevel;
		}
		
		this.isSafe = safeSoFar;
		
		return;
	}
	
	public boolean isSafe() {
		return this.isSafe;
	}
}
