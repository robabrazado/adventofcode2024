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

public class BoardInput {
	private final Map<String, Boolean> inputStates = new HashMap<String, Boolean>();
	
	public BoardInput() {
		
	}
	
	public boolean hasInputSignal(String wireId) {
		return this.inputStates.containsKey(wireId);
	}
	
	public boolean getInputSignal(String wireId) {
		if (!this.inputStates.containsKey(wireId)) {
			throw new RuntimeException("Wire " + wireId + " has not been intialized");
		}
		return this.inputStates.get(wireId).booleanValue();
	}
	
	public void setInputSignal(String wireId, boolean signal) {
		this.inputStates.put(wireId, Boolean.valueOf(signal));
		return;
	}
	
	@Override
	public String toString() {
		StringBuilder strb = new StringBuilder();
		Set<String> allIds = this.inputStates.keySet();
		
		
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
							.map(id -> id + "=" + (this.inputStates.get(id) ? '1' : '0'))
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
		Set<String> ids = new TreeSet<String>(this.inputStates.keySet());
		for (String id : ids) {
			pw.format("%s=%c%n", id, this.inputStates.get(id) ? '1' : '0');
		}
		return sw.toString();
	}
	
	// Destructive to strb and ids
	private void appendBits(StringBuilder strb, List<String> ids) {
		Collections.sort(ids, Comparator.reverseOrder());
		for (String id : ids) {
			strb.append(this.inputStates.get(id) ? '1' : '0');
		}
		return;
	}
	
}
