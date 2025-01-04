package com.robabrazado.aoc2024.day22;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Marketplace {
	private static final Pattern INPUT_PATTERN = Pattern.compile("^(\\d+)$");
	
	private final List<SecretNumberGenerator> buyers = new ArrayList<SecretNumberGenerator>();
	
	public Marketplace(Stream<String> puzzleInput) {
		Iterator<String> it = puzzleInput.iterator();
		while (it.hasNext()) {
			String line = it.next();
			Matcher m = INPUT_PATTERN.matcher(line);
			if (m.find()) {
				this.buyers.add(new SecretNumberGenerator(Integer.parseInt(m.group(1))));
			} else {
				throw new RuntimeException("Malformed puzzle input (expected number): " + line);
			}
		}
		return;
	}
	
	public BigInteger sumAfter2000() {
		BigInteger result = BigInteger.ZERO;
		
		for (SecretNumberGenerator buyer : this.buyers) {
			result = result.add(BigInteger.valueOf(buyer.next(2000)));
		}
		
		return result;
	}
	
	@Override
	public String toString() {
		return String.format("Marketplace with %d buyer(s)", this.buyers.size());
	}
}
