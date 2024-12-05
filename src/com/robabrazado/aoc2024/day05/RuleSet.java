package com.robabrazado.aoc2024.day05;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RuleSet {
	private final Map<Integer, List<Integer>> rules = new HashMap<Integer, List<Integer>>();
	
	public RuleSet() {
		
	}
	
	public void addRule(int earlierPage, int laterPage) {
		if (!this.rules.containsKey(earlierPage)) {
			this.rules.put(earlierPage, new ArrayList<Integer>());
		}
		this.rules.get(earlierPage).add(laterPage);
		return;
	}
	
	public boolean orderPasses(List<Integer> order) {
		boolean passes = true;
		
		int orderLen = order.size();
		for (int i = 0; i <orderLen && passes; i++) {
			int thisPage = order.get(i);
			if (this.rules.containsKey(thisPage)) {
				List<Integer> laterPages = this.rules.get(thisPage);
				int laterPagesLen = laterPages.size();
				
				for (int j = 0; j < laterPagesLen && passes; j++) {
					int laterIdx = order.indexOf(laterPages.get(j));
					passes = laterIdx < 0 || laterIdx > i;
				}
			}
		}
		
		return passes;
	}
	
}
