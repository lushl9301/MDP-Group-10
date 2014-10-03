package mdpAlgorithm;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.*;

class PCClient {

	private String hostname;
	private int port;
	private Socket socketClient;
	private BufferedReader reader;
	private PrintWriter writer;

	public PCClient(String hostname, int port){
		this.hostname = hostname;
		this.port = port;
	}

	public void connect() throws UnknownHostException, IOException {
		System.out.println("Attempting connection %");
		socketClient = new Socket(hostname, port);
		System.out.println("Connection established");
		reader = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
		writer = new PrintWriter(socketClient.getOutputStream(), true);
	}

	public void sendJSON(String type, String data) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("type", type);
		map.put("data", data);
		String jsonString = JSONValue.toJSONString(map);
		 
		writer.write(jsonString+"\n");
		writer.flush();
		System.out.println("Sent JSON to Network");
	}

	public JSONObject receiveJSON() {
		String jsonString;
		JSONObject map = null;
		
		 
		try {
			
			jsonString = reader.readLine();
			// TODO: not sure if can read until end of brace only
			if (jsonString != null) {
			
			// TODO: might have queue of JSON here
				map = (JSONObject) JSONValue.parse(jsonString);
				System.out.println("Received: "+ map.toString());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return map;
	}

	public void readInput() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String type = "";
		String data = "";

		while (true) {
			try {
				System.out.print("Input type: ");
				type = br.readLine();
				System.out.print("Input data: ");
				data = br.readLine();

				this.sendJSON(type, data);
				this.receiveJSON();
			} catch (IOException e) {
				System.out.println("IO error");		
			}
		}
	}
	
	public void close(){
		writer.close();
		try {
			reader.close();
			socketClient.close();
		} catch (IOException e) {
			System.out.println("Can't close network");
		}
	}

//	public static void main(String[] args) {
//		PCClient client = new PCClient("192.168.10.10", 8888);
//		try {
//			client.connect();
//			// if successful, try to send data
//			client.readInput();
//		} catch (UnknownHostException e) {
//			System.err.println("Unknown Host");
//		} catch (IOException e) {
//			System.err.println("Cannot establish connection. "+e.getMessage());
//		}
//	}
}