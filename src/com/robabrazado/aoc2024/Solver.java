package com.robabrazado.aoc2024;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;

import com.robabrazado.aoc2024.day01.Day01Solver;

public abstract class Solver {
	private final int day;
	private final String formattedDay;
	
	protected Solver(int dayNumber) {
		this.day = dayNumber;
		this.formattedDay = new DecimalFormat("00").format(this.day);
		return;
	}
	
	public void solve(OutputStream out, OutputStream err, boolean partOne, boolean testOnly) throws IOException {
		solve(new PrintWriter(out, true), new PrintWriter(out, true), partOne, testOnly);
	}
	
	/**
	 * Runs the solver on either part one or part two and uses either test data or not.
	 * 
	 * @param out
	 * @param err
	 * @param isPartOne {@code true} for part one; {@code false} for part two
	 * @param testOnly {@code true} to use test input; {@code false} to use real input
	 * @throws IOException
	 */
	protected abstract void solve(PrintWriter out, PrintWriter err, boolean isPartOne, boolean testOnly) throws IOException;
	
	protected String getInputResourceName(boolean testData) {
		StringBuilder strb = new StringBuilder("/puzzle-input/day");
		strb.append(this.formattedDay);
		strb.append("-input");
		if (testData) {
			strb.append("-test");
		}
		strb.append(".txt");
		
		return strb.toString();
	}
	
	protected InputStream getPuzzleInputStream(boolean testData) throws IOException {
		String resourceName = this.getInputResourceName(testData);
		InputStream in = Solver.class.getResourceAsStream(resourceName);
		if (in == null) {
			throw new IOException("Resource " + resourceName + " not found");
		}
		return in;
	}
	
	protected BufferedReader getPuzzleInputReader(boolean testData) throws IOException {
		return new BufferedReader(new InputStreamReader(this.getPuzzleInputStream(testData)));
	}
	
	public int getDay() {
		return this.day;
	}
	
	public String getFormattedDay() {
		return this.formattedDay;
	}
	
	public static Solver getSolver(int day) {
		switch (day) {
		case 1:
			return new Day01Solver();
		default:
			return new DefaultSolver(day);
		}
	}

}
