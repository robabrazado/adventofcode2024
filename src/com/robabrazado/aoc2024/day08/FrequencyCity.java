package com.robabrazado.aoc2024.day08;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import com.robabrazado.aoc2024.grid.Coords;

public class FrequencyCity {
	private final List<Antenna> antennae;
	private final int height;
	private final int width;
	
	public FrequencyCity(Stream<String> input) {
		List<Antenna> ants = new ArrayList<Antenna>();
		int height = 0;
		int width = 0;
		int tempWidth = -1;
		
		Iterator<String> it = input.iterator();
		while (it.hasNext()) {
			String line = it.next();
			
			for (char c : line.toCharArray()) {
				if (c != '.') {
					ants.add(new Antenna(c, new Coords(width, height)));
				}
				width++;
			}
			
			height++;
			if (tempWidth < 0) {
				tempWidth = width;
			} else {
				if (tempWidth != width) {
					throw new IllegalArgumentException("Input is not a rectangular grid");
				}
			}
			width = 0;
		}
		
		this.antennae = Collections.unmodifiableList(ants);
		this.height = height;
		this.width = tempWidth;
	}
	
	/**
	 * 
	 * @param doubleDistance true if using "double distance" requirement (part 1); false if using "resonant harmonics" (part 2)
	 * @return
	 */
	public int countAntinodes(boolean doubleDistance) {
		Set<Coords> antinodes = new HashSet<Coords>();
		Map<Character, List<Coords>> frequencyMap = new HashMap<Character, List<Coords>>();
		
		// Build map of frequencies to antenna locations
		for (Antenna ant : this.antennae) {
			char freq = ant.getFrequency();
			Coords coords = ant.getCoordinates();
			if (!frequencyMap.containsKey(freq)) {
				frequencyMap.put(freq, new ArrayList<Coords>());
			}
			frequencyMap.get(freq).add(coords);
		}
		
		
		for (char frequency : frequencyMap.keySet()) {
			List<Coords> theseLocations = frequencyMap.get(frequency);
			int numLocations = theseLocations.size();
			
			for (int i = 0; i < numLocations; i++) {
				List<Coords> otherLocations = new ArrayList<Coords>(theseLocations);
				Coords thisLocation = otherLocations.remove(i);
				
				// For each other antenna other than the one we're looking at, log antinode locations
				for (Coords otherLocation : otherLocations) {
					if (doubleDistance) { // I know I should nest this differently for performance but eh
						antinodes.addAll(this.getDoubleDistanceAntinodes(thisLocation, otherLocation));
					} else {
						antinodes.addAll(this.getResonantHarmonicsAntinodes(thisLocation, otherLocation));
					}
				}
			}
		}
		
		return antinodes.size();
	}
	
	private Set<Coords> getDoubleDistanceAntinodes(Coords firstNode, Coords secondNode) {
		Set<Coords> antinodes = new HashSet<Coords>();
		Coords firstOffset = firstNode.getOffsetTo(secondNode);
		Coords[] tempAntinodes = {secondNode.applyOffset(firstOffset), firstNode.applyOffset(firstOffset.invert())};
		for (Coords antinode : tempAntinodes) {
			if (this.isInBounds(antinode)) {
				antinodes.add(antinode);
			}
		}
		return antinodes;
	}
	
	private Set<Coords> getResonantHarmonicsAntinodes(Coords firstNode, Coords secondNode) {
		Set<Coords> antinodes = new HashSet<Coords>();
		Coords slope = firstNode.getOffsetTo(secondNode).reduceSlope();
		
		// Go one direction until out of bounds
		Coords thisNode = secondNode;
		while (this.isInBounds(thisNode)) {
			antinodes.add(thisNode);
			thisNode = thisNode.applyOffset(slope);
		}
		
		// Go the other direction until out of bounds
		slope = slope.invert();
		thisNode = secondNode.applyOffset(slope);
		while (this.isInBounds(thisNode)) {
			antinodes.add(thisNode);
			thisNode = thisNode.applyOffset(slope);
		}
		
		return antinodes;
	}
	
	public List<Antenna> getAntennae() {
		return Collections.unmodifiableList(this.antennae);
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public boolean isInBounds(Coords coords) {
		return coords.getCol() >= 0 &&
				coords.getCol() < this.width &&
				coords.getRow() >= 0 &&
				coords.getRow() < this.height;
	}
}
