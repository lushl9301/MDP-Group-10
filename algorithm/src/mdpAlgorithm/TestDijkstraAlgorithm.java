package mdpAlgorithm;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
public class TestDijkstraAlgorithm {

	private static List<DijVertex> nodes;
	private static List<DijEdge> edges;
	int[][] mapDescriptor3 = new int[15][20];
	
	public static void testExecute() {
		// initialise the nodes
		nodes = new ArrayList<DijVertex>();
		edges = new ArrayList<DijEdge>();
		
		for(int i = 0; i < 20; i++) {
			for (int j = 0; j< 15; j++) {
				DijVertex location = new DijVertex("Node_" + i +"_"+ j, "Node_" + i +"_"+ j);
				nodes.add(location);
			}
		}
		
		
		
		// hard code test values
		String md1 = "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff";
		String md2= "0000000000000000000000000000000000000000000000000000000000000000000000040000";
		
		// get md3
		MapGrid m = new MapGrid();
		int[][] md3 = m.getMapDesc3Testing(md1, md2);
		
		// print the map
		for(int i = 0; i < 20; i++) {
			for (int j = 0; j< 15; j++) {
				System.out.print(md3[j][i]);
			}
			System.out.println();
		}
		
		// process map and add edges for dijkstra calculation
		
		boolean flag = true;
		String edgeName;
		//System.out.println(md3[14][19]+"testsetset"); //[breadth][length]
		//md3[14][19] this is the bottom right hand corner
		//md3[0][19] this is the top right hand corner
		//md3[14][0] this is the bottom left hand corner

		
		for (int j = 0; j< 13; j++) {
			for(int i = 0; i < 18; i++) {
				for(int y = i; y < i+3; y++) {
					for (int x = j; x < j+3; x++) {
						if(md3[j][i] == 2) {
							flag = false;
							return;
						}	
					}
				}
				if(flag == true) {
					edgeName = "Edge_"+ i +"_" + j;
					addLane(edgeName, 8, 9, 1);
				}

			}
			
		}
		

//		flag = true;
//		// robot 3x3
//		for (int x = i; x < i+3; x++) {
//			for (int y = j; y < j+3; y++) {
//				if(md3[y][x] == 2) {
//					flag = false;
//					return;
//				}
//			}
//		}
//		
//		if(flag == true) {
//			
//			edgeName = "Edge_"+ i +"_" + j;
//			System.out.println(arrayCounter);
//			addLane(edgeName, arrayCounter, arrayCounter+1, 1);
//		}
		
		///////////////////////////////////////////////////////////////////////////
 
//    addLane("Edge_1", 0, 1, 1);
//    addLane("Edge_2", 0, 10, 12);
//    addLane("Edge_3", 1, 2, 12);
//    addLane("Edge_4", 1, 3, 2);
//    addLane("Edge_5", 3, 7, 1);
//    addLane("Edge_6", 5, 8, 1);
//    addLane("Edge_7", 8, 9, 1);
//    addLane("Edge_8", 7, 9, 1);
//    addLane("Edge_9", 4, 9, 1);
//    addLane("Edge_10", 9, 10, 1);
//    addLane("Edge_11", 1, 10, 1);

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