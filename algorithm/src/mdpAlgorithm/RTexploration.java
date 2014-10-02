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

import org.json.simple.*;

public class RTexploration implements Runnable{
	private static final Color OBSTACLE = Color.RED;
	private static final Color WALL = new Color(160, 80, 70);
	private static final Color EXPLORED = new Color(0, 128, 255);
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
				
			} while (!rtCompleted);
			
			// after exploration is completed
			// run getMapDescRealTime() on MapGrid.rtToConfirmObstacle to get MD3
			// do a new method for RTexecute in dijkstra straight using md3 from getMapDescRealTime()
			// end of new Dijkstra(map.getMapDesc(), map.getMapDesc2()) send back MainSimulator.shortestRoute to RPI

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
		
		// front sensors
		int U_F = Integer.parseInt(reading.get("U_F"));
		int short_LF = Integer.parseInt(reading.get("short_LF"));
		int short_RF = Integer.parseInt(reading.get("short_RF"));
		
		//left sensors
		int U_L = Integer.parseInt(reading.get("U_L"));
		int long_BL = Integer.parseInt(reading.get("long_BL"));
		
		// right sensors
		int short_FR = Integer.parseInt(reading.get("short_FR"));
		int U_R = Integer.parseInt(reading.get("U_R"));
		
		// robot orientation
		String orientation = reading.get("direction");
		
		
		
		
		return rob;
	}

}
