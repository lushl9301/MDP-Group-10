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
	
	public static int[][] recJson(String msg){
		
		String[] splited = msg.split("\\s+");
		Log.i("OutPut", splited[0]);
		int o = 0;
		int[][] array2D = new int[15][20];
		for(int i=0; i<15;i++)
			   for(int j=0;j<20;j++)
			   {
			       array2D[i][j] = Integer.parseInt(splited[(j%20+i*20)+7]);
			   }

		//JObjr = new JSONObject() ;
		return array2D;
		
		
	}
}
