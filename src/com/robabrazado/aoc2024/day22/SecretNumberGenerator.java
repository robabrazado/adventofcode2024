package com.robabrazado.aoc2024.day22;

// This is WILDLY not threadsafe. I don't know why I suddenly care, but it seems glaring in this class.
public class SecretNumberGenerator {
	private static final long PRUNE_MASK = (1 << 24) - 1;
	
	private final long first;
	
	private long current;
	
	public SecretNumberGenerator(long seed) {
		this.first = seed;
		this.current = seed;
		return;
	}
	
	public long getCurrentValue() {
		return this.current;
	}
	
	// Advances current value one step and returns it
	public long next() {
		// Multiply by 64, mix, and prune
		this.mixAndPrune(this.current << 6);
		
		// Divide by 32, mix, and prune
		this.mixAndPrune(this.current >> 5);
		
		// Multiply by 2048, mix, and prune
		this.mixAndPrune(this.current << 11);
		
		return this.current;
	}
	
	// Advances current value specified number of steps and returns it
	public long next(long num) {
		if (num < 0) {
			throw new IllegalArgumentException("Cannot advance negative steps");
		}
		
		long result = this.current;
		for (int i = 1; i <= num; i++) {
			result = this.next();
		}
		return result;
	}
	
	// Restores current value to starting value and returns it
	public long reset() {
		this.current = this.first;
		return this.current;
	}
	
	private long mixAndPrune(long mixValue) {
		this.current = (mixValue ^ this.current) & (PRUNE_MASK);
		return this.current;
	}
}
