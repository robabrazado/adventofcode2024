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
	
	public CommandGenerator getCommandGeneratorForHeadInput(String headInput) {
		if (this.worker != null) {
			List<PathGenerator> pathGens = new ArrayList<PathGenerator>();
			Iterator<String> myInputIt = this.worker.getCommandGeneratorForHeadInput(headInput).commandIterator();
			while (myInputIt.hasNext()) {
				pathGens.addAll(this.getCommandGeneratorForInput(myInputIt.next()).getPathGenerators());
			}
			return new CommandGenerator(pathGens);
		} else {
			// I AM HEAD
			return this.getCommandGeneratorForInput(headInput);
		}
	}
	
	public CommandGenerator getCommandGeneratorForInput(String input) {
		List<PathGenerator> pathGenerators = new ArrayList<PathGenerator>();
		
		if (input != null && !input.isEmpty()) {
			char from = 'A';
			for (char to : input.toCharArray()) {
				pathGenerators.add(this.operatedKeypad().getKeystrokePathGenerator(from, to));
				from = to;
			}
		}
		return new CommandGenerator(pathGenerators);
	}
	
	public String getBestCommandForHeadInput(String headInput) {
		
		throw new RuntimeException("Not yet implemented"); // TODO
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
	
	// Either the worker keypad or the nonworker keypad, as appropriate
	private Keypad operatedKeypad() {
		Keypad result = this.nonWorkerOperatedKeypad;
		if (this.worker != null) {
			result = worker.commandKeypad;
		}
		if (result != null) {
			return result;
		} else {
			// This is considered a throwable error state; constructors didn't enforce or something else codewise is wrong
			throw new IllegalStateException("Internal error: " + this.name + " is not operating a keypad");
		}
	}
}
