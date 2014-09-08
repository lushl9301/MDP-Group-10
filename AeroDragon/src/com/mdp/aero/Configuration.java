package com.mdp.aero;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Configuration extends Activity {
	
	EditText func_1_text;
	EditText func_2_text;
	public static final String DEFAULT = "N/A";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Set up the window layout
		setContentView(R.layout.config);
		
		func_1_text = (EditText) findViewById(R.id.fun_1_text);
		func_2_text = (EditText) findViewById(R.id.fun_2_text);
		load();
	}
	
	public void save(View view){
		
		SharedPreferences sp = getSharedPreferences("MyData", Context.MODE_PRIVATE);
		SharedPreferences.Editor editSP =  sp.edit();
		
		editSP.putString("Function1", func_1_text.getText().toString());
		editSP.putString("Function2", func_2_text.getText().toString());
		
		editSP.commit();
		
		Toast.makeText(this, "Saved Changes Successfully", Toast.LENGTH_SHORT).show();
	}
	
	public void load(){
		SharedPreferences sp = getSharedPreferences("MyData", Context.MODE_PRIVATE);
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
