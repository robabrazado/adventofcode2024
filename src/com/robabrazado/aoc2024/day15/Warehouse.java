package com.robabrazado.aoc2024.day15;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

import com.robabrazado.aoc2024.grid.Coords;
import com.robabrazado.aoc2024.grid.Dir;

public class Warehouse {
	private final Map<Coords, Item> warehouseMap = new HashMap<Coords, Item>(); // Not for robot!
	private final Deque<Dir> movements = new ArrayDeque<Dir>();
	private final int warehouseHeight;
	private final int warehouseWidth;
	private Coords robotPosition = null;
	
	public Warehouse(Stream<String> puzzleInput) {
		Map<Character, Item> itemMap = Item.map();
		Iterator<String> it = puzzleInput.iterator();
		
		// Input until first blank line will be warehouse map
		{
			int width = -1;
			int row = 0;
			String line = it.next();
			while (!line.isEmpty()) {
				int len = line.length();
				
				if (width < 0) {
					width = len;
				} else {
					if (width != len) {
						throw new RuntimeException("Warehouse map input is not rectangular");
					}
				}
				
				for (int col = 0; col < len; col++) {
					Character c = Character.valueOf(line.charAt(col));
					if (itemMap.containsKey(c)) {
						Item item = itemMap.get(c);
						if (item != Item.SPACE) {
							Coords coords = new Coords(col, row);
							if (item == Item.ROBOT) {
								if (this.robotPosition == null) {
									this.robotPosition = coords;
								} else {
									throw new RuntimeException("Warehouse map input contains multiple robots");
								}
							} else {
								this.warehouseMap.put(coords, item);
							}
						}
					} else {
						throw new RuntimeException("Unrecognized map character: " + c);
					}
				}
				
				row++;
				line = it.next();
			}
			this.warehouseWidth = width;
			this.warehouseHeight = row;
		}
		
		// Rest of input is list of movements
		while (it.hasNext()) {
			char[] chars = it.next().toCharArray();
			for (char c : chars) {
				switch (c) {
				case '>':
					this.movements.add(Dir.E);
					break;
				case 'v':
					this.movements.add(Dir.S);
					break;
				case '<':
					this.movements.add(Dir.W);
					break;
				case '^':
					this.movements.add(Dir.N);
					break;
				default:
					throw new RuntimeException("Unrecognized movement character: " + c);
				}
			}
		}
		
		return;
	}
	
	public void executeMoves() {
		while (this.movements.size() > 0) {
			Dir d = this.movements.poll();
			Deque<Coords> boxCells = new ArrayDeque<Coords>();
			Coords lookCell = this.robotPosition;
			boolean keepLooking = true;
			boolean move = false;
			while (keepLooking) {
				lookCell = lookCell.applyOffset(d);
				if (this.warehouseMap.containsKey(lookCell)) {
					Item item = this.warehouseMap.get(lookCell);
					if (item == Item.BOX) {
						// Record box and move on
						boxCells.push(lookCell);
					} else if (item == Item.WALL) {
						// We're done here
						keepLooking = false;
					} else {
						throw new RuntimeException("Unexpected warehouse item found: " + item.name());
					}
				} else {
					// This is a space
					keepLooking = false;
					move = true;
				}
			} // done looking
			
			if (move) {
				// Move any boxes
				while (boxCells.size() > 0) {
					Coords fromCell = boxCells.pop();
					Item box = this.warehouseMap.remove(fromCell);
					this.warehouseMap.put(fromCell.applyOffset(d), box);
				}
				
				// Move the robot
				this.robotPosition = this.robotPosition.applyOffset(d);
			}
		}
		return;
	}
	
	public int getGpsSum() {
		int total = 0;
		
		for (Coords cell : this.warehouseMap.keySet()) {
			if (this.warehouseMap.get(cell) == Item.BOX) {
				total += (cell.getRow() * 100) + cell.getCol();
			}
		}
		
		return total;
	}
	
	@Override
	public String toString() {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		
		char[][] grid = new char[this.warehouseHeight][this.warehouseWidth];
		for (char[] line : grid) {
			Arrays.fill(line, Item.SPACE.c);
		}
		
		for (Coords cell : this.warehouseMap.keySet()) {
			grid[cell.getRow()][cell.getCol()] = this.warehouseMap.get(cell).c;
		}
		
		grid[this.robotPosition.getRow()][this.robotPosition.getCol()] = Item.ROBOT.c;
		
		for (char[] line : grid) {
			pw.println(line);
		}
		pw.println();
		
		Iterator<Dir> it = this.movements.iterator();
		int counter = 1;
		while (it.hasNext()) {
			pw.print(it.next().name());
			if (counter == 80) {
				pw.println();
				counter = 1;
			} else {
				counter++;
			}
		}
		if (counter != 80) {
			pw.println();
		}
		
		return sw.toString();
	}
	
	public enum Item {
		SPACE		('.'),
		BOX			('O'),
		WALL		('#'),
		ROBOT		('@');
		
		private final char c;
		
		Item(char c) {
			this.c = c;
			return;
		}
		
		public char getCharacter() {
			return this.c;
		}
		
		public static Map<Character, Item> map() {
			Map<Character, Item> map = new HashMap<Character, Item>();
			for (Item item : Item.values()) {
				map.put(item.c, item);
			}
			return map;
		}
	}
}
