package com.robabrazado.aoc2024.day21;

import java.util.stream.Stream;

import com.robabrazado.aoc2024.Solver;

// --- Day 21: Keypad Conundrum ---
/*
 * Okay, time for a complete rewrite. Well...almost complete; I'm keeping
 * Keypad and some simple struct-type objects, but I'm saying goodbye to the
 * venerable Robot, and I'm definitely saying goodbye to all that generator
 * crap from before. I'm gonna pare part 1 down to bare bones and see what I
 * can get going. 
 * 
 * It starts (and now ends) with the Keypad, a grid-like collection of Keys.
 * The Keypads live in a controller-worker chain, with the "head" Keypad
 * having no worker and the "tail" Keypad having no controller. In terms of
 * the puzzle, the head Keypad operates the door, and the tail Keypad is
 * operated by the player.
 * 
 * A Keypad has a Cursor. This is only conceptual and is not reflected in the
 * object model. In terms of the puzzle, the Cursor is a robot arm. The Cursor
 * is operated with Commands. One Command either moves the Cursor one space or
 * presses the "active" Key (the Key over which the Cursor is positioned).
 * 
 * A Keypad produces Keypresses. A Keypress is...well, you know...The pressing
 * of a Key. A Keypress is produced by a Keystroke, which is (a) the Command
 * sequence necessary to position the Cursor (b) followed by an ACT Command.
 * Part (a), the positioning Sequence, is a Path through the Keypad. (A Path
 * is a sequence of directions, while a positioning sequence is a sequence of
 * Commands.)
 * 
 * The Cost of a Keystroke is the length of the shortest Command sequence
 * necessary to produce the desired Keystroke. The shortest Command sequence
 * is a sequence no longer than the taxicab distance between the starting
 * position of the Cursor and the ending position of the Cursor (the position
 * of the desired Key). At the beginning of any Keystroke sequence, the Cursor
 * is assumed to be positioned over the 'A' key. There may be more than one
 * valid shortest Path from Key to Key, but they will all have the same
 * length, and so the Keystroke will have a consistent Cost.
 * 
 * The Tail Cost of a Keystroke is the length of the shortest Command sequence
 * needed for the TAIL Keypad in order to produced the desired Keystroke on
 * the asked Keypad.
 */
public class Day21Solver extends Solver {
	
	public Day21Solver() {
		super(21);
		return;
	}
	
	@Override
	public String solve(Stream<String> puzzleInput, boolean partOne, boolean isTest) {
		Starship starship = new Starship(puzzleInput);
		System.out.println(starship.status());
		
		return String.valueOf(starship.getComplexitySum());
	}
	
}
