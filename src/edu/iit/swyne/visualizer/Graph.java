package edu.iit.swyne.visualizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import processing.core.PApplet;
import processing.core.PFont;

public class Graph extends PApplet {

	private static final long serialVersionUID = 3724380563287962874L;
	
	private int bg;
	private PFont font = loadFont("font.vlw");
	
	protected HashMap<String, Node> nodes = new HashMap<String, Node>();
	protected ArrayList<Edge> edges = new ArrayList<Edge>();
	
	public static void main(String[] args) {
		PApplet.main(new String[] { "--bgcolor=#FFFFFF", "edu.iit.swyne.visualizer.Graph" });
	}

	@Override
	public void setup() {
		bg = color(255, 150, 150);
		
		size(screen.width, screen.height, P2D);
		background(bg);
		textFont(font, 16);
		textSize(16);
	}
	
	@Override
	public void draw() {
		background(bg);
		
		// update nodes
		for (Iterator<Node> i = nodes.values().iterator(); i.hasNext();) {
			Node n = i.next();
			n.update();
		}
		
		
		// draw edges then nodes
		for (Iterator<Edge> i = edges.iterator(); i.hasNext();) {
			Edge e = i.next();
			e.draw();
		}
		for (Iterator<Node> i = nodes.values().iterator(); i.hasNext();) {
			Node n = i.next();
			n.draw();
		}
	}
	
	@Override
	public void mousePressed() {
		String name = "Node"+nodes.size();
		Node n = new Node(this, mouseX, mouseY, 60, 20, name);
		nodes.put(name, n);
		edges.add(new Edge(this, nodes.get((int) random(nodes.size()-1)), n));
	}
}
