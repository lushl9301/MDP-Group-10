package mdpAlgorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DijkstraAlgorithm {
	
	private final List<DijVertex> nodes;
	private final List<DijEdge> edges;
	private Set<DijVertex> settledNodes;
	private Set<DijVertex> unSettledNodes;
	private Map<DijVertex, DijVertex> predecessors;
	private Map<DijVertex, Integer> distance;
	
	public DijkstraAlgorithm(DijGraph graph) {
		// create a copy of the array so that we can operate on this array
		this.nodes = new ArrayList<DijVertex>(graph.getVertexes());
		this.edges = new ArrayList<DijEdge>(graph.getEdges());
	}
	
	public void execute(DijVertex source) {
		settledNodes = new HashSet<DijVertex>();
		unSettledNodes = new HashSet<DijVertex>();
		distance = new HashMap<DijVertex, Integer>();
		predecessors = new HashMap<DijVertex, DijVertex>();
		distance.put(source, 0);
		unSettledNodes.add(source);
		while (unSettledNodes.size() > 0) {
			DijVertex node = getMinimum(unSettledNodes);
			settledNodes.add(node);
			unSettledNodes.remove(node);
			findMinimalDistances(node);
		}
	}
	
	private void findMinimalDistances(DijVertex node) {
		List<DijVertex> adjacentNodes = getNeighbors(node);
		for (DijVertex target : adjacentNodes) {
			if (getShortestDistance(target) > getShortestDistance(node) + getDistance(node, target)) {
				if(predecessors.get(node) != null) {
		
					if( (Integer.parseInt(target.getName().toString().split("Node_")[1]) - Integer.parseInt(node.toString().split("Node_")[1])) != (Integer.parseInt(node.toString().split("Node_")[1]) - Integer.parseInt(predecessors.get(node).toString().split("Node_")[1]))) {
						distance.put(target, (getShortestDistance(node) + getDistance(node, target) + 1));
					}
					else
						distance.put(target, getShortestDistance(node) + getDistance(node, target));

				}
				else if(Integer.parseInt(node.toString().split("Node_")[1]) == 0 && Integer.parseInt(target.getName().toString().split("Node_")[1]) == 20) {
					distance.put(target, (getShortestDistance(node) + getDistance(node, target) + 1));
				}
				else distance.put(target, getShortestDistance(node) + getDistance(node, target));
				predecessors.put(target, node);
				unSettledNodes.add(target);
			}
		}
	}
	
	private int getDistance(DijVertex node, DijVertex target) {
		for (DijEdge edge : edges) {
			if (edge.getSource().equals(node) && edge.getDestination().equals(target)) {
				return edge.getWeight();
			}	
		}
		throw new RuntimeException("Should not happen");
	}
	
	private List<DijVertex> getNeighbors(DijVertex node) {
		List<DijVertex> neighbors = new ArrayList<DijVertex>();
		for (DijEdge edge : edges) {
			if (edge.getSource().equals(node) && !isSettled(edge.getDestination())) {
				neighbors.add(edge.getDestination());
			}
		}
		return neighbors;
	}
	
	private DijVertex getMinimum(Set<DijVertex> vertexes) {
		DijVertex minimum = null;
		for (DijVertex vertex : vertexes) {
			if (minimum == null) {
				minimum = vertex;
			} else {
				if (getShortestDistance(vertex) < getShortestDistance(minimum)) {
					minimum = vertex;
				}
			}
		}
		return minimum;
	}
	
	private boolean isSettled(DijVertex vertex) {
		return settledNodes.contains(vertex);
	}
	
	private int getShortestDistance(DijVertex destination) {
		Integer d = distance.get(destination);
		if (d == null) {
			return Integer.MAX_VALUE;
		} else {
			return d;
		}
	}
	
	/*
	 * This method returns the path from the source to the selected target and
	 * NULL if no path exists
	 */
	public LinkedList<DijVertex> getPath(DijVertex target) {
		LinkedList<DijVertex> path = new LinkedList<DijVertex>();
		DijVertex step = target;
		// check if a path exists
		if (predecessors.get(step) == null) {
			return null;
		}
		path.add(step);
		while (predecessors.get(step) != null) {
			step = predecessors.get(step);
			path.add(step);
		}
		// Put it into the correct order
		Collections.reverse(path);
		return path;
	}
}