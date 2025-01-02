package com.robabrazado.aoc2024.day21;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
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

/*
 * [These notes started out as me organizing my thoughts before writing the
 * code. At this point, I've done some coding and changed my mind about some
 * things. Below are the updated notes.]
 * 
 * Now that I better know what I'm talking about, let me start with defining
 * my terms (instead of figuring them out as I go along). I'm dealing
 * primarily with two objects, Keypad and Robot, that live in a controller-
 * worker chain. This chain is basically structured like a double-linked list,
 * so I'll use terms in that context. The "list" consists of nodes of
 * alternating types, Keypad and Robot. The head of the list is the
 * Keypad that controls the door. (The door is not represented in these
 * objects.) The head Keypad is the worker of a Robot, which is the worker of
 * a Keypad, which is the worker of a Robot, etc. The tail of the list is the
 * Keypad being operated by the player. From this, every Robot has a worker
 * Keypad and a controller Keypad. Every Keypad has a worker Robot and a
 * controller Robot (except the head has no worker, and the tail has no
 * controller).
 * 
 * An "input" is a collection of "keystrokes" that go into a Keypad. A
 * "Command" is the instruction that goes into a Robot. In this model,
 * commands are not issued directly to a Robot, but come from the Robot's
 * controller, which is a Keypad. E.g., a Keypad receives a '^' keystroke and
 * (conceptually) transmits an UP command to its worker Robot. (I am...
 * strongly suspicious that this abstract separation is unnecessary, but the
 * last time I tried to design without it, I got into trouble. Plus, it
 * pleases my brain to think a Robot's controller could be later swapped out
 * without much trouble.) A collections of commands is a "command string."
 * 
 * Both input and command strings are represented by character strings.
 * A "keypress" can be thought of as a unit of measurement. In an input, a
 * "keypress" represents the movement of a robot arm from one key to another
 * (or not, for pressing the same key) and the pressing of the key. A keypress
 * of input is (mostly) just one character, but the specifics of it depend
 * on the robot arm's previous position, i.e. the previous character. All
 * command strings assume the robot's arm starts at the 'A' position. One
 * "keypress" of command string is the list of commands necessary to move the
 * robot arm to the correct key, which is punctuated at the end by one ACT
 * command to press the key.
 * 
 * A Keypad's role is to have a key configuration of some kind and do its
 * own pathfinding; it can tell you the best routes from key to key.
 * A Robot's role is to translate desired input into desired command strings;
 * it can tell you which are the shortest command strings to produce input.
 * Those command strings can be used as input for that Robot's controller
 * Keypad, which is the worker of some other Robot, which translates that
 * input into its own command strings, and so on up the chain. It happens
 * that the String representation of a Robot's command string is the same
 * as the String representation of that Robot's controller Keypad's input.
 * 
 * So. Finding best command string for a given input involves two parts:
 * (1) the optimal route(s) through the Keypad and (2) the shortest command
 * string(s) for those routes.
 * 
 * Navigation between Keypad keys is just grid navigation, so there are many
 * "shortest" routes between two keys. Regardless of the actual sequence of
 * moves in each route, shortest routes can be described with a simple piece
 * of metadata: some number of column changes in one direction and some number
 * of row changes in another direction. The length of all those routes will
 * be the taxicab distance between keys. Determining this metadata is the job
 * of the Keypad.
 * 
 * The Robot must then determine, from among the various shortest route
 * options described by the metadata, which produces the shortest command
 * string. To begin with, the shortest command strings will result in routes
 * with the fewest changes in direction. This is because it takes fewer
 * commands for a Robot to execute the same keystroke many times in a row
 * than it does to execute keystrokes where it has to move its arm between
 * presses. Because we know the shortest routes contain the same number
 * of moves and that the order of them (mostly) doesn't matter, then we want
 * to choose from the routes with the most number of like moves in a row.
 * In addition, because the routes only consist of two directions, there are
 * only two "friendliest" options: (1) all the column changes followed by all
 * the row changes or (2) all the row changes followed by all the column
 * changes.
 * 
 * If the metadata indicates only one direction of movement (i.e. the number
 * of either row or column changes is zero) or if one of the friendliest
 * routes is invalid because it passes over empty space, then there is only
 * one option for shortest command. Otherwise, there are two options, and the
 * Robot only needs to know which (if any) of the two resulting command
 * strings is shorter.
 * 
 * Once that is in place, then any Robot can generate its own command string
 * from the input that the caller wants on the Robot's worker Keypad. That
 * command string then becomes input for the Robot's controller Keypad,
 * which is likewise desired input for THAT Keypad's controller, which can
 * generate its own command string, which becomes input for the next
 * controller, and so on. This also means that any Robot can determine its
 * command string for input on the head Keypad by asking its worker to
 * determine its command string for the head input, and so on, and in that
 * way, we can ask the last Robot in the chain for its command string for
 * input to eventually be passed to the head of the chain.
 */
public class KeypadRobot {
	private final String name;
	
	Keypad controller;
	Keypad worker;
	
	// Instantiates a robot with a default controller
	public KeypadRobot(Keypad worker, String robotName) {
		this(worker, robotName, (String) null);
	}
	
	// Instantiates a robot with a default controller with the specified name
	public KeypadRobot(Keypad worker, String robotName, String controllingKeypadName) {
		if (worker == null) {
			throw new RuntimeException("Robot must have a worker. Robot must have a purpose.");
		}
		
		// TODO prevent circular references
		
		this.setWorker(worker);
		
		if (robotName == null || robotName.isEmpty()) {
			throw new RuntimeException("Robot must have a name. Robot is an invidual.");
		}
		this.name = robotName;
		
		if (controllingKeypadName == null) {
			controllingKeypadName = "Controller of " + robotName;
		}
		this.setController(new Keypad(Keypad.KeypadType.DIRECTIONAL, controllingKeypadName));
		
		return;
	}
	
	public String getName() {
		return this.name;
	}
	
	public boolean hasWorker() {
		return this.worker != null;
	}
	
	// Returns non-null or throws
	public Keypad getWorker() {
		if (!this.hasWorker()) {
			throw new IllegalStateException(String.format("%s has no worker", this.name));
		}
		return this.worker;
	}
	
	void clearWorker() {
		if (this.worker != null) {
			this.worker.controller = null;
			this.worker = null;
		}
		return;
	}
	
	void setWorker(Keypad newWorker) {
		this.clearWorker();
		if (newWorker != null) {
			newWorker.controller = this;
			this.worker = newWorker;
		}
		return;
	}
	
	public boolean hasController() {
		return this.controller != null;
	}
	
	// Returns non-null or throws
	public Keypad getController() {
		if (!this.hasController()) {
			throw new IllegalStateException(String.format("%s has no controller", this.name));
		}
		return this.controller;
	}

	void clearController() {
		if (this.controller != null) {
			this.controller.worker = null;
			this.controller = null;
		}
		return;
	}
	
	void setController(Keypad newController) {
		this.clearController();
		if (newController != null) {
			newController.worker = this;
			this.controller = newController;
		}
		return;
	}
	
	// Returns the command string to get the specified input on the head Keypad in the control chain
	public String getCommandStringForHeadInput(String headInput) {
		String myInput;
		if (this.hasWorker() && this.getWorker().hasWorker()) {
			myInput = this.getWorker().getWorker().getCommandStringForHeadInput(headInput);
		} else {
			// I AM HEAD
			myInput = headInput;
		}
		return this.getCommandStringForInput(myInput);
	}
	
	// Returns the command string to get the specified input on this Robot's worker keypad
	public String getCommandStringForInput(String input) {
		StringBuilder strb = new StringBuilder();
		
		if (input != null && !input.isEmpty()) {
			char from = 'A'; // Assume always starting from 'A' position
			char[] chars = input.toCharArray();
			
			for (char to : chars) {
				strb.append(this.getCommandStringForKeypress(from, to));
				from = to;
			}
		}
		return strb.toString();
	}
	
	public String getCommandStringForKeypress(char from, char to) {
		if (!this.hasWorker()) {
			throw new IllegalStateException(String.format("%s is not controlling a keypad", this.name));
		}
		
		StringBuilder strb = new StringBuilder();
		
		if (from != to) {
			Coords metadata = this.worker.getKeypressMetadata(from, to);
			List<List<Dir>> candidates = new ArrayList<List<Dir>>(); // short-ass list
			if (metadata.getCol() != 0) {
				List<Dir> candidate = KeypadRobot.generatePathFromMetadata(metadata, Arrangement.COL_FIRST);
				if (this.worker.isValidPath(candidate, from, to)) {
					candidates.add(candidate);
				}
			}
			if (metadata.getRow() != 0) {
				List<Dir> candidate = KeypadRobot.generatePathFromMetadata(metadata, Arrangement.ROW_FIRST);
				if (this.worker.isValidPath(candidate, from, to)) {
					candidates.add(candidate);
				}
			}
			
			int candidateCount = candidates.size();
			if (candidateCount > 1) {
				// Find shortest candidate by sorting by length
				Collections.sort(candidates, Comparator.comparingInt(List::size));
			} else if (candidateCount == 0) {
				throw new RuntimeException(String.format("%s unable to find any valid paths for input keypress '%c' to '%c'",
						this.name, from, to));
			} // else only one candidate remains
			
			// Build command from best candidate
			for (Dir d : candidates.get(0)) {
				strb.append(Command.getCommandByDir(d).c);
			}
		}
		
		return strb.append(Command.ACT.c).toString(); // All keypresses end with ACT
	}
	
	@Override
	public String toString() {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		
		pw.println("Robot " + this.name);
		pw.println("Controlled by: " + this.controller.getName());
		pw.println("Controlling: " + this.worker.getName());
		
		return sw.toString();
	}
	
	private static List<Dir> generatePathFromMetadata(Coords metadata, Arrangement arrangement) {
		List<Dir> result = new ArrayList<Dir>();
		int col = metadata.getCol();
		int row = metadata.getRow();
		
		int firstCount, secondCount;
		Dir firstDir, secondDir;
		switch (arrangement) {
		case COL_FIRST:
			firstCount = Math.abs(col);
			firstDir = col > 0 ? Dir.E : Dir.W;
			secondCount = Math.abs(row);
			secondDir = row > 0 ? Dir.S : Dir.N;
			break;
		case ROW_FIRST:
			firstCount = Math.abs(row);
			firstDir = row > 0 ? Dir.S : Dir.N;
			secondCount = Math.abs(col);
			secondDir = col > 0 ? Dir.E : Dir.W;
			break;
		default:
			throw new RuntimeException("Unsupported arrangement: " + arrangement.name());
		}
		
		for (int i = 1; i <= firstCount; i++) {
			result.add(firstDir);
		}
		for (int i = 1; i <= secondCount; i++) {
			result.add(secondDir);
		}
		
		return result;
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
	
	private enum Arrangement {
		COL_FIRST		(),
		ROW_FIRST		();
	}
}
