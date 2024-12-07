package com.robabrazado.aoc2024.day07;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;

public enum Operator {
	SUM			("add", false),
	MULTIPLY	("multiply", false),
	CONCAT		("concatenate", true);
	
	private final Method op;
	private final String customOpName;
	
	Operator(String methodName, boolean custom) {
		if (custom) {
			this.customOpName = methodName;
			this.op = null;
		} else {
			try {
				this.op = BigInteger.class.getDeclaredMethod(methodName, BigInteger.class);
			} catch (NoSuchMethodException | SecurityException e) {
				throw new RuntimeException(e);
			}
			this.customOpName = null;
		}
	}
	
	
	public BigInteger operate(BigInteger left, BigInteger right) {
		BigInteger result = null;
		if (customOpName == null) {
			try {
				result = (BigInteger) this.op.invoke(left, right);
			} catch (IllegalAccessException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		} else {
			if ("concatenate".equals(this.customOpName)) {
				result = new BigInteger(left.toString() + right.toString()); // Better hope "right" is never negative, I guess!
			} else {
				throw new IllegalStateException("Unrecognized custom operator name: " + this.customOpName);
			}
		}
		return result;
	}
}
