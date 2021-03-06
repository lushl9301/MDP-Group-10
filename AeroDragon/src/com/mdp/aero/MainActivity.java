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

import java.util.Timer;
import com.mdp.aero.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * This is the main Activity that displays the current chat session.
 */
public class MainActivity extends Activity implements SensorEventListener{
	// Debugging
	private static final String TAG = "Aero Dragon";
	private static final boolean D = true;
	
	// Tilt Sensing
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private Sensor mMagnetometer;

	private float[] mLastAccelerometer = new float[3];
	private float[] mLastMagnetometer = new float[3];
	private boolean mLastAccelerometerSet = false;
	private boolean mLastMagnetometerSet = false;

	private float[] mR = new float[9];
	private float[] mOrientation = new float[3];

	private long lastUpdate;
	
	//Shared Preferences
	public static final String DEFAULT="N/A";
	String f1, f2, f1b,f2b,tilt,disp;

	// Message types sent from the Bluetooth Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	// Key names received from the Bluetooth Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE = 2;
	private static final int REQUEST_ENABLE_BT = 3;

	// Layout Views
	private ListView mConversationView;
	private TextView tvConnectionStatus;
	
	//Buttons
	private Button resetButton;
	private Button f1Button;
	private Button f2Button;
	private ToggleButton modeButton;
	private Button roundButton;
	private Button upButton;
	private Button downButton;
	private Button leftButton;
	private Button rightButton;
	private Button exploreButton;
	private Button shortButton;
	private ToggleButton startButton;
	
	//for stopwatch
	private TextView timerVal;
	private long startTime = 0L;
	long timeInMilliseconds = 0L;
	long timeSwapBuff = 0L;
	long updatedTime = 0L;
	private Handler customHandler = new Handler();
	
	Timer timer = new Timer();

	// Name of the connected device
	private String mConnectedDeviceName = null;
	// Array adapter for the conversation thread
	private ArrayAdapter<String> mConversationArrayAdapter;
	// String buffer for outgoing messages
	private StringBuffer mOutStringBuffer;
	// Local Bluetooth adapter
	private BluetoothAdapter ba = null;
	// Member object for the services
	private BluetoothManager btManager = null;
	
	//for status on sending
	String sentMsg = "No Action.";
	int autoAct = 0;
	
	MapGenerator map;
	
	public static JsonObj JsonO;
	

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
		map = new MapGenerator(gv,this);
		Log.i("tag","here4");
		
		tvConnectionStatus = (TextView)findViewById(R.id.tvConnectionStatus);
		tvConnectionStatus.setMovementMethod(new ScrollingMovementMethod());
		
		f1Button = (Button) findViewById(R.id.f1Button);
		f2Button = (Button) findViewById(R.id.f2Button);
		resetButton = (Button) findViewById(R.id.resetButton);
		modeButton = (ToggleButton) findViewById(R.id.modeButton);
		roundButton = (Button) findViewById(R.id.roundButton);
		upButton = (Button) findViewById(R.id.upButton);
		downButton = (Button) findViewById(R.id.downButton);
		leftButton = (Button) findViewById(R.id.leftButton);
		rightButton = (Button) findViewById(R.id.rightButton);
		exploreButton = (Button) findViewById(R.id.exploreBtn);
		shortButton = (Button) findViewById(R.id.shortBtn);
		timerVal = (TextView) findViewById(R.id.timer);
		startButton = (ToggleButton) findViewById(R.id.startBtn);
		
		
		
		
		load();
		//setting onClick listeners
		f1Button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				sendMessage(JsonObj.sendJson(f1, f1b));
			}		
		});
		
		f2Button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				sendMessage(JsonObj.sendJson(f2, f2b));
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
    	leftButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				map.turnLeftMap();
				sendMessage(JsonObj.sendJson("movement", MapGenerator.rotate));
			}
    	});
    	rightButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				map.turnRightMap();
				sendMessage(JsonObj.sendJson("movement", MapGenerator.rotate));
			}
    	});
    	upButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				map.moveForwardMap();
				sendMessage(JsonObj.sendJson("movement", MapGenerator.rotate));
			}
    	});
    	downButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				map.moveDownMap();
				sendMessage(JsonObj.sendJson("movement", MapGenerator.rotate));
			}
    	});
    	roundButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
			}
    	});
    	resetButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				sendMessage("Map Reset");
				map.resetMap(); //to reset map
			}
    	});

    	gv.setOnTouchListener(new OnSwipeTouchListener(this) {
			public void onSwipeTop() {
				map.moveForwardMap();
				sendMessage(JsonObj.sendJson("movement", MapGenerator.rotate));
			}

			public void onSwipeRight() {
				map.turnRightMap();
				sendMessage(JsonObj.sendJson("movement", MapGenerator.rotate));
			}

			public void onSwipeLeft() {
				map.turnLeftMap();
				sendMessage(JsonObj.sendJson("movement", MapGenerator.rotate));
			}

			public void onSwipeBottom() {
				map.moveDownMap();
				sendMessage(JsonObj.sendJson("movement", MapGenerator.rotate));
			}
		});

		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mMagnetometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		lastUpdate = System.currentTimeMillis();

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
			if (btManager == null)
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
		if (btManager != null) {
			// Only if the state is STATE_NONE, do we know that we haven't
			// started already
			if (btManager.getState() == BluetoothManager.STATE_NONE) {
				// Start the Bluetooth service
				btManager.start();
			}
		}
		
		load();
		
		mLastAccelerometerSet = false;
		mLastMagnetometerSet = false;
		mSensorManager.registerListener(this, mAccelerometer,
				SensorManager.SENSOR_DELAY_FASTEST);
		mSensorManager.registerListener(this, mMagnetometer,
				SensorManager.SENSOR_DELAY_FASTEST);
			
	}
	
	//to load shared preferences
	public void load(){
		SharedPreferences sp = getSharedPreferences("MyData", Context.MODE_PRIVATE);
		f1 = sp.getString("Function1", DEFAULT);
		f2 = sp.getString("Function2", DEFAULT);
		f1b = sp.getString("Function1b", DEFAULT);
		f2b = sp.getString("Function2b", DEFAULT);
		if(f1.equals(DEFAULT) || f2.equals(DEFAULT)||f1b.equals(DEFAULT) || f2b.equals(DEFAULT)){
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

		// Initialize the BluetoothManager to perform bluetooth connections
		btManager = new BluetoothManager(this, mHandler);

		// Initialize the buffer for outgoing messages
		mOutStringBuffer = new StringBuffer("");
	}

	@Override
	public synchronized void onPause() {
		super.onPause();
		if (D)
			Log.e(TAG, "- ON PAUSE -");
		invalidateOptionsMenu();
		mSensorManager.unregisterListener(this);
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
		// Stop the Bluetooth services
		if (btManager != null)
			btManager.stop();
		if (D)
			Log.e(TAG, "--- ON DESTROY ---");
	}

	//turn on and discover bluetooth
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

	//send message to AMD
	private void sendMessage(String message) {
		// Check that we're actually connected before trying anything
		if (btManager.getState() != BluetoothManager.STATE_CONNECTED) {
			Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT)
					.show();
			return;
		}

		// Check that there's actually something to send
		if (message.length() > 0) {
			sentMsg = message;
			// Get the message bytes and tell the Bluetooth Service to write
			byte[] send = message.getBytes();
			btManager.write(send);
		}
	}

	//set connection and action status
	private final void setStatus(int resId) {
		invalidateOptionsMenu();
		tvConnectionStatus.setText(resId);
	}

	private final void setStatus(CharSequence subTitle) {
		invalidateOptionsMenu();
		tvConnectionStatus.setText(subTitle);
	}

	// The Handler that gets information back from the BluetoothManager
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:
				if (D)
					Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
				switch (msg.arg1) {
				case BluetoothManager.STATE_CONNECTED:
					setStatus(getString(R.string.title_connected_to,
							mConnectedDeviceName) + sentMsg);
					sentMsg = "No Action.";
					mConversationArrayAdapter.clear();
					break;
				case BluetoothManager.STATE_CONNECTING:
					setStatus(R.string.title_connecting);
					break;
				case BluetoothManager.STATE_LISTEN:
				case BluetoothManager.STATE_NONE:
					setStatus(R.string.title_not_connected);
					break;
				case BluetoothManager.STATE_SEND:
					setStatus(getString(R.string.title_connected_to,
							mConnectedDeviceName) + sentMsg);
					break;

				}
				break;
			case MESSAGE_WRITE:
				byte[] writeBuf = (byte[]) msg.obj;
				// construct a string from the buffer
				String writeMessage = new String(writeBuf);
				//mConversationArrayAdapter.add("Me:  " + writeMessage);
				break;
				
			case MESSAGE_READ:
				byte[] readBuf = (byte[]) msg.obj;
				// construct a string from the valid bytes in the buffer
				String readMessage = new String(readBuf, 0, msg.arg1);
				JsonObj.recJson(readMessage);				
				map.getRobot().setPosition(JsonObj.position);
				map.plotObsAuto(JsonObj.array2D);
				Log.i("Tag", ""+JsonObj.array2D[0]);
				map.getRobot().setDirection(JsonObj.dir);
				if (JsonObj.dir==3)
				{
					map.setSouth(JsonObj.position);
				}
				else if (JsonObj.dir==1)
				{
					map.setNorth(JsonObj.position);
				}
				else if (JsonObj.dir==2)
				{
					map.setEast(JsonObj.position);
				}
				else if (JsonObj.dir==4)
				{
					map.setWest(JsonObj.position);
				}
				
				if (JsonObj.words.equals("END_EXP")|| JsonObj.words.equals("END_PATH")){
					startButton.setChecked(false);
					timeSwapBuff += timeInMilliseconds;
			    	customHandler.removeCallbacks(updateTimerThread);
			    	timeSwapBuff = 0;
				}
				
				setStatus(getString(R.string.title_connected_to,
						mConnectedDeviceName) + readMessage);
				
				
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
		btManager.connect(device);
	}

	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.option_menu, menu);
		return true;
	}

	
	//selecting things on action bar
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
			if(ba.isEnabled() && btManager.getState()==3){
				//if connected to device, disconnect
				btManager.stop();
			}
			else{
				Toast.makeText(getApplicationContext(), R.string.not_connected, Toast.LENGTH_SHORT).show();
			}
			return true;
		case R.id.dc_toggle:
			if(ba.isEnabled() && btManager.getState()!=3){
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
	
	
	
	//to refresh action bar
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
		
		if(ba.isEnabled() && btManager.getState()== btManager.STATE_CONNECTED){
			con_dev.setVisible(true);
            dis_dev.setVisible(false);
        }
        else{
        	con_dev.setVisible(false);
            dis_dev.setVisible(true);
        }
		
		
        return super.onPrepareOptionsMenu(menu);
	}
	
	
	//MODE BUTTON FUNCTION
	public void toggleMode(View view) {
	    // Is the toggle on?
	    boolean on = ((ToggleButton) view).isChecked();
	    
	    if (on) {
	    	//auto mode
	    	roundButton.setVisibility(View.INVISIBLE);
	    	upButton.setVisibility(View.INVISIBLE);
	    	downButton.setVisibility(View.INVISIBLE);
	    	leftButton.setVisibility(View.INVISIBLE);
	    	rightButton.setVisibility(View.INVISIBLE);
	    	
	    	exploreButton.setVisibility(View.VISIBLE);
	    	shortButton.setVisibility(View.VISIBLE);
	    	timerVal.setVisibility(View.VISIBLE);
	    	startButton.setVisibility(View.VISIBLE);
	    	
	    	sendMessage(JsonObj.sendJson("movement", "G"));
	    	
	    } else {
	    	//manual mode
	    	roundButton.setVisibility(View.VISIBLE);
	    	upButton.setVisibility(View.VISIBLE);
	    	downButton.setVisibility(View.VISIBLE);
	    	leftButton.setVisibility(View.VISIBLE);
	    	rightButton.setVisibility(View.VISIBLE);
	    	
	    	exploreButton.setVisibility(View.INVISIBLE);
	    	shortButton.setVisibility(View.INVISIBLE);
	    	timerVal.setVisibility(View.INVISIBLE);
	    	startButton.setVisibility(View.INVISIBLE);
	    	sendMessage(JsonObj.sendJson("command", "R"));
	    }
	}
	
	
	//START BUTTON FUNCTION
	public void toggleStart(View view) {
	    // Is the toggle on?
	    boolean on = ((ToggleButton) view).isChecked();
	    
	    if (on) {
	    	
	    	//timer = new Timer();
	    	
	    	//end timer
	    	if(btManager.getState() == BluetoothManager.STATE_CONNECTED){
	    		if(autoAct == 1){
	    			sendMessage(JsonObj.sendJson("command", "E"));
		    		startTime = SystemClock.uptimeMillis();
			    	customHandler.postDelayed(updateTimerThread, 0);
		    	}
		    	else if(autoAct ==2 ){
		    		sendMessage(JsonObj.sendJson("command", "P"));
		    		sendMessage(JsonObj.sendJson("path", JsonObj.fromRPI));
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
	    	sendMessage(JsonObj.sendJson("movement", "G"));
	    	timeSwapBuff += timeInMilliseconds;
	    	customHandler.removeCallbacks(updateTimerThread);
	    	timeSwapBuff = 0;
	    }
	}
	
	
	//STOPWATCH FUNCTION
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



public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	public void onSensorChanged(SensorEvent event) {

		SharedPreferences sp = getSharedPreferences("MyData", Context.MODE_PRIVATE);
		tilt = sp.getString("tilting", DEFAULT);
		
		long actualTime = event.timestamp;
		
		if (event.sensor == mAccelerometer) {
			System.arraycopy(event.values, 0, mLastAccelerometer, 0,
					event.values.length);
			mLastAccelerometerSet = true;
		} else if (event.sensor == mMagnetometer) {
			System.arraycopy(event.values, 0, mLastMagnetometer, 0,
					event.values.length);
			mLastMagnetometerSet = true;
		}
		if (mLastAccelerometerSet && mLastMagnetometerSet) {
			SensorManager.getRotationMatrix(mR, null, mLastAccelerometer,
					mLastMagnetometer);
			SensorManager.getOrientation(mR, mOrientation);
		}

		if (tilt.equals("On")) {
			

				if (((actualTime - lastUpdate) > 1000000000)) {
					float x = mOrientation[2];
					float y = mOrientation[1];
						if (Math.abs(x) > Math.abs(y)) {
							if (x > 0.7 ) {
								map.turnRightMap();
								Log.i(TAG, "RIGHT");

							}
							if (x < -0.7) {
								map.turnLeftMap();
								Log.i(TAG, "LEFT");
							}
						} else {
							if (y > 0.7) {
								map.moveForwardMap();
								Log.i(TAG, "UP");
							}
							if (y < -0.7) {
								map.moveDownMap();
								Log.i(TAG, "DOWN");
							}
						}
					
					lastUpdate = actualTime;
					sendMessage(JsonObj.sendJson("movement",
							MapGenerator.rotate));
				}
			
		} else {
			return;
		}

	}
}
