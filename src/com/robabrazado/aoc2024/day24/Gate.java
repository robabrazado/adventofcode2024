package com.robabrazado.aoc2024.day24;

class Gate {
	private final GateType type;
	private final Wire input1;
	private final Wire input2;
	private final Wire output;
	
	private Gate(GateType gateType, Wire input1, Wire input2, Wire output) {
		if (input1 == null || input2 == null || output == null) {
			throw new IllegalArgumentException("Gates must have all inputs and outputs connected");
		}
		this.input1 = input1;
		this.input2 = input2;
		this.output = output;
		this.type = gateType;
		return;
	}
	
	Wire getInputWire1() {
		return this.input1;
	}
	
	Wire getInputWire2() {
		return this.input2;
	}
	
	Wire getOutputWire() {
		return this.output;
	}
	
	// Throws exception if input signals not available
	// Destructive to input
	boolean getOutput(BoardSignals input) {
		switch (this.type) {
		case AND:
			return this.input1.getValue(input) && this.input2.getValue(input);
		case OR:
			return this.input1.getValue(input) || this.input2.getValue(input);
		case XOR:
			return this.input1.getValue(input) ^ this.input2.getValue(input);
		default: // Uh oh
			throw new RuntimeException("Unsupported gate type");
		}
	}
	
	@Override
	public String toString() {
		return String.format("%s %s %s -> %s",
				this.input1.getId(),
				this.type.name(),
				this.input2.getId(),
				this.output.getId());
	}
	
	static Gate connectGate(GateType gateType, Wire toInput1, Wire toInput2, Wire fromOutput) {
		Gate gate = new Gate(gateType, toInput1, toInput2, fromOutput);
		toInput1.connectToGateInput(gate);
		toInput2.connectToGateInput(gate);
		fromOutput.connectToGateOutput(gate);
		return gate;
	}
	
	enum GateType {
		AND		(),
		OR		(),
		XOR		();
		
	}
}
