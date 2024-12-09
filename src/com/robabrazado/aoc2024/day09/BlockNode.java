package com.robabrazado.aoc2024.day09;

// Traverse like a double-linked list
public class BlockNode {
	public static final int FREE_SPACE_FILE_ID = -1;
	
	private final int fileId;
	private int size = 0;
	private BlockNode next = null;
	private BlockNode prev = null;
	
	// Construct "free space" block
	public BlockNode(int size) {
		this(size, BlockNode.FREE_SPACE_FILE_ID);
	}
	
	// Construct file block
	public BlockNode(int size, int fileId) {
		this.fileId = fileId;
		this.size = size;
		return;
	}
	
	public int getFileId() {
		return this.fileId;
	}
	
	public int getSize() {
		return this.size;
	}
	
	public void setSize(int newSize) {
		this.size = newSize;
		return;
	}
	
	public int increaseSize(int by) {
		return this.size += by;
	}
	
	public int decreaseSize(int by) {
		return this.size -= by;
	}
	
	public BlockNode getNext() {
		return this.next;
	}
	
	public BlockNode getFinal() {
		BlockNode finalNode = this;
		while (finalNode.hasNext()) {
			finalNode = finalNode.getNext();
		}
		return finalNode;
	}
	
	public void setNext(BlockNode node) {
		this.next = node;
		if (node != null) {
			node.prev = this;
		}
		return;
	}
	
	public BlockNode getPrev() {
		return this.prev;
	}
	
	public boolean hasNext() {
		return this.next != null;
	}
	
	public boolean hasPrev() {
		return this.prev != null;
	}
	
	public boolean isFreeSpace() {
		return this.fileId == BlockNode.FREE_SPACE_FILE_ID;
	}
}
