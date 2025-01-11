package com.robabrazado.aoc2024.day21;

import java.io.PrintWriter;
import java.io.StringWriter;
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
	
	public Starship(Stream<String> puzzleInput) {
		this.doorKeypad = new Keypad("Vacuum Keypad", KeypadType.NUMERIC,
			new Keypad("Radiation Keypad", KeypadType.DIRECTIONAL,
				new Keypad("Frozen Keypad", KeypadType.DIRECTIONAL,
					new Keypad("Crowded Keypad", KeypadType.DIRECTIONAL))));
		
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

	public int getComplexitySum() {
		int result = 0;
		Pattern p = Pattern.compile("^(\\d+)A$");
		for (String code : this.requiredDoorCodes) {
			Matcher m = p.matcher(code);
			int numeric;
			if (m.find()) {
				numeric = Integer.parseInt(m.group(1));
			} else {
				throw new RuntimeException("Could not retrieve numeric portion of malformed door code: " + code);
			}
			int len = this.doorKeypad.getLowestTailCost(code);
			int complexity = numeric * len;
			System.out.format("%s (%d * %d): %d%n", code, len, numeric, complexity);
			result += complexity;
		}
		return result;
	}
}
