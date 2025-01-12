package com.robabrazado.aoc2024.day24;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Board {
	private final Map<String, Wire> wireMap = new HashMap<String, Wire>();
	
	public Board() {
		return;
	}
	
	public void connect(String input1, Gate.GateType type, String input2, String output) {
		Gate.connectGate(type,
				this.getOrCreateWire(input1),
				this.getOrCreateWire(input2),
				this.getOrCreateWire(output));
		return;
	}
	
	public BigInteger getZOutputValue(BoardSignals input) {
		return this.getZOutputValue(input, false);
	}
	
	public BigInteger getZOutputValue(BoardSignals input, boolean destructiveToInput) {
		if (!destructiveToInput) {
			input = input.copy();
		}
		StringBuilder strb = new StringBuilder();
		Set<String> wireIds = new TreeSet<String>(this.wireMap.keySet());
		for (String s : wireIds) {
			if (s.startsWith("z")) {
				strb.insert(0, this.wireMap.get(s).getValue(input) ? '1' : '0');
			}
		}
		return new BigInteger(strb.toString(), 2);
	}
	
	public String status() {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		Set<String> wires = new TreeSet<String>(new WireIdComparator());
		wires.addAll(this.wireMap.keySet());
		
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
