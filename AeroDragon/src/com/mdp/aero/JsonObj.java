package com.mdp.aero;

import org.json.*;

import android.util.Log;

public class JsonObj {
	public static JSONObject JObj;
	
	public JsonObj(){
		//JObj = new JSONObject();
	}
	
	public static String sendJson(String type, String data){
		
		JObj = new JSONObject() ;
		try {
			JObj.put("data", data);
			JObj.put("type", type);
			 //Log.i("TAG", type);
			 Log.i("OutPut", JObj.toString());
			return JObj.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return null;
		
	}
}
