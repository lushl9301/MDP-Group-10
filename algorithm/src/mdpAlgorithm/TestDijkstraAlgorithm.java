package mdpAlgorithm;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
public class TestDijkstraAlgorithm {

	private static List<DijVertex> nodes;
	private static List<DijEdge> edges;
	
	public static void testExecute() {
		// initialise the 300 nodes
		nodes = new ArrayList<DijVertex>();
		edges = new ArrayList<DijEdge>();
		int nodeCounter = 0;
		for (int j = 0; j< 15; j++) {
			for(int i = 0; i < 20; i++) {
				DijVertex location = new DijVertex("Node_" + nodeCounter, "Node_" + nodeCounter);
				nodes.add(location);
				//System.out.print("Node_" + nodeCounter);
				nodeCounter++;
			}
			//System.out.println();
		}

		// hard code test values
		String md1 = "fff87ff0ffe1ffc3ff87fff0ffe1ffc3ff87ff0ffe1ffc3ff87ff0ffe1ffc3ff87ff0ffe1fff";
		String md2= "00200400801003f07e00400801002004008010020041082104208410";
		
		// get md3
		MapGrid m = new MapGrid();
		int[][] md3 = m.getMapDesc3Testing(md1, md2);
		
		// print the map
//		for(int i = 0; i < 20; i++) {
//			for (int j = 0; j< 15; j++) {
//				System.out.print(md3[j][i]);
//			}
//			System.out.println();
//		}
		
		//print a space
		System.out.println();
		
		// process map and add edges for Dijkstra calculation
		//setting true and false for 3x3 availability
		boolean flag;
		boolean[][] vertexCheck= new boolean[15][20];
		boolean[] oneDmd3 = new boolean[300];
		//checking for vertex availability
		for(int i = 0; i < 18; i++) {
			for (int j = 0; j< 13; j++) {
			
				flag = true;
				// checking the 9 squares with vertex at top left hand corner
				for(int y = i; y < i+3; y++) {
					for (int x = j; x < j+3; x++) {
						if(md3[x][y] == 2) { // if this spot is a obstacle
							flag = false;
						}
					}
				}
				
				if(flag) {
					vertexCheck[j][i] = true;
				}
			}
		}
//		for(int i = 0; i < 20; i++) {
//			for (int j = 0; j< 15; j++) {
//				if(vertexCheck[j][i]) System.out.print("t");
//				else  System.out.print("f");
//			}
//			 System.out.println("");
//		}
//		System.out.println("");
		
		// part two of adding vertex
		int md3Counter = 0;
		for (int j = 0; j< 15; j++) {
			for(int i = 0; i < 20; i++) {
				if(vertexCheck[j][i]) oneDmd3[md3Counter] = true;
				md3Counter++;
			}
		}
//		
//		int testC = 0;
//		for (int i =0; i<300; i++) {
//			if(oneDmd3[i])
//				System.out.print("t");
//			else System.out.print("f");
//			
//			testC++;
//			if(testC == 20) {
//				testC = 0;
//				System.out.println();
//			}
//		}
		
		int whichEdge = 0;
		String edgeName;
		for (int i =0; i<300; i++) {
			if(oneDmd3[i]) {
				if(oneDmd3[i+1]) {
					whichEdge = i+1;
					edgeName = "Edge_"+whichEdge;
					addLane(edgeName, i, whichEdge, 1);
					//System.out.print(edgeName);
				}
				if(oneDmd3[i+20]) {
					whichEdge = i+20;
					edgeName = "Edge_"+whichEdge;
					addLane(edgeName, i, whichEdge, 1);
					//System.out.print(edgeName);
				}
				//System.out.println();
			}
			
		}		

    DijGraph graph = new DijGraph(nodes, edges);
    DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);
    dijkstra.execute(nodes.get(0));
    LinkedList<DijVertex> path =  dijkstra.getPath(nodes.get(257));      
    
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