package com.robabrazado.aoc2024.day02;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.robabrazado.aoc2024.Solver;

public class Day02Solver extends Solver {
	
	public Day02Solver() {
		super(2);
		return;
	}

	@Override
	protected void solve(PrintWriter out, PrintWriter err, boolean isPartOne, boolean testData) throws IOException {
		BufferedReader in = null;
		int safeCounter = 0;
		
		try {
			in = super.getPuzzleInputReader(testData);
			
			String line = in.readLine();
			while (line != null) {
				Report r = new Report(line);
				if (r.isSafe()) {
					safeCounter++;
				}
				line = in.readLine();
			}
		} finally {
			if (in != null) {
				in.close();
			}
		}
		
		out.println(safeCounter);
	}

}
