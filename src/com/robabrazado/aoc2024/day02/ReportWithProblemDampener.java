package com.robabrazado.aoc2024.day02;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReportWithProblemDampener {
	private final boolean safe;
	
	public ReportWithProblemDampener(String line) {
		boolean safeSoFar = new Report(line).isSafe();
		
		if (!safeSoFar) {
			List<String> fullLine = new ArrayList<String>();
			Pattern p = Pattern.compile("(\\d+)");
			Matcher m = p.matcher(line);
			
			while (m.find()) {
				fullLine.add(m.group());
			}
			int len = fullLine.size();
			for (int i = 0; i < len && !safeSoFar; i++) {
				// Trying removing single levels until I find a safe one (there MUST be a better way)
				List<String> thisLine = new ArrayList<String>();
				thisLine.addAll(fullLine);
				thisLine.remove(i);
				String thisLineStr = "";
				for (String s : thisLine) {
					thisLineStr += s + " ";
				}
				safeSoFar = new Report(thisLineStr).isSafe();
			}
		}
		
		this.safe = safeSoFar;
	}
	
	public boolean isSafe() {
		return this.safe;
	}
	
}
