package com.robabrazado.aoc2024.day21;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.robabrazado.aoc2024.Solver;

// --- Day 21: Keypad Conundrum ---
public class Day21Solver extends Solver {
	
	public Day21Solver() {
		super(21);
		return;
	}
	
	@Override
	public String solve(Stream<String> puzzleInput, boolean partOne, boolean isTest) {
		BigInteger result = BigInteger.ZERO;
		KeypadRobot myRobot = new KeypadRobot(KeypadRobot.KeypadType.DIRECTIONAL).setWorker(		// I control crowded robot, who controls...
				new KeypadRobot(KeypadRobot.KeypadType.DIRECTIONAL).setWorker(						// ...cold robot, who controls...
						new KeypadRobot(KeypadRobot.KeypadType.DIRECTIONAL).setWorker(				// ...radiation robot, who controls...
								new KeypadRobot(KeypadRobot.KeypadType.NUMERIC))));					// ...vacuum robot, who controls the door.
		
		Iterator<String> it = puzzleInput.iterator();
		Pattern inputP = Pattern.compile("^(\\d+)A$");
		while (it.hasNext()) {
			String baseInput = it.next();
			Matcher m = inputP.matcher(baseInput);
			int numericPortion;
			if (m.find()) {
				numericPortion = Integer.parseInt(m.group(1));
			} else {
				throw new RuntimeException("Unrecognized input line: " + baseInput);
			}
			System.out.println("Checking base input " + baseInput);
			
//			String command = myRobot.getShortestCommandForBaseInput(baseInput);
			String command = "SOME FOO";
			System.out.println("Found command string " + command);
			
			int complexity = numericPortion * command.length();
			System.out.println("Complexity " + complexity);
			result = result.add(BigInteger.valueOf(complexity));
		}
		
		// TESTING SHIT
		
		System.out.println("---");
		
		String testBaseInput = "029A";
		
		KeypadRobot testNumericRobot = new KeypadRobot(KeypadRobot.KeypadType.NUMERIC);
		System.out.println(testNumericRobot.getShortestCommandForBaseInput(testBaseInput));
		
		KeypadRobot testDirectionalRobot = new KeypadRobot(KeypadRobot.KeypadType.DIRECTIONAL);
		testDirectionalRobot.setWorker(testNumericRobot);
		System.out.println(testDirectionalRobot.getShortestCommandForBaseInput(testBaseInput));
		
		KeypadRobot testDirectionalRobot2 = new KeypadRobot(KeypadRobot.KeypadType.DIRECTIONAL);
		testDirectionalRobot2.setWorker(testDirectionalRobot);
		System.out.println(testDirectionalRobot2.getShortestCommandForBaseInput(testBaseInput));
		
		KeypadRobot testDirectionalRobot3 = new KeypadRobot(KeypadRobot.KeypadType.DIRECTIONAL);
		testDirectionalRobot3.setWorker(testDirectionalRobot2);
		System.out.println(testDirectionalRobot3.getShortestCommandForBaseInput(testBaseInput));
		
		System.out.println("---");
		
		// END TESTING
		
		return result.toString();
		
	}
	
}
