package com.robabrazado.aoc2024.day13;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.robabrazado.aoc2024.grid.Coords;

public class ClawMachine {
	private static final Pattern PATTERN_A = Pattern.compile("Button A: X\\+(\\d+), Y\\+(\\d+)");
	private static final Pattern PATTERN_B = Pattern.compile("Button B: X\\+(\\d+), Y\\+(\\d+)");
	private static final Pattern PATTERN_PRIZE = Pattern.compile("Prize: X=(\\d+), Y=(\\d+)");
	
	private final Coords offsetA;
	private final Coords offsetB;
	private final Coords prize;
	
	private ClawMachine(Coords offsetA, Coords offsetB, Coords prize) {
		this.offsetA = offsetA;
		this.offsetB = offsetB;
		this.prize = prize;
		return;
	}
	
	// Returns -1 if prize unreachable
	public int getLowestPrizeCost() {
		int result = -1;
		
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
		int nbNumerator = (this.offsetA.getCol() * this.prize.getRow()) - (this.offsetA.getRow() * this.prize.getCol());
		int nbDenominator = (this.offsetA.getCol() * this.offsetB.getRow()) - (this.offsetA.getRow() * this.offsetB.getCol());
		int nb = nbNumerator / nbDenominator; // May not be for real!
		
		if (nb >= 0 && (nbNumerator % nbDenominator == 0)) {
			int naNumerator = (this.prize.getCol() - (this.offsetB.getCol() * nb));
			int naDenominator = this.offsetA.getCol();
			int na = naNumerator / naDenominator;
			
			if (na > 0 && (naNumerator % naDenominator == 0)) {
				result = (na * 3) + nb;
			}
		}
		
		return result;
	}
	
	@Override
	public String toString() {
		return "A: " + this.offsetA + "; B: " + this.offsetB + "; Prize: " + this.prize; 
	}
	
	public static List<ClawMachine> parseClawMachines(Stream<String> puzzleInput) {
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
			
			result.add(new ClawMachine(
					ClawMachine.parseInit(PATTERN_A, buffer[0]),
					ClawMachine.parseInit(PATTERN_B, buffer[1]),
					ClawMachine.parseInit(PATTERN_PRIZE, buffer[2])
			));
			
			if (it.hasNext()) {
				if (!it.next().isEmpty()) {
					throw new RuntimeException("Unexpected non-empty line in puzzle input");
				}
			}
		}
		
		
		return result;
	}
	
	private static Coords parseInit(Pattern p, String s) {
		Matcher m = p.matcher(s);
		if (m.find()) {
			return new Coords(Integer.valueOf(m.group(1)), Integer.valueOf(m.group(2)));
		} else {
			throw new RuntimeException("Unrecognized puzzle input: " + s);
		}
	}
	
	
}
