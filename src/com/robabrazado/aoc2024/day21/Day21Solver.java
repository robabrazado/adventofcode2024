package com.robabrazado.aoc2024.day21;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.robabrazado.aoc2024.Solver;
import com.robabrazado.aoc2024.day21.Keypad.KeypadType;
import com.robabrazado.aoc2024.grid.Dir;

// --- Day 21: Keypad Conundrum ---
/*
 * Now my upteenth-plus-two rewrite, though it's like half a rewrite. My last
 * failed attempt was pretty deflating, but after some time away from the
 * code, I came up with an idea of (a) what I was doing wrong, and (b) what I
 * could try and do differently, so I'm gonna give that a shot by modifying
 * the current stuff rather than (yet) another complete rewrite. What follows
 * is mostly the same as the previous attempt as far as base concepts go, but
 * this time I'm abandoning the "shortcut" cost calculation stuff (which
 * turned out pointless anyway), and this time around I'm going to just lean
 * into calculating actual paths and do it generator-style instead of trying
 * to calculate them all at once. I also intend to smarten up a path object
 * (and possibly also the metadata, but they might end up being the same
 * thing) and just generally trying to do less repeated effort and different
 * stages of execution. Anyway...here's the new deal.
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
 * that relays commands to the Robot (in the fiction, I mean; it doesn't do
 * that as an object.) A Robot may optionally have an "operated Keypad,"
 * which is the Keypad that their robot arm is pressing keys on. Robots also
 * live in a control chain with each other; a Robot may be a worker, a
 * controller, or both, in relation to another Robot. This functions like a
 * linked list. The head of the list is the Robot with no worker; the "tail"
 * is the Robot with no controller. For purposes of the puzzle, the head Robot
 * is operating the keypad on the door. The tail Robot is the one whose keypad
 * is being operated by the player. Any controller Robot automatically has
 * their worker's command keypad set to the controller's operated keypad.
 * 
 * The Robot is where the business logic lives, and I'll go through some
 * data contexts relative to a Robot's perspective. A Robot's "input" is a
 * sequence of keys to be pressed on the Robot's operated Keypad. That input's
 * "command string" is the sequence of keys to be pressed on the Robot's
 * command Keypad to produce the input on the operated Keypad. A "keystroke"
 * is one key being pressed on the operated Keypad. It can be thought of as
 * context for data and a unit of measurement. One keystroke of input is
 * represented by one character but includes information provided by the
 * previous character in the sequence. (For the purposes of command strings,
 * input sequences are assumed to start with an implied 'A' key, because
 * that's where the Robot's arm always starts). A keystroke of input is
 * represented by the notation keystroke(from, to), where "from" is the key
 * over which the Robot's arm is initially positioned and "to" is the key to
 * which the Robot's arm must navigate and subsequently press. One keystroke
 * of command string is the sequence of commands that produces one keystroke
 * of input. It consists of zero or more commands that move Robot's arm to
 * the correct position followed by one command that directs them to press
 * the key.
 * 
 * For example, consider a Robot accepting commands via directional keypad and
 * operating a numeric keypad. For the input "012A," the first keystroke of
 * input is represented by the "0" and is the keystroke('A', '0'). The first
 * keystroke of that input's command string is "<A" and represents the
 * command sequence LEFT, ACT, which directs Robot's arm, starting from the
 * 'A' key, to navigate to the '0' key and press it. The second keystroke
 * would be keystroke('0', '1') with a command string "^<A" navigating the
 * Robot arm UP, LEFT, ACT to move from the '0' key to the '1' key and
 * pressing it. And so on. The input "012A" is thereby translated to the
 * command string "<A^<A>A>>vA" (the concatenation of all input keystrokes.)
 * 
 * [Here is where I deleted everything about "cost." I'm not trucking with
 * that stuff anymore.]
 *  
 * All "shortest" paths from key to key on a Keypad have the same length (and
 * therefore the same command string lengths), the taxicab distance between
 * keys. The collection of these routes can be described with the "metadata"
 * of the route: the number of column offsets in one direction and the number
 * of row offsets in another direction. (It turns out that this information is
 * neatly held by the Coords object.) A path can be represented by a sequence
 * of Dir objects. Notably, a path is only navigational information; a
 * keystroke includes both the translation of a path from Dir to Command plus
 * and additional ACT command.
 * 
 * A specific path can be identified by (or generated from!) a unit of
 * keystroke metadata, a Keypad (or strictly speaking a Keypad.KeypadType),
 * an (mostly internal) bitmap. Each bit in the bitmap represents either a row
 * or column offset direction, so while we know that any path can be
 * represented by a bit series with length equal to the path length, we also
 * know that rarely will ALL possible bit combinations in that space fulfill
 * the metadata criteria. The "bitmap number" is the number represented by
 * the binary number spelled out by the bitmap. The minimum bitmap number
 * will always be 0. The maximum bitmap number will be, in binary, a
 * length(path) number of 1 bits. In decimal terms, the maximum bit number
 * will be (2 ^ length(path)) - 1, but as said, probably not all bitmap
 * numbers in that range will be valid (the exception being when there are
 * only offsets in one direction, i.e. only column or only row offsets).
 * 
 * Example: consider a numeric keypad and the path metadata 1W2N (one
 * column offset west/left and two row offsets north/up). Specific paths can
 * be identified with three bits of data, so the valid ID numbers will be
 * between 0 and 7 (or 000 and 111), but out of the 8 slots in that space,
 * only 3 are valid ID numbers, the three binary numbers with two 1 bits,
 * 3, 5, and 6 (or 011, 101, and 110).
 * 
 * For completeness, a zero count in both offsets means a path from a key
 * to itself, i.e. also an empty path.
 * 
 * Because of the confusing nature of the bitmap, it's not my intention to
 * expose the bitmap number outside the context of the path generator. Its
 * public face will instead be a "path index." The path index is the ordinal
 * number (in a 0-indexed array) of a sorted list of all valid bitmap numbers;
 * the lowest path index will be 0, and the highest will be
 * length(bitmapList) - 1. In the example above, the 1W2N metadata on a
 * numeric keypad would have 3 path indices in the range 0-2. Path index 0
 * would correspond to bitmap 011, index 1 to bitmap 101, and index 2 to
 * bitmap 110. I don't think this path index will be tremendously useful
 * or informative, but it will be easier for consumers to deal with than
 * the bitmaps as far as identifying particular paths, and will surely make
 * it easier for generators (see below) to maintain state.
 * 
 * As mentioned earlier, the head Robot, the one with no worker, is operating
 * the door keypad, which is where our puzzle input ends up. This introduces
 * the concept of "head input," which is input that goes to the door keypad
 * (via the head Robot). Any Robot's input can be translated to a command
 * string, and any Robot's command string can be treated as input for that
 * Robot's controller. Put another way, any Robot can be asked for a command
 * string to generate head input. If the asked Robot is the head Robot, they
 * need only return their command string for the specified input. If the asked
 * Robot is not the head Robot, meaning they have a worker, they can ask their
 * worker for the worker's command string for the specified head input and
 * then return the asked Robot's command string using the worker's command
 * string as the asked Robot's input.
 * 
 * There will be MANY valid command strings as the input/command translation
 * continues along the control chain. (I think this is the part where I was
 * getting into trouble in the past.) So instead of generating all possible
 * command strings and passing them up the chain to generate even more
 * possible command strings, my intention now is to pass only metadata (and
 * KeypadType, though I guess it'll always be the same) along the chain, and
 * only the asked Robot needs to generate command strings. I intend to be able
 * to do this with a generator, and that way I don't need to hold all possible
 * command strings in memory. In this model, for puzzle purposes, the asked
 * Robot is going to end up being the tail Robot in the chain.
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
		
		System.out.println(doorKeypad);
		System.out.println(depressurizedRobot);
		System.out.println(radiationRobot);
		System.out.println(frozenRobot);
		
//		Robot myRobot = frozenRobot; // for convenience
		Robot myRobot = radiationRobot; // for testing
		
		CommandGenerator testgen = myRobot.getCommandGeneratorForHeadInput("029A");
		Iterator<String> commandIt = testgen.commandIterator();
		Map<Integer, Integer> counts = new HashMap<Integer, Integer>();
		while (commandIt.hasNext()) {
			int count = commandIt.next().length();
			if (!counts.containsKey(count)) {
				counts.put(count, 0);
			}
			counts.put(count, counts.get(count) + 1);
		}
		Set<Integer> countKeys = new TreeSet<Integer>(counts.keySet());
		for (Integer count : countKeys) {
			System.out.println(String.format("%d: %d", count, counts.get(count)));
		}
		
		
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
