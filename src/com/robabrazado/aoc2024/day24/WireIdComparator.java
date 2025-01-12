package com.robabrazado.aoc2024.day24;

import java.util.Comparator;

public class WireIdComparator implements Comparator<String> {

	@Override
	public int compare(String a, String b) {
		int sort = 0;
		
		if (a.startsWith("x")) {
			if (b.startsWith("x")) {
				sort = a.compareTo(b);
			} else {
				sort = -1;
			}
		} else if (a.startsWith("y")) {
			if (b.startsWith("x")) {
				sort = 1;
			} else if (b.startsWith("y")) {
				sort = a.compareTo(b);
			} else {
				sort = -1;
			}
		} else {
			if (b.startsWith("x") || b.startsWith("y")) {
				sort = 1;
			} else {
				sort = a.compareTo(b);
			}
		}
		
		return sort;
	}

}
