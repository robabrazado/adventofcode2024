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
 * Once more with feeling. This is now my upteenth-plus-one rewrite of this
 * solution. I believe I now know where I've been going wrong. As is now
 * tradition, here's me trying to organize my thoughts before coding. Some of
 * this is based on info from the last iteration; some of this is new. For a
 * more comprehensive view of where my brain was for previous implementations,
 * see comments on the now-defunct KeypadRobot class. For this iteration,
 * I'm keeping with the separate Robot and Keypad concepts, but they no longer
 * share a control chain. Also, in this iteration, many terms are being reused
 * from previous implementations, but have new meanings. Sorry about that.
 * 
 * Consider the Keypad. It is essentially a grid-like collection of Keys, and
 * it is responsible for reporting and managing routes between Keys, which
 * isn't hard, because it's just grid navigation. For these purposes, it is
 * a fairly unsophisticated object. It does not maintain a cursor state or
 * anything like that; it just basically manages and reports on the layouts of
 * Keys.
 * 
 * Consider the Robot. A Robot relates to up to two Keypads. Each Robot is
 * required to have a "command Keypad," which, understandably, is the Keypad
 * that relays commands to the Robot (in the fiction; it doesn't do that as
 * an object.) A Robot may optionally have an "operated Keypad," which is the
 * Keypad that their robot arm is pressing keys on. Robots also live in a
 * control chain with each other; a Robot may be a worker, a controller, or
 * both, in relation to another Robot. This functions like a linked list,
 * and the "head" of the list (or chain) is the Robot with no worker; the
 * "tail" is the Robot with no controller. For purposes of the puzzle, the
 * head Robot is operating the keypad on the door. The tail Robot is the one
 * whose keypad is being operated by the player.
 * 
 * The Robot is where the business logic lives, and it has certain contexts
 * for data depending on where it goes and what it does relative to the
 * Robot's position in their control chain. A Robot's "input" is a sequence of
 * characters that represent keys to be pressed on the Robot's operated
 * Keypad. That input's "command string" is the sequence of keys to be pressed
 * on the Robot's command Keypad to produce the input on the operated Keypad.
 * A "keystroke" is one key being pressed on the operated Keypad. It can be
 * though of as context for data and a unit of measurement. One keystroke of
 * input is represented by one character but includes information provided
 * by the previous character in the sequence (practically, sequences are
 * assumed to start with an implied 'A' key for these purposes, because
 * that's where the Robot's arm always starts). A keystroke of input is
 * represented by the notation keystroke(from, to), where "from" is the key
 * over which the Robot's arm is initially positioned and "to" is the key to
 * which the Robot's arm must navigate and subsequently press. One keystroke
 * of command string is the sequence of commands that produces one keystroke
 * of input. It is one or more commands that moves the robot's arm to the
 * correct position and directs it to press the key.
 * 
 * For example, consider a Robot controlled by a directional keypad and
 * operating a numeric keypad. For the input "012A," the first keystroke of
 * input is represented by the "0" and is the keystroke('A', '0'). The first
 * keystroke of that input's command string is "<A" and represents the
 * command sequence LEFT, ACT, which directs the Robot's arm, starting from
 * the 'A' key, to navigate to the '0' key and press it.
 *  
 * Every keystroke has a "cost," which is the length of the command string
 * necessary to be given to a Robot to produce a given input. The cost is
 * equal to the keystroke's command string length. The "tail cost" is the
 * length of the command string needed for the tail Robot in the control
 * chain. In the example above, for the input "012A," the first keystroke,
 * keystroke('A', '0'), has a command string of length 2 ("<A"). It also
 * has a tail cost of 2...if the Robot has no controller! If the Robot has a
 * controller (i.e. is itself a worker), that Robot's command string ("<A")
 * becomes the input for their controller (whose operated Keypad is the
 * Robot's command Keypad), and so the controller Robot examines input "<A"
 * and finds the first keystroke('A', '<') evaluates to the command string
 * "v<<A" which has a length of 4. The second keystroke of the input "<A"
 * is keystroke('<', 'A') which (predictably) also has length 4, because it's
 * just retracing the first keystroke's path. So from the first Robot's
 * perspective (the worker Robot's perspective), the command string length of
 * input keystroke('A', '0') is 2, cost('A', '0') is 2, but tailCost('A', '0')
 * is 8.
 * 
 * All "shortest" Keypad routes have the same length (and therefore the same
 * command string lengths), the taxicab distance between keys. The collection
 * of these routes can be described with only the "metadata" of the route:
 * the number of column offsets in one direction and the number of row offsets
 * in another direction. (This is neatly held by the Coords object.) All
 * command strings that fit the criteria outlined by the metadata are the
 * shortest command strings that produce the desired input. However, not all
 * these command strings will have equal cost. It is more costly to move
 * the robot's arm between keys than to not, so the least costly command
 * strings will be the ones with the fewest changes between keys. For any
 * given input keystroke, the least costly command string should be the
 * one with the fewest changes in key position, because cost(X, X) is always
 * 1 while cost(X, Y) is always greater than 1 where X != Y. In the first
 * case, the Robot only requires the ACT command, while in the second case,
 * the robot requires arm-positioning commands followed by the ACT command.
 * Therefore, there should generally be a two-way tie for least costly command
 * string for a given input keystroke, and it should be between "all column
 * offsets followed by all row offsets" or "all row offsets followed by all
 * column offsets." While the Robot is unable to execute a path that crosses
 * an empty space on the Keypad, it shouldn't matter, because the puzzle
 * Keypad all have the empty spaces in the corner, so in taxicab navigation,
 * if one route crosses the empty space, there is another route that doesn't.
 * Put another way, the empty space is never an obstacle to BOTH lowest-cost
 * paths. The two lowest cost options in the space of metadata-defined command
 * strings are referred to as the "friendliest" options.
 * 
 * The cost of either friendliest option can be calculated without generating
 * the full command string. There are at most three key changes in any given
 * friendliest option where A represents the 'A' key, C represents the column
 * change key ('<' or '>'), and R represents the row change key ('^' or 'v').
 * The costs will either be
 * 
 *     cost(A, C) + cost(C, R) + cost(R, A)
 *     
 * for the "column-first arrangement" or
 * 
 *     cost(A, R) + cost(R, C) + cost(C, A)
 *     
 * for the "row-first arrangement." There are only two key changes in the
 * cases where the are either no column offsets or no row offsets. There
 * are no key changes when there are no offsets, because that's just the
 * same key. The cost of the rest of the friendliest option is just the
 * sum of the number of column offsets minus one and the number of row
 * offsets minus one.
 * 
 * For example, consider the command string "CCCRRA" (represented by the
 * metadata 3C2R, or three column offsets in direction C and 2 row offsets in
 * direction R) in the column-first arrangement. We can first compute the
 * cost by examining the key changes:
 * 
 *     cost(A, C) + cost(C, R) + cost(R, A)
 *     
 * The first cost represents the first C keystroke, the second cost represents
 * the first R keystroke (coming from C), and the third cost represents the
 * terminal A keystroke (coming from R). The remaining keystrokes are all
 * repetitions of either the column or row offset keys. The command string
 * with _ indicating keystrokes that have already had their costs computed is
 * "_CC_R_" for two C key repetitions (each costing 1 for a total cost of 2)
 * and one R key repetition (cost 1), for a total additional cost of 3. So,
 * the cost of "CCCRRA" is cost(A, C) + cost(C, R) + cost(R, A) + 3.
 * 
 * Similarly to the tail Robot being significant because they are the ultimate
 * source of input cost, the head Robot is significant because they are
 * operating the door keypad, which is where the puzzle input ends up. In
 * addition to any Robot being able to need to find their tail Robot to
 * determine input cost, any Robot must also be able to find the head Robot
 * to specify head input. For this reason, the control chain is a double-
 * linked list.
 * 
 * Remember that any given Robot's command string is input for their
 * controller Robot, and cost(input) is just cost(A, input[0]) +
 * cost(input[0], input[1]) and so on. Additionally, cost is controller
 * command string length (if a controller is present), so cost(X, Y) is
 * length(controller.commandFor(X, Y). Conversely, cost(X, Y) without a
 * controller present is just length(commandFor(X, Y)).
 * 
 * To pull this all together, start with a Robot's input, which is a sequence
 * of keystrokes. The Keypad can translate input(A, B) to metadata(A, B)
 * which is some form of XCYR (where X and Y are non-negative integers, and C
 * and R are directions). This can represent a number of valid command strings
 * (that number is 2 ^ (X + Y)), but the two friendliest options are
 * (1) X repetitions of C followed by Y repetitions of R followed by A or
 * (2) Y repetitions of R followed by X repetitions of C followed by A. The
 * less costly of those two options is the best command for the input.
 */
public class Day21Solver extends Solver {
	
	public Day21Solver() {
		super(21);
		return;
	}
	
	@Override
	public String solve(Stream<String> puzzleInput, boolean partOne, boolean isTest) {
		int result = 0;
		
		Keypad doorKeypad = new Keypad("Depressurized Door Keypad", KeypadType.NUMERIC);
		Robot depressurizedRobot = new Robot("Depressurized Robot", doorKeypad);
		Robot radiationRobot = new Robot("Radiation Robot", depressurizedRobot);
		Robot frozenRobot = new Robot("Frozen Robot", radiationRobot);
		
//		System.out.println(doorKeypad);
//		System.out.println(depressurizedRobot);
//		System.out.println(radiationRobot);
//		System.out.println(frozenRobot);
		
		Robot myRobot = frozenRobot; // for convenience
//		Robot myRobot = depressurizedRobot; // for testing
		
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
			
			String command = myRobot.getBestCommandForHeadInput(headInput);
			int len = command.length();
			System.out.println("Found command: " + command + " (" + String.valueOf(len) + ")");
			
			int complexity = numericPortion * len;
			System.out.println("Complexity: " + complexity);
			result += complexity;
		}
		
		return String.valueOf(result);
		
	}
	
}
