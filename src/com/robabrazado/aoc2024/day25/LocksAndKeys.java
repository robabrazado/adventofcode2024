package com.robabrazado.aoc2024.day25;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class LocksAndKeys {
	private static final String LOCK_FIRST_LINE = "#####";
	private static final String KEY_FIRST_LINE = ".....";
	
	// For now, each lock and each key is just an array of column heights; we'll see how long that lasts
	private final List<int[]> locks = new ArrayList<int[]>();
	private final List<int[]> keys = new ArrayList<int[]>();
	private final int componentHeight;
	private final int componentWidth;
	
	public LocksAndKeys(Stream<String> puzzleInput) {
		Iterator<String> it = puzzleInput.iterator();
		// I'm gonna cheese this a little bit
		Integer tempHeight = 7;
		Integer tempWidth = 5;
		
		while (it.hasNext()) {
			int[] component = new int[tempWidth];
			Arrays.fill(component, 0);
			
			// More cheese
			String line = it.next();
			List<int[]> lockOrKeyList;
			if (LOCK_FIRST_LINE.equals(line)) {
				lockOrKeyList = this.locks;
			} else if (KEY_FIRST_LINE.equals(line)) {
				lockOrKeyList = this.keys;
			} else {
				throw new RuntimeException("Unexpected puzzle input (first line of block): " + line);
			}
			
			for (int i = 1; i <= 5; i++) {
				char[] chars = it.next().toCharArray();
				for (int col = 0; col < tempWidth; col++) {
					switch (chars[col]) {
					case '#':
						component[col]++;
						break;
					case '.':
						// do nothing
						break;
					default:
						throw new RuntimeException("Unexpected schematic character: " + chars[col]);
					}
				}
			}
			
			line = it.next();
			if (lockOrKeyList == this.locks && !line.equals(KEY_FIRST_LINE)) {
				throw new RuntimeException("Unexpected end of lock schematic");
			} else if (lockOrKeyList == this.keys && !line.equals(LOCK_FIRST_LINE)) {
				throw new RuntimeException("Unexpected end of key schematic");
			}
			
			lockOrKeyList.add(component);
			
			if (it.hasNext()) {
				if (!it.next().isEmpty()) {
					throw new RuntimeException("Unexpected puzzle input; expected blank line");
				}
			}
		}
		
		this.componentHeight = tempHeight;
		this.componentWidth = tempWidth;
		return;
	}
	
	public int possibleFitCount() {
		int result = 0;
		
		int availableSpace = this.componentHeight - 2;
		for (int[] lock : this.locks) {
			for (int[] key : this.keys) {
				boolean isPossibleFit = true;
				for (int col = 0; col < this.componentWidth && isPossibleFit; col++) {
					isPossibleFit = lock[col] + key[col] <= availableSpace;
				}
				if (isPossibleFit) {
					result++;
				}
			}
		}
		
		return result;
	}

	@Override
	public String toString() {
		String formatting = "%d,%d,%d,%d,%d";
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		
		pw.println("Locks:");
		for (int[] lock : locks) {
			pw.println(String.format(formatting, lock[0], lock[1], lock[2], lock[3], lock[4]));
		}
		pw.println();
		
		pw.println("Keys:");
		for (int[] key : keys) {
			pw.println(String.format(formatting, key[0], key[1], key[2], key[3], key[4]));
		}
		
		return sw.toString();
	}
}
