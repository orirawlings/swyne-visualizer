package edu.iit.swyne.visualizer;

import java.util.Iterator;

import edu.iit.swyne.visualizer.Entity.NoMoreNeighborsException;

import processing.core.PApplet;

public class EntityGraph extends Graph {

	private static final long serialVersionUID = -1398596555434867556L;
	
	private Node selected;

	private int selectedColor;
	private int defaultColor;
	
	
	@Override
	public void setup() {
		super.setup();
		
		selectedColor = color(255, 100, 100);
		defaultColor = color(255);
		
		nodes.put("Chicago", new Entity(this, random(width), random(height), 60, 20, "Chicago"));
		selected = nodes.get("Chicago");
	}
	
	@Override
	public void draw() {
		super.draw();
		selected.draw();
	}
	
	@Override
	public void mousePressed() {
		for (Iterator<Node> i = nodes.values().iterator(); i.hasNext();) {
			Node n = i.next();
			if (n instanceof Entity && n.collide(mouseX, mouseY)) {
				select(n);
				try {
					addLink(n, ((Entity) n).getNextNeighbor());
				} catch (NoMoreNeighborsException e) {
					// It's ok, there's just no more neighbors
				}
				break;
			}
		}
	}

	private void select(Node n) {
		if (!selected.equals(n)) {
			selected.setColor(defaultColor);
			n.setColor(selectedColor);
			selected = n;
		}
	}

	private void addLink(Node n1, Node n2) {
		Node source = nodes.get(n1.getLabel());
		if (source == null) {
			nodes.put(n1.getLabel(), n1);
			source = nodes.get(n1.getLabel());
		}
		
		Node target = nodes.get(n2.getLabel());
		if (target == null) {
			nodes.put(n2.getLabel(), n2);
			target = nodes.get(n2.getLabel());
		}
		
		edges.add(new Edge(this, source, target));
	}
	
	public static void main(String[] args) {
		PApplet.main(new String[] { "--bgcolor=#FFFFFF", "edu.iit.swyne.visualizer.EntityGraph" });
	}
}
