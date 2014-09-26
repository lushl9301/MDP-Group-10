package mdpAlgorithm;

import java.util.PriorityQueue;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;


class Vertex implements Comparable<Vertex> {
    public final String name;
    public Edge[] adjacencies;
    public double minDistance = Double.POSITIVE_INFINITY;
    public Vertex previous;
    public Vertex(String argName) { name = argName; }
    public String toString() { return name; }
    
    public int compareTo(Vertex other) {
        return Double.compare(minDistance, other.minDistance);
    }
}

class Edge {
    public final Vertex target;
    public final double weight;
    public Edge(Vertex argTarget, double argWeight)
    { target = argTarget; weight = argWeight; }
}


public class Dijkstra { // Based on Dijkstra algorithm	
	
    //public static void computePaths(Vertex source, Robot rob)
    public static void computePaths(Vertex startPoint) {
        startPoint.minDistance = 0.;
        PriorityQueue<Vertex> vertexQueue = new PriorityQueue<Vertex>();
      	vertexQueue.add(startPoint);

	while (!vertexQueue.isEmpty()) {
	    Vertex u = vertexQueue.poll();

            // Visit each edge exiting u
            for (Edge e : u.adjacencies) { 	// For-each loop to the edges that are adjacent to the current vertex
                Vertex v = e.target; 								// Sets the next target as vertex v
                double weight = e.weight; 							// Inserts the edge's weight
                double distanceThroughU = u.minDistance + weight; 	// Total dist from the startPoint is added to weight
				if (distanceThroughU < v.minDistance) { 			// If the distanceThruU is there (not infinity)
				    vertexQueue.remove(v);
				    v.minDistance = distanceThroughU ; 				// The minDistance is added to the vertex v
				    v.previous = u; 								// V's parent is u
				    vertexQueue.add(v); 							// Add v to the priorityQ
				}
            }
        }
    }

    public static List<Vertex> getShortestPathTo(Vertex target)  {
        List<Vertex> path = new ArrayList<Vertex>();
        for (Vertex vertex = target; vertex != null; vertex = vertex.previous)
            path.add(vertex);
        Collections.reverse(path);
        return path;
    }

    public static void main(String[] args) {
    	    	
    	/*	Map Descriptor 3: 0 unexplored, 1 explored&empty, 2 explored&obstacle
    	 *  
    	 *  MD3 for testing:
    	 * 
    	 	111111112000000
			111111112000000
			111111112000000
			111111112222000
			111111111111200
			222111111111200
			002111111111222
			002111111111111
			002111111111111
			002111111111111
			002111111111111
			002111111111111
			002111111111111
			002111111111111
			002111111111111
			000221112222111
			000021112002111
			000021112002111
			000021112002111
			000021112002111

    	 */

	    Vertex v0 = new Vertex("Redvile");
		Vertex v1 = new Vertex("Blueville");
		Vertex v2 = new Vertex("Greenville");
		Vertex v3 = new Vertex("Orangeville");
		Vertex v4 = new Vertex("Purpleville");
	
		v0.adjacencies = new Edge[]{ new Edge(v1, 5),
		                             new Edge(v2, 10),
	                               new Edge(v3, 8) };
		v1.adjacencies = new Edge[]{ new Edge(v0, 5),
		                             new Edge(v2, 3),
		                             new Edge(v4, 7) };
		v2.adjacencies = new Edge[]{ new Edge(v0, 10),
	                               new Edge(v1, 3) };
		v3.adjacencies = new Edge[]{ new Edge(v0, 8),
		                             new Edge(v4, 2) };
		v4.adjacencies = new Edge[]{ new Edge(v1, 7),
	                               new Edge(v3, 2) };
		
		/*
		 * 
		 Vertex currentGrid = new Vertex("Current Point");
		 
		 
		 currentGrid.adjacencies = new Edge[] { new Edge(north,1),
		 										new Edge(west, 1),
		 										new Edge(south, 1),
		 										new Edge(east, 1),
		 									  };
		 
		 Vertex[] vertices { currentGrid };
		 * 
		 */
	    
	    	
		Vertex[] vertices = { v0, v1, v2, v3, v4 };
	        computePaths(v0);
	        for (Vertex v : vertices) {
			    System.out.println("Distance to " + v + ": " + v.minDistance);
			    List<Vertex> path = getShortestPathTo(v);
			    System.out.println("Path: " + path);
        }
    }
}
