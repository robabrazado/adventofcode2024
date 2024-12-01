package com.robabrazado.aoc2024;

import java.io.IOException;
import java.io.PrintWriter;

public class DefaultSolver extends Solver {
	
	public DefaultSolver(int day) {
		super(day);
		return;
	}
	
	@Override
	public void solve(PrintWriter out, PrintWriter err, boolean partOne, boolean testOnly) throws IOException {
		err.println("Default solver for day " + super.getFormattedDay() + "; make sure appropriate solver is registered");
		out.println("No solution");
	}

}
