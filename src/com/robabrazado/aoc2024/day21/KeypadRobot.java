package com.robabrazado.aoc2024.day21;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.robabrazado.aoc2024.grid.Coords;
import com.robabrazado.aoc2024.grid.Dir;

/*
 * I got quite far up my own ass designing with solution under a different
 * (and I guess ultimately more confusing) object structure, so I'm just
 * starting over.
 * 
 * The new world is this main object, the keypad robot. It is looking at
 * and pushing buttons on a keypad, and this is the keypad I'm referring
 * to when I talk about the robot's keypad. It is also assumed to be
 * operated by a keypad, but we don't talk about that one. Instead, the
 * robot accepts "commands." The structure is a chain of robots accepting
 * commands and operating a keypad...it just happens that most of these
 * robots are, in the fiction, accepting these commands via other keypads.
 * But object-wise, this is just a chain of robots controlling robots.
 * The "base" robot is the one at the door; it is not controlling another
 * robot. Any given robot in the chain is the "controller." If the
 * controller is controlling another robot, that second robot is the
 * "worker." I think those are all the levels I need to know about.
 * The robot at the other (non-base) end of the chain is accepting
 * commands from the player; that's the one we'll be interfacing with.
 * Information we need from further down the chain will be passed
 * worker to worker as necessary, with the chain ending at the base robot.
 * 
 * For the moment, all the methods that return multiple commands are only
 * returning the shortest commands, on the assumption that one of the
 * shortest commands for me will be shortest for my controller, as well.
 * If that needs to change, the method signatures are already set up.
 */
public class KeypadRobot {
	private final KeypadType keypadType;
	private final Set<Key> keys;
	private final int width;
	private final int height;
	
	private KeypadRobot worker = null;
	
	private Map<Character, Key> charKeyMap = null;
	private Map<Coords, Key> coordsKeyMap = null;
	private Map<CharPair, List<String>> myCommandCache = new HashMap<CharPair, List<String>>(); // For now just the shortest commands are cached
	
	
	public KeypadRobot(KeypadType type) {
		this.keypadType = type;
		Set<Key> tempKeys = new HashSet<Key>();
		char[][] grid = type.keyGrid;
		this.height = grid.length;
		this.width = 3; // Boo
		
		for (int row = 0; row < this.height; row++) {
			for (int col = 0; col < this.width; col++) {
				char c = grid[row][col];
				if (c != 0) {
					Coords coords = new Coords(col, row);
					Key key = new Key(c, coords);
					tempKeys.add(key);
				}
			}
		}
		this.keys = Collections.unmodifiableSet(tempKeys);
		return;
	}
	
	// Returns self
	public KeypadRobot setWorker(KeypadRobot worker) {
		KeypadRobot checkCircular = this;
		while (checkCircular != null) {
			if (checkCircular == worker) {
				throw new RuntimeException("Robot cannot be its own worker. Such is Robot existence.");
			} else {
				checkCircular = checkCircular.worker;
			}
		}
		this.worker = worker;
		return this;
	}
	
	// Assumes starting at 'A' position; terminates each button press with ACT command
	public String getShortestCommandForBaseInput(String input) {
		List<String> commands = this.getCommandsForBaseInput(input);
		if (commands.isEmpty()) {
			throw new RuntimeException("Robot has no commands for base input " + input);
		}
		return commands.get(0);
	}
	
	// Assumes starting at 'A' position; terminates each button press with ACT command
	public List<String> getCommandsForBaseInput(String input) {
		List<String> result;
		if (this.worker != null) {
			List<String> workerCommands = worker.getCommandsForBaseInput(input);
			if (workerCommands.isEmpty()) {
				throw new RuntimeException("Robot's worker reports no command options for base input " + input);
			}
			result = new ArrayList<String>();
			result = workerCommands.stream()
					.map(workerCommand -> this.getShortestCommandForMyInput(workerCommand))
					.collect(Collectors.toList());
		} else {
			// I AM BASE
			result = this.getCommandsForMyInput(input);
		}
		Collections.sort(result, Comparator.comparingInt(String::length));
		return result;
	}
	
	// Assumes starting at 'A' position; terminates each button press with ACT command
	public String getShortestCommandForMyInput(String input) {
		List<String> commands = this.getCommandsForMyInput(input);
		if (commands.size() < 1) {
			throw new RuntimeException("Robot has no commands for this input");
		}
		return commands.get(0);
	}
	
	// Assumes starting at 'A' position; terminates each button press with ACT command
	public List<String> getCommandsForMyInput(String input) {
		List<String> commands = new ArrayList<String>(Collections.singletonList(""));
		char[] chars = input.toCharArray();
		char from = 'A';
		for (char to : chars) {
			List<String> newCommands = this.getCommandsForMyInput(from, to, true);
			if (newCommands.isEmpty()) {
				throw new RuntimeException("Robot found no command string from " + from + " to " + to);
			}
			List<String> oldCommands = new ArrayList<String>(commands);
			commands.clear();
			for (String oldCommand : oldCommands) {
				for (String newCommand : newCommands) {
					commands.add(oldCommand + newCommand);
				}
			}
			from = to;
		}
		
		return commands;
	}
	
	public List<String> getCommandsForMyInput(char from, char to, boolean terminateWithAct) {
		CharPair pair = new CharPair(from, to);
		if (!this.myCommandCache.containsKey(pair)) {
			List<String> commands = new ArrayList<String>();
			
			this.checkKey(from);
			Key fromKey = this.charKeyMap().get(from);
			this.checkKey(to);
			Key toKey = this.charKeyMap().get(to);
			
			Coords fromCoords = fromKey.position;
			Coords toCoords = toKey.position;
			Coords offset = fromCoords.getOffsetTo(toCoords);
			
			/* Okay, this may be kinda weird. So at this point, I know the row
			 * and col offset from the start to the end, so...first off, I
			 * know the shortest command will have a length of the taxicab
			 * distance away. Without other constraints, all commands that
			 * lead from start to end are valid, and there's only two ways
			 * to go (row offset and col offset), so that sounds like a
			 * string of bits to me. So the number of possible shortest
			 * commands is 2^N where N is the taxicab distance. So I'm
			 * generating those permutations and saving the ones that
			 * don't enter the forbidden zone where there's no key.
			 * Also any arrangement that has too many of one kind of
			 * offset are out (because they don't lead to end).
			 */
			Dir colOffsetDir = offset.getCol() > 0 ? Dir.E : Dir.W;
			Command colMove = Command.getCommandByDir(colOffsetDir);
			Dir rowOffsetDir = offset.getRow() > 0 ? Dir.S : Dir.N;
			Command rowMove = Command.getCommandByDir(rowOffsetDir);
			int maxColOffset = Math.abs(offset.getCol());
			int maxRowOffset = Math.abs(offset.getRow());
			int taxicabDistance = maxColOffset + maxRowOffset; // Number of bits in the command string
			if (taxicabDistance >= 31) {
				throw new RuntimeException("Robot is not equipped for this level of keypad grandiosity");
			} else if (taxicabDistance > 0) {
				int numCombos = 1 << taxicabDistance;
				for (int commandBits = 0; commandBits < numCombos; commandBits++) {
					// Arbitrarily, I'm calling col 0 and row 1.
					int colCount = 0;
					int rowCount = 0;
					StringBuilder strb = new StringBuilder();
					Coords cursor = fromKey.position;
					boolean canUse = true;
					for (int idxOffset = 0; idxOffset < taxicabDistance && canUse; idxOffset++) { // Check each bit (highest to lowest) and assemble command
						int checkBit = (numCombos / 2) >> idxOffset;
						if ((commandBits & checkBit) == 0) {
							// Column
							strb.append(colMove.c);
							cursor = cursor.applyOffset(colOffsetDir);
							colCount++;
						} else {
							// Row
							strb.append(rowMove.c);
							cursor = cursor.applyOffset(rowOffsetDir);
							rowCount++;
						}
						canUse = this.isInBounds(cursor) && this.coordsKeyMap().containsKey(cursor) &&
								colCount <= maxColOffset && rowCount <= maxRowOffset;
					}
					if (canUse) {
						if (terminateWithAct) {
							strb.append(Command.ACT.c);
						}
						commands.add(strb.toString());
					}
				}
			} else {
				// From and to are the same? I guess?
				commands.add("");
			}
			
			this.myCommandCache.put(pair, commands);
		}
		return this.myCommandCache.get(pair);
	}
	
	protected void checkKey(char c) {
		if (!this.charKeyMap().containsKey(c)) {
			throw new RuntimeException(this.keypadType.name() + " Robot does not see any '" + c + "' key");
		}
	}
	
	protected void checkKey(Key key) {
		if (!this.keys.contains(key)) {
			throw new RuntimeException(this.keypadType.name() + " Robot does not see key " + key.toString());
		}
	}
	
	protected boolean isInBounds(Coords c) {
		int col = c.getCol();
		int row = c.getRow();
		return col >= 0 && col < this.width &&
				row >= 0 && row < this.height;
	}
	
	protected Map<Character, Key> charKeyMap() {
		if (this.charKeyMap == null) {
			this.charKeyMap = new HashMap<Character, Key>();
			this.keys.stream().forEach(key -> this.charKeyMap.put(key.c, key));
		}
		return this.charKeyMap;
	}
	
	protected Map<Coords, Key> coordsKeyMap() {
		if (this.coordsKeyMap == null) {
			this.coordsKeyMap = new HashMap<Coords, Key>();
			this.keys.stream().forEach(key -> this.coordsKeyMap.put(key.position, key));
		}
		return this.coordsKeyMap;
	}
	
	public enum KeypadType {
		NUMERIC			(new char[][] {{'7', '8', '9'}, {'4', '5', '6'}, {'1', '2', '3'}, {0, '0', 'A'}}),
		DIRECTIONAL		(new char[][] {{0, '^', 'A'}, {'<', 'v', '>'}});
		
		final char[][] keyGrid;
		
		KeypadType(char[][] keys) {
			this.keyGrid = keys;
			return;
		}
		
	}
	
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
		
		static Command getCommandByDir(Dir d) {
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
	}
	
	class Key {
		final char c;
		final Coords position;
		
		Key(char c, Coords position) {
			if (c == 0) {
				throw new RuntimeException("Robot is forbidden from gazing upon the null key");
			}
			this.c = c;
			this.position = position;
			return;
		}
		
		@Override
		public String toString() {
			return String.format("'%c' key at %s", this.c, this.position.toString());
		}
	}
	
	record CharPair(char from, char to) {}
}
