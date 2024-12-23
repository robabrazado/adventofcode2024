package com.robabrazado.aoc2024.day22;

import java.math.BigInteger;


// I guess this is basically just a pared down BigInteger
public class SecretNumber {
	private static final BigInteger BIG64 = BigInteger.valueOf(64);
	private static final BigInteger BIG32 = BigInteger.valueOf(32);
	private static final BigInteger SECRET_MOD = BigInteger.valueOf(16777216);
	private static final BigInteger BIG2048 = BigInteger.valueOf(2048);
	
	private BigInteger value;
	
	public SecretNumber(String initialValue) {
		this.value = new BigInteger(initialValue);
		return;
	}
	
	public BigInteger getValue() {
		return this.value;
	}
	
	public void next() {
		this.value = this.value.multiply(BIG64).xor(this.value).remainder(SECRET_MOD);
		this.value = this.value.divide(BIG32).xor(this.value).remainder(SECRET_MOD);
		this.value = this.value.multiply(BIG2048).xor(this.value).remainder(SECRET_MOD);
		return;
	}
	
	@Override
	public String toString() {
		return this.value.toString();
	}
}
