package com.robabrazado.aoc2024.day11;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StoneBlinker {
	private static final BigInteger BIG2024 = BigInteger.valueOf(2024);
	
	private final List<BigInteger> initialStones = new ArrayList<BigInteger>();
	private final Map<StoneState, BigInteger> countMap = new HashMap<StoneState, BigInteger>();
	
	public StoneBlinker(Stream<String> puzzleInput) {
		String[] stoneStrings = puzzleInput.iterator().next().split(" ");
		for (String s : stoneStrings) {
			this.initialStones.add(new BigInteger(s));
		}
	}
	
	public BigInteger stoneCountAfterBlinks(int numBlinks) {
		BigInteger result = BigInteger.ZERO;
		List<StoneState> leftToCount = new ArrayList<StoneState>();
		
		for (BigInteger stone : this.initialStones) {
			leftToCount.add(new StoneState(stone, numBlinks));
		}
		
		while (!leftToCount.isEmpty()) {
			result = result.add(this.getCount(leftToCount.remove(0)));
		}
		
		return result;
	}
	
	public BigInteger getCount(StoneState state) {
		if (!this.countMap.containsKey(state)) {
			BigInteger thisCount;
			int numBlinks = state.blinksLeft;
			if (numBlinks == 0) {
				thisCount = BigInteger.ONE;
			} else if (numBlinks > 0) {
				List<StoneState> nextSteps = StoneBlinker.blink(state);
				thisCount = this.getCount(nextSteps.get(0));
				if (nextSteps.size() > 1) {
					thisCount = thisCount.add(this.getCount(nextSteps.get(1)));
				}
			} else {
				throw new IllegalArgumentException("Can't count negative blinks");
			}
			this.countMap.put(state, thisCount);
		}
		return this.countMap.get(state);
	}
	
	@Override
	public String toString() {
		return String.join(" ",
				this.initialStones.stream()
				.map(BigInteger::toString)
				.collect(Collectors.toList()));
	}
	
	private static List<StoneState> blink(StoneState state) {
		List<StoneState> result = new ArrayList<StoneState>();
		int blinksLeft = state.blinksLeft;
		int newBlinks = blinksLeft - 1;
		BigInteger value = state.stoneValue;
		
		if (blinksLeft < 1) {
			throw new IllegalArgumentException("This stone has " + String.valueOf(blinksLeft) + " blinks left; need at least 1");
		}
		
		if (value.equals(BigInteger.ZERO)) {
			result.add(new StoneState(BigInteger.ONE, newBlinks));
		} else {
			String s = value.toString();
			int len = s.length();
			if (len % 2 == 0) {
				int halfLen = len / 2;
				result.add(new StoneState(new BigInteger(s.substring(0, halfLen)), newBlinks));
				result.add(new StoneState(new BigInteger(s.substring(halfLen)), newBlinks));
			} else {
				result.add(new StoneState(value.multiply(BIG2024), newBlinks));
			}
		}
		
		return result;
	}
	
	private record StoneState(BigInteger stoneValue, int blinksLeft) {}
}
