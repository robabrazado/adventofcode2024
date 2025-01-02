package com.robabrazado.aoc2024.day21;

public abstract class ControlLink {
	protected final String name;
	protected ControlLink worker = null;
	protected ControlLink controller = null;
	
	protected ControlLink(String name) {
		this.name = name;
		return;
	}
	
	public boolean hasWorker() {
		return this.worker != null;
	}
	
	// Returns non-null or throws
	public ControlLink getWorker() {
		if (this.hasWorker()) {
			return this.worker;
		} else {
			throw new RuntimeException(this.name + " has no worker");
		}
	}
	
	protected void clearWorker() {
		if (this.worker != null) {
			this.worker.controller = null;
		}
		this.worker = null;
		return;
	}
	
	public void setWorker(ControlLink newWorker) {
		this.clearWorker();
		if (newWorker != null) {
			worker.controller = this;
			this.worker = newWorker;
		}
		return;
	}
	
	public boolean hasController() {
		return this.controller != null;
	}
	
	// Returns non-null or throws
	public ControlLink getController() {
		if (this.controller != null) {
			return this.controller;
		} else {
			throw new RuntimeException(this.name + " has no controller");
		}
	}
	
	protected void clearController() {
		if (this.controller != null) {
			controller.worker = null;
		}
		this.controller = null;
		return;
	}
	
	protected void setController(ControlLink newController) {
		this.clearController();
		if (newController != null) {
			newController.worker = this;
			this.controller = newController;
		}
		return;
	}
}
