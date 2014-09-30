import java.net.Socket;
import java.net.UnknownHostException;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

// TODO: import JSONValue

class PCClient {

	private String hostname;
	private int port;
	Socket socketClient;

	public PCClient(String hostname, int port){
		this.hostname = hostname;
		this.port = port;
	}

	public void connect() throws UnknownHostException, IOException {
		System.out.println("Attempting connection %");
		socketClient = new Socket(hostname, port);
		System.out.println("Connection established");
	}

	public void sendJSON(String type, String data) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("type", type);
		map.put("data", data);
		String jsonString = JSONValue.toJSONString(map);

		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socketClient.getOutputStream()));
		writer.write(jsonString);
		writer.flush();
		writer.close();
	}

	public HashMap<String, String> receiveJSON() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(socketClient.getInputStream());

		// TODO: not sure if can read until end of brace only
		String jsonString = reader.readLine();

		List jsonList = (JSONArray)JSONValue.parse(jsonString);

		// TODO: might have queue of JSON here
		HashMap<String, String> map = (JSONObject) jsonList.get(0);

		System.out.println("Received: "+ map.toString());
		return map;
	}

	public void readInput() {
		BufferedWriter br = new BufferedWriter(new InputStreamReader(System.in));
		String type = "";
		String data = "";

		while (true) {
			this.receiveJSON();
			try {
				System.out.print("Input type: ");
				type = br.readLine();
				System.out.print("Input data: ");
				data = br.readLine();

				this.sendJSON(type, data);
			} catch (IOException e) {
				System.out.println("IO error");		
			}
		}
	}

	public static void main(String[] args) {
		PCClient client = new PCClient("localhost", 8888)
		try {
			client.connect();
			// if successful, try to send data
			client.readInput();
		} catch (UnknownHostException e) {
			System.err.println("Unknown Host");
		} catch (IOException e) {
			System.err.println("Cannot establish connection. "+e.getMessage());
		}
	}
}