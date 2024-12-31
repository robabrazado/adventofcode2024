package com.robabrazado.aoc2024.day21;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.robabrazado.aoc2024.Solver;
import com.robabrazado.aoc2024.day21.Keypad.KeypadType;

// --- Day 21: Keypad Conundrum ---
/*
 * This is now my umpteenth rewrite for this puzzle, and I'm still just
 * working on part 1. That said, I've learned quite a lot from my
 * previous failed attempts, so I'm going into this one quite confident.
 * This may not end up being great for part 2, but damn it, it's going
 * to be great for part 1. XD
 * 
 * Here's the general layout:
 * 
 * There is the Keypad object, which, as you might expect, represents
 * a set of keys. Then there is the Robot object, which represents...
 * well...a robot. I have decided to keep the old KeypadRobot class
 * just as a legacy name and so the file history will make some sense.
 * It will technically be the class that holds all the implementation
 * details, but for purposes of these notes, it's just Robot.
 * 
 * In my previous iterations, these in-fiction items
 * shared an object. First the keypad was primary; then the robot. In
 * the end, I am SURE it's not necessary to separate these objects,
 * but it will at least be clearer. I abandoned my most recent iteration
 * of this solution after discovering that I had been working with a
 * longer robot chain than the puzzle requested, which is what was
 * toppling my memory heap. So in the interests of clarity, I am keeping
 * them as separate objects for this next try.
 * 
 * These two objects will live in a controller-worker kind of chain.
 * Each Robot MUST have a controller, which MUST be a keypad. Each Robot
 * MUST also have a worker, which MUST be a keypad. (Mental note: I guess it
 * would behoove me to prevent circular references.)
 * 
 * Each Keypad MAY have a worker; if it does, it MUST be the Robot for
 * whom this Keypad is a controller. If it does not, it is the "base"
 * Keypad in the chain. For puzzle fiction purposes, this is assumed to be
 * the door keypad.
 * 
 * "Controller" and "worker" are specific to Robot/Keypad relationships.
 * One robot de factor controls another, though, for the most part,
 * but in this case, I'll use the term "operates" or "operator," hopefully
 * to avoid confusion. E.g., Radiation Robot "controls" Radiation Keypad, but
 * Radiation Robot "operates" Vacuum Robot.
 * 
 * New in this version: each Robot and Keypad gets a name. This will
 * help me report errors, which was sadly lacking in previous iterations
 * and sucks when I'm doing so much recursion. XD
 * 
 * The Keypad object, as mentioned, stores the keys and returns the kind of
 * information you would get from observing a keypad, and it will
 * basically serve as the "grid" data structure that I keep in most
 * solutions like this. It will also handle its local pathfinding,
 * which is going to make it easier to do caching, because...let's face
 * it, once you've found a path on one directional keypad, it applies
 * to all directional keypads.
 * 
 * The Robot object is in charge of operating the keypad. It basically
 * serves as the bridge between inputting presses on one keypad and
 * getting results on another keypad.
 * 
 * It is not (or should not be) necessary for the consumer to keep pointers
 * to all the objects in play. I'm just doing it in the solution for my
 * own clarity, and possibly for bughunting.
 * 
 * There are some terms I use a lot that have specific meanings in certain
 * contexts. A given Robot's "input" is the sequence of keypresses you would
 * like that robot to execute on the keypad it is controlling. A Robot's
 * "command string" is the sequence of keypresses to be executed on that
 * Robot's controller. A "command" is a single such keypress. "Base input" is
 * the sequence of keypresses you would ultimately like to arrive on the base
 * keypad, regardless of which Robot you're speaking to. The basis of the
 * control chain is that a Robot can tell you what command string it requires
 * in order to produce a desired input on its keypad. Command strings and
 * inputs can both be chunked into "keypresses." A keypress worth of input
 * is a single character, whereas a keypress worth of command string is
 * some string punctuated by an ACT command.
 * 
 * My downfall in all previous iterations was due mostly to my drastic
 * overengineering and preparing for general cases that may never come up.
 * With that, my intention this time around is that this new implementation
 * (at least for part 1) be quite specialized, so we'll see how much part 2
 * makes me regret that.
 */
public class Day21Solver extends Solver {
	
	public Day21Solver() {
		super(21);
		return;
	}
	
	@Override
	public String solve(Stream<String> puzzleInput, boolean partOne, boolean isTest) {
		BigInteger result = BigInteger.ZERO;
		
		Keypad doorKeypad = new Keypad(KeypadType.NUMERIC, "Depressurized Door Controller");
		KeypadRobot vacuumRobot = new KeypadRobot(doorKeypad,
				"Vacuum Robot Operating Door", "Irradiated Vacuum Robot Controller");
		KeypadRobot radiationRobot = new KeypadRobot(vacuumRobot.getController(),
				"Radiation Robot Operating Vacuum Robot", "Frozen Radiation Robot Controller");
		KeypadRobot frozenRobot = new KeypadRobot(radiationRobot.getController(),
				"Frozen Robot Operating Radiation Robot", "Crowded Frozen Robot Controller");
		
//		KeypadRobot myRobot = frozenRobot; // For convenience
		KeypadRobot myRobot = vacuumRobot; // testing
		
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
			
			int complexity = numericPortion * myRobot.getBestCommandLengthForBaseInput(baseInput);
			System.out.println("Complexity " + complexity);
			result = result.add(BigInteger.valueOf(complexity));
		}
		
		return result.toString();
		
	}
	
}
