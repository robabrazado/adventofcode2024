package com.robabrazado.aoc2024.day07;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Equationator {
	private final List<List<BigInteger>> equations;
	
	public Equationator(Stream<String> puzzleInput) {
		List<List<BigInteger>> tempEquations = new ArrayList<List<BigInteger>>();
		Pattern p = Pattern.compile("\\d+");
		
		puzzleInput.forEach((String s) -> {
			List<BigInteger> equation = new ArrayList<BigInteger>();
			Matcher m = p.matcher(s);
			while (m.find()) {
				equation.add(new BigInteger(m.group()));
			}
			tempEquations.add(equation);
		});
		
		this.equations = Collections.unmodifiableList(tempEquations);
		
		
		return;
	}
	
	public String solve(boolean partOne) {
		BigInteger runningTotal = BigInteger.ZERO; 
		
		Map<Integer, List<List<Operator>>> operatorLists = new HashMap<Integer, List<List<Operator>>>(); // Local cache
		
		for (List<BigInteger> equation : this.equations) {
			List<BigInteger> operands = new ArrayList<BigInteger>(equation);
			// Pop the first element as the expected result
			BigInteger answer = operands.remove(0);
			
			// Get the possible operator combinations
			EnumSet<Operator> useOperators = EnumSet.allOf(Operator.class);
			if (partOne) {
				useOperators.remove(Operator.CONCAT);
			}
			List<List<Operator>> opCombinations = Equationator.getOperatorCombinations(operands.size() - 1, operatorLists, useOperators);
			
			// Try operator combinations until you find the right one (or not)
			BigInteger correctResult = null;
			int numCombinations = opCombinations.size();
			for (int i = 0; i < numCombinations && correctResult == null; i++) {
				BigInteger tempResult = Equationator.evaluate(operands, opCombinations.get(i), answer);
				if (tempResult != null && tempResult.equals(answer)) {
					correctResult = tempResult;
				}
			}
			if (correctResult != null) {
				runningTotal = runningTotal.add(correctResult);
			} // else no correct combination found
			
		}
		
		return runningTotal.toString();
	}
	
	public static BigInteger evaluate(List<BigInteger> operands, List<Operator> operators) {
		return Equationator.evaluate(operands, operators, (BigInteger) null);
	}
	
	// Assuming operators can only increase values, will abandon evaluation and return null if interstitial value exceeds expected answer
	// Null expected answer just means return answer no matter what
	public static BigInteger evaluate(List<BigInteger> operands, List<Operator> operators, BigInteger expectedAnswer) {
		List<BigInteger> rands = new ArrayList<BigInteger>(operands);
		List<Operator> rators = new ArrayList<Operator>(operators);
		BigInteger result = null;
		int numOperands = rands.size();
		
		if (numOperands != rators.size() + 1) {
			throw new IllegalArgumentException("Incompatible operands/operators list sizes");
		} else if (numOperands == 1) {
			result = rands.get(0);
		} else {
			// Replace first two operands with result of first operator expression, then repeat
			BigInteger answer = rators.get(0).operate(rands.get(0), rands.get(1));
			
			if (expectedAnswer != null) {
				// Check if this is never going to happen
				if (answer.compareTo(expectedAnswer) > 0) {
					result = null;
				} else {
					rands.remove(0);
					rands.set(0, answer);
					rators.remove(0);
					
					result = Equationator.evaluate(rands, rators, expectedAnswer);
				}
			}
		}
		return result;
	}
	
	// I'm sure there's a better way to do this
	public static List<List<Operator>> getOperatorCombinations(int numOperators, Map<Integer, List<List<Operator>>> combinations,
			EnumSet<Operator> useOperators) {
		if (!combinations.containsKey(numOperators)) {
			List<List<Operator>> newLists = new ArrayList<List<Operator>>();
			
			if (numOperators < 1) {
				throw new IllegalArgumentException("Can only generate combinations for 1 or more operators");
			} else {
				List<List<Operator>> baseLists = null;
				if (numOperators == 1) {
					baseLists = new ArrayList<List<Operator>>();
					baseLists.add(new ArrayList<Operator>());
				} else {
					baseLists = Equationator.getOperatorCombinations(numOperators - 1, combinations, useOperators);
				}
				
				for (Operator op : useOperators) {
					List<List<Operator>> oldLists = new ArrayList<List<Operator>>(baseLists);
					for (List<Operator> oldList : oldLists) {
						List<Operator> newList = new ArrayList<Operator>(oldList);
						newList.add(op);
						newLists.add(newList);
					}
				}
				combinations.put(numOperators, newLists);
			}
		}
		return combinations.get(numOperators);
	}

}
