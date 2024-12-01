package com.robabrazado.aoc2024;

import java.io.IOException;

public class Aoc2024Cli {

	public static void main(String[] argv) {
		int argc = argv.length;
		boolean showUsage = false;
		String errMsg = null;
		
		try {
			if (argc >= 2) {
				String day = argv[0];
				String part = argv[1];
				String test = argc >= 3 ? argv[2] : null;
				
				int dayNum = -1;
				try {
					dayNum = Integer.parseInt(day);
				} catch (NumberFormatException swallow) {}
				
				if (dayNum < 1 || dayNum > 25) {
					throw new Aoc2024Exception("Day must be an integer from 1 through 25");
				}
				
				int partNum = -1;
				try {
					partNum = Integer.parseInt(part);
				} catch (NumberFormatException swallow) {}
				
				if (partNum < 1 || partNum > 2) {
					throw new Aoc2024Exception("Part must be 1 or 2");
				}
				
				if (test != null && !"test".equalsIgnoreCase(test)) {
					throw new Aoc2024Exception("Third argument must be \"test\" or omitted");
				}
				
				Solver.getSolver(dayNum).solve(System.out, System.err, "1".equals(part), test != null);
			} else {
				errMsg = "Invalid arguments";
				showUsage = true;
			}
		} catch (Aoc2024Exception e) {
			errMsg = e.getMessage();
			showUsage = true;
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
		
		if (errMsg != null) {
			System.err.println(errMsg);
		}
		if (showUsage) {
			showUsage();
		}
		return;
	}
	
	private static void showUsage() {
		System.out.println("Usage:");
		System.out.println("\tAoc2024Cli <day> <part> [\"test\"]");
		return;
	}
}
