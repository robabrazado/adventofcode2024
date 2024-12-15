package com.robabrazado.aoc2024.day15;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import com.robabrazado.aoc2024.grid.Coords;
import com.robabrazado.aoc2024.grid.Dir;

public class DoubleWideWarehouse {
	private final Map<Coords, Item> warehouseMap = new HashMap<Coords, Item>(); // Not for robot!
	private final Deque<Dir> movements = new ArrayDeque<Dir>();
	private final int warehouseHeight;
	private final int warehouseWidth;
	private Coords robotPosition = null;
	
	public DoubleWideWarehouse(Stream<String> puzzleInput) {
		Iterator<String> it = puzzleInput.iterator();
		
		// Input until first blank line will be warehouse map
		{
			int width = -1;
			int row = 0;
			String line = it.next();
			while (!line.isEmpty()) {
				int len = line.length();
				
				if (width < 0) {
					width = len * 2;
				} else {
					if (width != len * 2) {
						throw new RuntimeException("Warehouse map input is not rectangular");
					}
				}
				
				for (int inCol = 0; inCol < len; inCol++) {
					int outLeftCol = inCol * 2;
					Coords coords = new Coords(outLeftCol, row);
					
					Character c = Character.valueOf(line.charAt(inCol));
					switch (c) {
					case '.':
						// Space - do nothing
						break;
					case '#':
						this.warehouseMap.put(coords, Item.WALL);
						this.warehouseMap.put(coords.applyOffset(Dir.E), Item.WALL);
						break;
					case 'O':
						this.warehouseMap.put(coords, Item.LEFTBOX);
						this.warehouseMap.put(coords.applyOffset(Dir.E), Item.RIGHTBOX);
						break;
					case '@':
						if (this.robotPosition == null) {
							this.robotPosition = coords;
						} else {
							throw new RuntimeException("Warehouse map input contains multiple robots");
						}
						break;
					default:
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
			Set<Coords> boxCells = new HashSet<Coords>(); // Left boxes only
			Deque<Coords> lookCells = new ArrayDeque<Coords>();
			lookCells.add(this.robotPosition.applyOffset(d)); // This works very differently than Warehouse!
			boolean canMove = true;
			
			while (lookCells.size() > 0 && canMove) { 
				Coords lookCell = lookCells.pop();
				if (this.warehouseMap.containsKey(lookCell)) {
					Item item = this.warehouseMap.get(lookCell);
					if (item == Item.RIGHTBOX) {
						// Handle all box processing from the left half
						lookCells.add(lookCell.applyOffset(Dir.W));
					} else if (item == Item.LEFTBOX) {
						if (this.boxWillHitWall(lookCell, d)) {
							// We're done
							canMove = false;
						} else {
							// This box can MAYBE move; keep checking
							switch (d) {
							case E:
								// If we're heading east, skip the next cell, because it's the right half of this box
								lookCells.add(lookCell.applyOffset(d).applyOffset(d));
								break;
							case W:
								lookCells.add(lookCell.applyOffset(d));
								break;
							case N:
							case S:
								// Check next cells from both halves of the box
								lookCells.add(lookCell.applyOffset(d));
								lookCells.add(lookCell.applyOffset(Dir.E).applyOffset(d));
								break;
							default:
								throw new RuntimeException("Unexpected movement: " + d.name());
							}
							
							// But if we CAN ultimately move, we'll need to move this box
							boxCells.add(lookCell);
						}
					} else if (item == Item.WALL) {
						// Nope; we're done
						canMove = false;
					} else {
						throw new RuntimeException("Unexpected warehouse item found: " + item.name());
					}
				} // else this is a space; do nothing
			}
			
			if (canMove) {
				// Move any applicable boxes
				// These have to move in the right order; move boxes that are free to do so first
				while (boxCells.size() > 0) {
					Iterator<Coords> it = boxCells.iterator();
					Coords leftCell = null;
					while (it.hasNext() && leftCell == null) {
						Coords tempLeftCell = it.next();
						if (this.boxCanMove(tempLeftCell, d)) {
							leftCell = tempLeftCell;
							boxCells.remove(tempLeftCell);
						}
					}
					if (leftCell != null) {
						this.moveBox(leftCell, d);
					} else {
						throw new RuntimeException("Looked for the next movable box but couldn't find one?!");
					}
				}
				
				// Move the robot
				this.robotPosition = this.robotPosition.applyOffset(d);
			}
		}
		return;
	}
	
	private boolean isWall(Coords cell) {
		return this.warehouseMap.containsKey(cell) && this.warehouseMap.get(cell) == Item.WALL;
	}
	
	private boolean boxWillHitWall(Coords leftCell, Dir d) {
		switch (d) {
		case E:
			return this.isWall(leftCell.applyOffset(Dir.E).applyOffset(d));
		case W:
			return this.isWall(leftCell.applyOffset(d));
		case N:
		case S:
			return this.isWall(leftCell.applyOffset(d)) || this.isWall(leftCell.applyOffset(Dir.E).applyOffset(d));
		default:
			throw new IllegalArgumentException("Unexpected movement: " + d.name());
		}
	}
	
	private boolean isSpace(Coords cell) {
		return !this.warehouseMap.containsKey(cell);
	}
	
	private boolean boxCanMove(Coords leftCell, Dir d) {
		switch (d) {
		case E:
			return this.isSpace(leftCell.applyOffset(Dir.E).applyOffset(d));
		case W:
			return this.isSpace(leftCell.applyOffset(d));
		case N:
		case S:
			return this.isSpace(leftCell.applyOffset(d)) && this.isSpace(leftCell.applyOffset(Dir.E).applyOffset(d));
		default:
			throw new IllegalArgumentException("Unexpected movement: " + d.name());
		}
	}
	
	
	private void moveBox(Coords leftCell, Dir d) {
		Coords rightCell = leftCell.applyOffset(Dir.E);
		this.warehouseMap.remove(leftCell);
		this.warehouseMap.remove(rightCell);
		this.warehouseMap.put(leftCell.applyOffset(d), Item.LEFTBOX);
		this.warehouseMap.put(rightCell.applyOffset(d), Item.RIGHTBOX);
		return;
	}
	
	public int getGpsSum() {
		int total = 0;
		
		for (Coords cell : this.warehouseMap.keySet()) {
			if (this.warehouseMap.get(cell) == Item.LEFTBOX) {
				total += (cell.getRow() * 100) + cell.getCol();
			}
		}
		
		return total;
	}
	
	@Override
	public String toString() {
		return this.toString(true);
	}
	
	public String toString(boolean includeMovementQueue) {
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
		
		if (includeMovementQueue) {
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
		}
		
		return sw.toString();
	}
	
	public enum Item {
		SPACE		('.'),
		LEFTBOX		('['),
		RIGHTBOX	(']'),
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
	}
}
