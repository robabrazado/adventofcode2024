package com.robabrazado.aoc2024.day20;

import java.util.Comparator;
import java.util.function.Function;

import com.robabrazado.aoc2024.grid.Coords;

public class Cell {
	private final Coords coords;
	private Integer startDistance = null; // null is infinity
	private Integer endDistance = null; // null is infinity
	
	public Cell(Coords c) {
		this.coords = c;
		return;
	}
	
	public Coords getCoords() {
		return this.coords;
	}

	public Integer getStartDistance() {
		return startDistance;
	}

	public void setStartDistance(Integer startDistance) {
		this.startDistance = startDistance;
	}

	public Integer getEndDistance() {
		return endDistance;
	}

	public void setEndDistance(Integer endDistance) {
		this.endDistance = endDistance;
	}
	
	public Integer getDistance(DistanceType type) {
		switch (type) {
		case START:
			return this.getStartDistance();
		case END:
			return this.getEndDistance();
		default: // Uh oh
			throw new RuntimeException("Unsupported distance type: " + type.name());
		}
	}
	
	public void setDistance(Integer distance, DistanceType type) {
		switch (type) {
		case START:
			this.setStartDistance(distance);
			break;
		case END:
			this.setEndDistance(distance);
			break;
		default: // Uh oh
			throw new RuntimeException("Unsupported distance type: " + type.name());
		}
	}
	
	public enum DistanceType {
		START		(),
		END			();
	}
	
	public abstract class CellComparatorFactory {
		public static Comparator<Cell> getComparator(DistanceType type) {
			Function<Cell, Integer> func;
			switch (type) {
			case START:
				func = Cell::getStartDistance;
				break;
			case END:
				func = Cell::getEndDistance;
				break;
			default: // Uh oh
				throw new RuntimeException("Unsupported distance type: " + type.name());
			}
			return Comparator.comparing(func, Comparator.nullsLast(Comparator.naturalOrder()));
		}
	}
}
