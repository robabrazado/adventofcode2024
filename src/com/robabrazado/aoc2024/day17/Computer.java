package com.robabrazado.aoc2024.day17;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Computer {
	private static final BigInteger MAX_VALUE = BigInteger.valueOf(Integer.MAX_VALUE);
	private static final BigInteger BIG8 = BigInteger.valueOf(8);
	private static final Pattern REGISTER_PATTERN = Pattern.compile("^Register ([ABC]): (\\d+)$");
	private static final String PROGRAM_INPUT_START = "Program: ";
	
	private BigInteger registerA = null;
	private BigInteger registerB = null;
	private BigInteger registerC = null;
	
	public Computer() {
		
	}
	
	public void initialize(BigInteger a, BigInteger b, BigInteger c) {
		if (a == null || b == null || c == null) {
			throw new IllegalArgumentException("Computer cannot be initialized with null values");
		}
		this.registerA = a;
		this.registerB = b;
		this.registerC = c;
		return;
	}
	
	BigInteger a() {
		return this.registerA;
	}
	
	BigInteger b() {
		return this.registerB;
	}
	
	BigInteger c() {
		return this.registerC;
	}
	
	public List<Integer> executeProgram(List<Integer> program) {
		List<Integer> output = new ArrayList<Integer>();
		Operator[] operators = Operator.values();
		int pointer = 0;
		int programLen = program != null ? program.size() : 0;
		
		while (pointer < programLen) {
			Operator operator = operators[program.get(pointer++)];
			Integer input = program.get(pointer++);
			
			switch (operator) {
			case ADV:
				this.registerA = this.registerA.divide(BigInteger.TWO.pow(this.getSafeCombo(input)));
				break;
			case BXL:
				this.registerB = this.registerB.xor(Operand.getLiteralOperand(input));
				break;
			case BST:
				this.registerB = Operand.getComboOperand(input, this).remainder(BIG8);
				break;
			case JNZ:
				if (!this.registerA.equals(BigInteger.ZERO)) {
					pointer = Operand.getLiteralOperand(input).intValue();
				}
				break;
			case BXC:
				this.registerB = this.registerB.xor(this.registerC);
				break;
			case OUT:
				output.add(Operand.getComboOperand(input, this).remainder(BIG8).intValue());
				break;
			case BDV:
				this.registerB = this.registerA.divide(BigInteger.TWO.pow(this.getSafeCombo(input)));
				break;
			case CDV:
				this.registerC = this.registerA.divide(BigInteger.TWO.pow(this.getSafeCombo(input)));
				break;
			default:
				throw new RuntimeException("Unsupported operator: " + operator.name());
			}
		}
		
		return output;
	}
	
	private int getSafeCombo(int input) {
		BigInteger result = Operand.getComboOperand(input, this);
		if (result.compareTo(MAX_VALUE) > 0) {
			// Probably (hopefully?!) this never comes up, but just to be safe...
			throw new RuntimeException("Unsupported exponent larger than " + MAX_VALUE.toString());
		}
		return result.intValue();
	}
	
	// Initializes computer and runs program as specified by puzzle input
	// Returns comma-delimited String of program output
	public static String part1(Stream<String> puzzleInput) {
		Computer computer = new Computer();
		ParsedPuzzle parsed = Computer.parsePuzzleInput(puzzleInput);
		
		computer.registerA = parsed.a;
		computer.registerB = parsed.b;
		computer.registerC = parsed.c;
		
		return Computer.formatOutput(computer.executeProgram(Computer.stringToProgram(parsed.programString)));
	}
	
	/*
	 * This might be highly specialized for my puzzle input; there's no way it's a general case solution.
	 * I didn't have the brain power to actually write a reverse interpreter or whatever I'd need to run
	 * the program backwards to get the right inputs for a given answer, but in examining the program for
	 * myself, I got an idea of how it works and figured I could at least narrow the search space. Broadly,
	 * there's one output per loop, so that let me figure out how many loops to get the target output. The
	 * looping stops when A holds 0, and at the end of each loop (before jumping), the value of A was
	 * effectively bit-shifted 3 to the right, so it figures the desired input would be no more than 3 bits
	 * per output. In addition, the output was determined primarily by the lowest 3 bits of the A
	 * register (and a varying number of high bits), so I figure 3 bits of search space per output,
	 * which shouldn't take NEARLY as long as the old brute force did. Basically, I go through the output
	 * "backward" to assemble the higher bits of the A input first, because once the high bits are set,
	 * they'll dictate the conditions for searching the low bit space, and so on until all desired
	 * outputs are accounted for.
	 */
	public static String part2(Stream<String> puzzleInput) {
//		System.out.println(Computer.decode(puzzleInput));
		
		Computer computer = new Computer();
		ParsedPuzzle parsed = Computer.parsePuzzleInput(puzzleInput);
		String desiredOutputString = parsed.programString;
		List<Integer> desiredOutput = new ArrayList<Integer>();
		String[] desiredOutputStrings = desiredOutputString.split(",");
		for (String s : desiredOutputStrings) {
			desiredOutput.add(Integer.parseInt(s));
		}
		
		List<BigInteger> baseCandidates = new ArrayList<BigInteger>();
		baseCandidates.add(BigInteger.ZERO);
		
		// Walk through the output in reverse order
		int desiredOutputLen = desiredOutput.size();
		for (int outputIdx = desiredOutputLen - 1; outputIdx >= 0; outputIdx--) {
			List<Integer> checkAgainst = desiredOutput.subList(outputIdx, desiredOutputLen); // The last N outputs (desired output for this "digit" of input)
			List<BigInteger> newBaseCandidates = new ArrayList<BigInteger>();
			while (baseCandidates.size() > 0) {
				BigInteger base = baseCandidates.remove(0);
				for (int i = 0; i < 8; i++) {
					BigInteger testInput = base.multiply(BIG8).add(BigInteger.valueOf(i)); // Shift left and add test digit
					System.out.print("Testing input " + testInput + "...");
					computer.registerA = testInput;
					computer.registerB = parsed.b;
					computer.registerC = parsed.c;
					List<Integer> thisResult = computer.executeProgram(desiredOutput);
					for (int j : thisResult) {
						System.out.print(j);
						System.out.print(',');
					}
					System.out.println();
					if (thisResult.equals(checkAgainst)) {
						newBaseCandidates.add(testInput);
					}
				}
			}
			
			if (newBaseCandidates.size() > 0) {
				baseCandidates = newBaseCandidates;
			} else {
				throw new RuntimeException("Narrow search failed");
			}
		}
		
		// In case of multiple answers, we want the lowest one
		baseCandidates.sort(null);
		
		return baseCandidates.get(0).toString();
	}
	
	// This is just diagnostic so I can get a better look at what's happening in there
	public static String decode(Stream<String> puzzleInput) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		ParsedPuzzle parsed = Computer.parsePuzzleInput(puzzleInput);
		
		pw.println("A: " + parsed.a.toString());
		pw.println("B: " + parsed.b.toString());
		pw.println("C: " + parsed.c.toString());
		pw.println();
		
		pw.println(parsed.programString);
		pw.println();
		
		String[] programStrings = parsed.programString.split(",");
		int len = programStrings.length;
		Operator[] ops = Operator.values();
		for (int i = 0; i < len; i++) {
			pw.print(ops[Integer.parseInt(programStrings[i++])].name());
			pw.print(" ");
			pw.println(programStrings[i]);
		}
		
		return sw.toString();
	}
	
	private static ParsedPuzzle parsePuzzleInput(Stream<String> puzzleInput) {
		Iterator<String> it = puzzleInput.iterator();
		
		BigInteger a = Computer.parseRegister(it.next(), "A");
		BigInteger b = Computer.parseRegister(it.next(), "B");
		BigInteger c = Computer.parseRegister(it.next(), "C");
		
		if (!it.next().isEmpty()) {
			throw new RuntimeException("Malformed puzzle input; expected blank line");
		}
		
		String programLine = it.next();
		String programString = null;
		if (programLine.startsWith(PROGRAM_INPUT_START)) {
			programString = programLine.substring(PROGRAM_INPUT_START.length());
		}
		
		if (programString == null) {
			throw new RuntimeException("Malformed program input: " + programLine);
		}
		
		return new ParsedPuzzle(a, b, c, programString);
	}
	
	private static BigInteger parseRegister(String line, String registerCode) {
		BigInteger result = null;
		Matcher m = REGISTER_PATTERN.matcher(line);
		if (m.find() && m.group(1).equals(registerCode)) {
			result = new BigInteger(m.group(2));
		}
		
		if (result != null) {
			return result;
		} else {
			throw new RuntimeException("Malformed register input for register " + registerCode + ": " + line);
		}
	}
	
	private static List<Integer> stringToProgram(String programString) {
		List<Integer> program = new ArrayList<Integer>();
		String[] programStrings = programString.split(",");
		for (String s : programStrings) {
			program.add(Integer.parseInt(s));
		}
		return program;
	}
	
	private static String formatOutput(List<Integer> output) {
		StringBuilder result = new StringBuilder();
		for (int o : output) {
			result.append(String.valueOf(o)).append(',');
		}
		result.deleteCharAt(result.length() - 1);
		
		return result.toString();
	}
	
	public enum Operator {
		ADV		(),
		BXL		(),
		BST		(),
		JNZ		(),
		BXC		(),
		OUT		(),
		BDV		(),
		CDV		();
	}
	
	private record ParsedPuzzle(BigInteger a, BigInteger b, BigInteger c, String programString) {}
}
