package com.mdp.aero;

import org.json.*;

import android.util.Log;

public class JsonObj {
	public static JSONObject JObj;
	public static JSONObject JObjr;
	public JsonObj(){
		//JObj = new JSONObject();
	}
	
	public static String sendJson(String type, String data){
		
		JObj = new JSONObject() ;
		try {
			JObj.put("data", data);
			JObj.put("type", type);
			 Log.i("OutPut", JObj.toString());
			return JObj.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return null;		
	}
	
	public int[][] recJson(){
		JObjr = new JSONObject() ;
		
		return null;
		
	}
}
