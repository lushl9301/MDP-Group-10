package mdpAlgorithm;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
public class Dijkstra {

	private static List<DijVertex> nodes;
	private static List<DijEdge> edges;
	public static boolean[] route = new boolean[300];
	
	public Dijkstra (String md1, String md2) {
		execute(md1, md2);
	}
	
	public static void execute(String md1, String md2) {
		// initialise the 300 nodes
		nodes = new ArrayList<DijVertex>();
		edges = new ArrayList<DijEdge>();
		int nodeCounter = 0;
		for (int j = 0; j< 15; j++) {
			for(int i = 0; i < 20; i++) {
				DijVertex location = new DijVertex("Node_" + nodeCounter, "Node_" + nodeCounter);
				nodes.add(location);
				//System.out.println("Node_" + nodeCounter);
				nodeCounter++;
			}
			//System.out.println();
		}

		// hard code test values
		//String md1 = "fff87ff0ffe1ffc3ff87fff0ffe1ffc3ff87ff0ffe1ffc3ff87ff0ffe1ffc3ff87ff0ffe1fff";
		//String md2= "00200400801003f07e00400801002004008010020041082104208410";
		
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
				if(i>=20) {
					if(oneDmd3[i-20]) {
						whichEdge = i-20;
						edgeName = "Edge_"+whichEdge;
						addLane(edgeName, i, whichEdge, 1);
						//System.out.print(edgeName);
					}
				}
				if((i%20)!=0) {
					if(oneDmd3[i-1]) {
						whichEdge = i-1;
						edgeName = "Edge_"+whichEdge;
						addLane(edgeName, i, whichEdge, 1);
						//System.out.print(edgeName);
					}
				}
				
				//System.out.println();
			}
			
		}		

    DijGraph graph = new DijGraph(nodes, edges);
    DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);
    dijkstra.execute(nodes.get(0));
    LinkedList<DijVertex> path =  dijkstra.getPath(nodes.get(257));      
    
    int currentVertexID;
    int previousVertexID = 0;
    int numOfSteps = 0;
    int previousStepCounter = 0;
    int newStepCounter = 0;
    String currentDirection = "E";
    MainSimulator.shortestRoute = "";
    
	for (DijVertex vertex : path) {
		
		currentVertexID = Integer.parseInt(vertex.getName().split("_")[1]);
    	route[currentVertexID] = true;
    	System.out.println(currentVertexID+"_"+numOfSteps);
    	newStepCounter = currentVertexID - previousVertexID;
    	
    	// If at starting point, there is obstacle in front of robot
    	if (currentVertexID==20 && previousVertexID==0)  {
    		MainSimulator.shortestRoute += "R";
    		currentDirection = "S";
    		previousStepCounter = 20;
    		numOfSteps--;
    	}
    	
    	// If at starting point, there is NO obstacle in front of robot
    	if (currentVertexID==1 && previousVertexID==0)  {
    		numOfSteps--;
    		
    	}
    	
    	// Movement counter algorithm
    	if (newStepCounter == previousStepCounter) { // if just walking straight
    		numOfSteps++;
    	}
    	if (newStepCounter != previousStepCounter) {
    		if(newStepCounter == 20) { // if need to go down
    			if (currentDirection == "E") {
    				MainSimulator.shortestRoute += Integer.toString(numOfSteps);
            		MainSimulator.shortestRoute += "R";
            		currentDirection = "S";
    			}
    			if (currentDirection == "W") {
    				MainSimulator.shortestRoute += Integer.toString(numOfSteps);
            		MainSimulator.shortestRoute += "L";
            		currentDirection = "S";
    			}
    		}
    		else if(newStepCounter == -20) { // if need to go up
    			if (currentDirection == "W") {
    				MainSimulator.shortestRoute += Integer.toString(numOfSteps);
            		MainSimulator.shortestRoute += "R";
            		currentDirection = "N";
    			}
    			if (currentDirection == "E") {
    				MainSimulator.shortestRoute += Integer.toString(numOfSteps);
            		MainSimulator.shortestRoute += "L";
            		currentDirection = "N";
    			}
    		}
    		else if(newStepCounter == 1) { // if need to go right
    			if (currentDirection == "S") {
    				MainSimulator.shortestRoute += Integer.toString(numOfSteps);
            		MainSimulator.shortestRoute += "L";
            		currentDirection = "E";
    			}
    			if (currentDirection == "N") {
    				MainSimulator.shortestRoute += Integer.toString(numOfSteps);
            		MainSimulator.shortestRoute += "R";
            		currentDirection = "E";
    			}
    		}
    		else if(newStepCounter == -1) { // if need to go left
    			if (currentDirection == "S") {
    				MainSimulator.shortestRoute += Integer.toString(numOfSteps);
            		MainSimulator.shortestRoute += "R";
            		currentDirection = "W";
    			}
    			if (currentDirection == "N") {
    				MainSimulator.shortestRoute += Integer.toString(numOfSteps);
            		MainSimulator.shortestRoute += "L";
            		currentDirection = "W";
    			}
    		}
    		numOfSteps = 1;
    	}

    	// If reached goal, print the last numOfSteps and end loop
    	if (currentVertexID==257) {
    		MainSimulator.shortestRoute += Integer.toString(numOfSteps);
    		MainSimulator.shortestRoute += "G";
    		break;
    	}
    	
    	// This is just for the first straight movement
    	if (previousStepCounter == 0) {
    		previousStepCounter = 1;
    	}
    	else {
    		previousStepCounter = newStepCounter;
    	}
    	
    	// prev = cur
    	previousVertexID = currentVertexID;
    	
    }
	System.out.println();
	System.out.println(MainSimulator.shortestRoute);
  }

  private static void addLane(String laneId, int sourceLocNo, int destLocNo, int duration) {
	  DijEdge lane = new DijEdge(laneId,nodes.get(sourceLocNo), nodes.get(destLocNo), duration);
	  edges.add(lane);
  }

} 