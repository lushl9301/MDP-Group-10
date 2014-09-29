package mdpAlgorithm;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
public class TestDijkstraAlgorithm {

	private static List<DijVertex> nodes;
	private static List<DijEdge> edges;

	public static void testExecute() {
		nodes = new ArrayList<DijVertex>();
		edges = new ArrayList<DijEdge>();
		for (int i = 0; i < 12; i++) {
			DijVertex location = new DijVertex("Node_" + i, "Node_" + i);
			nodes.add(location);
		}
    
//    boolean flag = true;
//    String edgeName;
//    int counter = 0;
//    // map 20 x 15
//    for (int i = 1; i < 21; i++) {
//		for (int j = 1; j < 16; j++) {
//			
//			flag = true;
//			// robot 3x3
//			for (int x = i; x < i+3; x++) {
//				for (int y = j; y < j+3; y++) {
//					if(mapdescriptor3[counter] == 2) {
//						flag = false;
//						return;
//					}
//				}
//			}
//			
//			if(flag == true ) {
//				edgeName = "Edge"+i+"_"+j;
//				addLane(edgeName, i,?, 1);
//			}
//			
//			counter ++;
//		}
//	}
    
    
    addLane("Edge_1", 0, 1, 1);
    addLane("Edge_2", 0, 10, 12);
    addLane("Edge_3", 1, 2, 12);
    addLane("Edge_4", 1, 3, 2);
    addLane("Edge_5", 3, 7, 1);
    addLane("Edge_6", 5, 8, 1);
    addLane("Edge_7", 8, 9, 1);
    addLane("Edge_8", 7, 9, 1);
    addLane("Edge_9", 4, 9, 1);
    addLane("Edge_10", 9, 10, 1);
    addLane("Edge_11", 1, 10, 15);

    // Lets check from location Loc_1 to Loc_10
    DijGraph graph = new DijGraph(nodes, edges);
    DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);
    dijkstra.execute(nodes.get(0));
    LinkedList<DijVertex> path = dijkstra.getPath(nodes.get(10));    
    
	for (DijVertex vertex : path) {
    	System.out.println(vertex);
    }
    
  }

  private static void addLane(String laneId, int sourceLocNo, int destLocNo, int duration) {
	  DijEdge lane = new DijEdge(laneId,nodes.get(sourceLocNo), nodes.get(destLocNo), duration);
	  edges.add(lane);
  }
  
  public static void main(String[] args) {
	  testExecute();
  }
} 