package com.mdp.aero;

import org.json.*;
import java.math.BigInteger;

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
	public static String words = "";
	public static String fromRPI = "";
	public JsonObj(){
		//JObj = new JSONObject();
		//r = new Robot();
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
			JSONObject jsonObj = new JSONObject(msg);
			fromRPI = jsonObj.getString("data");
				if(jsonObj.getString("type").equals("reading")){
					JSONObject data = jsonObj.getJSONObject("data");
					int x = data.getInt("X");
					int y = data.getInt("Y");
					dir = data.getInt("direction");
					
					position[0][0] = ((y*20)-20+(x-1))-21;
					position[0][1] = ((y*20)-20+(x-1))-20;
					position[0][2] = ((y*20)-20+(x-1))-19;
					position[1][0] = ((y*20)-20+(x-1))-1;
					position[1][1] = ((y*20)-20+(x-1));
					position[1][2] = ((y*20)-20+(x-1))+1;
					position[2][0] = ((y*20)-20+(x-1))+19;
					position[2][1] = ((y*20)-20+(x-1))+20;
					position[2][2] = ((y*20)-20+(x-1))+21;
					
					
				}
				else if(jsonObj.getString("type").equals("status")){
					
					words = jsonObj.getString("data");
					
				}
				else if(jsonObj.getString("type").equals("path")){
					
					Log.i("path", fromRPI);
					
				}
				else if(jsonObj.getString("type").equals("map")){
					String hex = jsonObj.getString("data");
					boolean isHex = hex.matches("[0-9A-F]+");
					//FOR MD1 ONLY
					if (isHex == true){
						String decode = toBinary(hex);
						int p = 0;
						String[] splited = new String[305];
						for (String sp: decode.split("")){
							splited[p]=sp;
							
							p++;
						}
						int q = 0;	
						for(int i=0; i<20;i++){
							   for(int j=0;j<15;j++)
							   {
								   //Log.i("SP", "j:"+j+"i:"+i+"t:"+splited[(q)]);
							       array2D[j][i] = Integer.parseInt(splited[(q)+3]);
							       q++;
							   }
						}
						
							
					}
					else{
						String[] split_b = new String[300];
						int l = 0;
						for (String sp: hex.split("")){
							split_b[l]=sp;
							
							l++;
						}
						for(int i=0; i<15;i++)
							   for(int j=0;j<20;j++)
							   {
							       array2D[i][j] = Integer.parseInt(split_b[(j%20+i*20)]);
							   }
						
					}
					//Log.i("abc", splited[0]);
					//String[] splited = decode.split("\\d");
					
					/*int[][] array = new int[15][20];
					 * String[] splited = msg.split("\\s+");
					//JSONArray data = jsonObj.getJSONArray("data");
					for (int y=0; y<15;y++){
						for (int z=0; z<20;z++){
							array[y][z]=data.getInt(z);
							//should have error
						}*/
				
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				//continue;
				e.printStackTrace();
			}
			
		
	}
	public static String toBinary(String hex) {
		return new BigInteger("1" + hex, 16).toString(2).substring(1);
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
