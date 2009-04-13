package edu.iit.swyne.visualizer;

import java.util.Iterator;

import edu.iit.swyne.QueryBean;

public class Entity extends Node {
	
	private Iterator<String> neighbors;

	@SuppressWarnings("unchecked")
	public Entity(Graph p, float x, float y, int width, int height, String label) {
		super(p, x, y, width, height, label);
		neighbors = (new QueryBean()).getNeighbors(label);
	}

	public Node getNextNeighbor() throws NoMoreNeighborsException {
		if (neighbors.hasNext()) {
			String neighbor = neighbors.next();
			return new Entity(p, p.random(p.width), p.random(p.height), 60, 20, neighbor);
		}
		else throw new NoMoreNeighborsException("No more neighbors for entity: "+label);
	}

	public class NoMoreNeighborsException extends Exception {
		private static final long serialVersionUID = -6313487518295915336L;
	
		public NoMoreNeighborsException(String string) {
			super(string);
		}
	}

}
