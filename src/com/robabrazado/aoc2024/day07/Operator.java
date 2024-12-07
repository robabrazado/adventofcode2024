package com.robabrazado.aoc2024.day07;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;

public enum Operator {
	SUM			("add"),
	MULTIPLY	("multiply");
	
	private final Method op;
	
	Operator(String methodName) {
		try {
			this.op = BigInteger.class.getDeclaredMethod(methodName, BigInteger.class);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	public BigInteger operate(BigInteger left, BigInteger right) {
		try {
			return (BigInteger) this.op.invoke(left, right);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
}
