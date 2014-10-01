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
	public Stack<Robot> pathTravelled;
	public MapGrid map;
	public Robot rob;
	public boolean rtCompleted;
	
	public RTexploration(MapGrid map, Robot rob) {
		this.rob = rob;
		this.map = map;
	}
	
	@Override
	public void run() {
		// instantiate connection to rpi
		PCClient client = new PCClient("192.168.10.10", 8888);
		try {
			client.connect();
			// if successful, try to send data
			//client.readInput();
			
			String status;
			String reading;
			do {  // get inputs while exploration is still not completed

				if(client.receiveJSON().get("type").toString().equals("status")) {
					status = client.receiveJSON().get("data").toString();
					if(status != null) {
						if(status.equals("END_EXP")) {
							rtCompleted = true;
							break;
						}
						
						System.out.println(status.toString());
					}
				}
				else if(client.receiveJSON().get("type").toString().equals("reading")) {
					reading = client.receiveJSON().get("data").toString();
				}
				
			} while (!rtCompleted);

		} catch (UnknownHostException e) {
			MainSimulator.rtThreadStarted = false;
			System.err.println("Unknown Host");
		} catch (IOException e) {
			MainSimulator.rtThreadStarted = false;
			System.err.println("Cannot establish connection. "+e.getMessage());
		}
	}
	
	public Robot getPos(MapGrid map, Robot rob){
		
		return rob;
	}

}
