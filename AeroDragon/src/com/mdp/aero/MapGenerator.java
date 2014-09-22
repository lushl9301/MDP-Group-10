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
	Robot r;
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
        		if (counter ==0 || counter ==1)
        		{
        			tv.setBackgroundColor(Color.parseColor("#FF0000"));
        		}
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
	public void resetMap(){
		TextView resetThis;
		//int currentDir = r.getDirection();
		//int[][] currentPosition = r.getPosition();
		int i =0;
		for (int row=0; row<15; row++){
        	for (int col=0; col<20; col++){
        		resetThis = (TextView)gv.getChildAt(i);
        		resetThis.setBackgroundColor(Color.parseColor("#686868")); //arena colour
        		i++;
        	}
		} 

		int initial[][] = new int[2][2];
		//To note: -1 due to array index starts from 0!!!
		initial[0][0] = 0;
		initial[0][1] = 1;
		initial[1][0] = 20;
		initial[1][1] = 21;
		
		//r.setPosition(initial);
		//r.setDirection(r.EAST);//default set to south DO WE WANT TO MAKE THIS A VARIABLE?
		//setEast(r.getPosition());
		  
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
