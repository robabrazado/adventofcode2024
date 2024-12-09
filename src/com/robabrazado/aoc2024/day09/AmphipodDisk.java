package com.robabrazado.aoc2024.day09;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AmphipodDisk {
	
	private BlockNode firstNode;
	
	public AmphipodDisk(String diskMap) {
		char[] chars = diskMap.toCharArray(); // char - 48 -> int
		int fileCounter = 0;
		boolean isFile = true;
		BlockNode zeroBlock = new BlockNode(0);
		BlockNode lastBlock = zeroBlock;
		
		for (char c : chars) {
			int size = c - 48;
			if (size > 0) { // Only store a node if there are blocks
				BlockNode newBlock = null;
				if (isFile) {
					newBlock = new BlockNode(size, fileCounter);
					fileCounter++;
				} else {
					newBlock = new BlockNode(size);
				}
				lastBlock.setNext(newBlock);
				lastBlock = newBlock;
			}
			isFile = !isFile;
		}
		this.firstNode = zeroBlock.getNext();
	}
	
	public void defragment() {
		BlockNode writeTo = AmphipodDisk.getNextFreeSpaceFragment(this.firstNode);
		BlockNode readFrom = AmphipodDisk.getPrevFile(this.firstNode.getFinal());
		
		while (writeTo != null && readFrom != null) {
			int writeSize = writeTo.getSize();
			int readSize = readFrom.getSize();
			int fileId = readFrom.getFileId();
			
			if (writeSize < readSize) {
				// Only taking a chunk from the readFrom node; writeTo is leaving the list (being overwritten)
				BlockNode newFileNode = new BlockNode(writeSize, fileId);
				if (writeTo.hasPrev()) {
					writeTo.getPrev().setNext(newFileNode);
				}
				newFileNode.setNext(writeTo.getNext());
				readFrom.decreaseSize(writeSize);
				writeTo = newFileNode;
			} else {
				// Move the entire readFrom node and shrink the writeTo
				BlockNode nextRead = readFrom.getPrev();
				nextRead.setNext(readFrom.getNext());
				if (writeTo.hasPrev()) {
					writeTo.getPrev().setNext(readFrom);
				}
				readFrom.setNext(writeTo);
				writeTo.decreaseSize(readSize);
				
				// If the writeTo has no more space left, just take it out of the list
				if (writeTo.getSize() == 0) {
					readFrom.setNext(writeTo.getNext());
				}
				
				readFrom = nextRead;
			}
			
			writeTo = AmphipodDisk.getNextFreeSpaceFragment(writeTo);
			readFrom = AmphipodDisk.getPrevFile(readFrom);
		} // We either ran out of files to read from or ran out of space fragments to write to
		return;
	}
	
	public void defragment2() {
		Map<Integer, BlockNode> fileNodeMap = new HashMap<Integer, BlockNode>();
		{
			BlockNode node = this.firstNode;
			while (node != null) {
				if (!node.isFreeSpace()) {
					fileNodeMap.put(node.getFileId(), node);
				}
				node = node.getNext();
			}
		}
		List<Integer> fileIds = new ArrayList<Integer>(fileNodeMap.keySet());
		Collections.sort(fileIds);
		Collections.reverse(fileIds);
		
		for (int fileId : fileIds) {
			BlockNode fileNode = fileNodeMap.get(fileId);
			int size = fileNode.getSize();
			BlockNode freeSpace = this.getFirstFreeSpaceToTheLeft(size, fileId);
			if (freeSpace != null) {
				// Move the file node to the new location and leave free space in its wake
				BlockNode newFreeSpace = new BlockNode(size);
				
				if (fileNode.hasPrev()) {
					fileNode.getPrev().setNext(newFreeSpace);
				}
				newFreeSpace.setNext(fileNode.getNext());
				
				if (freeSpace.hasPrev()) {
					freeSpace.getPrev().setNext(fileNode);
				}
				fileNode.setNext(freeSpace);
				freeSpace.decreaseSize(size);
				
				// If the free space was completely filled, remove the node from the list
				if (freeSpace.getSize() == 0) {
					fileNode.setNext(freeSpace.getNext());
				}
			}
		}
		return;
	}
	
	// Find the next (as of start) free space node that has a file node after it, or return null if none
	private static BlockNode getNextFreeSpaceFragment(BlockNode start) {
		BlockNode nextFrag = start;
		
		while (nextFrag != null && !nextFrag.isFreeSpace()) {
			nextFrag = nextFrag.getNext();
		}
		
		// If nextFrag has no subsequent file node, it's not a fragment, and we're done here
		if (AmphipodDisk.getNextFile(nextFrag) == null) {
			nextFrag = null;
		}
		
		return nextFrag;
	}
	
	// Find the next (as of start) file node, or return null if none
	private static BlockNode getNextFile(BlockNode start) {
		BlockNode nextFile = start;
		while (nextFile != null && nextFile.isFreeSpace()) {
			nextFile = nextFile.getNext();
		}
		return nextFile;
	}
	
	// Find the previous (as of end) file node, or return null if none
	private static BlockNode getPrevFile(BlockNode end) {
		BlockNode prevFile = end;
		while (prevFile != null && prevFile.isFreeSpace()) {
			prevFile = prevFile.getPrev();
		}
		return prevFile;
	}
	
	// Returns first free space of specified size or greater that is left of the specified file (or portion thereof), or return null if none
	private BlockNode getFirstFreeSpaceToTheLeft(int size, int fileId) {
		BlockNode firstSpace = null;
		BlockNode thisNode = this.firstNode;
		boolean doneLooking = false;
		
		while (thisNode != null && firstSpace == null && !doneLooking) {
			if (thisNode.isFreeSpace() && thisNode.getSize() >= size) {
				firstSpace = thisNode;
				doneLooking = true;
			} else if (thisNode.getFileId() == fileId) {
				doneLooking = true;
			}
			thisNode = thisNode.getNext();
		}
		return firstSpace;
	}
	
	public String checksum() {
		BigInteger checksum = BigInteger.ZERO;
		int positionCounter = 0;
		BlockNode thisNode = this.firstNode;
		
		while (thisNode != null) {
			int size = thisNode.getSize();
			if (!thisNode.isFreeSpace()) {
				int fileId = thisNode.getFileId();
				for (int i = 0; i < size; i++) {
					checksum = checksum.add(BigInteger.valueOf(positionCounter * fileId));
					positionCounter++;
				}
			} else {
				positionCounter += size;
			}
			
			thisNode = thisNode.getNext();
		}
		
		return checksum.toString();
	}
	
	@Override
	public String toString() {
		StringBuilder strb = new StringBuilder("|");
		
		BlockNode thisBlock = this.firstNode;
		while (thisBlock != null) {
			int size = thisBlock.getSize();
			String s = ".";
			if (!thisBlock.isFreeSpace()) {
				s = String.valueOf(thisBlock.getFileId());
			}
			for (int i = 0; i < size; i++) {
				strb.append(s).append("|");
			}
			thisBlock = thisBlock.getNext();
		}
		
		return strb.toString();
	}
}
