package com.robabrazado.aoc2024.day24;

import java.util.HashSet;
import java.util.Set;

class Wire {
	private final String id;
	
	private Boolean signal = null;
	private Gate.OutputLead input = null; // Zero or one input
	private Set<Gate.InputLead> outputs = new HashSet<Gate.InputLead>(); // Zero or more outputs
	
	public Wire(String id) {
		if (id == null || id.isEmpty()) {
			throw new IllegalArgumentException("Wire must have a valid ID");
		}
		this.id = id;
		return;
	}
	
	public String getId() {
		return this.id;
	}
	
	public Gate.OutputLead getInput() {
		return this.input;
	}
	
	public void setInput(Gate.OutputLead lead) {
		this.checkLeadOrThrow(lead, false);
		this.input = lead;
		return;
	}
	
	Set<Gate.InputLead> getOutputs() {
		return new HashSet<Gate.InputLead>(this.outputs);
	}
	
	public void addOutput(Gate.InputLead lead) {
		this.checkLeadOrThrow(lead, true);
		this.outputs.add(lead);
		return;
	}
	
	private void checkLeadOrThrow(Gate.Lead lead, boolean canBeNull) {
		if ((canBeNull || lead != null) && (!lead.isWireConnected() || !lead.getWire().getId().equals(this.id))) {
			throw new IllegalArgumentException("Specified lead is not connected to wire " + this.id);
		}
	}
	
	public boolean hasSignal() {
		return this.signal != null;
	}
	
	// Throws if no signal set
	public boolean getSignal() {
		if (this.signal == null) {
			throw new IllegalStateException("Wire " + this.id + " has no signal");
		}
		return this.signal.booleanValue();
	}
	
	public void setSignal(boolean signal) {
		this.signal = Boolean.valueOf(signal);
		for (Gate.InputLead leadOut : this.outputs) {
			leadOut.getGate().signalReady();
		}
		return;
	}
	
	public void clearSignal() {
		this.signal = null;
		return;
	}
	
	@Override
	public String toString() {
		StringBuilder strb = new StringBuilder();
		strb.append(this.id).append(' ');
		strb.append(this.input == null ? '0' : '1').append(" input(s); ");
		strb.append(this.outputs.size()).append(" output(s)");
		
		return strb.toString();
	}
}
