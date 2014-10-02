package mdpAlgorithm;

import java.util.LinkedList;
import java.util.Queue;

import mdpAlgorithm.BFSState;

public class BFSGraphImplementation {
	
	 public void dfs(BFSNode root)
	    {       
	        //Avoid infinite loops
	        if(root == null) return;

	        System.out.print(root.getVertex() + "\t");
	        root.state = BFSState.Visited;

	        //for every child
	        for(BFSNode n: root.getChild())
	        {
	            //if childs state is not visited then recurse
	            if(n.state == BFSState.Unvisited)
	            {
	                dfs(n);
	            }
	        }
	    }

	    public void bfs(BFSNode root)
	    {
	        //Since queue is a interface
	        Queue<BFSNode> queue = new LinkedList<BFSNode>();

	        if(root == null) return;

	        root.state = BFSState.Visited;
	         //Adds to end of queue
	        queue.add(root);

	        while(!queue.isEmpty())
	        {
	            //removes from front of queue
	        	BFSNode r = queue.remove(); 
	            System.out.print(r.getVertex() + "\t");

	            //Visit child first before grandchild
	            for(BFSNode n: r.getChild())
	            {
	                if(n.state == BFSState.Unvisited)
	                {
	                    queue.add(n);
	                    n.state = BFSState.Visited;
	                }
	            }
	        }


	    }

	    public static BFSGraph createNewGraph()
	    {
	    	BFSGraph g = new BFSGraph();        
	    	BFSNode[] temp = new BFSNode[300];

//	        temp[0] = new BFSNode("A", 3);
//	        temp[1] = new BFSNode("B", 3);
//	        temp[2] = new BFSNode("C", 1);
//	        temp[3] = new BFSNode("D", 1);
//	        temp[4] = new BFSNode("E", 1);
//	        temp[5] = new BFSNode("F", 1);
//
//	        temp[0].addChildNode(temp[1]);
//	        temp[0].addChildNode(temp[2]);
//	        temp[0].addChildNode(temp[3]);
//
//	        temp[1].addChildNode(temp[0]);
//	        temp[1].addChildNode(temp[4]);
//	        temp[1].addChildNode(temp[5]);
//
//	        temp[2].addChildNode(temp[0]);
//	        temp[3].addChildNode(temp[0]);
//	        temp[4].addChildNode(temp[1]);
//	        temp[5].addChildNode(temp[1]);
	        
	    	String md1 = "fff87ff0ffe1ffc3ff87fff0ffe1ffc3ff87ff0ffe1ffc3ff87ff0ffe1ffc3ff87ff0ffe1fff";
			String md2= "00200400801003f07e00400801002004008010020041082104208410";
			
			// get md3
			MapGrid m = new MapGrid();
			int[][] md3 = m.getMapDesc3(md1, md2);

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
//			for(int i = 0; i < 20; i++) {
	//			for (int j = 0; j< 15; j++) {
	//				if(vertexCheck[j][i]) System.out.print("t");
	//				else  System.out.print("f");
	//			}
	//			 System.out.println("");
//			}
		System.out.println("");
			int md3Counter = 0;
			String nodeName;
			for (int j = 0; j< 15; j++) {
				for(int i = 0; i < 20; i++) {
					nodeName = "Node_"+md3Counter;
					temp[md3Counter] = new BFSNode(nodeName, 2);
					//System.out.println(nodeName);
					if(vertexCheck[j][i]) oneDmd3[md3Counter] = true;
					md3Counter++;
				}
			}

//			int testC = 0;
//			for (int i =0; i<300; i++) {
//				if(oneDmd3[i])
//					System.out.print("t");
//				else System.out.print("f");
//				
//				testC++;
//				if(testC == 20) {
//					testC = 0;
//					System.out.println();
//				}
//			}
			
			//int whichEdge = 0;
			//String edgeName;
			for (int i =0; i<300; i++) {
				if(oneDmd3[i]) {
					System.out.print(temp[i].getVertex()+": ");
					if(oneDmd3[i+1]) {
						//whichEdge = i+1;
						//edgeName = "Edge_"+whichEdge;
						temp[i].addChildNode(temp[i+1]);
						System.out.print(temp[i+1].getVertex()+", ");
					}
					if(oneDmd3[i+20]) {
						//whichEdge = i+20;
						//edgeName = "Edge_"+whichEdge;
						temp[i].addChildNode(temp[i+20]);
						System.out.print(temp[i+20].getVertex()+". ");
					}
					System.out.println();
				}		
			}
	       
	        for (int i = 0; i < 257; i++) {
	            g.addNode(temp[i]);
	        }
	        
	        return g;
	    }

	    public static void main(String[] args) {

	    	//BFSGraph gDfs = createNewGraph();
	    	BFSGraphImplementation s = new BFSGraphImplementation();

//	        System.out.println("--------------DFS---------------");
//	        s.dfs(gDfs.getNode()[0]);
//	        System.out.println();
//	        System.out.println();
	        BFSGraph gBfs = createNewGraph();
	        System.out.println("---------------BFS---------------");
	        s.bfs(gBfs.getNode()[0]);

	    }
}
