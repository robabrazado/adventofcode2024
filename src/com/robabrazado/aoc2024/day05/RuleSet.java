package com.robabrazado.aoc2024.day05;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RuleSet {
	private final Map<Integer, List<Integer>> rules = new HashMap<Integer, List<Integer>>();
	private final List<int[]> flatRules = new ArrayList<int[]>();
	private List<Integer> sortOrder = null;
	
	public RuleSet() {
		
	}
	
	public void addRule(int earlierPage, int laterPage) {
		if (!this.rules.containsKey(earlierPage)) {
			this.rules.put(earlierPage, new ArrayList<Integer>());
		}
		this.rules.get(earlierPage).add(laterPage);
		this.flatRules.add(new int[] {earlierPage, laterPage});
		this.sortOrder = null;
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
	
	public List<Integer> reorderOrder(List<Integer> oldOrder) {
		List<Integer> newOrder = new ArrayList<Integer>();
		newOrder.addAll(oldOrder);
		
		List<Integer> sortOrder = this.getSortOrder();
		newOrder.sort((Integer a, Integer b) -> {
			 if (!sortOrder.contains(a) || !sortOrder.contains(b)) {
				 throw new RuntimeException("I don't expect this to happen, but I gotta bail if it does");
			 }
			 
			 return sortOrder.indexOf(a) - sortOrder.indexOf(b);
		});
		
		return newOrder;
		
	}
	
	private List<Integer> getSortOrder() {
		if (this.sortOrder == null) {
			List<Integer> tempSortOrder = new ArrayList<Integer>();
			
			if (this.flatRules.size() > 0) {
				List<int[]> rulesList = new ArrayList<int[]>(this.flatRules);
				
				while (rulesList.size() > 0) {
					if (rulesList.size() > 1) {
						// Collect all the "later" pages
						Set<Integer> laterPages = new HashSet<Integer>();
						for (int[] rule : rulesList) {
							laterPages.add(rule[1]);
						}
						
						// Find the "earliest page" remaining in the rules, which will be an earlier page that is never a later page
						int firstPage = -1;
						for (int i = 0; i < rulesList.size() && firstPage < 0; i++) {
							int testPage = rulesList.get(i)[0];
							if (!laterPages.contains(testPage)) {
								firstPage = testPage;
							}
						}
						
						// Set this as the next page in the sort order
						tempSortOrder.add(firstPage);
						
						// Remove all rules that have this page as the earlier page
						for (int i = 0; i < rulesList.size(); i++) {
							int[] rule = rulesList.get(i);
							if (rule[0] == firstPage) {
								rulesList.remove(i--);
							}
						}
					} else {
						// If there's only one rule left, it's the last two pages
						int[] rule = rulesList.get(0);
						tempSortOrder.add(rule[0]);
						tempSortOrder.add(rule[1]);
						rulesList.remove(0);
					}
				} // Lather, rinse, repeat
			}
			
			this.sortOrder = Collections.unmodifiableList(tempSortOrder);
		}
		return this.sortOrder;
	}
	
}
