package com.robabrazado.aoc2024.day21;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.robabrazado.aoc2024.grid.Coords;
import com.robabrazado.aoc2024.grid.Dir;

// See Day21Solver for explanation on object structure
public class KeypadRobot {
	private final String name;
	private final Keypad controller;
	private final Keypad worker;
	
	public KeypadRobot(Keypad worker, String robotName) {
		this(worker, robotName, (String) null);
	}
	
	public KeypadRobot(Keypad worker, String robotName, String controllingKeypadName) {
		if (worker == null) {
			throw new RuntimeException("Robot must have a worker. Robot must have a purpose.");
		}
		// TODO check for circular references? Somehow?
		this.worker = worker;
		
		if (robotName == null || robotName.isEmpty()) {
			throw new RuntimeException("Robot must have a name. Robot is an invidual.");
		}
		this.name = robotName;
		
		if (controllingKeypadName == null) {
			controllingKeypadName = "Controller of " + robotName;
		}
		this.controller = new Keypad(Keypad.KeypadType.DIRECTIONAL, controllingKeypadName);
		
		return;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Keypad getWorker() {
		return this.worker;
	}
	
	public Keypad getController() {
		return this.controller;
	}

	public int getBestCommandLengthForBaseInput(String baseInput) {
		if (this.worker.getWorker() != null) {
			throw new RuntimeException("Not yet implemented"); // TODO
		} else {
			// I AM BASE
			throw new RuntimeException("Not yet implemented"); // TODO
		}
	}
	
	/*
	 * Here is the heart of finding the best command strings without actually
	 * generating the command strings. Actually generating the command strings
	 * was my downfall in the past.
	 * 
	 * These notes started out as me organizing my thoughts before writing the
	 * code, and if all goes well, they'll be the post-coding documentation as
	 * well. :) If not, I'll modify as necessary.
	 * 
	 * All of this is done through grid navigation with basically no
	 * constraints, so all route lengths (inputs) can be calculated with
	 * taxicab distance. I say "basically" before, because the one constraint
	 * is that the robot arm (cursor) never occupy the empty space (the null
	 * key), but our saving grace is that the null key is always in the
	 * corner, so there will always be an alternate route of equivalent
	 * length. The length of the best route from one key to another will
	 * always be the taxicab distance between the keys. In addition, the
	 * shortest route will always consist of some non-negative number of
	 * column offsets in one direction, and some non-negative number of row
	 * offsets in one direction, and then an ACT command. In terms of
	 * route length, the order of these commands doesn't matter (except
	 * the ACT has to be at the end), but in terms of the command strings
	 * needed to execute the routes, the best (meaning easiest to input)
	 * route will be the ones with the fewest arm moves. Consider that inputs
	 * "<<^A" and "<^<A" have the same route length and achieve the same
	 * result, but the command string for the second will always be longer,
	 * because it has to move the arm off the '<' key to the '^' key and back
	 * to the '<' key, as opposed to the first command which doesn't have to
	 * move the arm at all between the first '<' and the second '<' press.
	 * Therefore, there are at most two choices for "friendliest" path
	 * between two keys: (1) all row offsets followed by all column offsets
	 * or (2) all column offsets followed by all row offsets. If one of
	 * those paths crosses the null key, then there is only one choice
	 * for friendliest path. In either case, the friendliest path can be
	 * described with some simple metadata: the number of moves needed
	 * in each direction.
	 * 
	 * To decide the better of the two friendliest arrangements, the command
	 * string length (cost) of the arrangements is the tiebreaker. Each
	 * keypress comes with some associated cost. If the Robot's arm is
	 * poised above the desired key, then the cost is only 1 (the ACT
	 * command). In any other circumstance, there is a cost associated with
	 * moving the arm from one key to another. Our assumption above, based
	 * on this principle, is that the fewer key changes there are in the
	 * input, the better. The next corollary is that we can compare the
	 * costs of the key changes needed in each arrangement to decide which
	 * is less costly. In the case of the arrangements we're discussing,
	 * there are (at most) three key changes to consider: 'A' (always
	 * the starting point for a command string) to the first offset key,
	 * the first offset key to the second offset key, and the second
	 * offset key back to 'A'. (Clearly, if one of the offset counts
	 * is zero, we can skip the middle key change, but also...that
	 * being the case, there is only one friendliest arrangement, so...)
	 * 
	 * It is worth noting that cost(X,Y) can NOT be assumed equal to
	 * cost(Y, X) in all cases. In the directional keypad in the puzzle,
	 * switching from row offset to column offset is the same in both
	 * directions, which is fine (though likely an optimization I won't
	 * be taking advantage of), but cost('A', '<') is always greater
	 * than cost('A', '>'), for example, so just reversing an input's
	 * directions will result in a different command string length.
	 *
	 * A "keypress" is a unit of measurement that applies to both inputs and
	 * command strings but can mean different things. Each keypress of input
	 * translates to one keypress of command string, but each keypress of
	 * input is one character, while each keypress of command string is...
	 * another command string. There is NOT a one-to-one mapping between a
	 * given keypress of input and the resulting command string. The command
	 * string for a particular keypress (and therefore also the associated
	 * cost) depends on the starting position of the Robot's arm (which is
	 * always 'A' at the beginning).
	 * 
	 * The last big thing to keep in mind is that all command strings derived
	 * from input keypresses will always terminate with an ACT command.
	 *
	 * Example:
	 * 
	 * Consider one Robot, the "asker," asks a second Robot, the "answerer,"
	 * for the friendliest command string metadata for the route from 'X' to
	 * 'Y'. The answerer determines the metadata of the friendliest path:
	 * C number of column offsets in H direction, R number of row offsets in
	 * V direction, and whether either arrangement (column-first or row-first)
	 * passes over the null key and is disqualified. This information is
	 * passed back to the asker. The asker can then determine based on the
	 * metadata which arrangement is less costly command-wise.
	 * 
	 * If there is only one valid arrangement based on the answerer's
	 * metadata, then that's it; we're done here. If there are two choices,
	 * the asker can determine the better option by comparing the costs.
	 * First, we can accept that cost(X, X) for any X will be 1 (for the ACT
	 * at the end and no prior movement). As a result, we only need to compare
	 * the costs of key changes. For example, consider the metadata 3L2U
	 * (three left, two up), and assume both arrangements ("LLLUU" and
	 * "UULLL") are valid inputs. The command strings to compare ("LLLUUA" and
	 * "UULLLA") can be compared by examining
	 * 
	 *     cost(A, L) + cost(L, U) + cost(U, A)
	 * 
	 *                      vs.
	 * 
	 *     cost(A, U) + cost(U, L) + cost(L, A)
	 *
	 * because the rest of the middle costs (whatever they are) will be the
	 * same between arrangements. (Non-changing costs will either be
	 * cost(U, U) or cost(L, L), which both evaluate to 1.)
	 * 
	 * P.S., mostly to myself. Converting inputs to metadata can probably be
	 * handled best by the Keypads; the conversion will be the same for
	 * different Keypads with the same configuration, which also means they
	 * can be cached (memoized) class-wide. In the above example, that would
	 * be the answerer querying its worker. The asker still must make the
	 * choice between same-length arrangements based on the needs of the
	 * asker's controller. In the puzzle, this will always be a directional
	 * Keypad, but that's my one concession to generalizing the functionality;
	 * it's the Robot's role to translate input to their controller to
	 * commands.
	 */
	
	@Override
	public String toString() {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		
		pw.println("Robot " + this.name);
		pw.println("Controlled by: " + this.controller.getName());
		pw.println("Controlling: " + this.worker.getName());
		
		return sw.toString();
	}
	
	// This is primarily to bridge between existing Dir uses and the new Keypad uses
	// Commands probably won't be directly useful by consumers, as consumers should issue commands via Robot's controlling Keypad
	public enum Command {
		UP		('^', Dir.N),
		DOWN	('v', Dir.S),
		LEFT	('<', Dir.W),
		RIGHT	('>', Dir.E),
		ACT		('A', (Dir) null);
		
		final char c;
		final Dir d;
		
		Command(char c, Dir d) {
			this.c = c;
			this.d = d;
		}
		
		static Command getCommandByDir(Dir d) {
			switch (d) {
			case N:
				return UP;
			case S:
				return DOWN;
			case W:
				return LEFT;
			case E:
				return RIGHT;
			default:
				throw new RuntimeException("Unsupported direction: " + d.name());
			}
		}
	}
}
