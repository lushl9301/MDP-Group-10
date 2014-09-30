package mdpAlgorithm;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;

import org.junit.BeforeClass;

public class BFStesting {
	public static BFSGraph graph1;

    @BeforeClass
    public static void makeGraphs() {
        BFSGraph g = graph1 = new BFSGraph();
        g.addEdge("A", "B");
        g.addEdge("B", "C");
        g.addEdge("B", "D");
        g.addEdge("B", "A");
        g.addEdge("B", "E");
        g.addEdge("B", "F");
        g.addEdge("C", "A");
        g.addEdge("D", "C");
        g.addEdge("E", "B");
        g.addEdge("F", "B");
    }
    
    private void expectIteration(String answer, Iterator<String> it) {
        StringBuilder sb = new StringBuilder();
        while (it.hasNext()) {
            sb.append(' ').append(it.next());
        }
        assertEquals(answer, sb.substring(1));
    }
    
    public static void main(String[] args) {
    	//makeGraphs("A B C D E F", new BFSalgorithm(graph1, "A"));
    }
    
}
