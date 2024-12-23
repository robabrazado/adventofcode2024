package com.robabrazado.aoc2024.day13;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class ClawMachine {
	private static final Pattern PATTERN_A = Pattern.compile("Button A: X\\+(\\d+), Y\\+(\\d+)");
	private static final Pattern PATTERN_B = Pattern.compile("Button B: X\\+(\\d+), Y\\+(\\d+)");
	private static final Pattern PATTERN_PRIZE = Pattern.compile("Prize: X=(\\d+), Y=(\\d+)");
	private static final BigInteger PART_TWO_ADDITIONAL = new BigInteger("10000000000000");
	
	// Thanks to part 2, I can't use Coords for these anymore
	private final BigTuple offsetA;
	private final BigTuple offsetB;
	private final BigTuple prize;
	
	private ClawMachine(BigTuple offsetA, BigTuple offsetB, BigTuple prize) {
		this.offsetA = offsetA;
		this.offsetB = offsetB;
		this.prize = prize;
		return;
	}
	
	// Returns -1 if prize unreachable
	public BigInteger getLowestPrizeCost() {
		BigInteger result = BigInteger.ONE.negate();
		
		/*
		 * This is just math, right? We've got two independent variables (the number of presses
		 * of each button) and two equations (the prize coordinates).
		 * 
		 * Let Button A consist of Ax and Ay, the x and y offsets.
		 * Let Button B consist of Bx and By.
		 * The prize is at coordinates Px and Py.
		 * Let Na be a number of Button A presses and Nb be the number of Button B presses.
		 * The claw will be over the prize when these two equations are satisfied:
		 * 
		 * (Ax)(Na) + (Bx)(Nb) = Px and (Ay)(Na) + (By)(Nb) = Py
		 * 
		 * The left equation in terms of Na is
		 * 
		 * Na = (Px - (Bx)(Nb)) / Ax
		 * 
		 * Substituting for Na in the right equation gives
		 * 
		 * (Ay)((Px - (Bx)(Nb)) / Ax) + (By)(Nb) = Py
		 * 
		 * Solving for Nb gives
		 * 
		 * (Ay)(Px - (Bx)(Nb)) + (Ax)(By)(Nb) = (Ax)(Py)            multiply both sides by Ax
		 * (Ay)(Px) - (Ay)(Bx)(Nb) + (Ax)(By)(Nb) = (Ax)(Py)        distribute Ay over the first term
		 * (Ax)(By)(Nb) - (Ay)(Bx)(Nb) = (Ax)(Py) - (Ay)(Px)        subtract (Ay)(Px) from both sides; rearrange Nb terms
		 * ((Ax)(By) - (Ay)(Bx))(Nb) = (Ax)(Py) - (Ay)(Px)          simplify Nb terms
		 * Nb = ((Ax)(Py) - (Ay)(Px)) / ((Ax)(By) - (Ay)(Bx))       solve for Nb
		 * 
		 * So we can get Nb, which means we can get Na. If both Na and Nb are non-negative integers,
		 * we can claim the prize and know how much it costs (3Na + Nb).
		 */
		BigInteger nbNumerator = this.offsetA.x.multiply(this.prize.y).subtract(this.offsetA.y.multiply(this.prize.x));
		BigInteger nbDenominator = this.offsetA.x.multiply(this.offsetB.y).subtract(this.offsetA.y.multiply(this.offsetB.x));
		
		if (nbNumerator.remainder(nbDenominator).equals(BigInteger.ZERO)) {
			BigInteger nb = nbNumerator.divide(nbDenominator);
			if (nb.compareTo(BigInteger.ZERO) >= 0) {
				BigInteger naNumerator = this.prize.x.subtract(this.offsetB.x.multiply(nb));
				BigInteger naDenominator = this.offsetA.x;
				
				if (naNumerator.remainder(naDenominator).equals(BigInteger.ZERO)) {
					BigInteger na = naNumerator.divide(naDenominator);
					if (na.compareTo(BigInteger.ZERO) >= 0) {
						result = na.multiply(BigInteger.valueOf(3)).add(nb);
					}
				}
			}
		}
		
		return result;
	}
	
	@Override
	public String toString() {
		return "A: " + this.offsetA + "; B: " + this.offsetB + "; Prize: " + this.prize; 
	}
	
	public static List<ClawMachine> parseClawMachines(Stream<String> puzzleInput, boolean partOne) {
		List<ClawMachine> result = new ArrayList<ClawMachine>();
		Iterator<String> it = puzzleInput.iterator();
		
		// Each machine should be three input lines and a blank line
		String[] buffer = new String[3];
		while (it.hasNext()) {
			for (int i = 0; i < 3; i++) {
				if (it.hasNext()) {
					buffer[i] = it.next();
				} else {
					throw new RuntimeException("Unexpected end of puzzle input");
				}
			}
			
			BigTuple tempPrize = ClawMachine.parseInit(PATTERN_PRIZE, buffer[2]);
			if (!partOne) {
				tempPrize = new BigTuple(tempPrize.x.add(PART_TWO_ADDITIONAL),
						tempPrize.y.add(PART_TWO_ADDITIONAL));
			}
			result.add(new ClawMachine(
					ClawMachine.parseInit(PATTERN_A, buffer[0]),
					ClawMachine.parseInit(PATTERN_B, buffer[1]),
					tempPrize
			));
			
			if (it.hasNext()) {
				if (!it.next().isEmpty()) {
					throw new RuntimeException("Unexpected non-empty line in puzzle input");
				}
			}
		}
		
		
		return result;
	}
	
	private static BigTuple parseInit(Pattern p, String s) {
		Matcher m = p.matcher(s);
		if (m.find()) {
			return new BigTuple(new BigInteger(m.group(1)), new BigInteger(m.group(2)));
		} else {
			throw new RuntimeException("Unrecognized puzzle input: " + s);
		}
	}
	
	private record BigTuple(BigInteger x, BigInteger y) {}
}
