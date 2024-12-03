package com.robabrazado.aoc2024.day03;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.robabrazado.aoc2024.Solver;

// 
public class Day03Solver extends Solver {
	
	public Day03Solver() {
		super(3);
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
		Pattern p = Pattern.compile("mul\\((\\d{1,3}),(\\d{1,3})\\)");
		int runningTotal = 0;
		
		try {
			in = super.getPuzzleInputReader(testData);
			
			String line = in.readLine();
			while (line != null) {
				Matcher m = p.matcher(line);
				
				while (m.find()) {
					runningTotal += (Integer.parseInt(m.group(1)) * Integer.parseInt(m.group(2)));
				}

				line = in.readLine();
			}
		} finally {
			if (in != null) {
				in.close();
			}
		}
		
		System.out.println(runningTotal);
	}
	
}
