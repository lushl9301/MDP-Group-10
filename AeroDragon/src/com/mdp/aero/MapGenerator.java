package com.mdp.aero;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.TextView;

public class MapGenerator {
	Context context;
	GridLayout gv = null;
	Robot r = setupRobot();
	ArrayList<TextView> obstacleArray = new ArrayList<TextView>();
	TextView topLeft, topRight, bottomLeft, bottomRight,midLeft, midRight, topMid, bottomMid,midMid;
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
	public Robot getRobot (){
		return r;
	}
	
	private Robot setupRobot()
	{
		int initial[][] = new int[3][3];
		//To note: -1 due to array index starts from 0!!!
		initial[0][0] = 0;
		initial[0][1] = 1;
		initial[0][2] = 2;
		initial[1][0] = 20;
		initial[1][1] = 21;
		initial[1][2] = 22;
		initial[2][0] = 40;
		initial[2][1] = 41;
		initial[2][2] = 42;
		
		Robot r = new Robot();
		r.setPosition(initial);
		r.setDirection(r.EAST);//default set to south DO WE WANT TO MAKE THIS A VARIABLE?
    	
		return r;
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
        		if (counter ==0 || counter ==1||counter ==2 || 
        				counter ==20||counter ==21 || counter ==22||
        				counter ==40||counter ==41 || counter ==42 ||
        				counter ==257 || counter ==258||counter ==259 || 
        				counter ==277||counter ==278 || counter ==279||
        				counter ==297||counter ==298 || counter ==299)
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
        		
        		if (i ==0 || i ==1||i ==2 || 
        				i ==20||i ==21 || i ==22||
        				i ==40||i ==41 || i ==42||
        				i ==257 || i ==258||i ==259 || 
        				i ==277||i ==278 || i ==279||
        				i ==297||i ==298 || i ==299)
        		{
        			resetThis.setBackgroundColor(Color.parseColor("#FF0000"));
        		}
        		i++;
        	}
		} 

		int initial[][] = new int[3][3];
		//To note: -1 due to array index starts from 0!!!
		initial[0][0] = 0;
		initial[0][1] = 1;
		initial[0][2] = 2;
		initial[1][0] = 20;
		initial[1][1] = 21;
		initial[1][2] = 22;
		initial[2][0] = 40;
		initial[2][1] = 41;
		initial[2][2] = 42;
		
		r.setPosition(initial);
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
	
	public void setNorth( int[][] currentPosition){
		topLeft = (TextView)gv.getChildAt(currentPosition[0][0]);
		topMid= (TextView)gv.getChildAt(currentPosition[0][1]);
        topRight = (TextView)gv.getChildAt(currentPosition[0][2]);
		midLeft= (TextView)gv.getChildAt(currentPosition[1][0]);
		midMid= (TextView)gv.getChildAt(currentPosition[1][1]);
		midRight= (TextView)gv.getChildAt(currentPosition[1][2]);
        bottomLeft = (TextView)gv.getChildAt(currentPosition[2][0]);
        bottomRight = (TextView)gv.getChildAt(currentPosition[2][2]);
        bottomMid= (TextView)gv.getChildAt(currentPosition[2][1]);
        
        Log.i("tag","in set North");
        colourRobot(topLeft, topRight, bottomLeft, bottomRight,topMid, bottomMid);
	}
	public void setEast(int[][] currentPosition){        
        topLeft = (TextView)gv.getChildAt(currentPosition[0][2]);
		topMid= (TextView)gv.getChildAt(currentPosition[1][2]);
        topRight = (TextView)gv.getChildAt(currentPosition[2][2]);
		midLeft= (TextView)gv.getChildAt(currentPosition[0][1]);
		midMid= (TextView)gv.getChildAt(currentPosition[1][1]);
		midRight= (TextView)gv.getChildAt(currentPosition[2][1]);
        bottomLeft = (TextView)gv.getChildAt(currentPosition[0][0]);
        bottomRight = (TextView)gv.getChildAt(currentPosition[2][0]);
        bottomMid= (TextView)gv.getChildAt(currentPosition[1][0]);
        Log.i("tag","in set East");
        colourRobot(topLeft, topRight, bottomLeft, bottomRight,topMid, bottomMid);
	}
	public void setSouth(int[][] currentPosition){        
        topLeft = (TextView)gv.getChildAt(currentPosition[2][2]);
		topMid= (TextView)gv.getChildAt(currentPosition[2][1]);
        topRight = (TextView)gv.getChildAt(currentPosition[2][0]);
		midLeft= (TextView)gv.getChildAt(currentPosition[1][2]);
		midMid= (TextView)gv.getChildAt(currentPosition[1][1]);
		midRight= (TextView)gv.getChildAt(currentPosition[1][0]);
        bottomLeft = (TextView)gv.getChildAt(currentPosition[0][2]);
        bottomRight = (TextView)gv.getChildAt(currentPosition[0][0]);
        bottomMid= (TextView)gv.getChildAt(currentPosition[0][1]);
        Log.i("tag","in set South");
        colourRobot(topLeft, topRight, bottomLeft, bottomRight,topMid, bottomMid);
	}
	public void setWest(int[][] currentPosition){
		topLeft = (TextView)gv.getChildAt(currentPosition[2][0]);
		topMid= (TextView)gv.getChildAt(currentPosition[1][0]);
        topRight = (TextView)gv.getChildAt(currentPosition[0][0]);
		midLeft= (TextView)gv.getChildAt(currentPosition[2][1]);
		midMid= (TextView)gv.getChildAt(currentPosition[1][1]);
		midRight= (TextView)gv.getChildAt(currentPosition[0][1]);
        bottomLeft = (TextView)gv.getChildAt(currentPosition[2][2]);
        bottomRight = (TextView)gv.getChildAt(currentPosition[0][2]);
        bottomMid= (TextView)gv.getChildAt(currentPosition[1][2]);
        Log.i("tag","in set West");
        colourRobot(topLeft, topRight, bottomLeft, bottomRight,topMid, bottomMid);
	}
	public void moveForwardMap() {
		 
        int currentPosition[][] =  r.getPosition();
        int direction = r.getDirection();

        TextView topLeft = (TextView)gv.getChildAt(currentPosition[0][0]);
        TextView topMid= (TextView)gv.getChildAt(currentPosition[0][1]);
        TextView topRight = (TextView)gv.getChildAt(currentPosition[0][2]);
        TextView midLeft= (TextView)gv.getChildAt(currentPosition[1][0]);
        TextView midMid= (TextView)gv.getChildAt(currentPosition[1][1]);
        TextView midRight= (TextView)gv.getChildAt(currentPosition[1][2]);
        TextView bottomLeft = (TextView)gv.getChildAt(currentPosition[2][0]);
        TextView bottomRight = (TextView)gv.getChildAt(currentPosition[2][2]);
        TextView bottomMid= (TextView)gv.getChildAt(currentPosition[2][1]);
	        
	        //Explored colour
	        topLeft.setBackgroundColor(Color.parseColor("#7fb2e5"));
	        topMid.setBackgroundColor(Color.parseColor("#7fb2e5"));
	        topRight.setBackgroundColor(Color.parseColor("#7fb2e5"));
	        midLeft.setBackgroundColor(Color.parseColor("#7fb2e5"));
	        midMid.setBackgroundColor(Color.parseColor("#7fb2e5"));
	        midRight.setBackgroundColor(Color.parseColor("#7fb2e5"));
	        bottomLeft.setBackgroundColor(Color.parseColor("#7fb2e5"));
	        bottomMid.setBackgroundColor(Color.parseColor("#7fb2e5"));
	        bottomRight.setBackgroundColor(Color.parseColor("#7fb2e5"));
	
	        if (direction == r.EAST){
	        	TextView curTopLeft = (TextView)gv.getChildAt(currentPosition[0][2]);
	            TextView curTopMid= (TextView)gv.getChildAt(currentPosition[1][2]);
	            TextView curTopRight = (TextView)gv.getChildAt(currentPosition[2][2]);
	            TextView curMidLeft= (TextView)gv.getChildAt(currentPosition[0][1]);
	            TextView curMidMid= (TextView)gv.getChildAt(currentPosition[1][1]);
	            TextView curMidRight= (TextView)gv.getChildAt(currentPosition[2][1]);
	            TextView curBottomLeft = (TextView)gv.getChildAt(currentPosition[0][0]);
	            TextView curBottomRight = (TextView)gv.getChildAt(currentPosition[2][0]);
	            TextView curBottomMid= (TextView)gv.getChildAt(currentPosition[1][0]);
	            
		        Log.i("tag","robot east");
		        Log.i("tag",curTopLeft.getText().toString());
		        int temp = Integer.parseInt(topLeft.getText().toString());
		        if ( (temp+2)%20==0 ){
		        	Log.i("tag","invalid move");
		        	colourRobot(curTopLeft, curTopRight, curBottomLeft, curBottomRight,topMid,bottomMid);
		        	return;
		        }
		        else {
			        currentPosition[0][0] += 1;
			        currentPosition[0][1] += 1;
			        currentPosition[0][2] += 1;
			        currentPosition[1][0] += 1;
			        currentPosition[1][1] += 1;
			        currentPosition[1][2] += 1;
			        currentPosition[2][0] += 1;
			        currentPosition[2][1] += 1;
			        currentPosition[2][2] += 1;
			        setEast(currentPosition);
		        }
	        }
	        if (direction == r.SOUTH){	            
	            TextView curTopLeft = (TextView)gv.getChildAt(currentPosition[2][2]);
	            TextView curTopMid= (TextView)gv.getChildAt(currentPosition[2][1]);
	            TextView curTopRight = (TextView)gv.getChildAt(currentPosition[2][0]);
	            TextView curMidLeft= (TextView)gv.getChildAt(currentPosition[1][2]);
	            TextView curMidMid= (TextView)gv.getChildAt(currentPosition[1][1]);
	            TextView curMidRight= (TextView)gv.getChildAt(currentPosition[1][0]);
	            TextView curBottomLeft = (TextView)gv.getChildAt(currentPosition[0][2]);
	            TextView curBottomRight = (TextView)gv.getChildAt(currentPosition[0][0]);
	            TextView curBottomMid= (TextView)gv.getChildAt(currentPosition[0][1]);
		        Log.i("tag","robot south");
		        Log.i("tag",curTopLeft.getText().toString());
		        if ( Integer.parseInt(curTopLeft.getText().toString())>=280 && Integer.parseInt(topLeft.getText().toString())<300){
		        	Log.i("tag","invalid move");
		        	colourRobot(curTopLeft, curTopRight, curBottomLeft, curBottomRight,topMid,bottomMid);
		        	return;
		        }
		        else {
		        	currentPosition[0][0] += 20;
			        currentPosition[0][1] += 20;
			        currentPosition[0][2] += 20;
			        currentPosition[1][0] += 20;
			        currentPosition[1][1] += 20;
			        currentPosition[1][2] += 20;
			        currentPosition[2][0] += 20;
			        currentPosition[2][1] += 20;
			        currentPosition[2][2] += 20;
			        setSouth(currentPosition);
		        }
	        }
	        if (direction == r.WEST){
	        	TextView curTopLeft = (TextView)gv.getChildAt(currentPosition[2][0]);
	            TextView curTopMid= (TextView)gv.getChildAt(currentPosition[1][0]);
	            TextView curTopRight = (TextView)gv.getChildAt(currentPosition[0][0]);
	            TextView curMidLeft= (TextView)gv.getChildAt(currentPosition[2][1]);
	            TextView curMidMid= (TextView)gv.getChildAt(currentPosition[1][1]);
	            TextView curMidRight= (TextView)gv.getChildAt(currentPosition[0][1]);
	            TextView curBottomLeft = (TextView)gv.getChildAt(currentPosition[2][2]);
	            TextView curBottomRight = (TextView)gv.getChildAt(currentPosition[0][2]);
	            TextView curBottomMid= (TextView)gv.getChildAt(currentPosition[1][2]);
	            
		        Log.i("tag","robot west");
		        Log.i("tag",curTopLeft.getText().toString());
		        if ( Integer.parseInt(curTopLeft.getText().toString())%20==0 ){
		        	Log.i("tag","invalid move");
		        	colourRobot(curTopLeft, curTopRight, curBottomLeft, curBottomRight,topMid,bottomMid);
		        	return;
		        }
		        else {
		        	
		        	currentPosition[0][0] -= 1;
			        currentPosition[0][1] -= 1;
			        currentPosition[0][2] -= 1;
			        currentPosition[1][0] -= 1;
			        currentPosition[1][1] -= 1;
			        currentPosition[1][2] -= 1;
			        currentPosition[2][0] -= 1;
			        currentPosition[2][1] -= 1;
			        currentPosition[2][2] -= 1; 
			        setWest(currentPosition);
		        }
	        }
	        if (direction == r.NORTH){
	        	TextView curTopLeft = (TextView)gv.getChildAt(currentPosition[0][0]);
	            TextView curTopMid= (TextView)gv.getChildAt(currentPosition[0][1]);
	            TextView curTopRight = (TextView)gv.getChildAt(currentPosition[0][2]);
	            TextView curMidLeft= (TextView)gv.getChildAt(currentPosition[1][0]);
	            TextView curMidMid= (TextView)gv.getChildAt(currentPosition[1][1]);
	            TextView curMidRight= (TextView)gv.getChildAt(currentPosition[1][1]);
	            TextView curBottomLeft = (TextView)gv.getChildAt(currentPosition[2][0]);
	            TextView curBottomRight = (TextView)gv.getChildAt(currentPosition[2][2]);
	            TextView curBottomMid= (TextView)gv.getChildAt(currentPosition[2][1]);
		        Log.i("tag","robot north");
		        Log.i("tag",curTopLeft.getText().toString());
		        if ( Integer.parseInt(topLeft.getText().toString())>=0 && Integer.parseInt(topLeft.getText().toString())<20){
		        	Log.i("tag","invalid move");
		        	colourRobot(curTopLeft, curTopRight, curBottomLeft, curBottomRight,topMid,bottomMid);
		        	return;
		        }
		        else {
		        	currentPosition[0][0] -= 20;
			        currentPosition[0][1] -= 20;
			        currentPosition[0][2] -= 20;
			        currentPosition[1][0] -= 20;
			        currentPosition[1][1] -= 20;
			        currentPosition[1][2] -= 20;
			        currentPosition[2][0] -= 20;
			        currentPosition[2][1] -= 20;
			        currentPosition[2][2] -= 20;
			        setNorth(currentPosition);
		        }
	        }
	        
	        r.setPosition(currentPosition);
        }
	public void turnRightMap() {
		int currentPosition[][] =  r.getPosition();
		int direction = r.getDirection();
	        
        if (direction == r.EAST){
        	r.setDirection(r.SOUTH);
        	Log.i("tag","now face south");
        	setSouth(currentPosition);
        }
        if (direction == r.SOUTH){
        	r.setDirection(r.WEST);
        	Log.i("tag","now face west");
        	setWest(currentPosition);
	        
        }
        if (direction == r.WEST){
        	r.setDirection(r.NORTH);
        	Log.i("tag","now face north");
        	setNorth(currentPosition);	        
        }
        if (direction == r.NORTH){
        	r.setDirection(r.EAST);
        	Log.i("tag","now face east");
	        setEast(currentPosition);
        }
	}
 

	public void turnLeftMap() {
	
	    int currentPosition[][] =  r.getPosition();
	    int direction = r.getDirection();
		
	
        if (direction == r.EAST){
        	r.setDirection(r.NORTH);
        	Log.i("tag","now face north");
        	setNorth(currentPosition);
        }
        if (direction == r.SOUTH){
        	r.setDirection(r.EAST);
        	Log.i("tag","now face east");
        	setEast(currentPosition);
	        
        }
        if (direction == r.WEST){
        	r.setDirection(r.SOUTH);
        	Log.i("tag","now face south");
        	setSouth(currentPosition);
	        
        }
        if (direction == r.NORTH){
        	r.setDirection(r.WEST);
        	Log.i("tag","now face west");
        	setWest(currentPosition);
        }        	        
	}
	
	//ONLY WORKING FOR 2x2
	protected void plotObstacle(int sensorPos, int o, int direction, int[][] pos){
		
	    TextView colourThis = (TextView)gv.getChildAt(0);
	    try{
		switch (sensorPos){
		case MainActivity.TOP_LEFT_SIDE:
			Log.i("tag","enter");
			if (direction==r.NORTH){
				Log.i("tag","north");
				colourThis= (TextView)gv.getChildAt((pos[0][0])-o);
				if (colourThis.getId()/10==pos[0][0]/10) colourThis.setBackgroundColor(Color.BLACK);
			}
			if (direction==r.EAST){
				//Log.i("TAG","e"+pos[0][2]);
				colourThis= (TextView)gv.getChildAt((pos[0][2])-(o*20));
				if (colourThis.getId()>0) colourThis.setBackgroundColor(Color.BLACK);
			}
			if (direction==r.SOUTH){
				Log.i("tag","south");
				colourThis= (TextView)gv.getChildAt((pos[2][2])+ o);
				if (colourThis.getId()/10==pos[2][2]/10) colourThis.setBackgroundColor(Color.BLACK);
			}
			if (direction==r.WEST){
				Log.i("tag","west");
				colourThis= (TextView)gv.getChildAt((pos[2][0])+(o*20));
				if (colourThis.getId()<299) colourThis.setBackgroundColor(Color.BLACK);
			}
			break;
			
		case MainActivity.TOP_LEFT_FRONT:
			if (direction==r.NORTH){
				colourThis= (TextView)gv.getChildAt((pos[0][0])-(o*20));
				if (colourThis.getId()>=0) colourThis.setBackgroundColor(Color.BLACK);
			}
			if (direction==r.EAST){
				colourThis= (TextView)gv.getChildAt((pos[0][2])+ o);
				if (colourThis.getId()/10==pos[0][2]/10) colourThis.setBackgroundColor(Color.BLACK);
			}
			if (direction==r.SOUTH){
				colourThis= (TextView)gv.getChildAt((pos[2][2])+(o*20));
				if (colourThis.getId()<300) colourThis.setBackgroundColor(Color.BLACK);
			}
			if (direction==r.WEST){
				colourThis= (TextView)gv.getChildAt((pos[2][0])- o);
				if (colourThis.getId()/10==pos[2][0]/10) colourThis.setBackgroundColor(Color.BLACK);
			}
			break;
			
		case MainActivity.TOP_RIGHT_FRONT:
			if (direction==r.NORTH){
				colourThis= (TextView)gv.getChildAt((pos[0][2])-(o*20));
				if (colourThis.getId()>=1) colourThis.setBackgroundColor(Color.BLACK);
			}
			if (direction==r.EAST){
				colourThis= (TextView)gv.getChildAt((pos[2][2])+ o);
				if (colourThis.getId()/10==pos[2][2]/10) colourThis.setBackgroundColor(Color.BLACK);
			}
			if (direction==r.SOUTH){
				colourThis= (TextView)gv.getChildAt((pos[2][0])+(o*20));
				if (colourThis.getId()<299) colourThis.setBackgroundColor(Color.BLACK);
			}
			if (direction==r.WEST){
				colourThis= (TextView)gv.getChildAt((pos[0][0])-o);
				if (colourThis.getId()/10==pos[0][0]/10) colourThis.setBackgroundColor(Color.BLACK);
			}
			break;
			
		case MainActivity.TOP_RIGHT_SIDE:
			if (direction==r.NORTH){
				colourThis= (TextView)gv.getChildAt((pos[0][2])+ o);
				if (colourThis.getId()/10==pos[0][2]/10) colourThis.setBackgroundColor(Color.BLACK);
			}
			if (direction==r.EAST){
				colourThis= (TextView)gv.getChildAt((pos[2][2])+(o*20));
				if (colourThis.getId()<=299) colourThis.setBackgroundColor(Color.BLACK);
			}
			if (direction==r.SOUTH){
				colourThis= (TextView)gv.getChildAt((pos[2][0])- o);
				if (colourThis.getId()/10==pos[2][0]/10) colourThis.setBackgroundColor(Color.BLACK);
			}
			if (direction==r.WEST){
				colourThis= (TextView)gv.getChildAt((pos[0][0])-(o*20));
				if (colourThis.getId()>=0) colourThis.setBackgroundColor(Color.BLACK);
			}
			break;
		}}catch(Exception e){return;}
		Log.i("tag","plotobstacle");
		obstacleArray.add(colourThis);
		Log.i("tag","add to array obstacle");
		
	}
	public void colourRobot(TextView topLeft, TextView topRight, TextView bottomLeft, TextView bottomRight,TextView topMid, TextView bottomMid){
		topLeft.setBackgroundColor(Color.GREEN);
		topMid.setBackgroundColor(Color.GREEN);
        topRight.setBackgroundColor(Color.GREEN);
        bottomLeft.setBackgroundColor(Color.BLUE);
        bottomMid.setBackgroundColor(Color.BLUE);
        bottomRight.setBackgroundColor(Color.BLUE);
	}
}