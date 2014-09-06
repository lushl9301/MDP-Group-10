package com.mdp.aero;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.TextView;

public class MapGenerator {
	Context context;
	GridLayout gv = null;
	public MapGenerator(GridLayout gv, Context context){
		this.gv=gv;
		this.context=context;
		Log.i("tag", "before create arena ok");
		int[][] arena = new int[15][20];
		Log.i("tag", "before create arena ok");
		createArena(gv);
		Log.i("tag", "create arena ok");
		arena  = getAccessToArena(gv);
		Log.i("tag", "create arena ok");
		printArray(arena);
		Log.i("tag", "print arena ok");

	}
	
	private void createArena(GridLayout gv)
	{
		Log.i("tag", "at create arena");
        TextView tv;
        int counter =0;
        
        for (int row=0; row<15; row++){
        	for (int col=0; col<20; col++){
        		tv = new TextView(this.context);
        		tv.setBackgroundColor(Color.parseColor("#686868")); //arena colour
        		//tv.setText("."); for the real output

        		tv.setId(counter);
        		tv.setText(Integer.toString(tv.getId()));
        		tv.setGravity(Gravity.CENTER);
        		
        		tv.setTextSize(9);
        		tv.setWidth(37);
           		tv.setHeight(37);
           		gv.addView(tv);
           		counter++;
           		
        	}
        }
 
        
	}
	private int[][] getAccessToArena(GridLayout gv)
	{
        int i=0;
    	int[ ][ ] tvArray = new int[15][20];
    	for (int k=0; k<15; k++){
    		for (int l =0; l<20; l++){
    			TextView child = (TextView)gv.getChildAt(i);
    			i++;
    			tvArray[k][l]=child.getId();
    		}
    	}
    	return tvArray;
	}
	
	private void printArray(int tvArray[][])
	{
	        for(int p =0; p < tvArray.length; p++)
	        {
		       	 for(int e=0; e < tvArray[p].length; e++)
		       	 {
		       		 System.out.print(tvArray[p][e] + " ");
		       	 }
		       	 	 System.out.println();
	        }
	}
}
