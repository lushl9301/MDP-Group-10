package mdpAlgorithm;

public class DijEdge {
	private final String id; 
	private final DijVertex source;
	private final DijVertex destination;
	private final int weight; 
	
	public DijEdge(String id, DijVertex source, DijVertex destination, int weight) {
		this.id = id;
		this.source = source;
		this.destination = destination;
		this.weight = weight;
	}
	
	public String getId() {
		return id;
	}
	
	public DijVertex getDestination() {
		return destination;
	}
	
	public DijVertex getSource() {
		return source;
	}
	
	public int getWeight() {
		return weight;
	}
	
	@Override
	public String toString() {
		return source + " " + destination;
	}
} 