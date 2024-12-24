package com.robabrazado.aoc2024.day23;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.stream.Stream;

/*
 * This is just a complete rewrite from my first attempt, which I thought would be easy to
 * just dash off, but then it turned out that I misunderstood the assignment.
 */
/*
 * Much later: why didn't I just make a real-ass graph with real-ass nodes?!
 */
public class LanGraph {
	private static final Pattern PARSE_PATTERN = Pattern.compile("^([a-z]{2})-([a-z]{2})$");
	private final Map<String, Set<String>> connections = new HashMap<String, Set<String>>();
	
	public LanGraph(Stream<String> puzzleInput) {
		Iterator<String> it = puzzleInput.iterator();
		while (it.hasNext()) {
			String line = it.next();
			Matcher m = PARSE_PATTERN.matcher(line);
			if (m.find()) {
				this.connect(m.group(1), m.group(2));
			} else {
				throw new RuntimeException("Unrecognized LAN connection input: " + line);
			}
		}
		return;
	}
	
	// Returns set of three-member interconnected computers
	public Set<Set<String>> getTrios() {
		Set<Set<String>> result = new HashSet<Set<String>>();
		Set<String> allComputers = this.connections.keySet();
		
		for (String base : allComputers) {
			Set<String> baseSet = Collections.singleton(base);
			Set<String> neighbors = this.connections.get(base);
			for (String neighbor : neighbors) {
				Set<String> checkSet = new HashSet<String>(baseSet);
				checkSet.add(neighbor);
				Set<String> thirds = this.sharedConnections(checkSet);
				for (String third : thirds) {
					Set<String> trio = new HashSet<String>(checkSet);
					trio.add(third);
					result.add(trio); // Set contract should ignore duplicate trios
				}
			}
		}
		
		return result;
	}
	
	public Set<String> getLargestInterconnectedNetwork() {
		/* First off, any computer connected to another computer will be part of
		 * an interconnected network, since A -> B means B -> A at least.
		 * 
		 * Any given computer C part of an interconnected network will be at
		 * least connected to all its fellow members, so the fellow members
		 * will (a) all be in its connection list and (b) connected to each other.
		 * So, the computer with the most connections that are connected to each
		 * other will be in the largest network. This will be a many-way tie.
		 */
		Set<String> largestNetwork = new HashSet<String>();
		int largestSize = 0;
		Set<String> computers = this.connections.keySet();
		for (String computer : computers) {
			Set<String> interconnections = new HashSet<String>();
			interconnections.add(computer);
			Set<String> connections = this.connections.get(computer);
			for (String connection : connections) {
				if (this.connections.get(connection).containsAll(interconnections)) {
					interconnections.add(connection);
				}
			}
			int size = interconnections.size();
			if (size > largestSize) {
				largestSize = size;
				largestNetwork = interconnections;
			}
		}
		return largestNetwork;
	}
	
	// Returns a List of computers connected to ALL specified computers
	// If no shared connections, returns an empty set
	// Also...if a complete interconnected set, also returns an empty set (makes me wonder how useful this is)
	// This also doesn't complain if the specified computers aren't themselves connected. This is a fragile method.
	public Set<String> sharedConnections(Set<String> checkComputers) {
		Set<String> result = new HashSet<String>();
		int numToCheck = checkComputers.size();
		
		if (numToCheck > 0) {
			Iterator<String> checkerator = checkComputers.iterator();
			// Start things off with the first computer to check
			result.addAll(this.connections.get(checkerator.next()));
			
			// Find intersections with the rest
			while (checkerator.hasNext()) {
				result.retainAll(this.connections.get(checkerator.next()));
			}
		}
		
		return result;
	}
	
	@Override
	public String toString() {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		Set<String> keys = this.connections.keySet();
		for (String key : keys) {
			pw.print(key + ": ");
			Set<String> values = this.connections.get(key);
			for (String value : values) {
				pw.print("[");
				pw.print(value);
				pw.print("]");
			}
			pw.println();
		}
		
		return sw.toString();
	}
	
	private void connect(String computer1, String computer2) {
		this.connectTo(computer1, computer2);
		this.connectTo(computer2, computer1);
		return;
	}
	
	private void connectTo(String fromComputer, String toComputer) {
		if (!this.connections.containsKey(fromComputer)) {
			this.connections.put(fromComputer, new HashSet<String>());
		}
		this.connections.get(fromComputer).add(toComputer);
		return;
	}
	
	public static String passwordify(Set<String> network) {
		StringBuilder strb = new StringBuilder();
		network = new TreeSet<String>(network);
		
		for (String s : network) {
			strb.append(s).append(',');
		}
		strb.deleteCharAt(strb.length() - 1);
		return strb.toString();
	}
}
