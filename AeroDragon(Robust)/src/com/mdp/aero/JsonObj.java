package com.mdp.aero;

import org.json.*;

import android.util.Log;

public class JsonObj {
	public static JSONObject JObj;
	public static JSONObject JObjr;
	public static int[][] array2D = new int[15][20];
	static Robot r;
	public JsonObj(){
		//JObj = new JSONObject();
		r = new Robot();
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
	
	public static void recJson(String msg){
		try {
			JSONObject jsonObj = new JSONObject("{\"type\": \"map\",\"data\": [0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0]}");
				
				if(jsonObj.getString("type").equals("reading")){
					JSONObject data = jsonObj.getJSONObject("data");
					int x = data.getInt("X");
					int y = data.getInt("Y");
					int direction = data.getInt("direction");
					r.setDirection(direction);
					
				}
				else if(jsonObj.getString("type").equals("status")){
					
					if(jsonObj.getString("data").equals("END_EXP")){
						//stop timer?
						//MainActivity.
					}
					else if(jsonObj.getString("data").equals("END_PATH")){
						//Log.i("Tag", jsonObj.getString("data"));
						//stop timer?
					}
				}
				else if(jsonObj.getString("type").equals("map")){
					
					int[] array = new int[300];
					JSONArray data = jsonObj.getJSONArray("data");
					for (int z=0; z<data.length();z++){
						array[z]=data.getInt(z);
					}
					for(int i=0; i<15;i++)
						   for(int j=0;j<20;j++)
						   {
						       array2D[i][j] = array[(j%20+i*20)];
						   }
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		
	}
	
	public static int[][] amdString(String msg){
		//for AMD tool
		
				String[] splited = msg.split("\\s+");
				Log.i("OutPut", splited[0]);
				int o = 0;
				int[][] arrayA = new int[15][20];
				for(int i=0; i<15;i++)
					   for(int j=0;j<20;j++)
					   {
					       arrayA[i][j] = Integer.parseInt(splited[(j%20+i*20)+7]);
					   }

				//JObjr = new JSONObject() ;
				return arrayA;
	}
}
