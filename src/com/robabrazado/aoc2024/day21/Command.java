package com.robabrazado.aoc2024.day21;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.robabrazado.aoc2024.grid.Dir;

public enum Command {
	UP		('^', Dir.N),
	DOWN	('v', Dir.S),
	LEFT	('<', Dir.W),
	RIGHT	('>', Dir.E),
	ACT		('A', (Dir) null);
	
	final char c;
	final Dir d;
	
	Command(char c, Dir d) {
		this.c = c;
		this.d = d;
	}
	
	public static Command getCommandByDir(Dir d) {
		switch (d) {
		case N:
			return UP;
		case S:
			return DOWN;
		case W:
			return LEFT;
		case E:
			return RIGHT;
		default:
			throw new RuntimeException("Unsupported direction: " + d.name());
		}
	}
	
	public List<Command> getCommandsByDirs(Collection<Dir> dirs) {
		List<Command> result = new ArrayList<Command>();
		if (dirs != null) {
			for (Dir d : dirs) {
				result.add(Command.getCommandByDir(d));
			}
		}
		return result;
	}
}
