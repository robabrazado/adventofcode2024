package com.robabrazado.aoc2024.day21;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.robabrazado.aoc2024.grid.Coords;
import com.robabrazado.aoc2024.grid.Dir;

/*
 * If a Robot is assigned a worker, that worker's command keypad becomes this Robot's operated keypad.
 */
public class Robot {
	private static final MetadataArrangement[] ARRANGEMENTS = MetadataArrangement.values();
	
	private final String name;
	private final Keypad commandKeypad;
	private final Robot worker;
	private final Keypad nonWorkerOperatedKeypad;
	
	private Robot controller = null;
	
	private Robot(String name, Robot worker, Keypad nonWorkerKeypad) {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("Robot must have a name. Robot is an individual.");
		}
		this.name = name;
		
		if (worker != null) {
			this.worker = worker;
			worker.controller = this;
			if (nonWorkerKeypad != null && nonWorkerKeypad != worker.commandKeypad) {
				throw new IllegalArgumentException(this.name + " cannot control a Robot and operate a different keypad");
			}
			this.nonWorkerOperatedKeypad = null;
		} else if (nonWorkerKeypad != null) {
			this.worker = null;
			this.nonWorkerOperatedKeypad = nonWorkerKeypad;
		} else { // Both null
			throw new IllegalArgumentException(this.name + " must either have a worker or an operated keypad");
		}
		
		this.commandKeypad = new Keypad("Command Keypad of " + this.name, Keypad.KeypadType.DIRECTIONAL);
		return;
	}
	
	public Robot(String name, Robot worker) {
		this(name, worker, (Keypad) null);
		return;
	}
	
	/*
	 *  NOTE: If this constructor is called passing in another Robot's command
	 *  keypad, that does NOT create a controller-worker relationship! Use
	 *  Robot(String, Robot) instead.
	 */
	public Robot(String name, Keypad operatedKeypad) {
		this(name, (Robot) null, operatedKeypad);
		return;
	}
	
	public String getName() {
		return this.name;
	}
	
	// Always non-null; if this is null, constructors didn't enforce or something else codewise is wrong
	private Keypad operatedKeypad() {
		Keypad result = this.nonWorkerOperatedKeypad;
		if (this.worker != null) {
			result = worker.commandKeypad;
		}
		if (result != null) {
			return result;
		} else {
			throw new IllegalStateException(this.name + " is not operating a keypad");
		}
	}
	
	public int getBestCommandLengthForHeadInput(String headInput) {
		return getBestCommandForHeadInput(headInput).length();
	}
	
	public String getBestCommandForHeadInput(String headInput) {
		StringBuilder strb = new StringBuilder();
		
		if (headInput != null && !headInput.isEmpty()) {
			char from = 'A'; // Always assume starting from the 'A' key position
			char[] chars = headInput.toCharArray();
			for (char to : chars) {
				strb.append(this.getBestCommandForHeadInput(from, to));
				from = to;
			}
		}
		return strb.toString();
	}
	
	private String getBestCommandForHeadInput(char from, char to) {
		String result;
		if (worker != null) {
			StringBuilder strb = new StringBuilder();
			String myInput = this.worker.getBestCommandForHeadInput(from, to);
			char f = 'A';
			char[] chars = myInput.toCharArray();
			for (char t : chars) {
				strb.append(this.getBestCommandForInput(f, t));
				f = t;
			}
			result = strb.toString();
		} else {
			// I AM HEAD
			result = getBestCommandForInput(from, to);
		}
		return result;
	}
	
	private String getBestCommandForInput(char from, char to) {
		Set<String> commands = this.getBestCommandsForInput(from, to);
		if (commands.isEmpty()) {
			throw new RuntimeException(String.format("%s could not find a best path from '%c' to '%c'", this.name, from, to));
		}
		
		if (commands.size() > 1 && this.controller != null) {
			// Use controller to break tie (three costs theory)
			
			// THIS IS SO DIRTY
			
			Map<String, Integer> costMap = new HashMap<String, Integer>();
			for (String command : commands) {
				char f = 'A';
				int cost = 0;
				String bar = "";
				for (char t : command.toCharArray()) {
					if (f != t) {
						String baz = this.controller.getBestCommandForInput(f, t);
						bar += baz + "/";
						cost += baz.length();
						f = t;
					}
				}
//				System.out.println(command + " -> " + bar + " (" + String.valueOf(cost) + ")");
				costMap.put(command, cost);
			}
			
			// ...and it does nothing. As suspected, controller costs are the same between both options
			
			return commands.iterator().next(); // TODO
		} else {
			// Arbitrarily choosing; they should be equal length
			return commands.iterator().next();
		}
	}
	
	// Return commands with the fewest direction changes that still represent a valid path
	private Set<String> getBestCommandsForInput(char from, char to) {
		Set<String> result = new HashSet<String>();
		Collection<List<Dir>> paths = this.operatedKeypad().getShortestValidPaths(from, to);
		if (!paths.isEmpty()) {
			Iterator<List<Dir>> it = paths.iterator();
			while (it.hasNext()) {
				List<Dir> path = it.next();
				StringBuilder strb = new StringBuilder();
				for (Dir d : path) {
					strb.append(Command.getCommandByDir(d).c);
				}
				strb.append(Command.ACT.c);
				result.add(strb.toString());
			}
		} else {
			throw new RuntimeException(String.format("%s's operated keypad did not return any valid paths from '%c' to '%c'", this.name, from, to));
		}
		return result;
	}
	
	@Override
	public String toString() {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		
		pw.println("Robot: " + this.name);
		
		pw.print("Operated keypad: ");
		if (this.operatedKeypad() != null) {
			pw.print(this.operatedKeypad().getName());
		} else {
			pw.print("none");
		}
		pw.println();
		
		pw.print("Controlled by: ");
		if (this.controller != null) {
			pw.print(this.controller.name);
		} else {
			pw.print("none");
		}
		pw.println();
		
		return sw.toString();
	}
}
