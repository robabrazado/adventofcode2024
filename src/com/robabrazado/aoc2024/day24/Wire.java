package com.robabrazado.aoc2024.day24;

import java.util.HashSet;
import java.util.Set;

class Wire {
	private final String id;
	private Gate inputGate = null; // Zero or one input
	private Set<Gate> outputGates = new HashSet<Gate>(); // Zero or more outputs
	
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
	// Destructive to input
	boolean getValue(BoardSignals input) {
		if (!input.hasSignal(this.id)) {
			input.setSignal(this.id, this.inputGate.getOutput(input));
		}
		return input.getSignal(this.id);
	}
	
	boolean isInputWire() {
		return this.inputGate == null;
	}
	
	@Override
	public String toString() {
		StringBuilder strb = new StringBuilder();
		strb.append(this.id).append(' ');
		strb.append(this.inputGate == null ? '0' : '1').append(" input(s); ");
		strb.append(this.outputGates.size()).append(" output(s)");
		
		return strb.toString();
	}
}
