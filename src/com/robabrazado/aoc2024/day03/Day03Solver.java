package com.robabrazado.aoc2024.day03;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.robabrazado.aoc2024.Solver;

// --- Day 3: Mull It Over ---
public class Day03Solver extends Solver {
	
	public Day03Solver() {
		super(3);
		return;
	}

	@Override
	protected void solve(PrintWriter out, PrintWriter err, boolean isPartOne, boolean testData) throws IOException {
		if (isPartOne) {
			this.solve1(out, err, testData);
		} else {
			this.solve2(out, err, testData);
		}
	}
	
	protected void solve1(PrintWriter out, PrintWriter err, boolean testData) throws IOException {
		BufferedReader in = null;
		Pattern p = Pattern.compile("mul\\((\\d{1,3}),(\\d{1,3})\\)");
		int runningTotal = 0;
		
		try {
			in = super.getPuzzleInputReader(testData);
			
			String line = in.readLine();
			while (line != null) {
				Matcher m = p.matcher(line);
				
				while (m.find()) {
					runningTotal += (Integer.parseInt(m.group(1)) * Integer.parseInt(m.group(2)));
				}

				line = in.readLine();
			}
		} finally {
			if (in != null) {
				in.close();
			}
		}
		
		System.out.println(runningTotal);
	}
	
	protected void solve2(PrintWriter out, PrintWriter err, boolean testData) throws IOException {
		/*
		 * This was my first attempt at part 2. It passes test data but not live data, and I don't know why.
		BufferedReader in = null;
		Pattern p = Pattern.compile("mul\\((\\d{1,3}),(\\d{1,3})\\)");
		Pattern pEnable = Pattern.compile("do\\(\\)");
		Pattern pDisable = Pattern.compile("don't\\(\\)");
		
		int runningTotal = 0;
		
		try {
			in = super.getPuzzleInputReader(testData);

			String line = in.readLine();
			String chunk = null;
			while (line != null) {
				// Iterate over one enabled chunk at a time until line is exhausted
				while (line.length() > 0) {
					// We always start enabled, so look for the next disable command
					Matcher mDisable = pDisable.matcher(line);
					if (mDisable.find()) {
						chunk = line.substring(0, mDisable.end());
					} else {
						// If none found, we're enabled to the end
						chunk = line;
					}
					
					// Process the enabled chunk
					Matcher m = p.matcher(chunk);
					while (m.find()) {
						runningTotal += (Integer.parseInt(m.group(1)) * Integer.parseInt(m.group(2)));
					}
					
					// Remove the processed chunk from the line
					line = line.substring(chunk.length());
					
					// Now we're disabled; look for the next enable command and discard everything through that command
					Matcher mEnable = pEnable.matcher(line);
					if (mEnable.find()) {
						line = line.substring(mEnable.end());
					} else {
						// No more enable; we're done here
						line = "";
					}
				}

				line = in.readLine();
			}
		} finally {
			if (in != null) {
				in.close();
			}
		}
		
		System.out.println(runningTotal);
		 */
		
		/*
		 * This is my second try at part 2, and it comes up with the same (wrong) result, so I guess it does the same thing
		 * although I thought it would act differently?!
		BufferedReader in = null;
		Pattern pChunk = Pattern.compile("do\\(\\)(.*?)don't\\(\\)");
		Pattern pMul = Pattern.compile("mul\\((\\d{1,3}),(\\d{1,3})\\)");
		
		int runningTotal = 0;
		
		try {
			in = super.getPuzzleInputReader(testData);

			String line = in.readLine();
			while (line != null) {
				// We'll always start enabled and always end disabled
				line = "do()" + line + "don't()";
				
				Matcher mChunk = pChunk.matcher(line);
				// Process the enabled chunk
				while (mChunk.find()) {
					Matcher mMul = pMul.matcher(mChunk.group(1));
					while (mMul.find()) {
						runningTotal += (Integer.parseInt(mMul.group(1)) * Integer.parseInt(mMul.group(2)));
					}
				}
				
				line = in.readLine();
			}
		} finally {
			if (in != null) {
				in.close();
			}
		}
		
		System.out.println(runningTotal);
		 */
		
		/*
		 * Third attempt, iterating on the first.
		 * 
		 * Maybe the enable/disable searching doesn't need to be regex? I'll try it with plan ol' String searching
		 * and use a cursor position instead of breaking the line into chunks.
		 * 
		 * This returns the same (incorrect) result as before! So it's not a regex issue, it IS an algorithm issue.
		 * Still don't know what, though.
		BufferedReader in = null;
		Pattern p = Pattern.compile("mul\\((\\d{1,3}),(\\d{1,3})\\)");
		String enableCmd = "do()";
		String disableCmd = "don't()";
		
		int runningTotal = 0;
		
		try {
			in = super.getPuzzleInputReader(testData);
			String line = in.readLine();

			while (line != null) {
				int cursorPos = 0;
				while (cursorPos < line.length()) {
					// We always start enabled, so look for the next disable command after the cursor
					int nextDisablePos = line.indexOf(disableCmd, cursorPos);
					if (nextDisablePos >= 0) {
						nextDisablePos += disableCmd.length();
					} else {
						// No disable command found; extend to end of line
						nextDisablePos = line.length();
					}
					
					// Process the enabled section
					Matcher m = p.matcher(line.substring(cursorPos, nextDisablePos));
					while (m.find()) {
						runningTotal += (Integer.parseInt(m.group(1)) * Integer.parseInt(m.group(2)));
					}
					
					// Advance the cursor
					cursorPos = nextDisablePos;
					
					if (cursorPos < line.length()) {
						// Now we're disabled; look for the next enable command
						int nextEnablePos = line.indexOf(enableCmd, cursorPos);
						if (nextEnablePos >= 0) {
							nextEnablePos += enableCmd.length();
						} else {
							// No enable command found; extend to end of line
							nextEnablePos = line.length();
						}

						// Advance cursor past disabled section
						cursorPos = nextEnablePos;
					}
				}

				line = in.readLine();
			}
		} finally {
			if (in != null) {
				in.close();
			}
		}
		
		System.out.println(runningTotal);
		 */
		
		/*
		 * Fourth attempt: new algorithm. Instead of looking ahead to future commands, I'll just react to each
		 * command as I come across it.
		 * 
		 * SAME ANSWER. I AM LOSING MY MIND.
		 * 
		 * After much consternation and finally comparing my code to Jeff's, I realized what I was doing wrong.
		 * I was interpreting each "line" of the input as a new "program" and so resetting the enabled flag to
		 * true. Turns out I shouldn't have been doing that. So it goes.
		 */
		BufferedReader in = null;
		Pattern p = Pattern.compile("(?:mul\\((?<mul1>\\d{1,3}),(?<mul2>\\d{1,3})\\))|(?:do\\(\\))|(?:don't\\(\\))");
		
		int runningTotal = 0;
		
		try {
			in = super.getPuzzleInputReader(testData);
			String line = in.readLine();

			boolean enabled = true; // This was the problem! This used to be inside the while loop.
			while (line != null) {
				Matcher m = p.matcher(line);
				while (m.find()) {
					if ("do()".equals(m.group())) {
						enabled = true;
					} else if ("don't()".equals(m.group())) {
						enabled = false;
					} else if (enabled) {
						runningTotal += (Integer.parseInt(m.group("mul1")) * Integer.parseInt(m.group("mul2")));
					} // else do nothing
				}

				line = in.readLine();
			}
		} finally {
			if (in != null) {
				in.close();
			}
		}
		
		System.out.println(runningTotal);
	}
	
}
