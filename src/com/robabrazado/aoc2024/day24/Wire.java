package com.robabrazado.aoc2024.day24;

import java.util.HashSet;
import java.util.Set;

class Wire {
	private final String id;
	private Gate inputGate = null; // Zero or one input
	private Set<Gate> outputGates = new HashSet<Gate>(); // Zero or more outputs
	private Boolean value = null;
	
	Wire(String id) {
		if (id == null || id.isEmpty()) {
			throw new IllegalArgumentException("Wire must have a valid ID");
		}
		this.id = id;
		return;
	}
	
	String getId() {
		return this.id;
	}
	
	Gate getInput() {
		return this.inputGate;
	}
	
	// This is the wire's "input"; there can be at most one
	void connectToGateOutput(Gate fromGate) {
		if (this.inputGate == null) {
			this.inputGate = fromGate;
		} else {
			throw new IllegalStateException("A wire can only be connected to one gate output");
		}
	}
	
	Set<Gate> getOutputs() {
		return new HashSet<Gate>(this.outputGates);
	}
	
	// This is the wire's "output"
	void connectToGateInput(Gate toGate) {
		this.outputGates.add(toGate);
		return;
	}
	
	// Throws error if value unavailable
	// Once called, value will remain in wire until cleared
	boolean getValue() {
		if (this.inputGate != null) {
			this.value = this.inputGate.getOutput();
		}
		if (this.value != null) {
			return this.value.booleanValue();
		} else {
			String message = this.inputGate == null ?
					"Input wire " + this.id + " has no value assigned" :
					"Wire " + this.id + " unable to read value from input gate";
			throw new RuntimeException(message);
		}
	}
	
	void setValue(boolean b) {
		if (this.isInputWire()) {
			this.value = Boolean.valueOf(b);
			return;
		} else {
			throw new IllegalStateException("Value cannot be manually set; " + this.id + " is not an input wire");
		}
	}
	
	void clearValue() {
		this.value = null;
	}
	
	boolean isInputWire() {
		return this.inputGate == null;
	}
	
	@Override
	public String toString() {
		StringBuilder strb = new StringBuilder();
		strb.append(this.id).append(" (");
		if (this.value == null) {
			strb.append('X');
		} else {
			strb.append(this.value ? '1' : '0');
		}
		strb.append("); ");
		
		strb.append(this.inputGate == null ? '0' : '1').append(" input; ");
		strb.append(this.outputGates.size()).append(" output(s)");
		
		return strb.toString();
	}
}
