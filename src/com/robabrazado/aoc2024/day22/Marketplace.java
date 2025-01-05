package com.robabrazado.aoc2024.day22;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Marketplace {
	private static final Pattern INPUT_PATTERN = Pattern.compile("^(\\d+)$");
	
	private final List<Buyer> buyers = new ArrayList<Buyer>();
	
	public Marketplace(Stream<String> puzzleInput) {
		Iterator<String> it = puzzleInput.iterator();
		while (it.hasNext()) {
			String line = it.next();
			Matcher m = INPUT_PATTERN.matcher(line);
			if (m.find()) {
				this.buyers.add(new Buyer(Integer.parseInt(m.group(1))));
			} else {
				throw new RuntimeException("Malformed puzzle input (expected number): " + line);
			}
		}
		return;
	}
	
	public BigInteger sumAfter(int count) {
		BigInteger result = BigInteger.ZERO;
		
		for (Buyer buyer : this.buyers) {
			result = result.add(BigInteger.valueOf(buyer.nextSecretNumber(count)));
		}
		
		return result;
	}
	
	public int maxBananaCountAfter(int numSteps) {
		Map<ChangeSequenceLog.Sequence, Integer> sequenceValues = new HashMap<ChangeSequenceLog.Sequence, Integer>();
		
		// All buyers generate their numbers and report their sequence values
		// Report highest accumulation
		int highestValue = 0;
		for (Buyer buyer : this.buyers) {
			buyer.nextSecretNumber(numSteps);
//			System.out.println(buyer + " generated numbers");
			
			Map<ChangeSequenceLog.Sequence, Integer> bidMap = buyer.getBidMap();
			Set<ChangeSequenceLog.Sequence> sequences = bidMap.keySet();
			for (ChangeSequenceLog.Sequence sequence : sequences) {
				int thisValue = bidMap.get(sequence);
				if (!sequenceValues.containsKey(sequence)) {
					sequenceValues.put(sequence, 0);
				}
				int oldValue = sequenceValues.get(sequence);
				int newValue = oldValue + thisValue;
				sequenceValues.put(sequence, newValue);
				highestValue = Math.max(newValue, highestValue);
			}
//			System.out.format("%s reported sequences (%d); highest value so far %d%n", buyer, sequences.size(), highestValue);
		}
		
		return highestValue;
	}

	@Override
	public String toString() {
		return String.format("Marketplace with %d buyer(s)", this.buyers.size());
	}
	
}
