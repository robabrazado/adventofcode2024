package com.robabrazado.aoc2024.day21;

import java.util.stream.Stream;

import com.robabrazado.aoc2024.Solver;

// --- Day 21: Keypad Conundrum ---
/*
 * I've rewritten this so many times. I don't think I even committed the last
 * one. Here we go again from the top.
 * 
 * It starts and ends with the Keypad, a grid-like collection of Keys. A
 * Keypad accepts Commands (input) and produces Keypresses (output). Any
 * Keypad can be asked what Commands will generate a sequence of Keypresses.
 * 
 * The Keypads live in a controller-worker chain, with the "head" Keypad
 * having no worker and the "tail" Keypad having no controller. In terms of
 * the puzzle, the head Keypad operates the door, and the tail Keypad is
 * operated by the player. If a Keypad has a controller, its Commands are
 * a sequence of directions that move a conceptual "cursor" around the Keypad.
 * (The cursor is, in the fiction, the robot arm operating the Keypad. It is
 * not reflected in the object model.) If a Keypad has no controller, its
 * Commands are simply a sequence of its own keys to be pressed.
 * 
 * A Keystroke is the production of a Keypress from a cursor starting
 * position. It can be represented by a sequence of Commands which (a) move
 * the cursor from the starting position to the desired key and (b) press the
 * key. If a Keypad has no controller, a Keystroke is, again, just a key to be
 * pressed.
 * 
 * Any given Keystroke has some number of associated Command sequences. The
 * shortest Path between two Keys is made up of a number of column offsets
 * in one direction plus a number of row offsets in another direction, but
 * because they can be in any order, all permutations are valid as long as
 * they keep the Cursor positioned over a Key at every step along the Path.
 * Either number of offsets can be zero. If they are both zero, the Cursor
 * is not moving, so a Command sequence with no positioning sequence denotes
 * a repeated Keypress.
 * 
 * The Cost associated with a Keystroke is the length of the Command sequence
 * needed to produce the desired Keypress. The Tail Cost of a Keystroke is
 * the length of the command string needed on the final Keypad in the control
 * chain (the tail Keypad), the one with no controller.
 * 
 * We're looking for the lowest Tail Cost for a given Kepress sequence on the
 * Keypad at the head of the chain, the Keypad with no worker.
 */
public class Day21Solver extends Solver {
	
	public Day21Solver() {
		super(21);
		return;
	}
	
	@Override
	public String solve(Stream<String> puzzleInput, boolean partOne, boolean isTest) {
		Starship starship = new Starship(puzzleInput, partOne);
		System.out.println(starship.status());
		
		return starship.getComplexitySum().toString();
	}
	
}
