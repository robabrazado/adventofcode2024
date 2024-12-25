package com.robabrazado.aoc2024.day17;

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
		StringBuilder result = new StringBuilder();
		Computer computer = new Computer();
		Iterator<String> it = puzzleInput.iterator();
		
		computer.registerA = Computer.parseRegister(it.next(), "A");
		computer.registerB = Computer.parseRegister(it.next(), "B");
		computer.registerC = Computer.parseRegister(it.next(), "C");
		
		if (!it.next().isEmpty()) {
			throw new RuntimeException("Malformed puzzle input; expected blank line");
		}
		
		String programLine = it.next();
		if (programLine.startsWith(PROGRAM_INPUT_START)) {
			List<Integer> program = new ArrayList<Integer>();
			String[] programStrings = programLine.substring(PROGRAM_INPUT_START.length()).split(",");
			for (String s : programStrings) {
				program.add(Integer.parseInt(s));
			}
			List<Integer> output = computer.executeProgram(program);
			for (int o : output) {
				result.append(String.valueOf(o)).append(',');
			}
			result.deleteCharAt(result.length() - 1);
		} else {
			throw new RuntimeException("Malformed program input: " + programLine);
		}
		
		return result.toString();
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
}
