package com.mdp.aero;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

public class Configuration extends Activity {

	EditText func_1a_text;
	EditText func_2a_text;
	EditText func_1b_text;
	EditText func_2b_text;
	Button twoD;
	Button threeD;
	Button twoFiveD;
	ToggleButton tiltBtn;
	public static final String DEFAULT = "N/A";
	String choice = "N/A";
	String tilt = "N/A";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set up the window layout
		setContentView(R.layout.config);

		twoD = (Button) findViewById(R.id.disp2d);
		twoFiveD = (Button) findViewById(R.id.disp25d);
		threeD = (Button) findViewById(R.id.disp3d);
		tiltBtn = (ToggleButton) findViewById(R.id.tiltButton);

		twoD.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				choice = "2d";
				twoD.setBackgroundColor(Color.GREEN);
				twoFiveD.setBackgroundResource(R.drawable.btn_default_normal_holo_dark);
				threeD.setBackgroundResource(R.drawable.btn_default_normal_holo_dark);
			}
		});
		twoFiveD.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				choice = "2.5d";
				twoFiveD.setBackgroundColor(Color.GREEN);
				threeD.setBackgroundResource(R.drawable.btn_default_normal_holo_dark);
				twoD.setBackgroundResource(R.drawable.btn_default_normal_holo_dark);
			}
		});
		threeD.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				choice = "3d";
				threeD.setBackgroundColor(Color.GREEN);
				twoFiveD.setBackgroundResource(R.drawable.btn_default_normal_holo_dark);
				twoD.setBackgroundResource(R.drawable.btn_default_normal_holo_dark);
			}
		});

		func_1a_text = (EditText) findViewById(R.id.fun_1a_text);
		func_2a_text = (EditText) findViewById(R.id.fun_2a_text);
		func_1b_text = (EditText) findViewById(R.id.fun_1b_text);
		func_2b_text = (EditText) findViewById(R.id.fun_2b_text);
		load(findViewById(R.id.load_btn));
	}

	public void save(View view) {

		SharedPreferences sp = getSharedPreferences("MyData",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editSP = sp.edit();

		editSP.putString("dispType", choice);
		editSP.putString("tilting", tilt);
		editSP.putString("Function1", func_1a_text.getText().toString());
		editSP.putString("Function2", func_2a_text.getText().toString());
		editSP.putString("Function1b", func_1b_text.getText().toString());
		editSP.putString("Function2b", func_2b_text.getText().toString());

		editSP.commit();

		Toast.makeText(this, "Saved Changes Successfully", Toast.LENGTH_SHORT)
				.show();
	}

	public void load(View view) {
		SharedPreferences sp = getSharedPreferences("MyData",
				Context.MODE_PRIVATE);

		String dispType = sp.getString("dispType", DEFAULT);
		if (dispType.equals("2d")) {
			// load 2d map
			twoD.setBackgroundColor(Color.GREEN);
			twoFiveD.setBackgroundResource(R.drawable.btn_default_normal_holo_dark);
			threeD.setBackgroundResource(R.drawable.btn_default_normal_holo_dark);
		} else if (dispType.equals("2.5d")) {
			// load 2.5d map
			twoFiveD.setBackgroundColor(Color.GREEN);
			threeD.setBackgroundResource(R.drawable.btn_default_normal_holo_dark);
			twoD.setBackgroundResource(R.drawable.btn_default_normal_holo_dark);
		} else if (dispType.equals("3d")) {
			// load 3d map
			threeD.setBackgroundColor(Color.GREEN);
			twoFiveD.setBackgroundResource(R.drawable.btn_default_normal_holo_dark);
			twoD.setBackgroundResource(R.drawable.btn_default_normal_holo_dark);
		} else {
			twoD.setBackgroundColor(Color.GREEN);
			twoFiveD.setBackgroundResource(R.drawable.btn_default_normal_holo_dark);
			threeD.setBackgroundResource(R.drawable.btn_default_normal_holo_dark);
		}
		
		String tiltType = sp.getString("tilting", DEFAULT);
		if(tiltType.equals("On")){
			tiltBtn.setChecked(true);
		}else{
			tiltBtn.setChecked(false);
		}

		String f1 = sp.getString("Function1", DEFAULT);
		String f2 = sp.getString("Function2", DEFAULT);
		String f1b = sp.getString("Function1b", DEFAULT);
		String f2b = sp.getString("Function2b", DEFAULT);
		if (f1.equals(DEFAULT) || f2.equals(DEFAULT) || f1b.equals(DEFAULT)
				|| f2b.equals(DEFAULT)) {
			Toast.makeText(this, "No Function Data Found", Toast.LENGTH_SHORT)
					.show();
		} else {
			Toast.makeText(this, "Function Loaded", Toast.LENGTH_SHORT).show();
			// setting the string
			func_1a_text.setText(f1);
			func_2a_text.setText(f2);
			func_1b_text.setText(f1b);
			func_2b_text.setText(f2b);
		}

	}

	// START BUTTON FUNCTION
	public void toggleTilt(View view) {
		// Is the toggle on?
		boolean on = ((ToggleButton) view).isChecked();

		if (on) {
			tilt = "On";
		} else {
			tilt = "Off";
		}
	}

}
