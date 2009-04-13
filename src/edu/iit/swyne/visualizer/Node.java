package edu.iit.swyne.visualizer;

import processing.core.PConstants;

public class Node {

	protected Graph p;
	private int color, textColor;
	private float x, y, width, height;
	protected String label;

	public Node(Graph p, float x, float y, int width, int height, String label) {
		this.p = p;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.label = label;
		init();
	}

	private void init() {
		color = p.color(255);
		textColor = p.color(0);
	}

	public void draw() {
		p.noStroke();
		p.fill(color);
		p.rectMode(PConstants.CENTER);
		p.rect(x, y, width, height);
		
		p.textAlign(PConstants.CENTER, PConstants.CENTER);
		p.fill(textColor);
		p.text(label, x, y);
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public void update() {
	}

	public boolean collide(int x, int y) {
		return x <= this.x+width/2 && x >= this.x-width/2 && y <= this.y+height/2 && y >= this.y-height/2;
	}

	public String getLabel() {
		return label;
	}

	public void setColor(int color) {
		this.color = color;
	}

}
