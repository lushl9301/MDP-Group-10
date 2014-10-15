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
	Robot r = setupRobot();
	int[][] obstacleArray;
	public static String rotate;
	public static int M = 0;
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
		setEast(r.getPosition());

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
		r.setDirection(r.EAST);//default set to east
		
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
		int i =0;
		for (int row=0; row<15; row++){
        	for (int col=0; col<20; col++){
        		resetThis = (TextView)gv.getChildAt(i);
        		resetThis.setBackgroundColor(Color.parseColor("#686868")); //arena colour
        		
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
		r.setDirection(r.EAST);//default set to east
		setEast(r.getPosition());
		  
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
        clearMapColourRobot(topLeft, topRight, bottomLeft, bottomRight,midLeft, midRight,topMid, bottomMid,midMid);
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
        clearMapColourRobot(topLeft, topRight, bottomLeft, bottomRight,midLeft, midRight,topMid, bottomMid,midMid);
        colourRobot(topLeft, topRight, bottomLeft, bottomRight,topMid, bottomMid);
	}
	public void setSouth(int[][] currentPosition){ 
		Log.i("tag", ""+currentPosition[2][2]);
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
        
        clearMapColourRobot(topLeft, topRight, bottomLeft, bottomRight,midLeft, midRight,topMid, bottomMid,midMid);
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
        clearMapColourRobot(topLeft, topRight, bottomLeft, bottomRight,midLeft, midRight,topMid, bottomMid,midMid);
        colourRobot(topLeft, topRight, bottomLeft, bottomRight,topMid, bottomMid);
	}
	
	public void moveDownMap() {
		int currentPosition[][] =  r.getPosition();
        int direction = r.getDirection();
        
        topLeft.setBackgroundColor(Color.parseColor("#7fb2e5"));
        topMid.setBackgroundColor(Color.parseColor("#7fb2e5"));
        topRight.setBackgroundColor(Color.parseColor("#7fb2e5"));
        midLeft.setBackgroundColor(Color.parseColor("#7fb2e5"));
        midMid.setBackgroundColor(Color.parseColor("#7fb2e5"));
        midRight.setBackgroundColor(Color.parseColor("#7fb2e5"));
        bottomLeft.setBackgroundColor(Color.parseColor("#7fb2e5"));
        bottomMid.setBackgroundColor(Color.parseColor("#7fb2e5"));
        bottomRight.setBackgroundColor(Color.parseColor("#7fb2e5"));
  		
        		
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
	        	clearMapColourRobot(curTopLeft, curTopRight, curBottomLeft, curBottomRight,curMidLeft, curMidRight,curTopMid, curBottomMid,curMidMid);
	        	colourRobot(curTopLeft, curTopRight, curBottomLeft, curBottomRight,topMid,bottomMid);
	        	return;
	        }
	       // else if()
		 else {
			 //Log.i("SomeTag",""+currentPosition);
			 	currentPosition[0][0] += 20;
  		        currentPosition[0][1] += 20;
  		        currentPosition[0][2] += 20;
  		        currentPosition[1][0] += 20;
  		        currentPosition[1][1] += 20;
  		        currentPosition[1][2] += 20;
  		        currentPosition[2][0] += 20;
  		        currentPosition[2][1] += 20;
  		        currentPosition[2][2] += 20;
  		        rotate ="1";
  		        setSouth(currentPosition);
  		        
  		        }
	        		  
        }
        
        
        if (direction == r.EAST){
        	r.setDirection(r.SOUTH);
        	Log.i("tag","now face south");
        	rotate = "R";
        	setSouth(currentPosition);
        }
        
        if (direction == r.WEST){
        	r.setDirection(r.SOUTH);
        	Log.i("tag","now face south");
        	rotate = "L";
        	setSouth(currentPosition);
        } 
        if (direction == r.NORTH){
        	r.setDirection(r.EAST);
        	rotate = "R";
        	setEast(currentPosition);
        	
        }
        r.setPosition(currentPosition);
        //i++;
        	}
		//} 
	//}
	public void moveForwardMap() {
		 
        int currentPosition[][] =  r.getPosition();
        int direction = r.getDirection();

        topLeft.setBackgroundColor(Color.parseColor("#7fb2e5"));
        topMid.setBackgroundColor(Color.parseColor("#7fb2e5"));
        topRight.setBackgroundColor(Color.parseColor("#7fb2e5"));
        midLeft.setBackgroundColor(Color.parseColor("#7fb2e5"));
        midMid.setBackgroundColor(Color.parseColor("#7fb2e5"));
        midRight.setBackgroundColor(Color.parseColor("#7fb2e5"));
        bottomLeft.setBackgroundColor(Color.parseColor("#7fb2e5"));
        bottomMid.setBackgroundColor(Color.parseColor("#7fb2e5"));
        bottomRight.setBackgroundColor(Color.parseColor("#7fb2e5"));

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
		        	clearMapColourRobot(curTopLeft, curTopRight, curBottomLeft, curBottomRight,curMidLeft, curMidRight,curTopMid, curBottomMid,curMidMid);
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
			        rotate = "1";
			        setNorth(currentPosition);
		        }
	        }
	        if (direction == r.EAST){
	        	r.setDirection(r.NORTH);
	        	Log.i("tag","now face north");
	        	rotate = "L";
	        	setNorth(currentPosition);
	        }
	        
	        if (direction == r.WEST){
	        	r.setDirection(r.NORTH);
	        	rotate = "R";
	        	Log.i("tag","now face north");
	        	setNorth(currentPosition);
	        }
	        
	        if (direction == r.SOUTH){
	        	r.setDirection(r.EAST);
	        	rotate = "L";
	        	setEast(currentPosition);
	        }
	        r.setPosition(currentPosition);
        }
	public void turnRightMap() {
		int currentPosition[][] =  r.getPosition();
		int direction = r.getDirection();
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
        int temp = Integer.parseInt(curTopLeft.getText().toString());
        if ( (temp+1)%20==0 ){
        	Log.i("tag","invalid move");
        	clearMapColourRobot(curTopLeft, curTopRight, curBottomLeft, curBottomRight,curMidLeft, curMidRight,curTopMid, curBottomMid,curMidMid);
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
	        rotate = "1";
	        setEast(currentPosition);
        }
    }
		 if (direction == r.NORTH){
	        	r.setDirection(r.EAST);
	        	Log.i("tag","now face east");
	        	rotate = "R";
	        	setEast(currentPosition);
	        }
		 
		 if (direction == r.SOUTH){
	        	r.setDirection(r.EAST);
	        	Log.i("tag","now face east");
	        	rotate = "L";
	        	setEast(currentPosition);
	        }
		 
		 if (direction == r.WEST){
	        	r.setDirection(r.NORTH);
	        	rotate = "R";
	        	setWest(currentPosition);
	        	
	        }
       
		 r.setPosition(currentPosition);
	}
 

	public void turnLeftMap() {
	
	    int currentPosition[][] =  r.getPosition();
	    int direction = r.getDirection();
		
	    topLeft.setBackgroundColor(Color.parseColor("#7fb2e5"));
        topMid.setBackgroundColor(Color.parseColor("#7fb2e5"));
        topRight.setBackgroundColor(Color.parseColor("#7fb2e5"));
        midLeft.setBackgroundColor(Color.parseColor("#7fb2e5"));
        midMid.setBackgroundColor(Color.parseColor("#7fb2e5"));
        midRight.setBackgroundColor(Color.parseColor("#7fb2e5"));
        bottomLeft.setBackgroundColor(Color.parseColor("#7fb2e5"));
        bottomMid.setBackgroundColor(Color.parseColor("#7fb2e5"));
        bottomRight.setBackgroundColor(Color.parseColor("#7fb2e5"));
        
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
	        	clearMapColourRobot(curTopLeft, curTopRight, curBottomLeft, curBottomRight,curMidLeft, curMidRight,curTopMid, curBottomMid,curMidMid);
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
		        rotate = "1";
		        setWest(currentPosition);
	        }
        }
        if (direction == r.NORTH){
        	r.setDirection(r.WEST);
        	Log.i("tag","now face west");
        	rotate = "L";
        	setWest(currentPosition);
        } 
        if (direction == r.SOUTH){
        	r.setDirection(r.WEST);
        	rotate = "R";
        	Log.i("tag","now face west");
        	setWest(currentPosition);
        } 
        if (direction == r.EAST){
        	r.setDirection(r.SOUTH);
        	rotate = "R";
        	setSouth(currentPosition);
        }
        r.setPosition(currentPosition);
	}
	
	protected void plotObsAuto(int[][] dummy)
	{
		
		
		TextView testThis;
		int currentDir = r.getDirection();
		int[][] currentPosition = r.getPosition();
		int i =0;
		for (int row=0; row<15; row++){
        	for (int col=0; col<20; col++){
        		testThis = (TextView)gv.getChildAt(i);
        		if(dummy[row][col]==2){
        			testThis.setBackgroundColor(Color.parseColor("#000000")); //arena colour
        			
        		}
        		else if(dummy[row][col]==1){
        			testThis.setBackgroundColor(Color.parseColor("#7fb2e5"));
        		}
        		else{
        			testThis.setBackgroundColor(Color.parseColor("#686868"));
        		}
        		
        		i++;
        	}
		} 
		r.setDirection(currentDir);
		r.setPosition(currentPosition);
		setEast(r.getPosition());
	
	}
	
	public void colourRobot(TextView topLeft, TextView topRight, TextView bottomLeft, TextView bottomRight,TextView topMid, TextView bottomMid){
		topLeft.setBackgroundColor(Color.GREEN);
        topRight.setBackgroundColor(Color.GREEN);
        bottomMid.setBackgroundColor(Color.BLUE);
	}

	public void clearMapColourRobot(TextView topLeft, TextView topRight, TextView bottomLeft, TextView bottomRight,TextView midLeft, TextView midRight, TextView topMid, TextView bottomMid,TextView midMid){
		topLeft.setBackgroundColor(Color.TRANSPARENT);
		topMid.setBackgroundColor(Color.MAGENTA);
        topRight.setBackgroundColor(Color.TRANSPARENT);
        bottomLeft.setBackgroundColor(Color.MAGENTA);
        midRight.setBackgroundColor(Color.MAGENTA);
        midLeft.setBackgroundColor(Color.MAGENTA);
        bottomMid.setBackgroundColor(Color.TRANSPARENT);
        bottomRight.setBackgroundColor(Color.MAGENTA);
        midMid.setBackgroundColor(Color.MAGENTA);
	}
}