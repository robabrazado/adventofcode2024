package com.robabrazado.aoc2024.day03;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.robabrazado.aoc2024.Solver;

// 
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
		
		
	}
	
}
