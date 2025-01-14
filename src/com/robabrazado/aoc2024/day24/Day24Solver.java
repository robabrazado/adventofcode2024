package com.robabrazado.aoc2024.day24;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.robabrazado.aoc2024.Solver;

// --- Day 24: Crossed Wires ---
/*
 * The object model is probably too convoluted, but (like most things), it made
 * sense at the time. The Solver interacts mostly with the Board. The Board is
 * (primarily) a collection of Wires, and the Wires are related through Gates.
 * Everything has an input and output direction, which is the direction that
 * signal is meant to flow. A Wire is connected to a Gate via leads. A Gate's
 * output is connected to a Wire's input via a Gate.OutputLead, and a Wire's
 * output is connected to a Gate's input via a Gate.InputLead.
 */
public class Day24Solver extends Solver {
	private static final Pattern STATE_PATTERN = Pattern.compile("^(\\w+): ([01])$");
	private static final Pattern GATE_PATTERN = Pattern.compile("^(\\w+) (\\w+) (\\w+) -> (\\w+)$");
	private static final Pattern OUTPUT_PATTERN = Pattern.compile("^[01]+$");
	
	public Day24Solver() {
		super(24);
		return;
	}
	
	@Override
	public String solve(Stream<String> puzzleInput, boolean partOne, boolean isTest) {
		DigestedInput digested = Day24Solver.ingestPuzzleInput(puzzleInput);
//		System.out.println(digested.input().status());
		System.out.println("Input: " + digested.input().toString());
//		System.out.println(digested.board().status());
		System.out.println("Board: " + digested.board().toString());
		
		if (partOne) {
			System.out.println("Before: " + digested.board.getZOutputValuesBitString());
			
			digested.board.setSignals(digested.input);
			String zOutput = digested.board.getZOutputValuesBitString();
			System.out.println("After : " + zOutput);
			
			Matcher m = OUTPUT_PATTERN.matcher(zOutput);
			if (!m.find()) {
				throw new RuntimeException("Output bit string was missing signals; make sure all input signals are in place");
			}
			return new BigInteger(zOutput, 2).toString();
		} else {
			int threshold = isTest ? 2 : 4;
			return digested.board.getSwapString(threshold);
		}
	}
	
	private static DigestedInput ingestPuzzleInput(Stream<String> puzzleInput) {
		Iterator<String> it = puzzleInput.iterator();
		String line = null;
		Board board = new Board();
		BoardSignals boardInput = new BoardSignals();
		
		// Input until first blank line is initial state
		while (it.hasNext()) {
			line = it.next();
			if (line.isEmpty()) {
				break;
			}
			
			Matcher m = STATE_PATTERN.matcher(line);
			if (m.find()) {
				boardInput.setSignal(m.group(1), m.group(2).equals("1"));
			} else {
				throw new RuntimeException("Unrecognized initial state input: " + line);
			}
		}
		
		// Rest of input is board configuration
		while (it.hasNext()) {
			line = it.next();
			Matcher m = GATE_PATTERN.matcher(line);
			if (m.find()) {
				board.connect(m.group(1), Gate.GateType.valueOf(m.group(2)), m.group(3), m.group(4));
			} else {
				throw new RuntimeException("Unrecognized gate input: " + line);
			}
		}
		
		return new DigestedInput(board, boardInput);
	}
	
	private record DigestedInput (Board board, BoardSignals input) {}
}
