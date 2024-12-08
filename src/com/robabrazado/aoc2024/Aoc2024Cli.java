package com.robabrazado.aoc2024;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.stream.Stream;

public class Aoc2024Cli {

	public static void main(String[] argv) {
		int argc = argv.length;
		boolean showUsage = false;
		String errMsg = null;
		BufferedReader in = null;
		
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
				
				String formattedDay = new DecimalFormat("00").format(dayNum);
				boolean partOne = "1".equals(part);
				boolean testData = test != null;
				
				// Instantiate solver
				Solver solver = (Solver) Class.forName("com.robabrazado.aoc2024.day" +
						formattedDay + ".Day" + formattedDay + "Solver").getDeclaredConstructor().newInstance();
				
				// Set up puzzle input stream
				StringBuilder strb = new StringBuilder("/puzzle-input/day");
				strb.append(formattedDay);
				strb.append("-input");
				if (testData) {
					strb.append("-test");
				}
				strb.append(".txt");
				
				in = new BufferedReader(new InputStreamReader(Aoc2024Cli.class.getResourceAsStream(strb.toString())));
				Stream<String> puzzleInput = in.lines();
				
				System.out.println(solver.solve(puzzleInput, partOne, testData));
			} else {
				errMsg = "Invalid arguments";
				showUsage = true;
			}
		} catch (Aoc2024Exception e) {
			errMsg = e.getMessage();
			showUsage = true;
		} catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException |
				InstantiationException | NoSuchMethodException e) {
			throw new RuntimeException(e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException doNothing) {}
			}
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
