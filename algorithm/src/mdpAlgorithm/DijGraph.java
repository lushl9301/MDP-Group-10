package mdpAlgorithm;

import java.util.List;

public class DijGraph {
	private final List<DijVertex> vertexes;
	private final List<DijEdge> edges;
	
	public DijGraph(List<DijVertex> vertexes, List<DijEdge> edges) {
		this.vertexes = vertexes;
		this.edges = edges;
	}
	
	public List<DijVertex> getVertexes() {
		return vertexes;
	}
	
	public List<DijEdge> getEdges() {
		return edges;
	}
} 