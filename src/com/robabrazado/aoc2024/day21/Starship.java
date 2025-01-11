package com.robabrazado.aoc2024.day21;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.robabrazado.aoc2024.day21.Keypad.KeypadType;

public class Starship {
	private final Keypad doorKeypad;
	private final List<String> requiredDoorCodes = new ArrayList<String>();
	
	public Starship(Stream<String> puzzleInput, boolean partOne) {
		if (partOne) {
			this.doorKeypad = new Keypad("Vacuum Keypad", KeypadType.NUMERIC,
				new Keypad("Radiation Keypad", KeypadType.DIRECTIONAL,
					new Keypad("Frozen Keypad", KeypadType.DIRECTIONAL,
						new Keypad("Crowded Keypad", KeypadType.DIRECTIONAL))));
		} else {
			// My keypad -> 25 intervening keypads -> door keypad
			Keypad keypad = new Keypad("Player Keypad", KeypadType.DIRECTIONAL);
			for (int i = 1; i <= 25; i++) {
				Keypad newKeypad = new Keypad(String.format("Interstitial Keypad $02d", i), KeypadType.DIRECTIONAL, keypad);
				keypad = newKeypad;
			}
			this.doorKeypad = new Keypad("Door Keypad", KeypadType.NUMERIC, keypad);
		}
		
		Iterator<String> it = puzzleInput.iterator();
		while (it.hasNext()) {
			this.requiredDoorCodes.add(it.next());
		}
	}
	
	@Override
	public String toString() {
		StringBuilder strb = new StringBuilder();
		String separator = " <- ";
		Keypad keypad = this.doorKeypad;
		while (keypad != null) {
			strb.append(keypad.getName()).append(separator);
			keypad = keypad.getController();
		}
		int len = strb.length();
		strb.delete(len - separator.length(), len);
		return strb.toString();
	}
	
	public String status() {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		
		pw.println(this.toString());
		for (String s : this.requiredDoorCodes) {
			pw.println(s);
		}
		
		return sw.toString();
	}

	public BigInteger getComplexitySum() {
		BigInteger result = BigInteger.ZERO;
		Pattern p = Pattern.compile("^(\\d+)A$");
		for (String code : this.requiredDoorCodes) {
			Matcher m = p.matcher(code);
			int numeric;
			if (m.find()) {
				numeric = Integer.parseInt(m.group(1));
			} else {
				throw new RuntimeException("Could not retrieve numeric portion of malformed door code: " + code);
			}
			BigInteger cost = this.doorKeypad.getLowestTailCost(code);
			BigInteger complexity = cost.multiply(BigInteger.valueOf(numeric));
			System.out.format("%s (%s * %d): %s%n", code, cost, numeric, complexity);
			result = result.add(complexity);
		}
		return result;
	}
}
