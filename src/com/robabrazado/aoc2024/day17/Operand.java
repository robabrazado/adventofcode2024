package com.robabrazado.aoc2024.day17;

import java.math.BigInteger;

public class Operand extends BigInteger {
	private static final long serialVersionUID = 1L;

	private Operand(BigInteger v) {
		super(v.toByteArray());
	}
	
	// Will always be 0-7
	public static Operand getLiteralOperand(Integer value) {
		if (value == null) {
			throw new IllegalArgumentException("Operand cannot have null value");
		}
		return new Operand(BigInteger.valueOf(value));
	}
	
	// Could be anything, including possible int overflow?!
	public static Operand getComboOperand(Integer value, Computer computer) {
		if (value == null) {
			throw new IllegalArgumentException("Operand cannot have null value");
		}
		if (value >= 0 && value <= 3) {
			return Operand.getLiteralOperand(value);
		} else if (value == 4) {
			return new Operand(computer.a());
		} else if (value == 5) {
			return new Operand(computer.b());
		} else if (value == 6) {
			return new Operand(computer.c());
		} else {
			throw new IllegalArgumentException("Unrecognize opcode: " + String.valueOf(value));
		}
	}
}
