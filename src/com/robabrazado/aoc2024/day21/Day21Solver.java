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
		
		KeypadRobot robotAtDepressurizedControllerAtRadiation = new KeypadRobot(KeypadRobot.KeypadType.NUMERIC);
		
		KeypadRobot robotAtRadiationControllerAtFreezing = new KeypadRobot(KeypadRobot.KeypadType.DIRECTIONAL);
		robotAtRadiationControllerAtFreezing.setWorker(robotAtDepressurizedControllerAtRadiation);
		
		KeypadRobot robotAtFreezingControllerAtCrowded = new KeypadRobot(KeypadRobot.KeypadType.DIRECTIONAL);
		robotAtFreezingControllerAtCrowded.setWorker(robotAtRadiationControllerAtFreezing);
		
		KeypadRobot myRobot = robotAtFreezingControllerAtCrowded;
		
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
			
			String command = myRobot.getShortCommandForBaseInput(baseInput);
			System.out.println("Found command string " + command);
			
			System.out.println("Previous command string " + robotAtRadiationControllerAtFreezing.getShortCommandForBaseInput(baseInput));
			
			int complexity = numericPortion * command.length();
			System.out.println("Complexity " + complexity);
			result = result.add(BigInteger.valueOf(complexity));
		}
		
		return result.toString();
		
	}
	
}
