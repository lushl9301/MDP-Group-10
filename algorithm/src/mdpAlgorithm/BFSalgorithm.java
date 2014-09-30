package mdpAlgorithm;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class BFSalgorithm implements Iterator<String> {
	private Set<String> visited = new HashSet<String>();
    private Queue<String> queue = new LinkedList<String>();
    private BFSGraph graph;

    public BFSalgorithm(BFSGraph g, String startingVertex) {
        this.graph = g;
        this.queue.add(startingVertex);
        this.visited.add(startingVertex);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasNext() {
        return !this.queue.isEmpty();
    }

    @Override
    public String next() {
        //removes from front of queue
        String next = queue.remove(); 
        for (String neighbor : this.graph.getNeighbors(next)) {
            if (!this.visited.contains(neighbor)) {
                this.queue.add(neighbor);
                this.visited.add(neighbor);
            }
        }
        return next;
    }
}
