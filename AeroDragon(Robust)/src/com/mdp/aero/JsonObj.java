package com.mdp.aero;

import org.json.*;

import android.util.Log;

public class JsonObj {
	public static JSONObject JObj;
	public static JSONObject JObjr;
	public static int[][] array2D = new int[15][20];
	static int y_head;
	static int x_head;
	static int y_tail;
	static int x_tail;
	static int [][] position = new int[3][3]; 
	static int dir =0;
	static Robot r;
	public static String words;
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
			return JObj.toString()+"\n";
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
					dir = data.getInt("direction");
					
					position[0][0] = ((y*20)-20+(x-1));
					position[0][1] = ((y*20)-20+(x-1))+1;
					position[0][2] = ((y*20)-20+(x-1))+2;
					position[1][0] = ((y*20)-20+(x-1))+20;
					position[1][1] = ((y*20)-20+(x-1))+21;
					position[1][2] = ((y*20)-20+(x-1))+22;
					position[2][0] = ((y*20)-20+(x-1))+40;
					position[2][1] = ((y*20)-20+(x-1))+41;
					position[2][2] = ((y*20)-20+(x-1))+42;
					
					
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
	public static String amdS(String msg){
		//for AMD tool
		
				return msg;
				
	}
	public static void amdString(String msg){
		//for AMD tool
				
				words = msg;
				String[] splited = msg.split("\\s+");
				Log.i("OutPut", splited[0]);
				x_head = Integer.parseInt(splited[3]);
				y_head = Integer.parseInt(splited[4]);
				x_tail = Integer.parseInt(splited[5]);
				y_tail = Integer.parseInt(splited[6]);
				
				if(y_head < y_tail){
					dir = 3;
				}
				else if (y_head > y_tail)
				{
					dir = 1;
				}
				else if (x_head < x_tail)
				{
					dir = 2;
				}
				else if (x_head > x_tail)
				{
					dir = 4;
				}
				position[0][0] = ((y_head*20)-20+(x_head-1));
				position[0][1] = ((y_head*20)-20+(x_head-1))+1;
				position[0][2] = ((y_head*20)-20+(x_head-1))+2;
				position[1][0] = ((y_head*20)-20+(x_head-1))+20;
				position[1][1] = ((y_head*20)-20+(x_head-1))+21;
				position[1][2] = ((y_head*20)-20+(x_head-1))+22;
				position[2][0] = ((y_head*20)-20+(x_head-1))+40;
				position[2][1] = ((y_head*20)-20+(x_head-1))+41;
				position[2][2] = ((y_head*20)-20+(x_head-1))+42;
				//Log.i("tag", Boolean.toString(y_head < y_tail));
				//r.setPosition(position);
				
				int o = 0;
				//int[][] arrayA = new int[15][20];
				if (splited[0].equals("GRID"))
				{
				for(int i=0; i<15;i++)
					   for(int j=0;j<20;j++)
					   {
					       array2D[i][j] = Integer.parseInt(splited[(j%20+i*20)+7]);
					   }
				}
				//return array2D;

				//JObjr = new JSONObject() ;
				
	}
}
