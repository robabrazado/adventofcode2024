package com.robabrazado.aoc2024.day24;

public abstract class Gate {
	private final InputLead input1;
	private final InputLead input2;
	private final OutputLead output;
	
	protected Gate() {
		this.input1 = new InputLead(this);
		this.input2 = new InputLead(this);
		this.output = new OutputLead(this);
		return;
	}
	
	public abstract GateType getType();
	
	protected abstract boolean operate(boolean in1, boolean in2);
	
	public InputLead connectToInput1(Wire wire) {
		this.input1.setWire(wire);
		return this.input1;
	}
	
	public InputLead connectToInput2(Wire wire) {
		this.input2.setWire(wire);
		return this.input2;
	}
	
	public OutputLead connectToOutput(Wire wire) {
		this.output.setWire(wire);
		return this.output;
	}
	
	public void signalReady() {
		if (this.input1.getWire().hasSignal() && this.input2.getWire().hasSignal()) {
			this.output.getWire().setSignal(this.operate(this.input1.getWire().getSignal(), this.input2.getWire().getSignal()));
		} else {
			this.output.getWire().clearSignal();
		}
		return;
	}
	
	public static void connectGate(GateType type, Wire toInput1, Wire toInput2, Wire fromOutput) {
		Gate gate = Gate.getGate(type);
		
		gate.input1.setWire(toInput1);
		toInput1.addOutput(gate.input1);
		
		gate.input2.setWire(toInput2);
		toInput2.addOutput(gate.input2);
		
		gate.output.setWire(fromOutput);
		fromOutput.setInput(gate.output);
		
		return;
	}
	
	private static Gate getGate(GateType type) {
		switch (type) {
		case AND:
			return new AndGate();
		case OR:
			return new OrGate();
		case XOR:
			return new XorGate();
		default:
			throw new RuntimeException("Unsupported gate type: " + type.name());
		}
	}
	
	enum GateType {
		AND		(),
		OR		(),
		XOR		();
		
	}
	
	private static class AndGate extends Gate {
		@Override
		public GateType getType() {
			return GateType.AND;
		}

		@Override
		protected boolean operate(boolean in1, boolean in2) {
			return in1 && in2;
		}
	}
	
	private static class OrGate extends Gate {
		@Override
		public GateType getType() {
			return GateType.OR;
		}

		@Override
		protected boolean operate(boolean in1, boolean in2) {
			return in1 || in2;
		}
	}
	
	private static class XorGate extends Gate {
		@Override
		public GateType getType() {
			return GateType.XOR;
		}

		@Override
		protected boolean operate(boolean in1, boolean in2) {
			return in1 ^ in2;
		}
	}
	
	public abstract class Lead {
		private final Gate gate;
		private Wire wire;
		
		protected Lead(Gate gate) {
			if (gate == null) {
				throw new IllegalArgumentException("Lead must be Connected to a non-null Gate");
			}
			this.gate = gate;
			return;
		}
		
		public Gate getGate() {
			return this.gate;
		}
		
		public boolean isWireConnected() {
			return this.wire != null;
		}
		
		// Throws if not connected
		public Wire getWire() {
			this.checkWireOrThrow();
			return this.wire;
		}
		
		public void setWire(Wire wire) {
			if (wire == null) {
				throw new IllegalArgumentException("Lead must be connected to a non-null Wire");
			}
			this.wire = wire;
			return;
		}
		
		protected void checkWireOrThrow() {
			if (this.wire == null) {
				throw new IllegalStateException("Lead is not connected to a wire");
			}
			return;
		}
	}
	
	public class InputLead extends Lead {
		private InputLead(Gate gate) {
			super(gate);
			return;
		}
	}
	
	public class OutputLead extends Lead {
		private OutputLead(Gate gate) {
			super(gate);
			return;
		}
	}
}
