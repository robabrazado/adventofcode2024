package com.robabrazado.aoc2024.day14;

import java.io.BufferedReader;
/* Uncomment this block of imports if you uncomment the "original" solution in solve2()
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
*/
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
		// Tests indicate that the state repeats after 10403 advances, so at least there's an upper limit
		// 10K frames can't be that much to go through, can it???
		// Spoiler: it was a lot.
		
		/*
		 * The block below was what I used to solve the puzzle day of. I don't like it because (a) it drops
		 * a new file on the file system and (b) it needs code revision to really "work" PLUS human review
		 * of the results (though, really, I don't see a way around human review on this one).
		 * 
		 * I'm leaving this as a comment block, but I'm rejiggering it to do more without involving external
		 * operations.
		 * 
		 * If you want to uncomment this block, you'll also need to uncomment several import statements above.
		 * 
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
				// It occurs to me only after the fact: I could have just searched for the string
				// in the dump file; I didn't have to keep re-running the program. OH WELL.

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
		 */
		
		/*
		 * Here I'm taking a more general shot, but it's not what I actually used as this part's "entry,"
		 * and it's tainted anyway, because I'm already more familiar with the answer. I guess just...the
		 * theory is sound? Ish?
		 * 
		 * The main assumption here is that the sought-after state is the one with the longest horizontal
		 * line. The secondary assumption is that the states are ultimately cyclical; otherwise this will
		 * just loop forever.
		 * 
		 * So...bottom line is that I know this will work with my specific puzzle input, but there are
		 * many ways that this solution could fail in the general case.
		 */
		TileArea area = new TileArea(puzzleInput, isTest);
		List<String> history = new ArrayList<String>();
		String state = area.toString();
		int step = 0;
		int resultStep = -1;
		Pattern p = Pattern.compile("#+");
		int longestLineLen = 0;
		
		
		while (!history.contains(state)) {
			BufferedReader br = new BufferedReader(new StringReader(state));
			Iterator<String> it = br.lines().iterator();
			while (it.hasNext()) {
				String line = it.next();
				Matcher m = p.matcher(line);
				while (m.find()) {
					int len = m.group().length();
					if (len > longestLineLen) {
						longestLineLen = len;
						resultStep = step;
					}
				}
			}
			
			history.add(state);
			area.advance(1);
			state = area.toString();
			step++;
		}
		
		if (resultStep < 0) {
			throw new RuntimeException("Couldn't even find a candidate frame");
		}
		
		System.out.println(history.get(resultStep));
		
		return String.valueOf(resultStep);
	}
}
