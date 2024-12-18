package com.robabrazado.aoc2024.day11;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class StoneBlinker {
	private static final Pattern inputPattern = Pattern.compile("\\d+");
	private final List<BigInteger> stones = new ArrayList<BigInteger>();
	
	public StoneBlinker(Stream<String> puzzleInput) {
		Matcher m = StoneBlinker.inputPattern.matcher(puzzleInput.iterator().next());
		while (m.find()) {
			this.stones.add(new BigInteger(m.group()));
		}
	}
	
	@Override
	public String toString() {
		StringBuilder strb = new StringBuilder();
		if (this.stones.size() > 0) {
			for (BigInteger i : this.stones) {
				strb.append(i).append(' ');
			}
			strb.deleteCharAt(strb.length() - 1);
		}
		return strb.toString();
	}

	public int countAfterBlink(int blinks) {
		BigInteger big2024 = new BigInteger("2024");
		for (int counter = 1; counter <= blinks; counter++) {
			for (int i = 0; i < this.stones.size(); i++) {
				BigInteger me = this.stones.get(i);
				if (me.equals(BigInteger.ZERO)) {
					this.stones.set(i, BigInteger.ONE);
				} else if (me.toString().length() % 2 == 0) {
					String s = me.toString();
					int halfLen = s.length() / 2;
					this.stones.set(i, new BigInteger(s.substring(0, halfLen)));
					this.stones.add(++i, new BigInteger(s.substring(halfLen)));
				} else {
					this.stones.set(i, me.multiply(big2024));
				}
			}
		}
		
		return this.stones.size();
	}
	
}
