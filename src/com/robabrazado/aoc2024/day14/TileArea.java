package com.robabrazado.aoc2024.day14;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.robabrazado.aoc2024.grid.Coords;
import com.robabrazado.aoc2024.grid.Dir;

public class TileArea {
	private final int width;
	private final int height;
	private final Robot[] robots;
	
	public TileArea(Stream<String> puzzleInput, boolean isTest) {
		this.width = isTest ? 11 : 101;
		this.height = isTest ? 7 : 103;
		
		List<Robot> tempRobots = new ArrayList<Robot>();
		Iterator<String> it = puzzleInput.iterator();
		while (it.hasNext()) {
			tempRobots.add(new Robot(it.next()));
		}
		this.robots = tempRobots.toArray(new Robot[0]);
		
		return;
	}
	
	public void advance(int steps) {
		for (Robot r : this.robots) {
			r.advance(steps, this.width, this.height);
		}
		return;
	}
	
	public int countByQuadrant() {
		Map<Dir, Integer> qCounts = new HashMap<Dir, Integer>();
		for (Dir d : Dir.diagonals()) {
			qCounts.put(d, 0);
		}
		
		for (Robot r : this.robots) {
			Dir q = this.robotInQuadrant(r);
			if (q != null) {
				qCounts.put(q, qCounts.get(q) + 1);
			}
		}
		
		int total = 1;
		for (int count : qCounts.values()) {
			total *= count;
		}
		return total;
	}
	
	// Returns null if robot on border
	private Dir robotInQuadrant(Robot r) {
		Dir d = null;
		Coords pos = r.getPosition();
		int col = pos.getCol();
		int row = pos.getRow();
		int halfWidth = this.width / 2;
		int halfHeight = this.height / 2;
		
		if (col < halfWidth) {
			if (row < halfHeight) {
				d = Dir.NW;
			} else if (row > halfHeight) {
				d = Dir.SW;
			}
		} else if (col > halfWidth) {
			if (row < halfHeight) {
				d = Dir.NE;
			} else if (row > halfHeight) {
				d = Dir.SE;
			}
		}
		
		return d;
	}
	
	@Override
	public String toString() {
		char[][] outputMatrix = new char[height][width];
		for (int i = 0; i < height; i++) {
			Arrays.fill(outputMatrix[i], '.');
		}
		
		for (Robot r : this.robots) {
			Coords pos = r.getPosition();
			outputMatrix[pos.getRow()][pos.getCol()] = '#';
		}
		
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		for (char[] arr : outputMatrix) {
			pw.println(arr);
		}
		return sw.toString();
	}
}
