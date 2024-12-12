package com.robabrazado.aoc2024.grid;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * NOTE: I may never use this, and in fact might just delete it. This is half-done and untested.
 * I may end up just deleting this, but I'm putting in one commit just in case I want to come
 * back to it.
 * 
 * Simple 2D array container. This assumes a rectangular collection with [0][0] in the upper left.
 * First index is row, second index is column.
 * 
 * @param <T> type of value in cells
 */
public class Grid<T> {
	private final T[][] matrix;
	private final int width;
	private final int height;
	
	/**
	 * Constructs a grid containing the specified values. If the input is not rectangular, the resulting
	 * grid will be forced into a rectangular shape with the missing values padded with nulls.
	 * 
	 * @param grid
	 */
	@SuppressWarnings("unchecked")
	public Grid(T[][] grid) {
		this.height = grid.length;
		int widest = 0;
		Object example = null;
		for (T[] row : grid) {
			int width = row != null ? row.length : 0;
			if (width > widest) {
				widest = width;
			}
			// Try to grab an example object for later class typing
			if (example == null) {
				for (int i = 0; i < width && example == null; i++) {
					if (row[i] != null) {
						example = row[i];
					}
				}
			}
		}
		this.width = widest;
		
		List<T[]> matrixRows = new ArrayList<T[]>();
		if (example != null) {
			for (T[] row : grid) {
				matrixRows.add(Arrays.copyOf(row, this.width));
			}
		} else {
			throw new RuntimeException("No values found");
		}
		
		this.matrix = matrixRows.toArray((T[][]) Array.newInstance(example.getClass(), 0, 0));
		
	}
	
	
	public T getValueAt(Coords coords) {
		return this.matrix[coords.getCol()][coords.getRow()];
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public boolean isInBounds(Coords coords) {
		return coords.getCol() >= 0 &&
				coords.getCol() < this.width &&
				coords.getRow() >= 0 &&
				coords.getRow() < this.height;
	}
	
}
