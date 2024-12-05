package com.robabrazado.aoc2024.day04;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.robabrazado.aoc2024.Solver;

// --- Day 4: Ceres Search ---
public class Day04Solver extends Solver {
	
	public Day04Solver() {
		super(4);
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
		int width = 0;
		int height = 0;
		List<char[]> rowList = new ArrayList<char[]>();
		
		try {
			in = super.getPuzzleInputReader(testData);
			
			int widest = 0;
			String line = in.readLine();
			while (line != null) {
				int len = line.length();
				if (len > widest) {
					widest = len;
				}
				char[] row = new char[len];
				for (int i = 0; i < len; i++) {
					row[i] = line.charAt(i);
				}
				rowList.add(row);
				
				line = in.readLine();
			}
			
			width = widest;
			height = rowList.size();
		} finally {
			if (in != null) {
				in.close();
			}
		}
		
		// Force into a rectangle
		char[][] grid = new char[height][width];
		for (int rowNum = 0; rowNum < height; rowNum++) {
			grid[rowNum] = Arrays.copyOf(rowList.get(rowNum), grid[rowNum].length);
		}
		this.printArr(grid, out);
		
		out.println("done");
	}
	
	protected void solve2(PrintWriter out, PrintWriter err, boolean testData) throws IOException {
		
	}
	
	private void printArr(char[][] grid, PrintWriter out) {
		for (char[] row : grid) {
			out.println(row);
		}
		return;
	}
	
}
