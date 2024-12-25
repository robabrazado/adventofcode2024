package com.robabrazado.aoc2024.day24;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/*
 * A Board is an assemblage of Wires and Gates. The state of the board is activated by
 * setting the value to input wires (wires whose inputs are not connected to any gate
 * outputs), and values can be read from any wire. A wire without a value generally
 * signals that the necessary input wires have not been assigned values or that the
 * Board is misconfigured. 
 */
public class Board {
	private static final Pattern STATE_PATTERN = Pattern.compile("(\\w+): ([01])");
	private static final Pattern GATE_PATTERN = Pattern.compile("(\\w+) (\\w+) (\\w+) -> (\\w+)");
	
	private final Map<String, Wire> wireMap = new HashMap<String, Wire>();
	
	// Instantiates and initializes board to starting state
	public Board(Stream<String> puzzleInput) {
		Iterator<String> it = puzzleInput.iterator();
		String line = null;
		
		// Input until first blank line is initial state
		while (it.hasNext()) {
			line = it.next();
			if (line.isEmpty()) {
				break;
			}
			
			Matcher m = STATE_PATTERN.matcher(line);
			if (m.find()) {
				Wire w = this.getOrCreateWire(m.group(1));
				w.setValue(m.group(2).equals("1"));
			} else {
				throw new RuntimeException("Unrecognized initial state input: " + line);
			}
		}
		
		// Rest of input is board configuration
		while (it.hasNext()) {
			line = it.next();
			Matcher m = GATE_PATTERN.matcher(line);
			if (m.find()) {
				Gate.connectGate(Gate.GateType.valueOf(m.group(2)),
						this.getOrCreateWire(m.group(1)),
						this.getOrCreateWire(m.group(3)),
						this.getOrCreateWire(m.group(4)));
			} else {
				throw new RuntimeException("Unrecognized gate input: " + line);
			}
		}
		
		
	}
	
	public BigInteger getZOutputValue() {
		StringBuilder strb = new StringBuilder();
		Set<String> wireIds = new TreeSet<String>(this.wireMap.keySet());
		for (String s : wireIds) {
			if (s.startsWith("z")) {
				strb.insert(0, this.wireMap.get(s).getValue() ? '1' : '0');
			}
		}
		return new BigInteger(strb.toString(), 2);
	}
	
	@Override
	public String toString() {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		Set<String> wires = new TreeSet<String>(this.wireMap.keySet());
		for (String wire : wires) {
			pw.println(this.wireMap.get(wire));
		}
		return sw.toString();
	}
	
	private Wire getOrCreateWire(String id) {
		if (!this.wireMap.containsKey(id)) {
			this.wireMap.put(id, new Wire(id));
		}
		return this.wireMap.get(id);
	}
}
