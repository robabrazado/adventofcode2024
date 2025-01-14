package com.robabrazado.aoc2024.day24;

import java.io.PrintWriter;
import java.io.StringWriter;
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
	
	public void setSignals(BoardSignals input) {
		Map<String, Boolean> inputSignals = input.getSignals();
		Set<String> inputIds = inputSignals.keySet();
		for (String id : inputIds) {
			if (this.wireMap.containsKey(id)) {
				this.wireMap.get(id).setSignal(inputSignals.get(id));
			} else {
				System.err.println("*** Warning: Signal input contains wire with unrecognized ID: " + id);
			}
		}
	}
	
	public String getZOutputValuesBitString() {
		StringBuilder strb = new StringBuilder();
		Set<String> wireIds = new TreeSet<String>(this.wireMap.keySet());
		for (String s : wireIds) {
			if (s.startsWith("z")) {
				Wire zOut = this.wireMap.get(s);
				if (zOut.hasSignal()) {
					strb.insert(0, zOut.getSignal() ? '1' : '0');
				} else {
					strb.insert(0, 'X');
				}
			}
		}
		return strb.toString();
	}
	
	public String getSwapString(int threshold) {
		throw new RuntimeException("Not yet implemented"); // TODO
	}
	
	@Override
	public String toString() {
		int xCount = 0;
		int yCount = 0;
		int zCount = 0;
		int otherCount = 0;
		
		for (String s : this.wireMap.keySet()) {
			switch (s.charAt(0)) {
			case 'x':
				xCount++;
				break;
			case 'y':
				yCount++;
				break;
			case 'z':
				zCount++;
				break;
			default:
				otherCount++;
			}
		}
		
		return String.format("%d x input wires; %d y input wires; %d interstitial wires, %d z output wires",
				xCount, yCount, otherCount, zCount);
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
