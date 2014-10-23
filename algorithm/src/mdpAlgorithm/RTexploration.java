package mdpAlgorithm;

import java.awt.Color;
import java.util.Stack;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;

public class RTexploration implements Runnable{
	private static final Color OBSTACLE = Color.RED;
	private static final Color WALL = new Color(160, 80, 70);
	private static final Color EXPLORED = new Color(0, 128, 255);
	private static final Color EXPLORE = new Color(146, 208, 80);
	private static final Color BORDER = new Color(225, 225, 225);
	private static final Color ROBOT = new Color(153, 204, 255);
	public Stack<Robot> curStack;
	public MapGrid map;
	public Robot rob;
	public boolean rtCompleted;
	public boolean completeLeaderboard;
	private JSONObject fakeHash = new JSONObject();
	
	public RTexploration(MapGrid map, Robot rob) {
		this.rob = rob;
		this.map = map;
	}
	
	@Override
	public void run() {
//		for (int j = 0; j< 15; j++) {
//			for(int i = 0; i < 20; i++) {
//				System.out.print(map.toConfirmObstacle[j][i]);
//			}
//			System.out.println();
//		}
//		System.out.println();
		
		curStack = new Stack<Robot>();
		curStack.push(rob);
		JSONObject reading = new JSONObject();
		/*
		int testx = 0;
		
		//front sensors
		int U_F = 50;
		int short_LF = 60;
		int short_RF = 60;
		//left sensors
		int U_L = 50;
		int long_BL = 80;
		int short_FL = 40;
		
		// right sensors
		int short_FR = 60;
		int U_R = 50;
		
		do {
			//short_LF -= 10;
			if(!MainSimulator.t2.isRunning()) {
				MainSimulator.t2.start();
				System.out.println("timer not running and starting it now");
			}
			reading.put("X", 10);
			reading.put("Y", 8+testx);
			reading.put("direction", "3");
			reading.put("U_F", U_F);
			reading.put("short_LF", short_LF);
			reading.put("short_FL", short_FL);
			reading.put("short_RF", short_RF);
			reading.put("U_L", U_L);
			reading.put("long_BL", long_BL);
			reading.put("short_FR", short_FR);
			reading.put("U_R", U_R);			

			Robot currentDir = new Robot(curStack.peek());
			currentDir = getPos(map, rob, reading);

			if (currentDir != null) {
				curStack.push(currentDir);
			}
			else {
				currentDir = curStack.pop();
			}
			testx++;
			
			for (int j = 0; j< 15; j++) {
				for(int i = 0; i < 20; i++) {
					if(map.toConfirmObstacle[j][i]>= 0)
						System.out.print("[+"+map.toConfirmObstacle[j][i]+"]");
					else
						System.out.print("["+map.toConfirmObstacle[j][i]+"]");
				}
				System.out.println();
			}
			System.out.println();
			short_LF -= 10;
			short_RF -= 10;
			
			//System.out.println(rob.getX() +", "+rob.getY());
		}while(testx<6);

		
		
		System.out.println("md1: "+map.getMapDesc());
		System.out.println("md2: "+map.getMapDesc2());
		
		int[][] test1 = map.getMapDesc3(map.getMapDesc(), map.getMapDesc2());
		for(int i = 0; i < 20; i++) {	
			for (int j = 0; j< 15; j++) {
				System.out.print(test1[j][i]);
			}
			System.out.println();
		}
		
		System.out.println("md3 real time");
		
		int[][] test = map.getMapDescRealTime();
		for(int i = 0; i < 20; i++) {	
			for (int j = 0; j< 15; j++) {
				System.out.print(test[j][i]);
			}
			System.out.println();
		}

		for(int i = 0; i < 20; i++) {
			for (int j = 0; j< 15; j++) {
				
				//reset the color on original map
				//clean the map in case of wrong obstacles plotted
				if(map.grid[j+1][i+1].getBackground().equals(OBSTACLE)){
					map.grid[j+1][i+1].setBackground(EXPLORED);
				}	
				
				// set the confirmed obstacles
				if(test[j][i] == 2) {
					map.grid[j+1][i+1].setBackground(OBSTACLE);
				}
			}
		}
		System.out.println("android md3 string");
		// convert to string - using our map grid view
		// send to android
		String testtest = ""; 
		for(int i = 0; i < 20; i++) {
			for (int j = 0; j< 15; j++) {
			
				testtest += test[j][i];
			}
			testtest += "\n";
		}
		System.out.print(testtest);
					
		System.out.println("this is new md2: "+ map.getRTMapDesc2(map.getMapDesc(), test));
*/

		// instantiate connection to rpi
		PCClient client = new PCClient("192.168.10.10", 8888);
		try {
			client.connect();
			// if successful, try to send data
			//client.readInput();
			
			//client.sendJSON("map", map.getMapDesc());
			
			String status;
			//JSONObject reading;
			do {  // get inputs while exploration is still not completed

				// handles type : status data
				JSONObject input = client.receiveJSON();
				
				if(String.valueOf(input.get("type")).equals("status")) {
					status = String.valueOf(input.get("data"));
					if(status != null) {
						//System.out.println(status);
						
						if(status.equals("END_EXP")) {
							System.out.println("end exp");
							rtCompleted = true;
							break;
						}
					}
				}
				// handles type : readings data
				else if(String.valueOf(input.get("type")).equals("reading")) {
					
					reading = (JSONObject) input.get("data");

					
					// push new position based on readings into stack
					Robot currentDir = new Robot(curStack.peek());
					currentDir = getPos(map, rob, reading);

					if (currentDir != null) {
						curStack.push(currentDir);
					}
					else {
						currentDir = curStack.pop();
					}
				
								
					// need to send explored map to android. send md1
					client.sendJSON("map", "E"+map.getMapDesc());
					// check the md1 that is sent
					System.out.println("md1: "+map.getMapDesc());
					System.out.println("md2: "+map.getMapDesc2());
					
					/*
					// print the md3 to check
					for (int j = 0; j< 15; j++) {
						for(int i = 0; i < 20; i++) {
							if(map.toConfirmObstacle[j][i]>= 0)
								System.out.print("[+"+map.toConfirmObstacle[j][i]+"]");
							else
								System.out.print("["+map.toConfirmObstacle[j][i]+"]");
						}
						System.out.println();
					}
					*/
					// repaint the walls
					map.initWalls();
				}
			} while (!rtCompleted);
			
			// after exploration is completed
			
			// stops timer
			MainSimulator.t2.stop();
			
			//To get the real-time robot's MD3 with regards to sensor sensing the obs
			
			// get md3 from toconfirmobstacles
			int[][] mapDesc3 = map.getMapDescRealTime();

			//prints final md1
			System.out.println("Final md1: "+map.getMapDesc());
			
			// prints md2
			//System.out.println("this is final md2"+map.getMapDesc2());
			System.out.println("Final md2: "+ map.getRTMapDesc2(map.getMapDesc(), mapDesc3));
			
			// set confirmed obstacles on the grid
			System.out.println("Final md3:");
			for(int i = 0; i < 20; i++) {
				for (int j = 0; j< 15; j++) {
					
					//reset the color on original map
					//clean the map in case of wrong obstacles plotted
					if(map.grid[j+1][i+1].getBackground().equals(OBSTACLE)){
						map.grid[j+1][i+1].setBackground(EXPLORED);
					}
					
					// set the confirmed obstacles
					if(mapDesc3[j][i] == 2) {
						map.grid[j+1][i+1].setBackground(OBSTACLE);
					}
					System.out.print(mapDesc3[j][i]); // prints the md3 in the grid form
				}
				System.out.println();
			}

			// convert to string
			String stringMd3 = ""; 
			for(int i = 0; i < 20; i++) {
				for (int j = 0; j< 15; j++) {	
					stringMd3 += mapDesc3[j][i];
				}
				//stringMd3 += "\n";
			}

			// send rpi md3 to send to android
			System.out.println("Final md3 in string format to android: "+stringMd3);
			client.sendJSON("map", "S"+stringMd3);

			int whichCounter = 0;
			int midCounter = 0;
			String[] midroute = new String[300];
			
			//run dijkstra using real time md3
			new Dijkstra(mapDesc3);
			for (int i = 1; i < 16; i++) {
    			for (int j = 1; j < 21; j++) {
    				if(Dijkstra.route[whichCounter]) {
    					for (int x = 0; x < 3; x++) {
    	        			for (int y = 0; y < 3; y++) {
    	        				map.grid[i+x][j+y].setBackground(ROBOT);
    	        				map.grid[i+x][j+y].setBorder(BorderFactory.createLineBorder(BORDER, 1));
    	        				
    	        				if(x == 1 && y == 1) {
    	        					midroute[midCounter] = (i+x) + "," + (j+y);
    	        					midCounter++;
    	        				}
    	        			}
    					}
    				}
    				whichCounter++;
    			}
			}
			int x;
			int y;
			for(int i = 0; i< midCounter; i++) {
				x = Integer.parseInt(midroute[i].split(",")[0]);
				y = Integer.parseInt(midroute[i].split(",")[1]);
				map.grid[x][y].setBackground(EXPLORE);
			}
			
			// end of new Dijkstra(map.getMapDesc(), map.getMapDesc2()) send back MainSimulator.shortestRoute to RPI
			System.out.println("fastest path: "+MainSimulator.shortestRoute);
			client.sendJSON("path", MainSimulator.shortestRoute);
			
			do {  // get inputs while leaderboard is still not completed, so android wont be disconnected.
				//this will probably never end lol

				// handles type : status data
				//JSONObject input = client.receiveJSON();
				
				//if(String.valueOf(input.get("type")).equals("status")) {
				//	status = String.valueOf(input.get("data"));
				//	if(status != null) {
				//		//System.out.println(status);
						
				//		if(status.equals("END_LEADERBOARD")) {
				//			System.out.println("END_LEADERBOARD");
				//			completeLeaderboard = true;
				//			break;
				//		}
				//	}
				//}
				System.out.print("");
			}while (!completeLeaderboard);
			
		} catch (UnknownHostException e) {
			MainSimulator.rtThreadStarted = false;
			System.err.println("Unknown Host");
		} catch (IOException e) {
			MainSimulator.rtThreadStarted = false;
			System.err.println("Cannot establish connection. "+e.getMessage());
		} finally {
			client.close();
		}
	}
	
	public Robot getPos(MapGrid map, Robot rob, JSONObject reading){
		try {
			// start timer if not started
			if(!MainSimulator.t2.isRunning())
				MainSimulator.t2.start();
			
			// use readings and move robot
			// update MapGrid.rtToConfirmObstacle as i am receiving readings (+1/ -1)
			
			// optional----------------------
			// update md1 and md2 then get md3
			// ------------------------------
			
			// X,Y coordinates
			int newX = Integer.valueOf(String.valueOf(reading.get("Y"))) -1;
			int newY = Integer.valueOf(String.valueOf(reading.get("X"))) -1;
			// robot orientation
			String newOrientation = String.valueOf(reading.get("direction"));
			String modNewOrientation = "";
	
			// change NESW to 1234
			switch(newOrientation) {
				case "1":
					modNewOrientation = "N";
					break;
				case "2":
					modNewOrientation = "E";
					break;
				case "3":
					modNewOrientation = "S";
					break;
				case "4":
					modNewOrientation = "W";
					break;
			}
			
			System.out.println("Virtual robot: "+ newX + "," + newY + " Facing: "+ modNewOrientation);
			System.out.println("Current robot: "+ rob.getX() + "," + rob.getY() + " Facing: "+ rob.getOrientation());
			
			// check if orientation is different. if different, rotate.
			if(!modNewOrientation.equals(rob.getOrientation())) {
				System.out.println("TURN");
				rob.rotateRobot(reading, map, modNewOrientation);
			}
			// if not rotating, means it is moving
			else if(newX != rob.getX() || newY != rob.getY()) {
				System.out.println("MOVED");
				//rob.moveRobot(reading, map, 1);
				rob.setRobotXY(map, newX, newY, "Move", modNewOrientation, reading);
				rob.setExplored(map);
			}
			
			// add delay if required
	//		try {
	//			Thread.sleep(700);
	//		} catch (InterruptedException e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		}
			
			
		}
		catch(ArrayIndexOutOfBoundsException e) {
			System.out.println("explored array out of bounds");
		}
		return rob;
	}

}
