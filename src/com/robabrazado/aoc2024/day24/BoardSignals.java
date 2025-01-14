package com.robabrazado.aoc2024.day24;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class BoardSignals {
	private final Map<String, Boolean> signals = new HashMap<String, Boolean>();
	
	public BoardSignals() {
		return;
	}
	
	public BoardSignals(BigInteger xValue, BigInteger yValue, int bitCount) {
		this();
		this.setSignals('x', bitCount, xValue);
		this.setSignals('y', bitCount, yValue);
		return;
	}
	
	public Map<String, Boolean> getSignals() {
		return new HashMap<String, Boolean>(this.signals);
	}
	
	public boolean hasSignal(String wireId) {
		return this.signals.containsKey(wireId);
	}
	
	public boolean getSignal(String wireId) {
		if (!this.signals.containsKey(wireId)) {
			throw new RuntimeException("Wire " + wireId + " has not been intialized");
		}
		return this.signals.get(wireId).booleanValue();
	}
	
	public BoardSignals setSignal(String wireId, boolean signal) {
		this.signals.put(wireId, Boolean.valueOf(signal));
		return this;
	}
	
	public BoardSignals setSignals(char prefix, int bitCount, BigInteger value) {
		if (bitCount < 0) {
			throw new RuntimeException("Bit count cannot be negative");
		} else if (value.shiftRight(bitCount).compareTo(BigInteger.ZERO) > 0) {
			System.err.println("*** Warning: value " + value.toString() + " size exceeds specified bit count " + String.valueOf(bitCount));
		}
		for (int i = 0; i < bitCount; i++) {
			String id = String.format("%c%02d", prefix, i);
			this.setSignal(id, value.testBit(i));
		}
		return this;
	}
	
	public BigInteger getSignalsValue(char prefix) {
		BigInteger result = BigInteger.ZERO;
		List<String> ids = this.signals.keySet().stream()
				.filter(s -> s.charAt(0) == prefix)
				.collect(Collectors.toList());
		if (!ids.isEmpty()) {
			Collections.sort(ids, Comparator.reverseOrder());
			result = new BigInteger(this.getBitString(ids).toString(), 2);
		}
		return result;
	}
	
	public BoardSignals copy() {
		BoardSignals copy = new BoardSignals();
		copy.signals.putAll(this.signals);
		return copy;
	}
	
	public String getBitString(List<String> ids) {
		StringBuilder strb = new StringBuilder();
		for (String id : ids) {
			strb.append(this.signals.get(id) ? '1' : '0');
		}
		return strb.toString();
	}
	
	@Override
	public String toString() {
		StringBuilder strb = new StringBuilder();
		Set<String> allIds = this.signals.keySet();
		
		
		if (!allIds.isEmpty()) {
			List<String> xIds = new ArrayList<String>();
			List<String> yIds = new ArrayList<String>();
			List<String> zIds = new ArrayList<String>();
			List<String> otherIds = new ArrayList<String>();
			
			for (String id : allIds) {
				if (id.startsWith("x")) {
					xIds.add(id);
				} else if (id.startsWith("y")) {
					yIds.add(id);
				} else if (id.startsWith("z")) {
					zIds.add(id);
				} else {
					otherIds.add(id);
				}
			}
			Collections.sort(xIds, Comparator.reverseOrder());
			Collections.sort(yIds, Comparator.reverseOrder());
			Collections.sort(otherIds);
			Collections.sort(zIds, Comparator.reverseOrder());
			
			strb.append(String.format("x (%d bit(s)): ", xIds.size()));
			if (!xIds.isEmpty()) {
				String s = this.getBitString(xIds);
				strb.append(s).append(" (").append(new BigInteger(s, 2).toString()).append(")");
			}
			strb.append(String.format("; y (%d bit(s)): ", yIds.size()));
			if (!yIds.isEmpty()) {
				String s = this.getBitString(yIds);
				strb.append(s).append(" (").append(new BigInteger(s, 2).toString()).append(")");
			}
			
			if (!otherIds.isEmpty()) {
				strb.append("; other: ").append(
						otherIds.stream()
							.map(id -> id + "=" + (this.signals.get(id) ? '1' : '0'))
							.collect(Collectors.joining("; ")));
			}
			
			if (!zIds.isEmpty()) {
				strb.append(String.format("; z (%d bit(s)): ", zIds.size()));
				String s = this.getBitString(zIds);
				strb.append(s).append(" (").append(new BigInteger(s, 2).toString()).append(")");
			}
		} else {
			strb.append("[EMPTY]");
		}
		
		return strb.toString();
	}
	
	public String status() {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		Set<String> ids = new TreeSet<String>(this.signals.keySet());
		for (String id : ids) {
			pw.format("%s=%c%n", id, this.signals.get(id) ? '1' : '0');
		}
		return sw.toString();
	}
	
}
