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
	
	public RTexploration(MapGrid map, Robot rob) {
		this.rob = rob;
		this.map = map;
	}
	
	@Override
	public void run() {
		
		curStack = new Stack<Robot>();
		curStack.push(rob);
				
		// instantiate connection to rpi
		PCClient client = new PCClient("192.168.10.10", 8888);
		try {
			client.connect();
			// if successful, try to send data
			//client.readInput();

			String status;
			HashMap<String, String> reading;
			do {  // get inputs while exploration is still not completed

				// handles type : status data
				HashMap<String, String> input = client.receiveJSON();
				
				if(input.get("type").equals("status")) {
					status = input.get("data");
					if(status != null) {
						System.out.println(status);
						
						if(status.equals("END_EXP")) {
							//System.out.println("end exp");
							rtCompleted = true;
							break;
						}
					}
				}
				// handles type : readings data
				else if(input.get("type").equals("reading")) {
					reading = (JSONObject) JSONValue.parse(input.get("data"));
					
					// push new position based on readings into stack
					Robot currentDir = new Robot(curStack.peek());
					getPos(map, rob, reading);
					if (currentDir != null) {
						curStack.push(currentDir);
					}
				}
				
				// need to send explored map to android. send md1
				client.sendJSON("map", map.getMapDesc());
				
			} while (!rtCompleted);
			
			// after exploration is completed
			
			//To get the real-time robot's MD3 with regards to sensor sensing the obs		
			int[][] mapDesc3 = map.getMapDescRealTime();
			
			// print the md3 to check
//			for(int i = 0; i < 20; i++) {	
//				for (int j = 0; j< 15; j++) {
//					System.out.print(mapDesc3[j][i]);
//				}
//				System.out.println();
//			}	
			
			// convert to string
			String stringMd3 = ""; 
			for (int j = 0; j< 15; j++) {
				for(int i = 0; i < 20; i++) {	
					stringMd3 += mapDesc3[j][i];
				}
				//stringMd3 += "\n";
			}
			
			// send rpi md3 to send to android
			client.sendJSON("map", stringMd3);

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
			client.sendJSON("movement", MainSimulator.shortestRoute);

		} catch (UnknownHostException e) {
			MainSimulator.rtThreadStarted = false;
			System.err.println("Unknown Host");
		} catch (IOException e) {
			MainSimulator.rtThreadStarted = false;
			System.err.println("Cannot establish connection. "+e.getMessage());
		}
	}
	
	public Robot getPos(MapGrid map, Robot rob, HashMap<String, String> reading){
		
		// use readings and move robot
		// update MapGrid.rtToConfirmObstacle as i am receiving readings (+1/ -1)
		
		// optional----------------------
		// update md1 and md2 then get md3
		// ------------------------------

		// X,Y coordinates
		int newX = Integer.parseInt(reading.get("X"));
		int newY = Integer.parseInt(reading.get("Y"));
		// robot orientation
		String newOrientation = reading.get("direction");
		String modNewOrientation;
		// change NESW to 1234
		switch(newOrientation) {
			case "1":
				modNewOrientation = "N";
			case "2":
				modNewOrientation = "E";
			case "3":
				modNewOrientation = "S";
			case "4":
				modNewOrientation = "W";
			default:
				modNewOrientation = "";
		}
		
		// check if orientation is different. if different, rotate.
		if(!modNewOrientation.equals(rob.getOrientation())) {
			rob.rotateRobot(reading, map, modNewOrientation);
		}
		// if not rotating, means it is moving
		else  {
			rob.moveRobot(reading, map, 1);
		}

		return rob;
	}

}
