package com.robabrazado.aoc2024.day21;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.robabrazado.aoc2024.Solver;
import com.robabrazado.aoc2024.day21.Keypad.KeypadType;
import com.robabrazado.aoc2024.grid.Dir;

// --- Day 21: Keypad Conundrum ---
/*
 * This is now my upteenth rewrite. Mostly due to how I designed these
 * objects, this Solver does a lot more that most other Solver objects.
 * 
 * See the comments on KeypadRobot for detailed usage explanation.
 */
public class Day21Solver extends Solver {
	
	public Day21Solver() {
		super(21);
		return;
	}
	
	@Override
	public String solve(Stream<String> puzzleInput, boolean partOne, boolean isTest) {
		
		Keypad pad = new Keypad(KeypadType.NUMERIC, "test");
		KeypadRobot bot = new KeypadRobot(pad, "testbot");
//		System.out.println(bot.getInputKeypressCost('A', '1'));
		
		System.out.println(pad.getKeypressMetadata('1', 'A'));
//		System.out.println(bot.getInputKeypressCost('1', 'A'));
		
//		System.out.println(bot.getInputKeypressCost('A', '5'));
		
		// //////////////////////////////
		
		BigInteger result = BigInteger.ZERO;
		
		Keypad doorKeypad = new Keypad(KeypadType.NUMERIC, "Depressurized Door Controller");
		KeypadRobot vacuumRobot = new KeypadRobot(doorKeypad,
				"Vacuum Robot Operating Door", "Irradiated Vacuum Robot Controller");
		KeypadRobot radiationRobot = new KeypadRobot(vacuumRobot.getController(),
				"Radiation Robot Operating Vacuum Robot", "Frozen Radiation Robot Controller");
		KeypadRobot frozenRobot = new KeypadRobot(radiationRobot.getController(),
				"Frozen Robot Operating Radiation Robot", "Crowded Frozen Robot Controller");
		
//		KeypadRobot myRobot = frozenRobot; // For convenience
		KeypadRobot myRobot = radiationRobot; // For testing
		
		Iterator<String> it = puzzleInput.iterator();
		Pattern inputP = Pattern.compile("^(\\d+)A$");
		while (it.hasNext()) {
			String headInput = it.next();
			Matcher m = inputP.matcher(headInput);
			int numericPortion;
			if (m.find()) {
				numericPortion = Integer.parseInt(m.group(1));
			} else {
				throw new RuntimeException("Unrecognized input line: " + headInput);
			}
			System.out.println("Checking head input: " + headInput);
			
			String commandString = myRobot.getCommandStringForHeadInput(headInput);
			System.out.println("Got command string: " + commandString);
			
			int complexity = numericPortion * commandString.length();
			System.out.println("Complexity: " + complexity);
			result = result.add(BigInteger.valueOf(complexity));
		}
		
		return result.toString();
		
	}
	
}
