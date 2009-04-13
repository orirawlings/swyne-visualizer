package edu.iit.swyne.visualizer;

import processing.core.PApplet;

public class Edge {

	private PApplet p;
	private int color;
	private Node node1, node2;
	
	public Edge(Graph p, Node node1, Node node2) {
		this.p = p;
		this.node1 = node1;
		this.node2 = node2;
		init();
	}

	private void init() {
		color = p.color(0);
	}

	public void draw() {
		p.stroke(color);
		p.line(node1.getX(), node1.getY(), node2.getX(), node2.getY());
	}

}
