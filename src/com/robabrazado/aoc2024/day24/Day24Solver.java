package com.robabrazado.aoc2024.day24;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.robabrazado.aoc2024.Solver;

// --- Day 24: Crossed Wires ---
public class Day24Solver extends Solver {
	private static final Pattern STATE_PATTERN = Pattern.compile("^(\\w+): ([01])$");
	private static final Pattern GATE_PATTERN = Pattern.compile("^(\\w+) (\\w+) (\\w+) -> (\\w+)$");
	
	public Day24Solver() {
		super(24);
		return;
	}
	
	@Override
	public String solve(Stream<String> puzzleInput, boolean partOne, boolean isTest) {
		DigestedInput digested = Day24Solver.ingestPuzzleInput(puzzleInput);
		System.out.println(digested.input().status());
		System.out.println(digested.board().status());
		
		if (partOne) {
			BigInteger zOutput = digested.board.getZOutputValue(digested.input);
			System.out.println(zOutput.toString(2));
			return zOutput.toString();
		} else {
			throw new RuntimeException("Not yet implemented"); // TODO
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
