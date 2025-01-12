package com.robabrazado.aoc2024.day24;

import java.io.PrintWriter;
import java.io.StringWriter;
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
	
	public boolean hasSignal(String wireId) {
		return this.signals.containsKey(wireId);
	}
	
	public boolean getSignal(String wireId) {
		if (!this.signals.containsKey(wireId)) {
			throw new RuntimeException("Wire " + wireId + " has not been intialized");
		}
		return this.signals.get(wireId).booleanValue();
	}
	
	public void setSignal(String wireId, boolean signal) {
		this.signals.put(wireId, Boolean.valueOf(signal));
		return;
	}
	
	public BoardSignals copy() {
		BoardSignals copy = new BoardSignals();
		copy.signals.putAll(this.signals);
		return copy;
	}
	
	@Override
	public String toString() {
		StringBuilder strb = new StringBuilder();
		Set<String> allIds = this.signals.keySet();
		
		
		if (!allIds.isEmpty()) {
			List<String> xIds = new ArrayList<String>();
			List<String> yIds = new ArrayList<String>();
			List<String> otherIds = new ArrayList<String>();
			
			for (String id : allIds) {
				if (id.startsWith("X")) {
					xIds.add(id);
				} else if (id.startsWith("Y")) {
					yIds.add(id);
				} else {
					otherIds.add(id);
				}
			}
			
			strb.append("X: ");
			if (!xIds.isEmpty()) {
				this.appendBits(strb, xIds);
			}
			strb.append("; Y: ");
			if (!yIds.isEmpty()) {
				this.appendBits(strb, yIds);
			}
			
			if (!otherIds.isEmpty()) {
				Collections.sort(otherIds);
				strb.append("; ").append(
						otherIds.stream()
							.map(id -> id + "=" + (this.signals.get(id) ? '1' : '0'))
							.collect(Collectors.joining("; ")));
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
	
	// Destructive to strb and ids
	private void appendBits(StringBuilder strb, List<String> ids) {
		Collections.sort(ids, Comparator.reverseOrder());
		for (String id : ids) {
			strb.append(this.signals.get(id) ? '1' : '0');
		}
		return;
	}
	
}
