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
					String firstLetter = hex.substring(0, 1);
					Log.i("huh", ""+firstLetter);
					if (firstLetter.equals("E"))
					{
						
						String decode = toBinary(hex.substring(1));
						int p = 0;
						String[] splited = new String[307];
						for (String sp: decode.split("")){
							
							splited[p]=sp;
							
							p++;
						}
						int q = 0;	
						for(int i=0; i<20;i++){
							   for(int j=0;j<15;j++)
							   {
								  
							       array2D[j][i] = Integer.parseInt(splited[(q)+3]);
							       q++;
							       
							   }
						}
						
					}
					else if (firstLetter.equals("S")){
						String decodeS = hex.substring(1);
						String[] split_b = new String[302];
						Log.i("huh", ""+decodeS);
						int l = 0;
						for (String sp: decodeS.split("")){
							split_b[l]=sp;
							
							l++;
						}
						int k = 0;	
						for(int i=0; i<20;i++){
							   for(int j=0;j<15;j++)
							   {
								  
							       array2D[j][i] = Integer.parseInt(split_b[(k)+1]);
							       k++;
							   }
						}
					}
				
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		
	}
	public static String toBinary(String hex) {
		return new BigInteger("1" + hex, 16).toString(2).substring(1);
	}

}
