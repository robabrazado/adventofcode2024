package com.robabrazado.aoc2024.day19;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Stream;

public class TowelArranger {
	private final Set<String> availableTowels;
	private final Set<String> desiredDesigns = new HashSet<String>();
	
	public TowelArranger(Set<String> towels) {
		Set<String> temp = new HashSet<String>();
		if (towels != null && towels.size() > 0) {
			temp = Collections.unmodifiableSet(towels);
		}
		this.availableTowels = temp;
		return;
	}
	
	public void addDesign(String design) {
		if (design != null && !design.isEmpty()) {
			this.desiredDesigns.add(design);
		}
		return;
	}
	
	public int possibleDesignCount() {
		int possibleCounter = 0;
		Map<String, Boolean> knownDesigns = new HashMap<String, Boolean>();
		
		for (String s : this.desiredDesigns) {
			System.out.print("Checking " + s + "...");
			if (this.isDesignPossible(s, knownDesigns)) {
				System.out.println("yes");
				possibleCounter++;
			} else {
				System.out.println("no");
			}
		}
		
		return possibleCounter;
	}
	
	// Tries to match an available towel to the design, then tries to match left and right portions around the match, and so on
	// Passes a cache around
	private boolean isDesignPossible(String design, Map<String, Boolean> knownDesigns) {
		boolean possible = false;
		int designLen = design.length();
		
		if (designLen > 0) {
			if (!knownDesigns.containsKey(design)) {
				// Build search space (trying longest patterns first; why not)
				PriorityQueue<String> towels = new PriorityQueue<String>(Comparator.comparingInt(String::length).reversed());
				for (String s : this.availableTowels) {
					if (s.length() <= designLen && design.contains(s)) {
						towels.add(s);
					}
				}
				
				// Is possible if part before match and part after match are also possible
				while (towels.size() > 0 && !possible) {
					String towel = towels.poll();
					int towelLen = towel.length();
					int matchStart = design.indexOf(towel); // This should be guaranteed at this point, right?
					
					String left = design.substring(0, matchStart);
					String right = design.substring(matchStart + towelLen);
					possible = this.isDesignPossible(left, knownDesigns) &&
							this.isDesignPossible(right, knownDesigns);
				}
				knownDesigns.put(design, Boolean.valueOf(possible));
			}
			possible = knownDesigns.get(design).booleanValue();
		} else {
			// Empty design is always possible
			possible = true;
		}
		
		return possible;
	}
	

	public BigInteger possibleArrangementsCount() {
		BigInteger counter = BigInteger.ZERO;
		Map<String, BigInteger> knownDesignCounts = new HashMap<String, BigInteger>();
		
		for (String s : this.desiredDesigns) {
			System.out.print("Checking " + s + "...");
			BigInteger num = this.possibleArrangementsCount(s, knownDesignCounts);
			System.out.println("found " + String.valueOf(num));
			counter = counter.add(num);
		}
		
		return counter;
	}
	
	/*
	 *  Third try at part 2. Second try was starting from scratch instead of modifying part 1,
	 *  but the new algorithm was flawed. Back to the drawing board.
	 *  
	 *  This time I'm going to try matching from the left of the design and then recursively
	 *  rightward. It doesn't feel like caching is going to help me much on this one, but I'll
	 *  keep it in there anyway. Call it an artifact from previous attempts.
	 *  
	 *  [Later] Well, it passed test data, which is a first. But it overflowed int, so I'll
	 *  rewrite everything with BigInteger. Can the answer really be that high?
	 *  
	 *  [A little later] The answer really was that high. *sigh*
	 */
	private BigInteger possibleArrangementsCount(String design, Map<String, BigInteger> knownDesignCounts) {
		BigInteger result = BigInteger.ZERO;
		int designLen = design.length();
		
		if (designLen > 0) {
			for (String towel : this.availableTowels) {
				if (design.startsWith(towel)) {
					String rightPart = design.substring(towel.length());
					if (!knownDesignCounts.containsKey(rightPart)) {
						knownDesignCounts.put(rightPart, this.possibleArrangementsCount(rightPart, knownDesignCounts));
					}
					result = result.add(knownDesignCounts.get(rightPart));
				}
			}
		} else {
			// Empty design is always one arrangement (no towels)
			result = BigInteger.ONE;
		}
		
		return result;
	}
	
	@Override
	public String toString() {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		
		for (String s : availableTowels) {
			pw.print('[');
			pw.print(s);
			pw.print(']');
		}
		pw.println();
		pw.println();
		for (String s : desiredDesigns) {
			pw.println(s);
		}
		
		return sw.toString();
	}
	
	public static TowelArranger arrangerFromPuzzle(Stream<String> puzzleInput) {
		Iterator<String> it = puzzleInput.iterator();
		
		String[] availArr = it.next().split(", ");
		TowelArranger arranger = new TowelArranger(new HashSet<String>(Arrays.asList(availArr)));
		
		if (!it.next().isEmpty()) {
			throw new RuntimeException("Malformed puzzle input: expected blank line");
		}
		
		while (it.hasNext()) {
			arranger.addDesign(it.next());
		}
		
		return arranger;
	}
	
}
