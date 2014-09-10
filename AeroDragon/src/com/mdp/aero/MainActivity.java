/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mdp.aero;

import com.mdp.aero.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * This is the main Activity that displays the current chat session.
 */
public class MainActivity extends Activity {
	// Debugging
	private static final String TAG = "Aero Dragon";
	private static final boolean D = true;
	
	//for robot and map sensor position
	public static final int TOP_LEFT_SIDE=0;
	public static final int TOP_RIGHT_SIDE=1;
	public static final int TOP_LEFT_FRONT=2;
	public static final int TOP_RIGHT_FRONT=3;
	
	
	//Shared Preferences
	public static final String DEFAULT="N/A";
	String f1, f2;

	// Message types sent from the BluetoothChatService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	// Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE = 2;
	private static final int REQUEST_ENABLE_BT = 3;

	// Layout Views
	private ListView mConversationView;
	private EditText mOutEditText;
	private TextView tvConnectionStatus;
	
	//Buttons
	private Button resetButton;
	private Button f1Button;
	private Button f2Button;
	private ToggleButton modeButton;
	private Button roundButton;
	private Button mSendButton;
	private Button exploreButton;
	private Button shortButton;
	private ToggleButton startButton;
	
	//for timer
	private TextView timerVal;
	private long startTime = 0L;
	long timeInMilliseconds = 0L;
	long timeSwapBuff = 0L;
	long updatedTime = 0L;
	private Handler customHandler = new Handler();

	// Name of the connected device
	private String mConnectedDeviceName = null;
	// Array adapter for the conversation thread
	private ArrayAdapter<String> mConversationArrayAdapter;
	// String buffer for outgoing messages
	private StringBuffer mOutStringBuffer;
	// Local Bluetooth adapter
	private BluetoothAdapter ba = null;
	// Member object for the chat services
	private BluetoothChatService mChatService = null;
	
	//for status on sending
	String sentMsg = "No Action.";
	int autoAct = 0;
	
	MapGenerator map;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (D)
			Log.e(TAG, "+++ ON CREATE +++");

		// Set up the window layout
		setContentView(R.layout.main);

		// Get local Bluetooth adapter
		ba = BluetoothAdapter.getDefaultAdapter();

		// If the adapter is null, then Bluetooth is not supported
		if (ba == null) {
			Toast.makeText(this, "Bluetooth is not available",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		GridLayout gv = (GridLayout)findViewById(R.id.grid2);
		Log.i("tag","here3");
		map = new MapGenerator(gv, this);
		Log.i("tag","here4");
		
		tvConnectionStatus = (TextView)findViewById(R.id.tvConnectionStatus);
		
		f1Button = (Button) findViewById(R.id.f1Button);
		f2Button = (Button) findViewById(R.id.f2Button);
		resetButton = (Button) findViewById(R.id.resetButton);
		modeButton = (ToggleButton) findViewById(R.id.modeButton);
		roundButton = (Button) findViewById(R.id.roundButton);
		exploreButton = (Button) findViewById(R.id.exploreBtn);
		shortButton = (Button) findViewById(R.id.shortBtn);
		timerVal = (TextView) findViewById(R.id.timer);
		startButton = (ToggleButton) findViewById(R.id.startBtn);
		
		load();
		
		f1Button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				sendMessage(f1);
			}		
		});
		
		f2Button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				sendMessage(f2);
			}		
		});
    	exploreButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				autoAct = 1;
			}
    	});
    	shortButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				autoAct = 2;
			}
    		
    	});
		
	}
	

	@Override
	public void onStart() {
		super.onStart();
		if (D)
			Log.e(TAG, "++ ON START ++");

		// If BT is not on, request that it be enabled.
		// setupChat() will then be called during onActivityResult
		if (!ba.isEnabled()) {
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
			// Otherwise, setup the chat session
		} else {
			if (mChatService == null)
				setupChat();
		}
	}

	@Override
	public synchronized void onResume() {
		super.onResume();
		if (D)
			Log.e(TAG, "+ ON RESUME +");
		invalidateOptionsMenu();
		// Performing this check in onResume() covers the case in which BT was
		// not enabled during onStart(), so we were paused to enable it...
		// onResume() will be called when ACTION_REQUEST_ENABLE activity
		// returns.
		if (mChatService != null) {
			// Only if the state is STATE_NONE, do we know that we haven't
			// started already
			if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
				// Start the Bluetooth chat services
				mChatService.start();
			}
		}
		
		load();
			
	}
	
	
	public void load(){
		SharedPreferences sp = getSharedPreferences("MyData", Context.MODE_PRIVATE);
		f1 = sp.getString("Function1", DEFAULT);
		f2 = sp.getString("Function2", DEFAULT);
		if(f1.equals(DEFAULT) || f2.equals(DEFAULT)){
			Toast.makeText(this, "No Function Data Found", Toast.LENGTH_SHORT).show();
		}else{
			Toast.makeText(this, "Function Loaded", Toast.LENGTH_SHORT).show();
		}
	
	}
	

	private void setupChat() {
		Log.d(TAG, "setupChat()");

		// Initialize the array adapter for the conversation thread
		mConversationArrayAdapter = new ArrayAdapter<String>(this,
				R.layout.message);
		mConversationView = (ListView) findViewById(R.id.in);
		mConversationView.setAdapter(mConversationArrayAdapter);

		/*
		 // Initialize the compose field with a listener for the return key
		
		mOutEditText = (EditText) findViewById(R.id.edit_text_out);
		mOutEditText.setOnEditorActionListener(mWriteListener);

		// Initialize the send button with a listener that for click events
		mSendButton = (Button) findViewById(R.id.button_send);
		mSendButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Send a message using content of the edit text widget
				TextView view = (TextView) findViewById(R.id.edit_text_out);
				String message = view.getText().toString();
				sendMessage(message);
			}
		});
		*/
		// Initialize the BluetoothChatService to perform bluetooth connections
		mChatService = new BluetoothChatService(this, mHandler);

		// Initialize the buffer for outgoing messages
		mOutStringBuffer = new StringBuffer("");
	}

	@Override
	public synchronized void onPause() {
		super.onPause();
		if (D)
			Log.e(TAG, "- ON PAUSE -");
		invalidateOptionsMenu();
	}

	@Override
	public void onStop() {
		super.onStop();
		if (D)
			Log.e(TAG, "-- ON STOP --");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Stop the Bluetooth chat services
		if (mChatService != null)
			mChatService.stop();
		if (D)
			Log.e(TAG, "--- ON DESTROY ---");
	}

	private void ensureDiscoverable() {
		if (D)
			Log.d(TAG, "ensure discoverable");
		if (ba.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(
					BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			startActivity(discoverableIntent);
		}
	}

	/**
	 * Sends a message.
	 * 
	 * @param message
	 *            A string of text to send.
	 */
	private void sendMessage(String message) {
		// Check that we're actually connected before trying anything
		if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
			Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT)
					.show();
			return;
		}

		// Check that there's actually something to send
		if (message.length() > 0) {
			sentMsg = message;
			// Get the message bytes and tell the BluetoothChatService to write
			byte[] send = message.getBytes();
			mChatService.write(send);
			/*
			// Reset out string buffer to zero and clear the edit text field
			mOutStringBuffer.setLength(0);
			mOutEditText.setText(mOutStringBuffer);
			*/
		}
	}

	// The action listener for the EditText widget, to listen for the return key
	private TextView.OnEditorActionListener mWriteListener = new TextView.OnEditorActionListener() {
		public boolean onEditorAction(TextView view, int actionId,
				KeyEvent event) {
			// If the action is a key-up event on the return key, send the
			// message
			if (actionId == EditorInfo.IME_NULL
					&& event.getAction() == KeyEvent.ACTION_UP) {
				String message = view.getText().toString();
				sendMessage(message);
			}
			if (D)
				Log.i(TAG, "END onEditorAction");
			return true;
		}
	};

	private final void setStatus(int resId) {
		invalidateOptionsMenu();
		tvConnectionStatus.setText(resId);
	}

	private final void setStatus(CharSequence subTitle) {
		invalidateOptionsMenu();
		tvConnectionStatus.setText(subTitle);
	}

	// The Handler that gets information back from the BluetoothChatService
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:
				if (D)
					Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
				switch (msg.arg1) {
				case BluetoothChatService.STATE_CONNECTED:
					setStatus(getString(R.string.title_connected_to,
							mConnectedDeviceName) + sentMsg);
					sentMsg = "No Action.";
					mConversationArrayAdapter.clear();
					break;
				case BluetoothChatService.STATE_CONNECTING:
					setStatus(R.string.title_connecting);
					break;
				case BluetoothChatService.STATE_LISTEN:
				case BluetoothChatService.STATE_NONE:
					setStatus(R.string.title_not_connected);
					break;
				case BluetoothChatService.STATE_SEND:
					setStatus(getString(R.string.title_connected_to,
							mConnectedDeviceName) + sentMsg);
					break;

				}
				break;
			case MESSAGE_WRITE:
				byte[] writeBuf = (byte[]) msg.obj;
				// construct a string from the buffer
				String writeMessage = new String(writeBuf);
				mConversationArrayAdapter.add("Me:  " + writeMessage);
				break;
			case MESSAGE_READ:
				byte[] readBuf = (byte[]) msg.obj;
				// construct a string from the valid bytes in the buffer
				String readMessage = new String(readBuf, 0, msg.arg1);
				mConversationArrayAdapter.add(mConnectedDeviceName + ":  "
						+ readMessage);
				break;
			case MESSAGE_DEVICE_NAME:
				// save the connected device's name
				mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
				Toast.makeText(getApplicationContext(),
						"Connected to " + mConnectedDeviceName,
						Toast.LENGTH_SHORT).show();
				break;
			case MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(),
						msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	};

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (D)
			Log.d(TAG, "onActivityResult " + resultCode);
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				connectDevice(data);
			}
			break;
		case REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) {
				// Bluetooth is now enabled, so set up a chat session
				setupChat();
			} else {
				// User did not enable Bluetooth or an error occurred
				Log.d(TAG, "BT not enabled");
				Toast.makeText(this, R.string.bt_need_enable,
						Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}

	private void connectDevice(Intent data) {
		// Get the device MAC address
		String address = data.getExtras().getString(
				DeviceListActivity.EXTRA_DEVICE_ADDRESS);
		// Get the BluetoothDevice object
		BluetoothDevice device = ba.getRemoteDevice(address);
		// Attempt to connect to the device
		mChatService.connect(device);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.option_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		invalidateOptionsMenu();
		Intent serverIntent = null;
		switch (item.getItemId()) {
		case R.id.on_toggle:
			//BT is on, so switch off
			if(ba.isEnabled()){
				ba.disable();
			}else{
				Toast.makeText(getApplicationContext(), R.string.bt_already_off, Toast.LENGTH_SHORT).show();
			}
			return true;
		case R.id.off_toggle:
			//BT is off, so switch on
			if(!ba.isEnabled()){
				ensureDiscoverable();
			}else{
				Toast.makeText(getApplicationContext(), R.string.bt_already_on, Toast.LENGTH_SHORT).show();
			}
			return true;
		case R.id.search_toggle:
			if(ba.isEnabled() && mChatService.getState()==3){
				//if connected to device, disconnect
				mChatService.stop();
			}
			else{
				Toast.makeText(getApplicationContext(), R.string.not_connected, Toast.LENGTH_SHORT).show();
			}
			return true;
		case R.id.dc_toggle:
			if(ba.isEnabled() && mChatService.getState()!=3){
				// Launch the DeviceListActivity to see devices and do scan
				serverIntent = new Intent(this, DeviceListActivity.class);
				startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
			}
			else{
				Toast.makeText(getApplicationContext(), R.string.bt_not_enabled, Toast.LENGTH_SHORT).show();
			}
			return true;
		case R.id.configure:
			// for config of string
			serverIntent = new Intent(MainActivity.this, Configuration.class);
	        startActivity(serverIntent);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean onPrepareOptionsMenu (Menu menu){
		MenuItem switchOn = menu.findItem(R.id.on_toggle);
		MenuItem switchOff = menu.findItem(R.id.off_toggle);
		MenuItem con_dev = menu.findItem(R.id.search_toggle);
		MenuItem dis_dev = menu.findItem(R.id.dc_toggle);
		if(ba.isEnabled()){
            switchOn.setVisible(true);
            switchOff.setVisible(false);
        }
        else{
        	switchOn.setVisible(false);
            switchOff.setVisible(true);
        }
		
		if(ba.isEnabled() && mChatService.getState()== mChatService.STATE_CONNECTED){
			con_dev.setVisible(true);
            dis_dev.setVisible(false);
        }
        else{
        	con_dev.setVisible(false);
            dis_dev.setVisible(true);
        }
		
		
        return super.onPrepareOptionsMenu(menu);
	}
	
	public void toggleMode(View view) {
	    // Is the toggle on?
	    boolean on = ((ToggleButton) view).isChecked();
	    
	    if (on) {
	    	//auto mode
	    	roundButton.setVisibility(View.INVISIBLE);
	    	
	    	exploreButton.setVisibility(View.VISIBLE);
	    	shortButton.setVisibility(View.VISIBLE);
	    	timerVal.setVisibility(View.VISIBLE);
	    	startButton.setVisibility(View.VISIBLE);
	    	
	    } else {
	    	//manual mode
	    	//all manual buttons available
	    	roundButton.setVisibility(View.VISIBLE);
	    	
	    	exploreButton.setVisibility(View.INVISIBLE);
	    	shortButton.setVisibility(View.INVISIBLE);
	    	timerVal.setVisibility(View.INVISIBLE);
	    	startButton.setVisibility(View.INVISIBLE);
	    }
	}
	
	public void toggleStart(View view) {
	    // Is the toggle on?
	    boolean on = ((ToggleButton) view).isChecked();
	    
	    if (on) {
	    	//do something to end run?
	    	
	    	//end timer
	    	if(mChatService.getState() == BluetoothChatService.STATE_CONNECTED){
	    		if(autoAct == 1){
		    		sendMessage("Let's Explore!");
		    		startTime = SystemClock.uptimeMillis();
			    	customHandler.postDelayed(updateTimerThread, 0);
		    	}
		    	else if(autoAct ==2 ){
		    		sendMessage("Let's find the Shortest Path!");
		    		startTime = SystemClock.uptimeMillis();
			    	customHandler.postDelayed(updateTimerThread, 0);
		    	}
		    	else{
		    		startButton.setChecked(false);
		    		Toast.makeText(this, R.string.no_action, Toast.LENGTH_SHORT).show();
		    	}
	    	}
	    	else{
	    		startButton.setChecked(false);
	    		Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
	    	}
	    	
	    	
	    	autoAct = 0;
	    	
	    } else {
	    	//get something to say it is explore/shortest path
	    	//then when start is pressed then start the activity
	    	timeSwapBuff += timeInMilliseconds;
	    	customHandler.removeCallbacks(updateTimerThread);
	    	timeSwapBuff = 0;
	    }
	}
	
	private Runnable updateTimerThread = new Runnable() {

		public void run() {

			timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

			updatedTime = timeSwapBuff + timeInMilliseconds;

			int secs = (int) (updatedTime / 1000);
			int mins = secs / 60;
			secs = secs % 60;
			int milliseconds = (int) (updatedTime % 1000);
			timerVal.setText("" + mins + ":"
					+ String.format("%02d", secs) + ":"
					+ String.format("%03d", milliseconds));
			customHandler.postDelayed(this, 0);
		}

	};
	
	

}
