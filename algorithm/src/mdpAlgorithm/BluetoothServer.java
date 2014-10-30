package mdpAlgorithm;

import java.io.IOException;
import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

public class BluetoothServer extends Thread{
	
    private final StreamConnectionNotifier mmServerSocket;
    //000110100001000800000805F9B34FB
    //UUID mdpUUID  = new UUID("0000110100001000800000805f9b34fb",false);
    UUID mdpUUID  = new UUID("000110100001000800000805F9B34FB",false);
    public final String name = "MDPServer";  
    public final String url  =  "btspp://localhost:" + mdpUUID.toString()
            + ";name=MDPServer";
	
	private LocalDevice mBluetoothAdapter;
	private StreamConnection conn = null;
	
	
	public BluetoothServer(){
		// Use a temporary object that is later assigned to mmServerSocket,
        // because mmServerSocket is final
		StreamConnectionNotifier tmp = null;
              
        try {
        	mBluetoothAdapter = LocalDevice.getLocalDevice();
            mBluetoothAdapter.setDiscoverable(DiscoveryAgent.GIAC);
        	tmp = (StreamConnectionNotifier)Connector.open(url);
        } catch (IOException e) { }
        mmServerSocket = tmp;

	}
	public void run() {
        // Keep listening until exception occurs or a socket is returned
        while (true) {
            try {
            	System.out.println("Bluetooth started");
            	conn = mmServerSocket.acceptAndOpen();
            	System.out.println("Bluetooth Stop");
            } catch (IOException e) {
                break;
            }
            // If a connection was accepted
            if (conn != null) {
            	RTexploration.bluetoothList = new BluetoothListener(conn);
            	RTexploration.bluetoothList.start();
                // Do work to manage the connection (in a separate thread)
            	System.out.println("Bluetooth Connected");
                try {
					mmServerSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                break;
            }
        }
    }
 
    /** Will cancel the listening socket, and cause the thread to finish */
    public void cancel() {
        try {
            mmServerSocket.close();
        } catch (IOException e) { }
    }

}
