package mdpAlgorithm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.StreamConnection;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class BluetoothListener extends Thread{
	private StreamConnection mConnection;
	private OutputStream mmOutStream;
	public BluetoothListener(StreamConnection connection)
	{
		mConnection = connection;
		try {
			mmOutStream = mConnection.openOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void run() {
		try {
			// prepare to receive data
			InputStream inputStream = mConnection.openInputStream();

			System.out.println("waiting for input");
			byte[] buffer = new byte[1024];  // buffer store for the stream
	        int bytes;
			while (true) {
				bytes = inputStream.read(buffer);
				processCommand(bytes,buffer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void processCommand(int bytes, byte[] buffer) {
		JSONObject map = null;
		String string = new String(buffer,0,bytes);
		
		if (!string.equals("Map Reset")) {
			map = (JSONObject) JSONValue.parse(string);
			string = map.get("type").toString();
		}
		//System.out.println(map.get("type"));
		
		switch(string){
			case "movement" : 		
				//System.out.println("movement");
				RTexploration.client.sendJSON("movement", map.get("data").toString());
				break;					
			case "command" : 	
				//System.out.println("command");
				RTexploration.client.sendJSON("command",  map.get("data").toString());
				break;	
			case "status" : 	
				//System.out.println("status");
				RTexploration.client.sendJSON("status",  map.get("data").toString());
				break;	
			case "Map Reset" : 	
				System.out.println("Map Reset");
				break;	
			default :
				System.out.println(string);
				break;
		}
	}
	public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) { }
    }

}
