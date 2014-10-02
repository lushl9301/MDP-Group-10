package com.mdp.aero;

import android.content.Context;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

public class MapCanvas extends View {
	Context context;
	GridLayout gv = null;
	Robot r = setupRobot();
	int[][] obstacleArray;
	public static String rotate;
	public static int M = 0;
	TextView topLeft, topRight, bottomLeft, bottomRight,midLeft, midRight, topMid, bottomMid,midMid;
	public MapCanvas(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
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
}
