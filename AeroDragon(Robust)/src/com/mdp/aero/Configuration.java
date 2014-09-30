package com.mdp.aero;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Configuration extends Activity {
	
	EditText func_1_text;
	EditText func_2_text;
	Button twoD;
	Button threeD;
	Button twoFiveD;
	public static final String DEFAULT = "N/A";
	String choice = "N/A";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Set up the window layout
		setContentView(R.layout.config);
		
		twoD = (Button) findViewById(R.id.disp2d);
		twoFiveD = (Button) findViewById(R.id.disp25d);
		threeD = (Button) findViewById(R.id.disp3d);
		
		twoD.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				choice = "2d";
				twoD.setBackgroundColor(Color.GREEN);
				twoFiveD.setBackgroundResource(R.drawable.btn_default_normal_holo_dark);
				threeD.setBackgroundResource(R.drawable.btn_default_normal_holo_dark);
			}	
		});
		twoFiveD.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				choice = "2.5d";
				twoFiveD.setBackgroundColor(Color.GREEN);
				threeD.setBackgroundResource(R.drawable.btn_default_normal_holo_dark);
				twoD.setBackgroundResource(R.drawable.btn_default_normal_holo_dark);
			}	
		});
		threeD.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				choice = "3d";
				threeD.setBackgroundColor(Color.GREEN);
				twoFiveD.setBackgroundResource(R.drawable.btn_default_normal_holo_dark);
				twoD.setBackgroundResource(R.drawable.btn_default_normal_holo_dark);
			}	
		});
		
		func_1_text = (EditText) findViewById(R.id.fun_1_text);
		func_2_text = (EditText) findViewById(R.id.fun_2_text);
		load(findViewById(R.id.load_btn));
	}
	
	public void save(View view){
		
		SharedPreferences sp = getSharedPreferences("MyData", Context.MODE_PRIVATE);
		SharedPreferences.Editor editSP =  sp.edit();
		
		editSP.putString("dispType", choice);
		
		editSP.putString("Function1", func_1_text.getText().toString());
		editSP.putString("Function2", func_2_text.getText().toString());
		
		editSP.commit();
		
		Toast.makeText(this, "Saved Changes Successfully", Toast.LENGTH_SHORT).show();
	}
	
	public void load(View view){
		SharedPreferences sp = getSharedPreferences("MyData", Context.MODE_PRIVATE);
		
		String dispType = sp.getString("dispType", DEFAULT);
		if(dispType.equals("2d")) {
		    //    load  2d map
			twoD.setBackgroundColor(Color.GREEN);
			twoFiveD.setBackgroundResource(R.drawable.btn_default_normal_holo_dark);
			threeD.setBackgroundResource(R.drawable.btn_default_normal_holo_dark);
		} else if(dispType.equals("2.5d")) {
		    //    load  2.5d map
			twoFiveD.setBackgroundColor(Color.GREEN);
			threeD.setBackgroundResource(R.drawable.btn_default_normal_holo_dark);
			twoD.setBackgroundResource(R.drawable.btn_default_normal_holo_dark);
		} else if(dispType.equals("3d")) {
		    //    load  3d map
			threeD.setBackgroundColor(Color.GREEN);
			twoFiveD.setBackgroundResource(R.drawable.btn_default_normal_holo_dark);
			twoD.setBackgroundResource(R.drawable.btn_default_normal_holo_dark);
		}else{
			twoD.setBackgroundColor(Color.GREEN);
			twoFiveD.setBackgroundResource(R.drawable.btn_default_normal_holo_dark);
			threeD.setBackgroundResource(R.drawable.btn_default_normal_holo_dark);
		}
		
		String f1 = sp.getString("Function1", DEFAULT);
		String f2 = sp.getString("Function2", DEFAULT);
		if(f1.equals(DEFAULT) || f2.equals(DEFAULT)){
			Toast.makeText(this, "No Function Data Found", Toast.LENGTH_SHORT).show();
		}else{
			Toast.makeText(this, "Function Loaded", Toast.LENGTH_SHORT).show();
			//setting the string
			func_1_text.setText(f1);
			func_2_text.setText(f2);
		}
	
	}
	
	
}
