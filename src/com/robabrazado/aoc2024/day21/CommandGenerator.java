package com.robabrazado.aoc2024.day21;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/*
 * This generator collects generator objects and manages the probably
 * sickening number of permutations involved.
 */
public class CommandGenerator {
	private final List<PathGenerator> pathGenerators = new ArrayList<PathGenerator>();
	
	public CommandGenerator(PathGenerator generator) {
		if (generator == null) {
			throw new IllegalArgumentException("Generator cannot be null");
		}
		this.pathGenerators.add(generator);
		return;
	}
	
	public CommandGenerator(List<PathGenerator> generators) {
		if (generators != null && !generators.isEmpty()) {
			this.pathGenerators.addAll(generators);
		}
		return;
	}
	
	public String generateCommand(List<Integer> bitmaps) {
		if (bitmaps.size() != this.pathGenerators.size()) {
			throw new IllegalArgumentException(String.format("Expected %d bitmaps; received %d", this.pathGenerators.size(), bitmaps.size()));
		}
		StringBuilder strb = new StringBuilder();
		int numGens = this.pathGenerators.size();
		for (int i = 0; i < numGens; i++) {
			strb.append(this.pathGenerators.get(i).generateCommand(bitmaps.get(i)));
		}
		return strb.toString();
	}
	
	public List<PathGenerator> getPathGenerators() {
		return Collections.unmodifiableList(this.pathGenerators);
	}
	
	public Iterator<String> commandIterator() {
		return new CommandIterator(this);
	}
	
	private class CommandIterator implements Iterator<String> {
		private final CommandGenerator generator;
		private final List<Integer> lastIdxs = new ArrayList<Integer>();
		private final List<Integer> currentIdxs = new ArrayList<Integer>();
		
		public CommandIterator(CommandGenerator generator) {
			this.generator = generator;
			for (PathGenerator gen : generator.pathGenerators) {
				this.lastIdxs.add(gen.getIndexCount() - 1);
				this.currentIdxs.add(0);
			}
			return;
		}

		@Override
		public boolean hasNext() {
			boolean has = true;
			int numGens = this.generator.pathGenerators.size();
			for (int i = 0; i < numGens && has; i++) {
				has = this.currentIdxs.get(i) <= this.lastIdxs.get(i);
			}
			return has;
		}

		@Override
		public String next() {
			String result = this.generator.generateCommand(currentIdxs);
			
			int lastGenIdx = generator.pathGenerators.size() - 1;
			this.currentIdxs.set(lastGenIdx, this.currentIdxs.get(lastGenIdx) + 1);
			boolean doneCarrying = false;
			for (int i = lastGenIdx - 1; i >= 0 && !doneCarrying; i--) {
				int nextIdx = i + 1;
				if (this.currentIdxs.get(nextIdx) > this.lastIdxs.get(nextIdx)) {
					this.currentIdxs.set(nextIdx, 0);
					this.currentIdxs.set(i, this.currentIdxs.get(i) + 1);
				} else {
					doneCarrying = true;
				}
			}
			return result;
		}
		
	}
}
