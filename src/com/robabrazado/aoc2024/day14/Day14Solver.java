package com.robabrazado.aoc2024.day14;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.robabrazado.aoc2024.Solver;

// --- Day 14: Restroom Redoubt ---
public class Day14Solver extends Solver {
	
	public Day14Solver() {
		super(9);
		return;
	}
	
	@Override
	public String solve(Stream<String> puzzleInput, boolean partOne, boolean isTest) {
		if (partOne) {
			return this.solve1(puzzleInput, isTest);
		} else {
			return this.solve2(puzzleInput, isTest);
		}
	}
	
	private String solve1(Stream<String> puzzleInput, boolean isTest) {
		TileArea area = new TileArea(puzzleInput, isTest);
		area.advance(100);
		return String.valueOf(area.countByQuadrant());
	}
	
	private String solve2(Stream<String> puzzleInput, boolean isTest) {
		// Tests indicate that the state repeats after 10403 advances, so at least there's an upper limit
		// 10K frames can't be that much to go through, can it???
		// Spoiler: it was a lot.
		
		/*
		TileArea area = new TileArea(puzzleInput, isTest);
		
		while (!history.contains(state)) {
			System.out.println(counter);
			history.add(state);
			area.advance(1);
			counter++;
			state = area.toString();
		}
		return String.valueOf(counter);
		*/
		
		
		TileArea area = new TileArea(puzzleInput, isTest);
		List<String> history = new ArrayList<String>();
		String state = area.toString();
		int step = 0;
		PrintWriter out = null;
		try {
			File f = new File("day14part2output.txt");
			f.createNewFile();
			FileOutputStream fOut = new FileOutputStream(f);
			out = new PrintWriter(fOut, true);
			
			
			while (!history.contains(state)) {
				history.add(state);
				out.println(state);
				out.println("After " + String.valueOf(step) + " steps");
				out.println();
				// I dunno...look for a straight line?!
				// I just kept making the line longer until I got to a reasonable search space, and then found it manually. What a chore.
				if (state.contains("#######")) {
					System.out.println("Check step " + String.valueOf(step));
				}
				/* It occurs to me only after the fact: I could have just searched for the string
				 * in the dump file; I didn't have to keep re-running the program. OH WELL.
				 */

				area.advance(1);
				state = area.toString();
				step++;
			}
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (out != null) {
				out.close();
			}
		}
		
		return null;
	}
}
