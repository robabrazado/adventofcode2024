package com.robabrazado.aoc2024.day22;

import java.util.HashMap;
import java.util.Map;

public class Buyer {
	private final SecretNumberGenerator secretNumberGenerator;
	private final ChangeSequenceLog log = new ChangeSequenceLog();
	private final Map<ChangeSequenceLog.Sequence, Integer> bidMap = new HashMap<ChangeSequenceLog.Sequence, Integer>();
	
	private int currentPrice;
	
	public Buyer(long seed) {
		this.secretNumberGenerator = new SecretNumberGenerator(seed);
		this.currentPrice = Buyer.getPrice(this.secretNumberGenerator.getCurrentValue());
		return;
	}
	
	public int getCurrentPrice() {
		return this.currentPrice;
	}
	
	public long nextSecretNumber() {
		long nextSecretNumber = this.secretNumberGenerator.next();
		int newPrice = Buyer.getPrice(nextSecretNumber);
		int lastPrice = this.currentPrice;
		int diff = newPrice - lastPrice;
		
		this.log.logChange(diff);
		if (this.log.hasSnapshot()) {
			ChangeSequenceLog.Sequence sequence = this.log.snapshot();
			if (!this.bidMap.containsKey(sequence)) {
				this.bidMap.put(sequence, newPrice);
			}
		}
		this.currentPrice = newPrice;
		return nextSecretNumber;
	}
	
	public long nextSecretNumber(int num) {
		long result = this.currentPrice;
		for (int i = 1; i <= num; i++) {
			result = this.nextSecretNumber();
		}
		return result;
	}
	
	public Map<ChangeSequenceLog.Sequence, Integer> getBidMap() {
		return new HashMap<ChangeSequenceLog.Sequence, Integer>(this.bidMap);
	}
	
	private static int getPrice(long secretNumber) {
		return (int) (secretNumber % 10);
	}
	
	@Override
	public String toString() {
		return "Buyer " + String.valueOf(this.secretNumberGenerator.getFirstValue());
	}
}
