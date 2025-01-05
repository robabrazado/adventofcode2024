package com.robabrazado.aoc2024.day22;

import java.util.ArrayList;
import java.util.List;

public class ChangeSequenceLog {
	private static final int SNAPSHOT_SIZE = 4;
	
	private List<Integer> buffer = new ArrayList<Integer>(SNAPSHOT_SIZE);
	
	public ChangeSequenceLog() {
		return;
	}
	
	public void logChange(int change) {
		this.buffer.add(change);
		if (this.buffer.size() > SNAPSHOT_SIZE) {
			this.buffer.remove(0);
		}
		return;
	}
	
	public boolean hasSnapshot() {
		return this.buffer.size() == SNAPSHOT_SIZE;
	}
	
	// Returns valid or throws
	public Sequence snapshot() {
		if (this.buffer.size() != SNAPSHOT_SIZE) {
			throw new IllegalStateException("Log unable to return snapshot; wrong element count in buffer");
		}
		return new Sequence(this.buffer.get(0), this.buffer.get(1), this.buffer.get(2), this.buffer.get(3));
	}
	
	public record Sequence(int bid1, int bid2, int bid3, int bid4) {}
}
