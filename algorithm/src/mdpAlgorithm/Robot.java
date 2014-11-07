package mdpAlgorithm;

import java.awt.Color;
import java.util.HashMap;

import javax.swing.BorderFactory;

import org.json.simple.JSONObject;

public class Robot {
	private static final Color ROBOT = new Color(153, 204, 255);
	private static final Color FRONTROBOT = new Color(146, 208, 80);
	private static final Color BORDER = new Color(153, 204, 255);
	private static final Color GRIDBORDER = new Color(225, 225, 225);
	private static final Color EXPLORED = new Color(0, 128, 255); //new Color(0, 128, 255);
	private static final Color CONFIRMOBSTACLE = Color.RED;
	private static final Color OBSTACLE = Color.DARK_GRAY;
	private static final Color SENSOR = new Color(0, 128, 255); //new Color(0, 128, 255);
	private static final Color WALL =  new Color(160, 80, 70);
	private static final int SHORTSENSOR = 3;
	private static final int ULTRASONIC = 3;
	private static final int LONGSENSOR = 5;
	private int robotX, robotY, rCount;
	private String robotOrientation;
	
	// Edit below for sensor reading grids
	
	private static final int short_LF_Grid1 = 14; // Grid 1 is an obs if the value is below 13
	private static final int short_LF_Grid2 = 26; // Grid 2 is an obs if the value is btw 13 and 22
	private static final int short_LF_Grid3 = 36; // Grid 3 is an obs if the value is btw 22 and 32
	
	private static final int short_RF_Grid1 = 14;
	private static final int short_RF_Grid2 = 26;
	private static final int short_RF_Grid3 = 36;
	
	private static final int short_FL_Grid1 = 13; // short sensor - left (NEW SENSOR ADDED ON 10.10.2014)
	private static final int short_FL_Grid2 = 26; // short sensor - left (NEW SENSOR ADDED ON 10.10.2014)
	private static final int short_FL_Grid3 = 41; // short sensor - left (NEW SENSOR ADDED ON 10.10.2014)
	
	private static final int long_BL_Grid1 = 21;
	private static final int long_BL_Grid2 = 28;
	private static final int long_BL_Grid3 = 40;
	private static final int long_BL_Grid4 = 51;
	private static final int long_BL_Grid5 = 62;
	
	private static final int short_FR_Grid1 = 13;
	private static final int short_FR_Grid2 = 26;
	private static final int short_FR_Grid3 = 38;
	
	private static final int U_F_Grid1 = 14;
	private static final int U_F_Grid2 = 23;
	
	private static final int U_L_Grid1 = 13;
	private static final int U_L_Grid2 = 23;
	
	private static final int U_R_Grid1 = 13;
	private static final int U_R_Grid2 = 23;
	
	
	public Robot (MapGrid map) {
		initRobot(map);
	}
	
	public Robot(Robot rob) {
		robotOrientation = rob.getOrientation();
		robotX = rob.getX();
		robotY = rob.getY();
		rCount = rob.getRCount();
	}
	
	public int getX() {
		return robotX;
	}
	
	public int getY() {
		return robotY;
	}
	
	public int getRCount() {
		return rCount;
	}
	
	public void setRCount(int count){
		rCount = count;
	}
	
	public String getOrientation() {
		return robotOrientation;
	}
	
	private void initRobot(MapGrid map) {
		JSONObject fakeHash = new JSONObject();
		setRobotXY(map, 7, 9, "ExploreStart", "N", fakeHash); // 7,9 top left of robot
		robotX = 7;
		robotY = 9;
		robotOrientation = "N";
		rCount = 0;
	}
	
	public void setRobotXY(MapGrid map, int x, int y, String text, String orientation, JSONObject reading) {
		robotX = x;
		robotY = y;
		robotOrientation = orientation;
		
		for (int i = x; i < x+3; i++) {
			for (int j = y; j < y+3; j++) {
				if(map.grid[i][j].getBackground() != OBSTACLE) {
					map.grid[i][j].setBackground(ROBOT);
					map.grid[i][j].setBorder(BorderFactory.createLineBorder(BORDER, 1));
				}
			}
		}
		
		switch(orientation) {
			case "N": 
				if(map.grid[x][y+1].getBackground() != OBSTACLE)
					map.grid[x][y+1].setBackground(FRONTROBOT);
				break;
			case "S":
				if(map.grid[x+2][y+1].getBackground() != OBSTACLE)
					map.grid[x+2][y+1].setBackground(FRONTROBOT);
				break;
			case "E":
				if(map.grid[x+1][y+2].getBackground() != OBSTACLE)
					map.grid[x+1][y+2].setBackground(FRONTROBOT);
				break;
			case "W":
				if(map.grid[x+1][y].getBackground() != OBSTACLE)
					map.grid[x+1][y].setBackground(FRONTROBOT);
				break;		
		}
		if(map.getName().equals("map"))
			setSensors(map);
		else if(map.getName().equals("map2") && reading.get("X") != null) {
			setRTSensors(map, reading);
		}
	}
	
	public void setRTSensors(MapGrid map, JSONObject reading){
		
		// X,Y coordinates
		int newX = Integer.valueOf(String.valueOf(reading.get("Y")))-1;
		int newY = Integer.valueOf(String.valueOf(reading.get("X")))-1;
		
		// front sensors
		int U_F = Integer.valueOf(String.valueOf(reading.get("U_F")));
		if (U_F == 0) U_F = 500;
		int short_LF = Integer.valueOf(String.valueOf(reading.get("short_LF")));
		int short_RF = Integer.valueOf(String.valueOf(reading.get("short_RF")));
		
		//left sensors
		int short_FL = Integer.valueOf(String.valueOf(reading.get("short_FL"))); // NEW SENSOR ADDED ON 10.10.2014
		int U_L = Integer.valueOf(String.valueOf(reading.get("U_L")));
		if (U_L == 0) U_L = 500;
		int long_BL = Integer.valueOf(String.valueOf(reading.get("long_BL")));
		
		// right sensors
		int short_FR = Integer.valueOf(String.valueOf(reading.get("short_FR")));
		int U_R = Integer.valueOf(String.valueOf(reading.get("U_R")));
		if (U_R == 0) U_R = 500;
		
		// robot orientation
		String newOrientation = String.valueOf(reading.get("direction"));
		
		switch(newOrientation) {
			case "1":

				// short sensor 1 - front
				try {
					if (short_LF <= 0) break;
					else {
						if (short_LF <= short_LF_Grid1) { // 1st grid
							if(!map.grid[newX-1][newY].getBackground().equals(WALL)) {
								confirmObstacle(map, newX-1, newY);
								confirmObstacle(map, newX-1, newY);
							}
						}
						else if (short_LF > short_LF_Grid1 && short_LF <= short_LF_Grid2) { // 2nd grid
							confirmObstacle(map, newX-2, newY);
							confirmObstacle(map, newX-2, newY);
							map.grid[newX-1][newY].setBackground(SENSOR);
							map.grid[newX-1][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, newX-1, newY);
						}
						else if (short_LF > short_LF_Grid2 && short_LF <= short_LF_Grid3) { // 3rd grid
							confirmObstacle(map, newX-3, newY);
							confirmObstacle(map, newX-3, newY);
							map.grid[newX-1][newY].setBackground(SENSOR);
							map.grid[newX-1][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.grid[newX-2][newY].setBackground(SENSOR);
							map.grid[newX-2][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, newX-1, newY);
							map.setMapDesc(false, newX-2, newY);
						}
						else {
							if(map.grid[newX-1][newY].getBackground().equals(WALL)) {
								
							}
							else if (map.grid[newX-2][newY].getBackground().equals(WALL)) {
								map.grid[newX-1][newY].setBackground(SENSOR);
								map.grid[newX-1][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX-1, newY);
							}
							else if (map.grid[newX-3][newY].getBackground().equals(WALL)) {
								map.grid[newX-1][newY].setBackground(SENSOR);
								map.grid[newX-1][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX-2][newY].setBackground(SENSOR);
								map.grid[newX-2][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX-1, newY);
								map.setMapDesc(false, newX-2, newY);
							}
							else {
								map.grid[newX-1][newY].setBackground(SENSOR);
								map.grid[newX-1][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX-2][newY].setBackground(SENSOR);
								map.grid[newX-2][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX-3][newY].setBackground(SENSOR);
								map.grid[newX-3][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX-1, newY);
								map.setMapDesc(false, newX-2, newY);
								map.setMapDesc(false, newX-3, newY);
							}
						}
					}
				}
				catch(ArrayIndexOutOfBoundsException e) {
					System.out.println("tried to color wrong grid");
				}

				// ultrasonic sensor 1 - front
				/* 
				 * READINGS FROM THIS SENSOR:
				 * 0 - 5cm   : Still within the robot 3x3 grids
				 * 5 - 15cm  : Obstacle in the first grid
				 * 16 - 49cm : Ignore
				 * 50 - 80cm : No obstacle in the front 3 grids. 4th grid onwards undetermined.
				 */
				try {
					if (U_F <= 0) break;
					else {
						if (U_F <= U_F_Grid1) { // 1st grid
							confirmObstacle(map, newX-1, newY+1);
							confirmObstacle(map, newX-1, newY+1);
							confirmObstacle(map, newX-1, newY+1);
							confirmObstacle(map, newX-1, newY+1);
						}
//						else if (U_F > U_F_Grid1 && U_F <= U_F_Grid2) { // 2nd grid
//							confirmObstacle(map, newX-2, newY+1);
//							map.grid[newX-1][newY+1].setBackground(SENSOR);
//							map.grid[newX-1][newY+1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
//							map.setMapDesc(false, newX-1, newY+1);
//						}
						else { //if (U_F > 40 && U_F <= 70) { // 3 grids no obstacle
							if(map.grid[newX-1][newY+1].getBackground().equals(WALL)) {
								
							}
							else if (map.grid[newX-2][newY+1].getBackground().equals(WALL)) {
								map.grid[newX-1][newY+1].setBackground(SENSOR);
								map.grid[newX-1][newY+1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX-1, newY+1);
							}
							else if (map.grid[newX-3][newY+1].getBackground().equals(WALL)) {
								map.grid[newX-1][newY+1].setBackground(SENSOR);
								map.grid[newX-1][newY+1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX-2][newY+1].setBackground(SENSOR);
								map.grid[newX-2][newY+1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX-1, newY+1);
								map.setMapDesc(false, newX-2, newY+1);
							}
							else {
								map.grid[newX-1][newY+1].setBackground(SENSOR);
								map.grid[newX-1][newY+1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX-2][newY+1].setBackground(SENSOR);
								map.grid[newX-2][newY+1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX-3][newY+1].setBackground(SENSOR);
								map.grid[newX-3][newY+1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX-1, newY+1);
								map.setMapDesc(false, newX-1, newY+1);
								map.setMapDesc(false, newX-2, newY+1);
								map.setMapDesc(false, newX-3, newY+1);
							}
						}
						
					}
				}
				catch(ArrayIndexOutOfBoundsException e) {
					System.out.println("tried to color wrong grid");
				}
				
				// short sensor 2 - front
				try {
					if (short_RF <= 0) break;
					else {
						if (short_RF <= short_RF_Grid1) { // 1st grid
							confirmObstacle(map, newX-1, newY+2);
							confirmObstacle(map, newX-1, newY+2);
						}
						else if (short_RF > short_RF_Grid1 && short_RF <= short_RF_Grid2) { // 2nd grid
							confirmObstacle(map, newX-2, newY+2);
							confirmObstacle(map, newX-2, newY+2);
							map.grid[newX-1][newY+2].setBackground(SENSOR);
							map.grid[newX-1][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, newX-1, newY+2);
						}
						else if (short_RF > short_RF_Grid2 && short_RF <= short_RF_Grid3) { // 3rd grid
							confirmObstacle(map, newX-3, newY+2);
							confirmObstacle(map, newX-3, newY+2);
							map.grid[newX-1][newY+2].setBackground(SENSOR);
							map.grid[newX-1][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.grid[newX-2][newY+2].setBackground(SENSOR);
							map.grid[newX-2][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, newX-1, newY+2);
							map.setMapDesc(false, newX-2, newY+2);
						}
						else {
							if(map.grid[newX-1][newY+2].getBackground().equals(WALL)) {
								
							}
							else if (map.grid[newX-2][newY+2].getBackground().equals(WALL)) {
								map.grid[newX-1][newY+2].setBackground(SENSOR);
								map.grid[newX-1][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX-1, newY+2);
							}
							else if (map.grid[newX-3][newY+2].getBackground().equals(WALL)) {
								map.grid[newX-1][newY+2].setBackground(SENSOR);
								map.grid[newX-1][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX-2][newY+2].setBackground(SENSOR);
								map.grid[newX-2][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX-1, newY+2);
								map.setMapDesc(false, newX-2, newY+2);
							}
							else {
								map.grid[newX-1][newY+2].setBackground(SENSOR);
								map.grid[newX-1][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX-2][newY+2].setBackground(SENSOR);
								map.grid[newX-2][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX-3][newY+2].setBackground(SENSOR);
								map.grid[newX-3][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX-1, newY+2);
								map.setMapDesc(false, newX-2, newY+2);
								map.setMapDesc(false, newX-3, newY+2);
							}
						}
					}
				}
				catch(ArrayIndexOutOfBoundsException e) {
					System.out.println("tried to color wrong grid");
				}
				
				// short sensor - left (NEW SENSOR ADDED ON 10.10.2014)
				try {
					if (short_FL <= 0) break;
					else {
						if (short_FL <= short_FL_Grid1) { // 1st grid
							confirmObstacle(map, newX, newY-1);
							confirmObstacle(map, newX, newY-1);
						}
						else if (short_FL > short_FL_Grid1 && short_FL <= short_FL_Grid2) { // 2nd grid
							confirmObstacle(map, newX, newY-2);
							confirmObstacle(map, newX, newY-2);
							map.grid[newX][newY-1].setBackground(SENSOR);
							map.grid[newX][newY-1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, newX, newY-1);
						}
						else if (short_FL > short_FL_Grid2 && short_FL <= short_FL_Grid3) { // 3rd grid
							confirmObstacle(map, newX, newY-3);
							confirmObstacle(map, newX, newY-3);
							map.grid[newX][newY-1].setBackground(SENSOR);
							map.grid[newX][newY-1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.grid[newX][newY-2].setBackground(SENSOR);
							map.grid[newX][newY-2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, newX, newY-1);
							map.setMapDesc(false, newX, newY-2);
						}
						else {
							if(map.grid[newX][newY-1].getBackground().equals(WALL)) {
								
							}
							else if (map.grid[newX][newY-2].getBackground().equals(WALL)){
								map.grid[newX][newY-1].setBackground(SENSOR);
								map.grid[newX][newY-1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX, newY-1);
							}
							else if (map.grid[newX][newY-3].getBackground().equals(WALL)){
								map.grid[newX][newY-1].setBackground(SENSOR);
								map.grid[newX][newY-1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX][newY-2].setBackground(SENSOR);
								map.grid[newX][newY-2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX, newY-1);
								map.setMapDesc(false, newX, newY-2);
							}
							map.grid[newX][newY-1].setBackground(SENSOR);
							map.grid[newX][newY-1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.grid[newX][newY-2].setBackground(SENSOR);
							map.grid[newX][newY-2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.grid[newX][newY-3].setBackground(SENSOR);
							map.grid[newX][newY-3].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, newX, newY-1);
							map.setMapDesc(false, newX, newY-2);
							map.setMapDesc(false, newX, newY-3);
						}
					}
				}
				catch(ArrayIndexOutOfBoundsException e) {
					System.out.println("tried to color wrong grid");
				}
				
				
				
				// long sensor - left
				try {
					if (long_BL <= 0) break;
					else {
						if (long_BL <= long_BL_Grid1) { // 1st grid
							confirmObstacle(map, newX+2, newY-1);
							confirmObstacle(map, newX+2, newY-1);
						}
						else if (long_BL > long_BL_Grid1 && long_BL <= long_BL_Grid2) { // 2nd grid
							confirmObstacle(map, newX+2, newY-2);
							confirmObstacle(map, newX+2, newY-2);
							map.grid[newX+2][newY-1].setBackground(SENSOR);
							map.grid[newX+2][newY-1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, newX+2, newY-1);
						}
						else if (long_BL > long_BL_Grid2 && long_BL <= long_BL_Grid3) { // 3rd grid
							confirmObstacle(map, newX+2, newY-3);
							confirmObstacle(map, newX+2, newY-3);
							map.grid[newX+2][newY-1].setBackground(SENSOR);
							map.grid[newX+2][newY-1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.grid[newX+2][newY-2].setBackground(SENSOR);
							map.grid[newX+2][newY-2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, newX+2, newY-1);
							map.setMapDesc(false, newX+2, newY-2);
						}
						else if (long_BL > long_BL_Grid3 && long_BL <= long_BL_Grid4) { // 4th grid
							confirmObstacle(map, newX+2, newY-4);
							confirmObstacle(map, newX+2, newY-4);
							map.grid[newX+2][newY-1].setBackground(SENSOR);
							map.grid[newX+2][newY-1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.grid[newX+2][newY-2].setBackground(SENSOR);
							map.grid[newX+2][newY-2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.grid[newX+2][newY-3].setBackground(SENSOR);
							map.grid[newX+2][newY-3].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, newX+2, newY-1);
							map.setMapDesc(false, newX+2, newY-2);
							map.setMapDesc(false, newX+2, newY-3);
						}
						else if (long_BL > long_BL_Grid4 && long_BL <= long_BL_Grid5) { // 5th grid
							confirmObstacle(map, newX+2, newY-5);
							confirmObstacle(map, newX+2, newY-5);
							map.grid[newX+2][newY-1].setBackground(SENSOR);
							map.grid[newX+2][newY-1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.grid[newX+2][newY-2].setBackground(SENSOR);
							map.grid[newX+2][newY-2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.grid[newX+2][newY-3].setBackground(SENSOR);
							map.grid[newX+2][newY-3].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.grid[newX+2][newY-4].setBackground(SENSOR);
							map.grid[newX+2][newY-4].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, newX+2, newY-1);
							map.setMapDesc(false, newX+2, newY-2);
							map.setMapDesc(false, newX+2, newY-3);
							map.setMapDesc(false, newX+2, newY-4);
						}
						else {
							if(map.grid[newX+2][newY-1].getBackground().equals(WALL)) {
								
							}
							else if (map.grid[newX+2][newY-2].getBackground().equals(WALL)){
								map.grid[newX+2][newY-1].setBackground(SENSOR);
								map.grid[newX+2][newY-1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+2, newY-1);
							}
							else if (map.grid[newX+2][newY-3].getBackground().equals(WALL)){
								map.grid[newX+2][newY-1].setBackground(SENSOR);
								map.grid[newX+2][newY-1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+2][newY-2].setBackground(SENSOR);
								map.grid[newX+2][newY-2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+2, newY-1);
								map.setMapDesc(false, newX+2, newY-2);
							}
							else if (map.grid[newX+2][newY-4].getBackground().equals(WALL)){
								map.grid[newX+2][newY-1].setBackground(SENSOR);
								map.grid[newX+2][newY-1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+2][newY-2].setBackground(SENSOR);
								map.grid[newX+2][newY-2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+2][newY-3].setBackground(SENSOR);
								map.grid[newX+2][newY-3].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+2, newY-1);
								map.setMapDesc(false, newX+2, newY-2);
								map.setMapDesc(false, newX+2, newY-3);
							}
							else if (map.grid[newX+2][newY-5].getBackground().equals(WALL)){
								map.grid[newX+2][newY-1].setBackground(SENSOR);
								map.grid[newX+2][newY-1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+2][newY-2].setBackground(SENSOR);
								map.grid[newX+2][newY-2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+2][newY-3].setBackground(SENSOR);
								map.grid[newX+2][newY-3].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+2][newY-4].setBackground(SENSOR);
								map.grid[newX+2][newY-4].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+2, newY-1);
								map.setMapDesc(false, newX+2, newY-2);
								map.setMapDesc(false, newX+2, newY-3);
								map.setMapDesc(false, newX+2, newY-4);
							}
							else {
								map.grid[newX+2][newY-1].setBackground(SENSOR);
								map.grid[newX+2][newY-1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+2][newY-2].setBackground(SENSOR);
								map.grid[newX+2][newY-2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+2][newY-3].setBackground(SENSOR);
								map.grid[newX+2][newY-3].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+2][newY-4].setBackground(SENSOR);
								map.grid[newX+2][newY-4].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+2][newY-5].setBackground(SENSOR);
								map.grid[newX+2][newY-5].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+2, newY-1);
								map.setMapDesc(false, newX+2, newY-2);
								map.setMapDesc(false, newX+2, newY-3);
								map.setMapDesc(false, newX+2, newY-4);
								map.setMapDesc(false, newX+2, newY-5);
							}
						}
					}
				}
				catch(ArrayIndexOutOfBoundsException e) {
					System.out.println("tried to color wrong grid");
				}
				
				// ultrasonic sensor 2 - left
				try {
					if (U_L <= 0) break;
					else {
						if (U_L <= U_L_Grid1) { // 1st grid
							confirmObstacle(map, newX+1, newY-1);
						}
//						else if (U_L > U_L_Grid1 && U_L <= U_L_Grid2) { // 2nd grid
//							confirmObstacle(map, newX+1, newY-2);
//							map.grid[newX+1][newY-1].setBackground(SENSOR);
//							map.grid[newX+1][newY-1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
//							map.setMapDesc(false, newX+1, newY-1);
//						}
						else {
							if(map.grid[newX+1][newY-1].getBackground().equals(WALL)) {
								
							}
							else if (map.grid[newX+1][newY-2].getBackground().equals(WALL)){
								map.grid[newX+1][newY-1].setBackground(SENSOR);
								map.grid[newX+1][newY-1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+1, newY-1);
							}
							else {
								map.grid[newX+1][newY-1].setBackground(SENSOR);
								map.grid[newX+1][newY-1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+1][newY-2].setBackground(SENSOR);
								map.grid[newX+1][newY-2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+1, newY-1);
								map.setMapDesc(false, newX+1, newY-2);
							}
						}
					}
				}
				catch(ArrayIndexOutOfBoundsException e) {
					System.out.println("tried to color wrong grid");
				}
				
				// short sensor 3 - right
				try {
					if (short_FR <= 0) break;
					else {
						if (short_FR <= short_FR_Grid1) { // 1st grid
							confirmObstacle(map, newX, newY+3);
						}
						else if (short_FR > short_FR_Grid1 && short_FR <= short_FR_Grid2) { // 2nd grid
							confirmObstacle(map, newX, newY+4);
							map.grid[newX][newY+3].setBackground(SENSOR);
							map.grid[newX][newY+3].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, newX, newY+3);
						}
						else if (short_FR > short_FR_Grid2 && short_FR <= short_FR_Grid3) { // 3rd grid
							confirmObstacle(map, newX, newY+5);
							map.grid[newX][newY+3].setBackground(SENSOR);
							map.grid[newX][newY+3].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.grid[newX][newY+4].setBackground(SENSOR);
							map.grid[newX][newY+4].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, newX, newY+3);
							map.setMapDesc(false, newX, newY+4);
						}
						else {
							if(map.grid[newX][newY+3].getBackground().equals(WALL)) {
								
							}
							else if(map.grid[newX][newY+4].getBackground().equals(WALL)) {
								map.grid[newX][newY+3].setBackground(SENSOR);
								map.grid[newX][newY+3].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX, newY+3);
							}
							else if(map.grid[newX][newY+5].getBackground().equals(WALL)) {
								map.grid[newX][newY+3].setBackground(SENSOR);
								map.grid[newX][newY+3].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX][newY+4].setBackground(SENSOR);
								map.grid[newX][newY+4].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX, newY+3);
								map.setMapDesc(false, newX, newY+4);
							}
							else {
								map.grid[newX][newY+3].setBackground(SENSOR);
								map.grid[newX][newY+3].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX][newY+4].setBackground(SENSOR);
								map.grid[newX][newY+4].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX][newY+5].setBackground(SENSOR);
								map.grid[newX][newY+5].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX, newY+3);
								map.setMapDesc(false, newX, newY+4);
								map.setMapDesc(false, newX, newY+5);
							}
						}
					}
				}
				catch(ArrayIndexOutOfBoundsException e) {
					System.out.println("tried to color wrong grid");
				}
				
				// ultrasonic sensor 3 - right
				try {
					if (U_R <= 0) break;
					else {
						if (U_R <= U_R_Grid1) { // 1st grid
							confirmObstacle(map, newX+1, newY+3);
						}
//						else if (U_R > U_R_Grid1 && U_R <= U_R_Grid2) { // 2nd grid
//							confirmObstacle(map, newX+1, newY+4);
//							map.grid[newX+1][newY+3].setBackground(SENSOR);
//							map.grid[newX+1][newY+3].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
//							map.setMapDesc(false, newX+1, newY+3);
//						}
						else {
							if(map.grid[newX+1][newY+3].getBackground().equals(WALL)) {
							
							}
							else if(map.grid[newX+1][newY+4].getBackground().equals(WALL)) {
								map.grid[newX+1][newY+3].setBackground(SENSOR);
								map.grid[newX+1][newY+3].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+1, newY+3);
							}
							else {
								map.grid[newX+1][newY+3].setBackground(SENSOR);
								map.grid[newX+1][newY+3].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+1][newY+4].setBackground(SENSOR);
								map.grid[newX+1][newY+4].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+1, newY+3);
								map.setMapDesc(false, newX+1, newY+4);
							}
						}
					}
				}
				catch(ArrayIndexOutOfBoundsException e) {
					System.out.println("tried to color wrong grid");
				}
				
				
				break;
				
			case "2":
				
				// short sensor 1 - front
				try {
					if (short_LF <= 0) break;
					else {
						if (short_LF <= short_LF_Grid1) { // 1st grid
							confirmObstacle(map, newX, newY+3);
							confirmObstacle(map, newX, newY+3);
						}
						else if (short_LF > short_LF_Grid1 && short_LF <= short_LF_Grid2) { // 2nd grid
							confirmObstacle(map, newX, newY+4);
							confirmObstacle(map, newX, newY+4);
							map.grid[newX][newY+3].setBackground(SENSOR);
							map.grid[newX][newY+3].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, newX, newY+4);
						}
						else if (short_LF > short_LF_Grid2 && short_LF <= short_LF_Grid3) { // 3rd grid
							confirmObstacle(map, newX, newY+5);
							confirmObstacle(map, newX, newY+5);
							map.grid[newX][newY+3].setBackground(SENSOR);
							map.grid[newX][newY+3].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.grid[newX][newY+4].setBackground(SENSOR);
							map.grid[newX][newY+4].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, newX, newY+3);
							map.setMapDesc(false, newX, newY+4);
						}
						else {
							if(map.grid[newX][newY+3].getBackground().equals(WALL)) {
								
							}
							else if(map.grid[newX][newY+4].getBackground().equals(WALL)) {
								map.grid[newX][newY+3].setBackground(SENSOR);
								map.grid[newX][newY+3].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX, newY+3);
							}
							else if(map.grid[newX][newY+5].getBackground().equals(WALL)) {
								map.grid[newX][newY+3].setBackground(SENSOR);
								map.grid[newX][newY+3].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX][newY+4].setBackground(SENSOR);
								map.grid[newX][newY+4].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX, newY+3);
								map.setMapDesc(false, newX, newY+4);
							}
							else {
								map.grid[newX][newY+3].setBackground(SENSOR);
								map.grid[newX][newY+3].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX][newY+4].setBackground(SENSOR);
								map.grid[newX][newY+4].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX][newY+5].setBackground(SENSOR);
								map.grid[newX][newY+5].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX, newY+3);
								map.setMapDesc(false, newX, newY+4);
								map.setMapDesc(false, newX, newY+5);
							}
						}
					}
				}
				catch(ArrayIndexOutOfBoundsException e) {
					System.out.println("tried to color wrong grid");
				}
				
				// ultrasonic sensor 1 - front
				/* 
				 * READINGS FROM THIS SENSOR:
				 * 0 - 5cm   : Still within the robot 3x3 grids
				 * 5 - 15cm  : Obstacle in the first grid
				 * 16 - 49cm : Ignore
				 * 50 - 80cm : No obstacle in the front 3 grids. 4th grid onwards undetermined.
				 */
				try {
					if (U_F <= 0) break;
					else {
						if (U_F <= U_F_Grid1) { // 1st grid
							confirmObstacle(map, newX+1, newY+3);
							confirmObstacle(map, newX+1, newY+3);
							confirmObstacle(map, newX+1, newY+3);
							confirmObstacle(map, newX+1, newY+3);
						}
//						else if (U_F > U_F_Grid1 && U_F <= U_F_Grid2) { // 2nd grid
//							confirmObstacle(map, newX+1, newY+4);
//							map.grid[newX+1][newY+3].setBackground(SENSOR);
//							map.grid[newX+1][newY+3].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
//							map.setMapDesc(false, newX+1, newY+3);
//						}
						else { // if (U_F > 40 && U_F <= 70) { // 3 grids no obstacle
							if(map.grid[newX+1][newY+3].getBackground().equals(WALL)) {
								
							}
							else if(map.grid[newX+1][newY+4].getBackground().equals(WALL)) {
								map.grid[newX+1][newY+3].setBackground(SENSOR);
								map.grid[newX+1][newY+3].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+1, newY+3);
							}
							else if(map.grid[newX+1][newY+5].getBackground().equals(WALL)) {
								map.grid[newX+1][newY+3].setBackground(SENSOR);
								map.grid[newX+1][newY+3].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+1][newY+4].setBackground(SENSOR);
								map.grid[newX+1][newY+4].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+1, newY+3);
								map.setMapDesc(false, newX+1, newY+4);
							}
							else {
								map.grid[newX+1][newY+3].setBackground(SENSOR);
								map.grid[newX+1][newY+3].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+1][newY+4].setBackground(SENSOR);
								map.grid[newX+1][newY+4].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+1][newY+5].setBackground(SENSOR);
								map.grid[newX+1][newY+5].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+1, newY+3);
								map.setMapDesc(false, newX+1, newY+3);
								map.setMapDesc(false, newX+1, newY+4);
								map.setMapDesc(false, newX+1, newY+5);
							}
						}
					}
				}
				catch(ArrayIndexOutOfBoundsException e) {
					System.out.println("tried to color wrong grid");
				}
				
				// short sensor 2 - front
				try {
					if (short_RF <= 0) break;
					else {
						if (short_RF <= short_RF_Grid1) { // 1st grid
							confirmObstacle(map, newX+2, newY+3);
							confirmObstacle(map, newX+2, newY+3);
						}
						else if (short_RF > short_RF_Grid1 && short_RF <= short_RF_Grid2) { // 2nd grid
							confirmObstacle(map, newX+2, newY+4);
							confirmObstacle(map, newX+2, newY+4);
							map.grid[newX+2][newY+3].setBackground(SENSOR);
							map.grid[newX+2][newY+3].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, newX+2, newY+3);
						}
						else if (short_RF > short_RF_Grid2 && short_RF <= short_RF_Grid3) { // 3rd grid
							confirmObstacle(map, newX+2, newY+5);
							confirmObstacle(map, newX+2, newY+5);
							map.grid[newX+2][newY+3].setBackground(SENSOR);
							map.grid[newX+2][newY+3].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.grid[newX+2][newY+4].setBackground(SENSOR);
							map.grid[newX+2][newY+4].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, newX+2, newY+3);
							map.setMapDesc(false, newX+2, newY+4);
						}
						else {
							if(map.grid[newX+2][newY+3].getBackground().equals(WALL)) {
								
							}
							else if(map.grid[newX+2][newY+4].getBackground().equals(WALL)) {
								map.grid[newX+2][newY+3].setBackground(SENSOR);
								map.grid[newX+2][newY+3].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+2, newY+3);
							}
							else if(map.grid[newX+2][newY+5].getBackground().equals(WALL)) {
								map.grid[newX+2][newY+3].setBackground(SENSOR);
								map.grid[newX+2][newY+3].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+2][newY+4].setBackground(SENSOR);
								map.grid[newX+2][newY+4].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+2, newY+3);
								map.setMapDesc(false, newX+2, newY+4);
							}
							else {
								map.grid[newX+2][newY+3].setBackground(SENSOR);
								map.grid[newX+2][newY+3].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+2][newY+4].setBackground(SENSOR);
								map.grid[newX+2][newY+4].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+2][newY+5].setBackground(SENSOR);
								map.grid[newX+2][newY+5].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+2, newY+3);
								map.setMapDesc(false, newX+2, newY+4);
								map.setMapDesc(false, newX+2, newY+5);
							}
						}
					}
				}
				catch(ArrayIndexOutOfBoundsException e) {
					System.out.println("tried to color wrong grid");
				}
				
				// short sensor - left (NEW SENSOR ADDED ON 10.10.2014)
				try {
					if (short_FL <= 0) break;
					else {
						if (short_FL <= short_FL_Grid1) { // 1st grid
							confirmObstacle(map, newX-1, newY+2);
							confirmObstacle(map, newX-1, newY+2);
						}
						else if (short_FL > short_FL_Grid1 && short_FL <= short_FL_Grid2) { // 2nd grid
							confirmObstacle(map, newX-2, newY+2);
							confirmObstacle(map, newX-2, newY+2);
							map.grid[newX-1][newY+2].setBackground(SENSOR);
							map.grid[newX-1][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, newX-1, newY+2);
						}
						else if (short_FL > short_FL_Grid2 && short_FL <= short_FL_Grid3) { // 3rd grid
							confirmObstacle(map, newX-3, newY+2);
							confirmObstacle(map, newX-3, newY+2);
							map.grid[newX-1][newY+2].setBackground(SENSOR);
							map.grid[newX-1][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.grid[newX-2][newY+2].setBackground(SENSOR);
							map.grid[newX-2][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, newX-1, newY+2);
							map.setMapDesc(false, newX-2, newY+2);
						}
						else {
							if(map.grid[newX-1][newY+2].getBackground().equals(WALL)) {
								
							}
							else if (map.grid[newX-2][newY+2].getBackground().equals(WALL)) {
								map.grid[newX-1][newY+2].setBackground(SENSOR);
								map.grid[newX-1][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX-1, newY+2);
							}
							else if (map.grid[newX-3][newY+2].getBackground().equals(WALL)) {
								map.grid[newX-1][newY+2].setBackground(SENSOR);
								map.grid[newX-1][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX-2][newY+2].setBackground(SENSOR);
								map.grid[newX-2][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX-1, newY+2);
								map.setMapDesc(false, newX-2, newY+2);
							}
							else {
								map.grid[newX-1][newY+2].setBackground(SENSOR);
								map.grid[newX-1][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX-2][newY+2].setBackground(SENSOR);
								map.grid[newX-2][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX-3][newY+2].setBackground(SENSOR);
								map.grid[newX-3][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX-1, newY+2);
								map.setMapDesc(false, newX-2, newY+2);
								map.setMapDesc(false, newX-3, newY+2);
							}
						}
					}
				}
				catch(ArrayIndexOutOfBoundsException e) {
					System.out.println("tried to color wrong grid");
				}
				
				// long sensor - left
				try {
					if (long_BL <= 0) break;
					else {
						if (long_BL <= long_BL_Grid1) { // 1st grid
							confirmObstacle(map, newX-1, newY);
							confirmObstacle(map, newX-1, newY);
						}
						else if (long_BL > long_BL_Grid1 && long_BL <= long_BL_Grid2) { // 2nd grid
							confirmObstacle(map, newX-2, newY);
							confirmObstacle(map, newX-2, newY);
							map.grid[newX-1][newY].setBackground(SENSOR);
							map.grid[newX-1][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, newX-1, newY);
						}
						else if (long_BL > long_BL_Grid2 && long_BL <= long_BL_Grid3) { // 3rd grid
							confirmObstacle(map, newX-3, newY);
							confirmObstacle(map, newX-3, newY);
							map.grid[newX-1][newY].setBackground(SENSOR);
							map.grid[newX-1][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.grid[newX-2][newY].setBackground(SENSOR);
							map.grid[newX-2][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, newX-1, newY);
							map.setMapDesc(false, newX-2, newY);
						}
						else if (long_BL > long_BL_Grid3 && long_BL <= long_BL_Grid4) { // 4th grid
							confirmObstacle(map, newX-4, newY);
							confirmObstacle(map, newX-4, newY);
							map.grid[newX-1][newY].setBackground(SENSOR);
							map.grid[newX-1][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.grid[newX-2][newY].setBackground(SENSOR);
							map.grid[newX-2][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.grid[newX-3][newY].setBackground(SENSOR);
							map.grid[newX-3][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, newX-1, newY);
							map.setMapDesc(false, newX-2, newY);
							map.setMapDesc(false, newX-3, newY);
						}
						else if (long_BL > long_BL_Grid4 && long_BL <= long_BL_Grid5) { // 5th grid
							confirmObstacle(map, newX-5, newY);
							confirmObstacle(map, newX-5, newY);
							map.grid[newX-1][newY].setBackground(SENSOR);
							map.grid[newX-1][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.grid[newX-2][newY].setBackground(SENSOR);
							map.grid[newX-2][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.grid[newX-3][newY].setBackground(SENSOR);
							map.grid[newX-3][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.grid[newX-4][newY].setBackground(SENSOR);
							map.grid[newX-4][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, newX-1, newY);
							map.setMapDesc(false, newX-2, newY);
							map.setMapDesc(false, newX-3, newY);
							map.setMapDesc(false, newX-4, newY);
						}
						else {
							if(map.grid[newX-1][newY].getBackground().equals(WALL)) {
								
							}
							else if(map.grid[newX-2][newY].getBackground().equals(WALL)) {
								map.grid[newX-1][newY].setBackground(SENSOR);
								map.grid[newX-1][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX-1, newY);
							}
							else if(map.grid[newX-3][newY].getBackground().equals(WALL)) {
								map.grid[newX-1][newY].setBackground(SENSOR);
								map.grid[newX-1][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX-2][newY].setBackground(SENSOR);
								map.grid[newX-2][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX-1, newY);
								map.setMapDesc(false, newX-2, newY);
							}
							else if(map.grid[newX-4][newY].getBackground().equals(WALL)) {
								map.grid[newX-1][newY].setBackground(SENSOR);
								map.grid[newX-1][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX-2][newY].setBackground(SENSOR);
								map.grid[newX-2][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX-3][newY].setBackground(SENSOR);
								map.grid[newX-3][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX-1, newY);
								map.setMapDesc(false, newX-2, newY);
								map.setMapDesc(false, newX-3, newY);
							}
							else if(map.grid[newX-5][newY].getBackground().equals(WALL)) {
								map.grid[newX-1][newY].setBackground(SENSOR);
								map.grid[newX-1][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX-2][newY].setBackground(SENSOR);
								map.grid[newX-2][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX-3][newY].setBackground(SENSOR);
								map.grid[newX-3][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX-4][newY].setBackground(SENSOR);
								map.grid[newX-4][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX-1, newY);
								map.setMapDesc(false, newX-2, newY);
								map.setMapDesc(false, newX-3, newY);
								map.setMapDesc(false, newX-4, newY);
							}
							else {
								map.grid[newX-1][newY].setBackground(SENSOR);
								map.grid[newX-1][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX-2][newY].setBackground(SENSOR);
								map.grid[newX-2][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX-3][newY].setBackground(SENSOR);
								map.grid[newX-3][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX-4][newY].setBackground(SENSOR);
								map.grid[newX-4][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX-5][newY].setBackground(SENSOR);
								map.grid[newX-5][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX-1, newY);
								map.setMapDesc(false, newX-2, newY);
								map.setMapDesc(false, newX-3, newY);
								map.setMapDesc(false, newX-4, newY);
								map.setMapDesc(false, newX-5, newY);
							}
						}
	
					}
				}
				catch(ArrayIndexOutOfBoundsException e) {
					System.out.println("tried to color wrong grid");
				}
				
				// ultrasonic sensor 2 - left
				try {
					if (U_L <= 0) break;
					else {
						if (U_L <= U_L_Grid1) { // 1st grid
							confirmObstacle(map, newX-1, newY+1);
						}
//						else if (U_L > U_L_Grid1 && U_L <= U_L_Grid2) { // 2nd grid
//							confirmObstacle(map, newX-2, newY+1);
//							map.grid[newX-1][newY+1].setBackground(SENSOR);
//							map.grid[newX-1][newY+1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
//							map.setMapDesc(false, newX-1, newY+1);
//						}
						else {
							if(map.grid[newX-1][newY+1].getBackground().equals(WALL)) {
								
							}
							else if(map.grid[newX-2][newY+1].getBackground().equals(WALL)) {
								map.grid[newX-1][newY+1].setBackground(SENSOR);
								map.grid[newX-1][newY+1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX-1, newY+1);
							}
							else {
								map.grid[newX-1][newY+1].setBackground(SENSOR);
								map.grid[newX-1][newY+1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX-2][newY+1].setBackground(SENSOR);
								map.grid[newX-2][newY+1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX-1, newY+1);
								map.setMapDesc(false, newX-2, newY+1);
							}
						}
					}
				}
				catch(ArrayIndexOutOfBoundsException e) {
					System.out.println("tried to color wrong grid");
				}
				
				// short sensor 3 - right
				try {
					if (short_FR <= 0) break;
					else {
						if (short_FR <= short_FR_Grid1) { // 1st grid
							confirmObstacle(map, newX+3, newY+2);
						}
						else if (short_FR > short_FR_Grid1 && short_FR <= short_FR_Grid2) { // 2nd grid
							confirmObstacle(map, newX+4, newY+2);
							map.grid[newX+3][newY+2].setBackground(SENSOR);
							map.grid[newX+3][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, newX+3, newY+2);
						}
						else if (short_FR > short_FR_Grid2 && short_FR <= short_FR_Grid3) { // 3rd grid
							confirmObstacle(map, newX+5, newY);
							map.grid[newX+3][newY+2].setBackground(SENSOR);
							map.grid[newX+3][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.grid[newX+4][newY+2].setBackground(SENSOR);
							map.grid[newX+4][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, newX+3, newY+2);
							map.setMapDesc(false, newX+4, newY+2);
						}
						else {
							if(map.grid[newX+3][newY+2].getBackground().equals(WALL)) {
								
							}
							else if(map.grid[newX+4][newY+2].getBackground().equals(WALL)) {
								map.grid[newX+3][newY+2].setBackground(SENSOR);
								map.grid[newX+3][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+3, newY+2);
							}
							else if(map.grid[newX+5][newY+2].getBackground().equals(WALL)) {
								map.grid[newX+3][newY+2].setBackground(SENSOR);
								map.grid[newX+3][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+4][newY+2].setBackground(SENSOR);
								map.grid[newX+4][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+3, newY+2);
								map.setMapDesc(false, newX+4, newY+2);
							}
							else {
								map.grid[newX+3][newY+2].setBackground(SENSOR);
								map.grid[newX+3][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+4][newY+2].setBackground(SENSOR);
								map.grid[newX+4][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+5][newY+2].setBackground(SENSOR);
								map.grid[newX+5][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+3, newY+2);
								map.setMapDesc(false, newX+4, newY+2);
								map.setMapDesc(false, newX+5, newY+2);
							}
						}
					}
				}
				catch(ArrayIndexOutOfBoundsException e) {
					System.out.println("tried to color wrong grid");
				}
				
				// ultrasonic sensor 3 - right
				try {
					if (U_R <= 0) break;
					else {
						if (U_R <= U_R_Grid1) { // 1st grid
							confirmObstacle(map, newX+3, newY+1);
						}
//						else if (U_R > U_R_Grid1 && U_R <= U_R_Grid2) { // 2nd grid
//							confirmObstacle(map, newX+4, newY+1);
//							map.grid[newX+3][newY+1].setBackground(SENSOR);
//							map.grid[newX+3][newY+1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
//							map.setMapDesc(false, newX+3, newY+1);
//						}
						else {
							if(map.grid[newX+3][newY+2].getBackground().equals(WALL)) {
								
							}
							else if(map.grid[newX+4][newY+2].getBackground().equals(WALL)) {
								map.grid[newX+3][newY+2].setBackground(SENSOR);
								map.grid[newX+3][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+3, newY+2);
							}
							else {
								map.grid[newX+3][newY+1].setBackground(SENSOR);
								map.grid[newX+3][newY+1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+4][newY+1].setBackground(SENSOR);
								map.grid[newX+4][newY+1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+3, newY+1);
								map.setMapDesc(false, newX+4, newY+1);
							}
						}
					}
				}
				catch(ArrayIndexOutOfBoundsException e) {
					System.out.println("tried to color wrong grid");
				}
				
				break;
				
			case "3":
				// short sensor 1 - front
				try {
					if (short_LF <= 0) break;
					else {
						if (short_LF <= short_LF_Grid1) { // 1st grid
							confirmObstacle(map, newX+3, newY+2);
							confirmObstacle(map, newX+3, newY+2);
						}
						else if (short_LF > short_LF_Grid1 && short_LF <= short_LF_Grid2) { // 2nd grid
							confirmObstacle(map, newX+4, newY+2);
							confirmObstacle(map, newX+4, newY+2);
							map.grid[newX+3][newY+2].setBackground(SENSOR);
							map.grid[newX+3][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, newX+3, newY+2);
						}
						else if (short_LF > short_LF_Grid2 && short_LF <= short_LF_Grid3) { // 3rd grid
							confirmObstacle(map, newX+5, newY+2);
							confirmObstacle(map, newX+5, newY+2);
							map.grid[newX+3][newY+2].setBackground(SENSOR);
							map.grid[newX+3][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.grid[newX+4][newY+2].setBackground(SENSOR);
							map.grid[newX+4][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, newX+3, newY+2);
							map.setMapDesc(false, newX+4, newY+2);
						}
						else {
							if(map.grid[newX+3][newY+2].getBackground().equals(WALL)) {
								
							}
							else if (map.grid[newX+4][newY+2].getBackground().equals(WALL)) {
								map.grid[newX+3][newY+2].setBackground(SENSOR);
								map.grid[newX+3][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+3, newY+2);
							}
							else if (map.grid[newX+5][newY+2].getBackground().equals(WALL)) {
								map.grid[newX+3][newY+2].setBackground(SENSOR);
								map.grid[newX+3][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+4][newY+2].setBackground(SENSOR);
								map.grid[newX+4][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+3, newY+2);
								map.setMapDesc(false, newX+4, newY+2);
							}
							else {
								map.grid[newX+3][newY+2].setBackground(SENSOR);
								map.grid[newX+3][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+4][newY+2].setBackground(SENSOR);
								map.grid[newX+4][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+5][newY+2].setBackground(SENSOR);
								map.grid[newX+5][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+3, newY+2);
								map.setMapDesc(false, newX+4, newY+2);
								map.setMapDesc(false, newX+5, newY+2);
							}
						}
					}
				}
				catch(ArrayIndexOutOfBoundsException e) {
					System.out.println("tried to color wrong grid");
				}
				
				// ultrasonic sensor 1 - front
				/* 
				 * READINGS FROM THIS SENSOR:
				 * 0 - 5cm   : Still within the robot 3x3 grids
				 * 5 - 15cm  : Obstacle in the first grid
				 * 16 - 49cm : Ignore
				 * 50 - 80cm : No obstacle in the front 3 grids. 4th grid onwards undetermined.
				 */
				try {
					if (U_F <= 0) break;
					else {
						if (U_F <= U_F_Grid1) { // 1st grid
							confirmObstacle(map, newX+3, newY+1);
							confirmObstacle(map, newX+3, newY+1);
							confirmObstacle(map, newX+3, newY+1);
							confirmObstacle(map, newX+3, newY+1);
						}
//						else if (U_F > U_F_Grid1 && U_F <= U_F_Grid2) { // 2nd grid
//							confirmObstacle(map, newX+4, newY+1);
//							map.grid[newX+3][newY+1].setBackground(SENSOR);
//							map.grid[newX+3][newY+1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
//							map.setMapDesc(false, newX+3, newY+1);
//						}
						else { //if (U_F > 40 && U_F <= 70) { // 3 grids no obstacle
							if(map.grid[newX+3][newY+1].getBackground().equals(WALL)) {
								
							}
							else if (map.grid[newX+4][newY+1].getBackground().equals(WALL)) {
								map.grid[newX+3][newY+1].setBackground(SENSOR);
								map.grid[newX+3][newY+1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+3, newY+1);
							}
							else if (map.grid[newX+5][newY+1].getBackground().equals(WALL)) {
								map.grid[newX+3][newY+1].setBackground(SENSOR);
								map.grid[newX+3][newY+1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+4][newY+1].setBackground(SENSOR);
								map.grid[newX+4][newY+1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+3, newY+1);
								map.setMapDesc(false, newX+4, newY+1);
							}
							else {
								map.grid[newX+3][newY+1].setBackground(SENSOR);
								map.grid[newX+3][newY+1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+4][newY+1].setBackground(SENSOR);
								map.grid[newX+4][newY+1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+5][newY+1].setBackground(SENSOR);
								map.grid[newX+5][newY+1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+3, newY+1);
								map.setMapDesc(false, newX+3, newY+1);
								map.setMapDesc(false, newX+4, newY+1);
								map.setMapDesc(false, newX+5, newY+1);
							}
						}
					}
				}
				catch(ArrayIndexOutOfBoundsException e) {
					System.out.println("tried to color wrong grid");
				}
				
				// short sensor 2 - front
				try {
					if (short_RF <= 0) break;
					else {
						if (short_RF <= short_RF_Grid1) { // 1st grid
							confirmObstacle(map, newX+3, newY);
							confirmObstacle(map, newX+3, newY);
						}
						else if (short_RF > short_RF_Grid1 && short_RF <= short_RF_Grid2) { // 2nd grid
							confirmObstacle(map, newX+4, newY);
							confirmObstacle(map, newX+4, newY);
							map.grid[newX+3][newY].setBackground(SENSOR);
							map.grid[newX+3][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, newX+3, newY);
						}
						else if (short_RF > short_RF_Grid2 && short_RF <= short_RF_Grid3) { // 3rd grid
							confirmObstacle(map, newX+5, newY);
							confirmObstacle(map, newX+5, newY);
							map.grid[newX+3][newY].setBackground(SENSOR);
							map.grid[newX+3][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.grid[newX+4][newY].setBackground(SENSOR);
							map.grid[newX+4][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, newX+3, newY);
							map.setMapDesc(false, newX+4, newY);
						}
						else {
							if(map.grid[newX+3][newY].getBackground().equals(WALL)) {
								
							}
							else if (map.grid[newX+4][newY].getBackground().equals(WALL)) {
								map.grid[newX+3][newY].setBackground(SENSOR);
								map.grid[newX+3][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+3, newY);
							}
							else if (map.grid[newX+5][newY].getBackground().equals(WALL)) {
								map.grid[newX+3][newY].setBackground(SENSOR);
								map.grid[newX+3][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+4][newY].setBackground(SENSOR);
								map.grid[newX+4][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+3, newY);
								map.setMapDesc(false, newX+4, newY);
							}
							else {
								map.grid[newX+3][newY].setBackground(SENSOR);
								map.grid[newX+3][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+4][newY].setBackground(SENSOR);
								map.grid[newX+4][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+5][newY].setBackground(SENSOR);
								map.grid[newX+5][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+3, newY);
								map.setMapDesc(false, newX+4, newY);
								map.setMapDesc(false, newX+5, newY);
							}
						}
					}
				}
				catch(ArrayIndexOutOfBoundsException e) {
					System.out.println("tried to color wrong grid");
				}
				
				// short sensor - left (NEW SENSOR ADDED ON 10.10.2014)
				try {
					if (short_FL <= 0) break;
					else {
						if (short_FL <= short_FL_Grid1) { // 1st grid
							confirmObstacle(map, newX+2, newY+3);
							confirmObstacle(map, newX+2, newY+3);
						}
						else if (short_FL > short_FL_Grid1 && short_FL <= short_FL_Grid2) { // 2nd grid
							confirmObstacle(map, newX+2, newY+4);
							confirmObstacle(map, newX+2, newY+4);
							map.grid[newX+2][newY+3].setBackground(SENSOR);
							map.grid[newX+2][newY+3].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, newX+2, newY+3);
						}
						else if (short_FL > short_FL_Grid2 && short_FL <= short_FL_Grid3) { // 3rd grid
							confirmObstacle(map, newX+2, newY+5);
							confirmObstacle(map, newX+2, newY+5);
							map.grid[newX+2][newY+3].setBackground(SENSOR);
							map.grid[newX+2][newY+3].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.grid[newX+2][newY+4].setBackground(SENSOR);
							map.grid[newX+2][newY+4].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, newX+2, newY+3);
							map.setMapDesc(false, newX+2, newY+4);
						}
						else {
							if(map.grid[newX+2][newY+3].getBackground().equals(WALL)) {
								
							}
							else if(map.grid[newX+2][newY+4].getBackground().equals(WALL)) {
								map.grid[newX+2][newY+3].setBackground(SENSOR);
								map.grid[newX+2][newY+3].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+2, newY+3);
							}
							else if(map.grid[newX+2][newY+5].getBackground().equals(WALL)) {
								map.grid[newX+2][newY+3].setBackground(SENSOR);
								map.grid[newX+2][newY+3].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+2][newY+4].setBackground(SENSOR);
								map.grid[newX+2][newY+4].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+2, newY+3);
								map.setMapDesc(false, newX+2, newY+4);
							}
							else {
								map.grid[newX+2][newY+3].setBackground(SENSOR);
								map.grid[newX+2][newY+3].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+2][newY+4].setBackground(SENSOR);
								map.grid[newX+2][newY+4].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+2][newY+5].setBackground(SENSOR);
								map.grid[newX+2][newY+5].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+2, newY+3);
								map.setMapDesc(false, newX+2, newY+4);
								map.setMapDesc(false, newX+2, newY+5);
							}
						}
					}
				}
				catch(ArrayIndexOutOfBoundsException e) {
					System.out.println("tried to color wrong grid");
				}
				
				// long sensor - left
				try {
					if (long_BL <= 0) break;
					else {
						if (long_BL <= long_BL_Grid1) { // 1st grid
							confirmObstacle(map, newX, newY+3);
							confirmObstacle(map, newX, newY+3);
						}
						else if (long_BL > long_BL_Grid1 && long_BL <= long_BL_Grid2) { // 2nd grid
							confirmObstacle(map, newX, newY+4);
							confirmObstacle(map, newX, newY+4);
							map.grid[newX][newY+3].setBackground(SENSOR);
							map.grid[newX][newY+3].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, newX, newY+3);
						}
						else if (long_BL > long_BL_Grid2 && long_BL <= long_BL_Grid3) { // 3rd grid
							confirmObstacle(map, newX, newY+5);
							confirmObstacle(map, newX, newY+5);
							map.grid[newX][newY+3].setBackground(SENSOR);
							map.grid[newX][newY+3].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.grid[newX][newY+4].setBackground(SENSOR);
							map.grid[newX][newY+4].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, newX, newY+3);
							map.setMapDesc(false, newX, newY+4);
						}
						else if (long_BL > long_BL_Grid3 && long_BL <= long_BL_Grid4) { // 4th grid
							confirmObstacle(map, newX+2, newY+6);
							confirmObstacle(map, newX+2, newY+6);
							map.grid[newX][newY+3].setBackground(SENSOR);
							map.grid[newX][newY+3].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.grid[newX][newY+4].setBackground(SENSOR);
							map.grid[newX][newY+4].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.grid[newX][newY+5].setBackground(SENSOR);
							map.grid[newX][newY+5].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, newX, newY+3);
							map.setMapDesc(false, newX, newY+4);
							map.setMapDesc(false, newX, newY+5);
						}
						else if (long_BL > long_BL_Grid4 && long_BL <= long_BL_Grid5) { // 5th grid
							confirmObstacle(map, newX, newY+7);
							confirmObstacle(map, newX, newY+7);
							map.grid[newX][newY+3].setBackground(SENSOR);
							map.grid[newX][newY+3].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.grid[newX][newY+4].setBackground(SENSOR);
							map.grid[newX][newY+4].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.grid[newX][newY+5].setBackground(SENSOR);
							map.grid[newX][newY+5].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.grid[newX][newY+6].setBackground(SENSOR);
							map.grid[newX][newY+6].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, newX, newY+3);
							map.setMapDesc(false, newX, newY+4);
							map.setMapDesc(false, newX, newY+5);
							map.setMapDesc(false, newX, newY+6);
						}
						else {
							if(map.grid[newX][newY+3].getBackground().equals(WALL)) {
								
							}
							else if(map.grid[newX][newY+4].getBackground().equals(WALL)) {
								map.grid[newX][newY+3].setBackground(SENSOR);
								map.grid[newX][newY+3].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX, newY+3);
							}
							else if(map.grid[newX][newY+5].getBackground().equals(WALL)) {
								map.grid[newX][newY+3].setBackground(SENSOR);
								map.grid[newX][newY+3].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX][newY+4].setBackground(SENSOR);
								map.grid[newX][newY+4].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX, newY+3);
								map.setMapDesc(false, newX, newY+4);
							}
							else if(map.grid[newX][newY+6].getBackground().equals(WALL)) {
								map.grid[newX][newY+3].setBackground(SENSOR);
								map.grid[newX][newY+3].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX][newY+4].setBackground(SENSOR);
								map.grid[newX][newY+4].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX][newY+5].setBackground(SENSOR);
								map.grid[newX][newY+5].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX, newY+3);
								map.setMapDesc(false, newX, newY+4);
								map.setMapDesc(false, newX, newY+5);
							}
							else if(map.grid[newX][newY+7].getBackground().equals(WALL)) {
								map.grid[newX][newY+3].setBackground(SENSOR);
								map.grid[newX][newY+3].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX][newY+4].setBackground(SENSOR);
								map.grid[newX][newY+4].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX][newY+5].setBackground(SENSOR);
								map.grid[newX][newY+5].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX][newY+6].setBackground(SENSOR);
								map.grid[newX][newY+6].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX, newY+3);
								map.setMapDesc(false, newX, newY+4);
								map.setMapDesc(false, newX, newY+5);
								map.setMapDesc(false, newX, newY+6);
							}
							else {
								map.grid[newX][newY+3].setBackground(SENSOR);
								map.grid[newX][newY+3].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX][newY+4].setBackground(SENSOR);
								map.grid[newX][newY+4].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX][newY+5].setBackground(SENSOR);
								map.grid[newX][newY+5].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX][newY+6].setBackground(SENSOR);
								map.grid[newX][newY+6].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX][newY+7].setBackground(SENSOR);
								map.grid[newX][newY+7].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX, newY+3);
								map.setMapDesc(false, newX, newY+4);
								map.setMapDesc(false, newX, newY+5);
								map.setMapDesc(false, newX, newY+6);
								map.setMapDesc(false, newX, newY+7);
							}
						}
					}
				}
				catch(ArrayIndexOutOfBoundsException e) {
					System.out.println("tried to color wrong grid");
				}
				
				// ultrasonic sensor 2 - left
				try {
					if (U_L <= 0) break;
					else {
						if (U_L <= U_L_Grid1) { // 1st grid
							confirmObstacle(map, newX+1, newY+3);
						}
//						else if (U_L > U_L_Grid1 && U_L <= U_L_Grid2) { // 2nd grid
//							confirmObstacle(map, newX+1, newY+4);
//							map.grid[newX+1][newY+3].setBackground(SENSOR);
//							map.grid[newX+1][newY+3].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
//							map.setMapDesc(false, newX+1, newY+3);
//						}
						else {
							if(map.grid[newX+1][newY+3].getBackground().equals(WALL)) {
								
							}
							else if(map.grid[newX+1][newY+4].getBackground().equals(WALL)) {
								map.grid[newX+1][newY+3].setBackground(SENSOR);
								map.grid[newX+1][newY+3].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+1, newY+3);
							}
							else {
								map.grid[newX+1][newY+3].setBackground(SENSOR);
								map.grid[newX+1][newY+3].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+1][newY+4].setBackground(SENSOR);
								map.grid[newX+1][newY+4].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+1, newY+3);
								map.setMapDesc(false, newX+1, newY+4);
							}
						}
					}
				}
				catch(ArrayIndexOutOfBoundsException e) {
					System.out.println("tried to color wrong grid");
				}
				
				// short sensor 3 - right
				try {
					if (short_FR <= 0) break;
					else {
						if (short_FR <= short_FR_Grid1) { // 1st grid
							confirmObstacle(map, newX+2, newY-1);
						}
						else if (short_FR > short_FR_Grid1 && short_FR <= short_FR_Grid2) { // 2nd grid
							confirmObstacle(map, newX+2, newY-2);
							map.grid[newX+2][newY-1].setBackground(SENSOR);
							map.grid[newX+2][newY-1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, newX+2, newY-1);
						}
						else if (short_FR > short_FR_Grid2 && short_FR <= short_FR_Grid3) { // 3rd grid
							confirmObstacle(map, newX+2, newY-3);
							map.grid[newX+2][newY-1].setBackground(SENSOR);
							map.grid[newX+2][newY-1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.grid[newX+2][newY-2].setBackground(SENSOR);
							map.grid[newX+2][newY-2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, newX+2, newY-1);
							map.setMapDesc(false, newX+2, newY-2);
						}
						else {
							if(map.grid[newX+2][newY-1].getBackground().equals(WALL)) {
								
							}
							else if(map.grid[newX+2][newY-2].getBackground().equals(WALL)) {
								map.grid[newX+2][newY-1].setBackground(SENSOR);
								map.grid[newX+2][newY-1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+2, newY-1);
							}
							else if(map.grid[newX+2][newY-2].getBackground().equals(WALL)) {
								map.grid[newX+2][newY-1].setBackground(SENSOR);
								map.grid[newX+2][newY-1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+2][newY-2].setBackground(SENSOR);
								map.grid[newX+2][newY-2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+2, newY-1);
								map.setMapDesc(false, newX+2, newY-2);
							}
							else {
								map.grid[newX+2][newY-1].setBackground(SENSOR);
								map.grid[newX+2][newY-1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+2][newY-2].setBackground(SENSOR);
								map.grid[newX+2][newY-2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+2][newY-3].setBackground(SENSOR);
								map.grid[newX+2][newY-3].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+2, newY-1);
								map.setMapDesc(false, newX+2, newY-2);
								map.setMapDesc(false, newX+2, newY-3);
							}
						}
					}
				}
				catch(ArrayIndexOutOfBoundsException e) {
					System.out.println("tried to color wrong grid");
				}
				
				// ultrasonic sensor 3 - right
				try {
					if (U_R <= 0) break;
					else {
						if (U_R <= U_R_Grid1) { // 1st grid
							confirmObstacle(map, newX+1, newY-1);
						}
//						else if (U_R > U_R_Grid1 && U_R <= U_R_Grid2) { // 2nd grid
//							confirmObstacle(map, newX+1, newY-2);
//							map.grid[newX+1][newY-1].setBackground(SENSOR);
//							map.grid[newX+1][newY-1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
//							map.setMapDesc(false, newX+1, newY-1);
//						}
						else {
							if(map.grid[newX+1][newY-1].getBackground().equals(WALL)) {
								
							}
							else if(map.grid[newX+1][newY-2].getBackground().equals(WALL)) {
								map.grid[newX+1][newY-1].setBackground(SENSOR);
								map.grid[newX+1][newY-1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+1, newY-1);
							}
							else {
								map.grid[newX+1][newY-1].setBackground(SENSOR);
								map.grid[newX+1][newY-1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+1][newY-2].setBackground(SENSOR);
								map.grid[newX+1][newY-2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+1, newY-1);
								map.setMapDesc(false, newX+1, newY-2);
							}
						}
					}
				}
				catch(ArrayIndexOutOfBoundsException e) {
					System.out.println("tried to color wrong grid");
				}
				
				break;
				
			case "4":
				
				// short sensor 1 - front
				try {
					if (short_LF <= 0) break;
					else {
						if (short_LF <= short_LF_Grid1) { // 1st grid
							confirmObstacle(map, newX+2, newY-1);
							confirmObstacle(map, newX+2, newY-1);
						}
						else if (short_LF > short_LF_Grid1 && short_LF <= short_LF_Grid2) { // 2nd grid
							confirmObstacle(map, newX+2, newY-2);
							confirmObstacle(map, newX+2, newY-2);
							map.grid[newX+2][newY-1].setBackground(SENSOR);
							map.grid[newX+2][newY-1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, newX+2, newY-1);
						}
						else if (short_LF > short_LF_Grid2 && short_LF <= short_LF_Grid3) { // 3rd grid
							confirmObstacle(map, newX+2, newY-3);
							confirmObstacle(map, newX+2, newY-3);
							map.grid[newX+2][newY-1].setBackground(SENSOR);
							map.grid[newX+2][newY-1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.grid[newX+2][newY-2].setBackground(SENSOR);
							map.grid[newX+2][newY-2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, newX+2, newY-1);
							map.setMapDesc(false, newX+2, newY-2);
						}
						else {
							if(map.grid[newX+2][newY-1].getBackground().equals(WALL)) {
								
							}
							else if (map.grid[newX+2][newY-2].getBackground().equals(WALL)){
								map.grid[newX+2][newY-1].setBackground(SENSOR);
								map.grid[newX+2][newY-1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+2, newY-1);
							}
							else if (map.grid[newX+2][newY-3].getBackground().equals(WALL)){
								map.grid[newX+2][newY-1].setBackground(SENSOR);
								map.grid[newX+2][newY-1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+2][newY-2].setBackground(SENSOR);
								map.grid[newX+2][newY-2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+2, newY-1);
								map.setMapDesc(false, newX+2, newY-2);
							}
							else {
								map.grid[newX+2][newY-1].setBackground(SENSOR);
								map.grid[newX+2][newY-1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+2][newY-2].setBackground(SENSOR);
								map.grid[newX+2][newY-2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+2][newY-3].setBackground(SENSOR);
								map.grid[newX+2][newY-3].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+2, newY-1);
								map.setMapDesc(false, newX+2, newY-2);
								map.setMapDesc(false, newX+2, newY-3);
							}
						}
					}
				}
				catch(ArrayIndexOutOfBoundsException e) {
					System.out.println("tried to color wrong grid");
				}
				
				// ultrasonic sensor 1 - front
				/* 
				 * READINGS FROM THIS SENSOR:
				 * 0 - 5cm   : Still within the robot 3x3 grids
				 * 5 - 15cm  : Obstacle in the first grid
				 * 16 - 49cm : Ignore
				 * 50 - 80cm : No obstacle in the front 3 grids. 4th grid onwards undetermined.
				 */
				try {
					if (U_F <= 0) break;
					else {
						if (U_F <= U_F_Grid1) { // 1st grid
							confirmObstacle(map, newX+1, newY-1);
							confirmObstacle(map, newX+1, newY-1);
							confirmObstacle(map, newX+1, newY-1);
							confirmObstacle(map, newX+1, newY-1);
						}
//						else if (U_F > U_F_Grid1 && U_F <= U_F_Grid2) { // 2nd grid
//							confirmObstacle(map, newX+1, newY-2);
//							map.grid[newX+1][newY-1].setBackground(SENSOR);
//							map.grid[newX+1][newY-1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
//							map.setMapDesc(false, newX+1, newY-1);
//						}
						else { //if (U_F > 40 && U_F <= 70) { // 3 grids no obstacle
							if(map.grid[newX+1][newY-1].getBackground().equals(WALL)) {
								
							}
							else if (map.grid[newX+1][newY-2].getBackground().equals(WALL)){
								map.grid[newX+1][newY-1].setBackground(SENSOR);
								map.grid[newX+1][newY-1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+1, newY-1);
							}
							else if (map.grid[newX+1][newY-3].getBackground().equals(WALL)){
								map.grid[newX+1][newY-1].setBackground(SENSOR);
								map.grid[newX+1][newY-1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+1][newY-2].setBackground(SENSOR);
								map.grid[newX+1][newY-2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+1, newY-1);
								map.setMapDesc(false, newX+1, newY-2);
							}
							else {
								map.grid[newX+1][newY-1].setBackground(SENSOR);
								map.grid[newX+1][newY-1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+1][newY-2].setBackground(SENSOR);
								map.grid[newX+1][newY-2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+1][newY-3].setBackground(SENSOR);
								map.grid[newX+1][newY-3].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+1, newY-1);
								map.setMapDesc(false, newX+1, newY-1);
								map.setMapDesc(false, newX+1, newY-2);
								map.setMapDesc(false, newX+1, newY-3);
							}
						}
					}
				}
				catch(ArrayIndexOutOfBoundsException e) {
					System.out.println("tried to color wrong grid");
				}
				
				// short sensor 2 - front
				try {
					if (short_RF <= 0) break;
					else {
						if (short_RF <= short_RF_Grid1) { // 1st grid
							confirmObstacle(map, newX, newY-1);
							confirmObstacle(map, newX, newY-1);
						}
						else if (short_RF > short_RF_Grid1 && short_RF <= short_RF_Grid2) { // 2nd grid
							confirmObstacle(map, newX, newY-2);
							confirmObstacle(map, newX, newY-2);
							map.grid[newX][newY-1].setBackground(SENSOR);
							map.grid[newX][newY-1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, newX, newY-1);
						}
						else if (short_RF > short_RF_Grid2 && short_RF <= short_RF_Grid3) { // 3rd grid
							confirmObstacle(map, newX, newY-3);
							confirmObstacle(map, newX, newY-3);
							map.grid[newX][newY-1].setBackground(SENSOR);
							map.grid[newX][newY-1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.grid[newX][newY-2].setBackground(SENSOR);
							map.grid[newX][newY-2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, newX, newY-1);
							map.setMapDesc(false, newX, newY-2);
						}
						else {
							if(map.grid[newX][newY-1].getBackground().equals(WALL)) {
								
							}
							else if (map.grid[newX][newY-2].getBackground().equals(WALL)){
								map.grid[newX][newY-1].setBackground(SENSOR);
								map.grid[newX][newY-1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX, newY-1);
							}
							else if (map.grid[newX][newY-3].getBackground().equals(WALL)){
								map.grid[newX][newY-1].setBackground(SENSOR);
								map.grid[newX][newY-1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX][newY-2].setBackground(SENSOR);
								map.grid[newX][newY-2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX, newY-1);
								map.setMapDesc(false, newX, newY-2);
							}
							map.grid[newX][newY-1].setBackground(SENSOR);
							map.grid[newX][newY-1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.grid[newX][newY-2].setBackground(SENSOR);
							map.grid[newX][newY-2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.grid[newX][newY-3].setBackground(SENSOR);
							map.grid[newX][newY-3].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, newX, newY-1);
							map.setMapDesc(false, newX, newY-2);
							map.setMapDesc(false, newX, newY-3);
						}
					}
				}
				catch(ArrayIndexOutOfBoundsException e) {
					System.out.println("tried to color wrong grid");
				}
				
				// short sensor - left (NEW SENSOR ADDED ON 10.10.2014)
				try {
					if (short_FL <= 0) break;
					else {
						if (short_FL <= short_FL_Grid1) { // 1st grid
							confirmObstacle(map, newX+3, newY);
							confirmObstacle(map, newX+3, newY);
						}
						else if (short_FL > short_FL_Grid1 && short_FL <= short_FL_Grid2) { // 2nd grid
							confirmObstacle(map, newX+4, newY);
							confirmObstacle(map, newX+4, newY);
							map.grid[newX+3][newY].setBackground(SENSOR);
							map.grid[newX+3][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, newX+3, newY);
						}
						else if (short_FL > short_FL_Grid2 && short_FL <= short_FL_Grid3) { // 3rd grid
							confirmObstacle(map, newX+5, newY);
							confirmObstacle(map, newX+5, newY);
							map.grid[newX+3][newY].setBackground(SENSOR);
							map.grid[newX+3][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.grid[newX+4][newY].setBackground(SENSOR);
							map.grid[newX+4][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, newX+3, newY);
							map.setMapDesc(false, newX+4, newY);
						}
						else {
							if(map.grid[newX+3][newY].getBackground().equals(WALL)) {
								
							}
							else if (map.grid[newX+4][newY].getBackground().equals(WALL)) {
								map.grid[newX+3][newY].setBackground(SENSOR);
								map.grid[newX+3][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+3, newY);
							}
							else if (map.grid[newX+5][newY].getBackground().equals(WALL)) {
								map.grid[newX+3][newY].setBackground(SENSOR);
								map.grid[newX+3][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+4][newY].setBackground(SENSOR);
								map.grid[newX+4][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+3, newY);
								map.setMapDesc(false, newX+4, newY);
							}
							else {
								map.grid[newX+3][newY].setBackground(SENSOR);
								map.grid[newX+3][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+4][newY].setBackground(SENSOR);
								map.grid[newX+4][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+5][newY].setBackground(SENSOR);
								map.grid[newX+5][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+3, newY);
								map.setMapDesc(false, newX+4, newY);
								map.setMapDesc(false, newX+5, newY);
							}
						}
					}
				}
				catch(ArrayIndexOutOfBoundsException e) {
					System.out.println("tried to color wrong grid");
				}
					
				// long sensor - left
				try {
					if (long_BL <= 0) break;
					else {
						if (long_BL <= long_BL_Grid1) { // 1st grid
							confirmObstacle(map, newX+3, newY+2);
							confirmObstacle(map, newX+3, newY+2);
						}
						else if (long_BL > long_BL_Grid1 && long_BL <= long_BL_Grid2) { // 2nd grid
							confirmObstacle(map, newX+4, newY+2);
							confirmObstacle(map, newX+4, newY+2);
							map.grid[newX+3][newY+2].setBackground(SENSOR);
							map.grid[newX+3][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, newX+3, newY+2);
						}
						else if (long_BL > long_BL_Grid2 && long_BL <= long_BL_Grid3) { // 3rd grid
							confirmObstacle(map, newX+5, newY+2);
							confirmObstacle(map, newX+5, newY+2);
							map.grid[newX+3][newY+2].setBackground(SENSOR);
							map.grid[newX+3][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.grid[newX+4][newY+2].setBackground(SENSOR);
							map.grid[newX+4][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, newX+3, newY+2);
							map.setMapDesc(false, newX+4, newY+2);
						}
						else if (long_BL > long_BL_Grid3 && long_BL <= long_BL_Grid4) { // 4th grid
							confirmObstacle(map, newX+6, newY+2);
							confirmObstacle(map, newX+6, newY+2);
							map.grid[newX+3][newY+2].setBackground(SENSOR);
							map.grid[newX+3][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.grid[newX+4][newY+2].setBackground(SENSOR);
							map.grid[newX+4][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.grid[newX+5][newY+2].setBackground(SENSOR);
							map.grid[newX+5][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, newX+3, newY+2);
							map.setMapDesc(false, newX+4, newY+2);
							map.setMapDesc(false, newX+5, newY+2);
						}
						else if (long_BL > long_BL_Grid4 && long_BL <= long_BL_Grid5) { // 5th grid
							confirmObstacle(map, newX+7, newY+2);
							confirmObstacle(map, newX+7, newY+2);
							map.grid[newX+3][newY+2].setBackground(SENSOR);
							map.grid[newX+3][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.grid[newX+4][newY+2].setBackground(SENSOR);
							map.grid[newX+4][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.grid[newX+5][newY+2].setBackground(SENSOR);
							map.grid[newX+5][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.grid[newX+6][newY+2].setBackground(SENSOR);
							map.grid[newX+6][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, newX+3, newY+2);
							map.setMapDesc(false, newX+4, newY+2);
							map.setMapDesc(false, newX+5, newY+2);
							map.setMapDesc(false, newX+6, newY+2);
						}
						else {
							if(map.grid[newX+3][newY+2].getBackground().equals(WALL)) {
								
							}
							else if(map.grid[newX+4][newY+2].getBackground().equals(WALL)) {
								map.grid[newX+3][newY+2].setBackground(SENSOR);
								map.grid[newX+3][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+3, newY+2);
							}
							else if(map.grid[newX+5][newY+2].getBackground().equals(WALL)) {
								map.grid[newX+3][newY+2].setBackground(SENSOR);
								map.grid[newX+3][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+4][newY+2].setBackground(SENSOR);
								map.grid[newX+4][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+3, newY+2);
								map.setMapDesc(false, newX+4, newY+2);
							}
							else if(map.grid[newX+6][newY+2].getBackground().equals(WALL)) {
								map.grid[newX+3][newY+2].setBackground(SENSOR);
								map.grid[newX+3][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+4][newY+2].setBackground(SENSOR);
								map.grid[newX+4][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+5][newY+2].setBackground(SENSOR);
								map.grid[newX+5][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+3, newY+2);
								map.setMapDesc(false, newX+4, newY+2);
								map.setMapDesc(false, newX+5, newY+2);
							}
							else if(map.grid[newX+7][newY+2].getBackground().equals(WALL)) {
								map.grid[newX+3][newY+2].setBackground(SENSOR);
								map.grid[newX+3][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+4][newY+2].setBackground(SENSOR);
								map.grid[newX+4][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+5][newY+2].setBackground(SENSOR);
								map.grid[newX+5][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+6][newY+2].setBackground(SENSOR);
								map.grid[newX+6][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+3, newY+2);
								map.setMapDesc(false, newX+4, newY+2);
								map.setMapDesc(false, newX+5, newY+2);
								map.setMapDesc(false, newX+6, newY+2);
							}
							else {
								map.grid[newX+3][newY+2].setBackground(SENSOR);
								map.grid[newX+3][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+4][newY+2].setBackground(SENSOR);
								map.grid[newX+4][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+5][newY+2].setBackground(SENSOR);
								map.grid[newX+5][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+6][newY+2].setBackground(SENSOR);
								map.grid[newX+6][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+7][newY+2].setBackground(SENSOR);
								map.grid[newX+7][newY+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+3, newY+2);
								map.setMapDesc(false, newX+4, newY+2);
								map.setMapDesc(false, newX+5, newY+2);
								map.setMapDesc(false, newX+6, newY+2);
								map.setMapDesc(false, newX+7, newY+2);
							}
						}
					}		
				}
				catch(ArrayIndexOutOfBoundsException e) {
					System.out.println("tried to color wrong grid");
				}
				
				// ultrasonic sensor 2 - left
				try {
					if (U_L <= 0) break;
					else {
						if (U_L <= U_L_Grid1) { // 1st grid
							confirmObstacle(map, newX+3, newY+1);
						}
//						else if (U_L > U_L_Grid1 && U_L <= U_L_Grid2) { // 2nd grid
//							confirmObstacle(map, newX+4, newY+1);
//							map.grid[newX+3][newY+1].setBackground(SENSOR);
//							map.grid[newX+3][newY+1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
//							map.setMapDesc(false, newX+3, newY+1);
//						}
						else {
							if(map.grid[newX+3][newY+1].getBackground().equals(WALL)) {
								
							}
							else if(map.grid[newX+4][newY+1].getBackground().equals(WALL)) {
								map.grid[newX+3][newY+1].setBackground(SENSOR);
								map.grid[newX+3][newY+1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+3, newY+1);
							}
							else {
								map.grid[newX+3][newY+1].setBackground(SENSOR);
								map.grid[newX+3][newY+1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX+4][newY+1].setBackground(SENSOR);
								map.grid[newX+4][newY+1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX+3, newY+1);
								map.setMapDesc(false, newX+4, newY+1);
							}
						}
					}
				}
				catch(ArrayIndexOutOfBoundsException e) {
					System.out.println("tried to color wrong grid");
				}
					
				// short sensor 3 - right
				try {
					if (short_FR <= 0) break;
					else {
						if (short_FR <= short_FR_Grid1) { // 1st grid
							confirmObstacle(map, newX-1, newY);
						}
						else if (short_FR > short_FR_Grid1 && short_FR <= short_FR_Grid2) { // 2nd grid
							confirmObstacle(map, newX-2, newY);
							map.grid[newX-1][newY].setBackground(SENSOR);
							map.grid[newX-1][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, newX-1, newY);
						}
						else if (short_FR > short_FR_Grid2 && short_FR <= short_FR_Grid3) { // 3rd grid
							confirmObstacle(map, newX-3, newY);
							map.grid[newX-1][newY].setBackground(SENSOR);
							map.grid[newX-1][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.grid[newX-2][newY].setBackground(SENSOR);
							map.grid[newX-2][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, newX-1, newY);
							map.setMapDesc(false, newX-2, newY);
						}
						else {
							if(map.grid[newX-1][newY].getBackground().equals(WALL)) {
								
							}
							else if(map.grid[newX-2][newY].getBackground().equals(WALL)) {
								map.grid[newX-1][newY].setBackground(SENSOR);
								map.grid[newX-1][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX-1, newY);
							}
							else if(map.grid[newX-3][newY].getBackground().equals(WALL)) {
								map.grid[newX-1][newY].setBackground(SENSOR);
								map.grid[newX-1][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX-2][newY].setBackground(SENSOR);
								map.grid[newX-2][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX-1, newY);
								map.setMapDesc(false, newX-2, newY);
							}
							else {
								map.grid[newX-1][newY].setBackground(SENSOR);
								map.grid[newX-1][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX-2][newY].setBackground(SENSOR);
								map.grid[newX-2][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX-3][newY].setBackground(SENSOR);
								map.grid[newX-3][newY].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX-1, newY);
								map.setMapDesc(false, newX-2, newY);
								map.setMapDesc(false, newX-3, newY);
							}
						}
					}
				}
				catch(ArrayIndexOutOfBoundsException e) {
					System.out.println("tried to color wrong grid");
				}
				
				// ultrasonic sensor 3 - right
				try {
					if (U_R <= 0) break;
					else {
						if (U_R <= U_R_Grid1) { // 1st grid
							confirmObstacle(map, newX-1, newY+1);
						}
//						else if (U_R > U_R_Grid1 && U_R <= U_R_Grid2) { // 2nd grid
//							confirmObstacle(map, newX-2, newY+1);
//							map.grid[newX-1][newY+1].setBackground(SENSOR);
//							map.grid[newX-1][newY+1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
//							map.setMapDesc(false, newX-1, newY+1);
//						}
						else {
							if(map.grid[newX-1][newY+1].getBackground().equals(WALL)) {
								
							}
							else if(map.grid[newX-2][newY+1].getBackground().equals(WALL)) {
								map.grid[newX-1][newY+1].setBackground(SENSOR);
								map.grid[newX-1][newY+1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX-1, newY+1);
							}
							else {
								map.grid[newX-1][newY+1].setBackground(SENSOR);
								map.grid[newX-1][newY+1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.grid[newX-2][newY+1].setBackground(SENSOR);
								map.grid[newX-2][newY+1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
								map.setMapDesc(false, newX-1, newY+1);
								map.setMapDesc(false, newX-2, newY+1);
							}
						}
					}
				}
				catch(ArrayIndexOutOfBoundsException e) {
					System.out.println("tried to color wrong grid");
				}
				
				break;
			}
	}
	
	public void setSensors(MapGrid map){
		int x = this.getX();
		int y = this.getY();
		String orientation = this.getOrientation();
		
		switch(orientation) {
			case "N":
				// short sensor 1 - front
				for (int i = 1; i <= SHORTSENSOR; i++) {
					if(x-i < 1) break;
					if (map.grid[x-i+1][y].getBackground() == OBSTACLE || map.grid[x-i+1][y].getBackground() == CONFIRMOBSTACLE) break;
					else {
						if(map.grid[x-i][y].getBackground() == OBSTACLE || map.grid[x-i][y].getBackground() == CONFIRMOBSTACLE)
							confirmObstacle(map, x-i, y);
						else if(!map.grid[x-i][y].getBackground().equals(WALL)){
							map.grid[x-i][y].setBackground(SENSOR);
							map.grid[x-i][y].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, x-i, y);
						}
					}
				}
				
				// ultrasonic sensor 1 - front
				for (int i = 1; i <= ULTRASONIC; i++) {
					if(x-i < 1) break;
					if (map.grid[x-i+1][y+1].getBackground() == OBSTACLE || map.grid[x-i+1][y+1].getBackground() == CONFIRMOBSTACLE) break;
					else {
						if(map.grid[x-i][y+1].getBackground() == OBSTACLE || map.grid[x-i][y+1].getBackground() == CONFIRMOBSTACLE)
							confirmObstacle(map, x-i, y+1);
						else if(!map.grid[x-i][y+1].getBackground().equals(WALL)) {
							map.grid[x-i][y+1].setBackground(SENSOR);
							map.grid[x-i][y+1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, x-i, y+1);
						}
					}
				}
				
				// short sensor 2 - front
				for (int i = 1; i <= SHORTSENSOR; i++) {
					if(x-i < 1) break;
					if (map.grid[x-i+1][y+2].getBackground() == OBSTACLE || map.grid[x-i+1][y+2].getBackground() == CONFIRMOBSTACLE) break;
					else {
						if(map.grid[x-i][y+2].getBackground() == OBSTACLE || map.grid[x-i][y+2].getBackground() == CONFIRMOBSTACLE)
							confirmObstacle(map, x-i, y+2);
						else if(!map.grid[x-i][y+2].getBackground().equals(WALL)) {
							map.grid[x-i][y+2].setBackground(SENSOR);
							map.grid[x-i][y+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, x-i, y+2);
						}
					}
				}
				
				// short sensor - left (ADDED ON 10.10.2014)
				for (int i = 1; i <= SHORTSENSOR; i++) {
					if(y-i < 1) break;
					if (map.grid[x][y-i+1].getBackground() == OBSTACLE || map.grid[x][y-i+1].getBackground() == CONFIRMOBSTACLE) break;
					else {
						if(map.grid[x][y-i].getBackground() == OBSTACLE || map.grid[x][y-i].getBackground() == CONFIRMOBSTACLE)
							confirmObstacle(map, x, y-i);
						else if(!map.grid[x][y-i].getBackground().equals(WALL)) {
							map.grid[x][y-i].setBackground(SENSOR);
							map.grid[x][y-i].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, x, y-i);
						}
					}
				}
				
				// long sensor - left
				for (int i = 1; i <= LONGSENSOR; i++) {
					if(y-i < 1) break;
					if (map.grid[x+2][y-i+1].getBackground() == OBSTACLE || map.grid[x+2][y-i+1].getBackground() == CONFIRMOBSTACLE) break;
					else {
						if(map.grid[x+2][y-i].getBackground() == OBSTACLE || map.grid[x+2][y-i].getBackground() == CONFIRMOBSTACLE)
							confirmObstacle(map, x+2, y-i);
						else if(!map.grid[x+2][y-i].getBackground().equals(WALL)) {
							map.grid[x+2][y-i].setBackground(SENSOR);
							map.grid[x+2][y-i].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, x+2, y-i);
						}
					}
				}
				
				// ultrasonic sensor 2 - left
				for (int i = 1; i <= ULTRASONIC; i++) {
					if(y-i < 1) break;
					if (map.grid[x+1][y-i+1].getBackground() == OBSTACLE || map.grid[x+1][y-i+1].getBackground() == CONFIRMOBSTACLE) break;
					else {
						if(map.grid[x+1][y-i].getBackground() == OBSTACLE || map.grid[x+1][y-i].getBackground() == CONFIRMOBSTACLE)
							confirmObstacle(map, x+1, y-i);
						else if(!map.grid[x+1][y-i].getBackground().equals(WALL)) {
							map.grid[x+1][y-i].setBackground(SENSOR);
							map.grid[x+1][y-i].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, x+1, y-i);
						}
					}
				}
				
				// short sensor 3 - right
				for (int i = 1; i <= SHORTSENSOR; i++) {
					if(y+i+2 > 21) break;
					if (map.grid[x][y+1+i].getBackground() == OBSTACLE || map.grid[x][y+1+i].getBackground() == CONFIRMOBSTACLE) break;
					else {
						if(map.grid[x][y+2+i].getBackground() == OBSTACLE || map.grid[x][y+2+i].getBackground() == CONFIRMOBSTACLE)
							confirmObstacle(map, x, y+2+i);
						else if(!map.grid[x][y+2+i].getBackground().equals(WALL)) {
							map.grid[x][y+2+i].setBackground(SENSOR);
							map.grid[x][y+2+i].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, x, y+2+i);
						}
					}
				}
				
				// ultrasonic sensor 3 - right
				for (int i = 1; i <= ULTRASONIC; i++) {
					if(y+i+2 > 21) break;
					if (map.grid[x+1][y+1+i].getBackground() == OBSTACLE || map.grid[x+1][y+1+i].getBackground() == CONFIRMOBSTACLE) break;
					else {
						if(map.grid[x+1][y+2+i].getBackground() == OBSTACLE || map.grid[x+1][y+2+i].getBackground() == CONFIRMOBSTACLE)
							confirmObstacle(map, x+1, y+2+i);
						else if(!map.grid[x+1][y+2+i].getBackground().equals(WALL)) {
							map.grid[x+1][y+2+i].setBackground(SENSOR);
							map.grid[x+1][y+2+i].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, x+1, y+2+i);
						}
					}
				}
							
				break;
			case "S":
				// short sensor 1 - front
				for (int i = 1; i <= SHORTSENSOR; i++) {
					if(x+i+2> 16) break;
					if (map.grid[x+i+1][y].getBackground() == OBSTACLE || map.grid[x+i+1][y].getBackground() == CONFIRMOBSTACLE) break;
					else {
						if(map.grid[x+i+2][y].getBackground() == OBSTACLE || map.grid[x+i+2][y].getBackground() == CONFIRMOBSTACLE)
							confirmObstacle(map, x+i+2, y);
						else if(!map.grid[x+i+2][y].getBackground().equals(WALL)) {
							map.grid[x+i+2][y].setBackground(SENSOR);
							map.grid[x+i+2][y].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, x+i+2, y);
						}
					}
				}
				
				// ultrasonic sensor 1 - front
				for (int i = 1; i <= ULTRASONIC; i++) {
					if(x+i+2> 16) break;
					if (map.grid[x+i+1][y+1].getBackground() == OBSTACLE || map.grid[x+i+1][y+1].getBackground() == CONFIRMOBSTACLE) break;
					else {
						if(map.grid[x+i+2][y+1].getBackground() == OBSTACLE || map.grid[x+i+2][y+1].getBackground() == CONFIRMOBSTACLE)
							confirmObstacle(map, x+i+2, y+1);
						else if(!map.grid[x+i+2][y+1].getBackground().equals(WALL)) {
							map.grid[x+i+2][y+1].setBackground(SENSOR);
							map.grid[x+i+2][y+1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, x+i+2, y+1);
						}
					}
				}
				
				// short sensor 2 - front
				for (int i = 1; i <= SHORTSENSOR; i++) {
					if(x+i+2> 16) break;
					if (map.grid[x+i+1][y+2].getBackground() == OBSTACLE || map.grid[x+i+1][y+2].getBackground() == CONFIRMOBSTACLE) break;
					else {
						if(map.grid[x+i+2][y+2].getBackground() == OBSTACLE || map.grid[x+i+2][y+2].getBackground() == CONFIRMOBSTACLE)
							confirmObstacle(map, x+i+2, y+2);
						else if(!map.grid[x+i+2][y+2].getBackground().equals(WALL)) {
							map.grid[x+i+2][y+2].setBackground(SENSOR);
							map.grid[x+i+2][y+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, x+i+2, y+2);
						}
					}
				}
				
				// short sensor - left (ADDED ON 10.10.2014)
				for (int i = 1; i <= SHORTSENSOR; i++) {
					if(y+i+2 > 21) break;
					if (map.grid[x+2][y+i+1].getBackground() == OBSTACLE || map.grid[x+2][y+i+1].getBackground() == CONFIRMOBSTACLE) break;
					else {
						if(map.grid[x+2][y+i+2].getBackground() == OBSTACLE || map.grid[x+2][y+i+2].getBackground() == CONFIRMOBSTACLE)
							confirmObstacle(map, x+2, y+i+2);
						else if(!map.grid[x+2][y+i+2].getBackground().equals(WALL)){
							map.grid[x+2][y+i+2].setBackground(SENSOR);
							map.grid[x+2][y+i+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, x+2, y+i+2);
						}
					}
				}
				
				// long sensor - left
				for (int i = 1; i <= LONGSENSOR; i++) {
					if(y+i+2 > 21) break;
					if (map.grid[x][y+i+1].getBackground() == OBSTACLE || map.grid[x][y+i+1].getBackground() == CONFIRMOBSTACLE) break;
					else {
						if(map.grid[x][y+i+2].getBackground() == OBSTACLE || map.grid[x][y+i+2].getBackground() == CONFIRMOBSTACLE)
							confirmObstacle(map, x, y+i+2);
						else if(!map.grid[x][y+i+2].getBackground().equals(WALL)) {
							map.grid[x][y+i+2].setBackground(SENSOR);
							map.grid[x][y+i+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, x, y+i+2);
						}
					}
				}
				
				// ultrasonic sensor 2 - left
				for (int i = 1; i <= ULTRASONIC; i++) {
					if(y+i+2 > 21) break;
					if (map.grid[x+1][y+i+1].getBackground() == OBSTACLE || map.grid[x+1][y+i+1].getBackground() == CONFIRMOBSTACLE) break;
					else {
						if(map.grid[x+1][y+i+2].getBackground() == OBSTACLE || map.grid[x+1][y+i+2].getBackground() == CONFIRMOBSTACLE)
							confirmObstacle(map, x+1, y+i+2);
						else if(!map.grid[x+1][y+i+2].getBackground().equals(WALL)) {
							map.grid[x+1][y+i+2].setBackground(SENSOR);
							map.grid[x+1][y+i+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, x+1, y+i+2);
						}
					}
				}
				
				// short sensor 3 - right
				for (int i = 1; i <= SHORTSENSOR; i++) {
					if(y-i < 1) break;
					if (map.grid[x+2][y-i+1].getBackground() == OBSTACLE || map.grid[x+2][y-i+1].getBackground() == CONFIRMOBSTACLE) break;
					else {
						if(map.grid[x+2][y-i].getBackground() == OBSTACLE || map.grid[x+2][y-i].getBackground() == CONFIRMOBSTACLE)
							confirmObstacle(map, x+2, y-i);
						else if(!map.grid[x+2][y-i].getBackground().equals(WALL)) {
							map.grid[x+2][y-i].setBackground(SENSOR);
							map.grid[x+2][y-i].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, x+2, y-i);
						}
					}
				}
				
				// ultrasonic sensor 3 - right
				for (int i = 1; i <= ULTRASONIC; i++) {
					if(y-i < 1) break;
					if (map.grid[x+1][y-i+1].getBackground() == OBSTACLE || map.grid[x+1][y-i+1].getBackground() == CONFIRMOBSTACLE) break;
					else {
						if(map.grid[x+1][y-i].getBackground() == OBSTACLE || map.grid[x+1][y-i].getBackground() == CONFIRMOBSTACLE)
							confirmObstacle(map, x+1, y-i);
						else if(!map.grid[x+1][y-i].getBackground().equals(WALL)) {
							map.grid[x+1][y-i].setBackground(SENSOR);
							map.grid[x+1][y-i].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, x+1, y-i);
						}
					}
				}
				break;
			case "E":
				// short sensor 1 - front
				for (int i = 1; i <= SHORTSENSOR; i++) {
					if(y+i+2 > 21) break;
					if (map.grid[x][y+i+1].getBackground() == OBSTACLE || map.grid[x][y+i+1].getBackground() == CONFIRMOBSTACLE) break;
					else {
						if(map.grid[x][y+i+2].getBackground() == OBSTACLE || map.grid[x][y+i+2].getBackground() == CONFIRMOBSTACLE)
							confirmObstacle(map, x, y+i+2);
						else if(!map.grid[x][y+i+2].getBackground().equals(WALL)) {
							map.grid[x][y+i+2].setBackground(SENSOR);
							map.grid[x][y+i+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, x, y+i+2);
						}
					}
				}
				
				// ultrasonic sensor 1 - front
				for (int i = 1; i <= ULTRASONIC; i++) {
					if(y+i+2 > 21) break;
					if (map.grid[x+1][y+i+1].getBackground() == OBSTACLE || map.grid[x+1][y+i+1].getBackground() == CONFIRMOBSTACLE) break;
					else {
						if(map.grid[x+1][y+i+2].getBackground() == OBSTACLE || map.grid[x+1][y+i+2].getBackground() == CONFIRMOBSTACLE)
							confirmObstacle(map, x+1, y+i+2);
						else if(!map.grid[x+1][y+i+2].getBackground().equals(WALL)) {
							map.grid[x+1][y+i+2].setBackground(SENSOR);
							map.grid[x+1][y+i+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, x+1, y+i+2);
						}
					}
				}
				
				// short sensor 2 - front
				for (int i = 1; i <= SHORTSENSOR; i++) {
					if(y+i+2 > 21) break;
					if (map.grid[x+2][y+i+1].getBackground() == OBSTACLE || map.grid[x+2][y+i+1].getBackground() == CONFIRMOBSTACLE) break;
					else {
						if(map.grid[x+2][y+i+2].getBackground() == OBSTACLE || map.grid[x+2][y+i+2].getBackground() == CONFIRMOBSTACLE)
							confirmObstacle(map, x+2, y+i+2);
						else if(!map.grid[x+2][y+i+2].getBackground().equals(WALL)){
							map.grid[x+2][y+i+2].setBackground(SENSOR);
							map.grid[x+2][y+i+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, x+2, y+i+2);
						}
					}
				}
				
				// short sensor - left (ADDED ON 10.10.2014)
				for (int i = 1; i <= SHORTSENSOR; i++) {
					if(x-i < 1) break;
					if (map.grid[x-i+1][y+2].getBackground() == OBSTACLE || map.grid[x-i+1][y+2].getBackground() == CONFIRMOBSTACLE) break;
					else {
						if(map.grid[x-i][y+2].getBackground() == OBSTACLE || map.grid[x-i][y+2].getBackground() == CONFIRMOBSTACLE)
							confirmObstacle(map, x-i, y+2);
						else if(!map.grid[x-i][y+2].getBackground().equals(WALL)) {
							map.grid[x-i][y+2].setBackground(SENSOR);
							map.grid[x-i][y+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, x-i, y+2);
						}
					}
				}
				
				// long sensor - left
				for (int i = 1; i <= LONGSENSOR; i++) {
					if(x-i < 1) break;
					if (map.grid[x-i+1][y].getBackground() == OBSTACLE || map.grid[x-i+1][y].getBackground() == CONFIRMOBSTACLE) break;
					else {
						if(map.grid[x-i][y].getBackground() == OBSTACLE || map.grid[x-i][y].getBackground() == CONFIRMOBSTACLE)
							confirmObstacle(map, x-i, y);
						else if(!map.grid[x-i][y].getBackground().equals(WALL)){
							map.grid[x-i][y].setBackground(SENSOR);
							map.grid[x-i][y].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, x-i, y);
						}
					}
				}
				
				// ultrasonic sensor 2 - left
				for (int i = 1; i <= ULTRASONIC; i++) {
					if(x-i < 1) break;
					if (map.grid[x-i+1][y+1].getBackground() == OBSTACLE || map.grid[x-i+1][y+1].getBackground() == CONFIRMOBSTACLE) break;
					else {
						if(map.grid[x-i][y+1].getBackground() == OBSTACLE || map.grid[x-i][y+1].getBackground() == CONFIRMOBSTACLE)
							confirmObstacle(map, x-i, y+1);
						else if(!map.grid[x-i][y+1].getBackground().equals(WALL)) {
							map.grid[x-i][y+1].setBackground(SENSOR);
							map.grid[x-i][y+1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, x-i, y+1);
						}
					}
				}
				
				// short sensor 3 - right
				for (int i = 1; i <= SHORTSENSOR; i++) {
					if(x+i+2> 16) break;
					if (map.grid[x+1+i][y+1].getBackground() == OBSTACLE || map.grid[x+1+i][y+1].getBackground() == CONFIRMOBSTACLE) break;
					else {
						if(map.grid[x+2+i][y+1].getBackground() == OBSTACLE || map.grid[x+2+i][y+1].getBackground() == CONFIRMOBSTACLE)
							confirmObstacle(map, x+2+i, y+1);
						else if(!map.grid[x+2+i][y+1].getBackground().equals(WALL)) {
							map.grid[x+2+i][y+1].setBackground(SENSOR);
							map.grid[x+2+i][y+1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, x+2+i, y+1);
						}
					}
				}
				
				// ultrasonic sensor 3 - right
				for (int i = 1; i <= ULTRASONIC; i++) {
					if(x+i+2> 16) break;
					if (map.grid[x+1+i][y+2].getBackground() == OBSTACLE || map.grid[x+1+i][y+2].getBackground() == CONFIRMOBSTACLE) break;
					else {
						if(map.grid[x+2+i][y+2].getBackground() == OBSTACLE || map.grid[x+2+i][y+2].getBackground() == CONFIRMOBSTACLE)
							confirmObstacle(map, x+2+i, y+2);
						else if(!map.grid[x+2+i][y+2].getBackground().equals(WALL)) {
							map.grid[x+2+i][y+2].setBackground(SENSOR);
							map.grid[x+2+i][y+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, x+2+i, y+2);
						}
					}
				}
				break;
			case "W":
				// short sensor 1 - front
				for (int i = 1; i <= SHORTSENSOR; i++) {
					if(y-i < 1) break;
					if (map.grid[x][y-i+1].getBackground() == OBSTACLE || map.grid[x][y-i+1].getBackground() == CONFIRMOBSTACLE) break;
					else {
						if(map.grid[x][y-i].getBackground() == OBSTACLE || map.grid[x][y-i].getBackground() == CONFIRMOBSTACLE)
							confirmObstacle(map, x, y-i);
						else if(!map.grid[x][y-i].getBackground().equals(WALL)) {
							map.grid[x][y-i].setBackground(SENSOR);
							map.grid[x][y-i].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, x, y-i);
						}
					}
				}
				
				// ultrasonic sensor 1 - front
				for (int i = 1; i <= ULTRASONIC; i++) {
					if(y-i < 1) break;
					if (map.grid[x+1][y-i+1].getBackground() == OBSTACLE || map.grid[x+1][y-i+1].getBackground() == CONFIRMOBSTACLE) break;
					else {
						if(map.grid[x+1][y-i].getBackground() == OBSTACLE || map.grid[x+1][y-i].getBackground() == CONFIRMOBSTACLE)
							confirmObstacle(map, x+1, y-i);
						else if(!map.grid[x+1][y-i].getBackground().equals(WALL)) {
							map.grid[x+1][y-i].setBackground(SENSOR);
							map.grid[x+1][y-i].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, x+1, y-i);
						}
					}
				}
				
				// short sensor 2 - front
				for (int i = 1; i <= SHORTSENSOR; i++) {
					if(y-i < 1) break;
					if (map.grid[x+2][y-i+1].getBackground() == OBSTACLE || map.grid[x+2][y-i+1].getBackground() == CONFIRMOBSTACLE) break;
					else {
						if(map.grid[x+2][y-i].getBackground() == OBSTACLE || map.grid[x+2][y-i].getBackground() == CONFIRMOBSTACLE)
							confirmObstacle(map, x+2, y-i);
						else if(!map.grid[x+2][y-i].getBackground().equals(WALL)) {
							map.grid[x+2][y-i].setBackground(SENSOR);
							map.grid[x+2][y-i].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, x+2, y-i);
						}
					}
				}
				
				// short sensor - left (ADDED ON 10.10.2014)
				for (int i = 1; i <= SHORTSENSOR; i++) {
					if(x+i+2> 16) break;
					if (map.grid[x+i+1][y].getBackground() == OBSTACLE || map.grid[x+i+1][y].getBackground() == CONFIRMOBSTACLE) break;
					else {
						if(map.grid[x+i+2][y].getBackground() == OBSTACLE || map.grid[x+i+2][y].getBackground() == CONFIRMOBSTACLE)
							confirmObstacle(map, x+i+2, y);
						else if(!map.grid[x+i+2][y].getBackground().equals(WALL)) {
							map.grid[x+i+2][y].setBackground(SENSOR);
							map.grid[x+i+2][y].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, x+i+2, y);
						}
					}
				}
				
				// long sensor - left
				for (int i = 1; i <= LONGSENSOR; i++) {
					if(x+i+2> 16) break;
					if (map.grid[x+i+1][y+2].getBackground() == OBSTACLE || map.grid[x+i+1][y+2].getBackground() == CONFIRMOBSTACLE) break;
					else {
						if(map.grid[x+i+2][y+2].getBackground() == OBSTACLE || map.grid[x+i+2][y+2].getBackground() == CONFIRMOBSTACLE)
							confirmObstacle(map, x+i+2, y+2);
						else if(!map.grid[x+i+2][y+2].getBackground().equals(WALL)) {
							map.grid[x+i+2][y+2].setBackground(SENSOR);
							map.grid[x+i+2][y+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, x+i+2, y+2);
						}
					}
				}
				
				// ultrasonic sensor 2 - left
				for (int i = 1; i <= ULTRASONIC; i++) {
					if(x+i+2> 16) break;
					if (map.grid[x+i+1][y+1].getBackground() == OBSTACLE || map.grid[x+i+1][y+1].getBackground() == CONFIRMOBSTACLE) break;
					else {
						if(map.grid[x+i+2][y+1].getBackground() == OBSTACLE || map.grid[x+i+2][y+1].getBackground() == CONFIRMOBSTACLE)
							confirmObstacle(map, x+i+2, y+1);
						else if(!map.grid[x+i+2][y+1].getBackground().equals(WALL)) {
							map.grid[x+i+2][y+1].setBackground(SENSOR);
							map.grid[x+i+2][y+1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, x+i+2, y+1);
						}
					}
				}
				
				// short sensor 3 - right
				for (int i = 1; i <= SHORTSENSOR; i++) {
					if(x-i < 1) break;
					if (map.grid[x-i+1][y].getBackground() == OBSTACLE || map.grid[x-i+1][y].getBackground() == CONFIRMOBSTACLE) break;
					else {
						if(map.grid[x-i][y].getBackground() == OBSTACLE || map.grid[x-i][y].getBackground() == CONFIRMOBSTACLE)
							confirmObstacle(map, x-i, y);
						else if(!map.grid[x-i][y].getBackground().equals(WALL)){
							map.grid[x-i][y].setBackground(SENSOR);
							map.grid[x-i][y].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, x-i, y);
						}
					}
				}
				
				// ultrasonic sensor 3 - right
				for (int i = 1; i <= ULTRASONIC; i++) {
					if(x-i < 1) break;
					if (map.grid[x-i+1][y+1].getBackground() == OBSTACLE || map.grid[x-i+1][y+1].getBackground() == CONFIRMOBSTACLE) break;
					else {
						if(map.grid[x-i][y+1].getBackground() == OBSTACLE || map.grid[x-i][y+1].getBackground() == CONFIRMOBSTACLE)
							confirmObstacle(map, x-i, y+1);
						else if(!map.grid[x-i][y+1].getBackground().equals(WALL)) {
							map.grid[x-i][y+1].setBackground(SENSOR);
							map.grid[x-i][y+1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
							map.setMapDesc(false, x-i, y+1);
						}
					}
				}
				break;
			}

	}
	
	public void moveRobot(JSONObject reading, MapGrid map, int noOfSteps) {
		int x = this.getX();
		int y = this.getY();
		String orientation = this.getOrientation();
		while (noOfSteps > 0) {
			switch(orientation) {
				case "N": 
					x--;
					break;
				case "S": 
					x++;
					break;
				case "E": 
					y++;
					break;
				case "W": 				
					y--;
					break;
			}
			if(map.getName().equals("map")) {
				JSONObject fakeHash = new JSONObject();
				setRobotXY(map, x, y, "Move", orientation, fakeHash);
			}
			else {
				setRobotXY(map, x, y, "Move", orientation, reading);
			}
			setExplored(map);
			noOfSteps--;
		}
	}
	
	public void rotateRobot(JSONObject reading, MapGrid map, String turnWhere) {
		if(map.getName().equals("map")) {
			JSONObject fakeHash = new JSONObject();
			setRobotXY(map, this.getX(), this.getY(), "Rotate", turnWhere, fakeHash);
		}
		else {
			setRobotXY(map, this.getX(), this.getY(), "Rotate", turnWhere, reading);
		}
		
		
	}
	
	/*
	 * 
	 * 
	public void rotate(int degree) {
		while (degree != 0) {
			System.out.println("rotating " + degree);
			if (degree >= 90) {
				if (getDirection() == 'N')
					setDirection('E');
				else if (getDirection() == 'E')
					setDirection('S');
				else if (getDirection() == 'S')
					setDirection('W');
				else if (getDirection() == 'W')
					setDirection('N');
				
				degree = degree - 90;
			}
			else if (degree <= -90) {
				if (getDirection() == 'N')
					setDirection('W');
				else if (getDirection() == 'W')
					setDirection('S');
				else if (getDirection() == 'S')
					setDirection('E');
				else if (getDirection() == 'E')
					setDirection('N');
				
				degree = degree + 90;
			}
			
			
			if (degree < 90 && degree > 0)
				degree = 0;
			
			if (degree > -90 && degree < 0)
				degree = 0;
		}
		
	}*/
	
	public void detectObstacle(MapGrid map, int x, int y, String orientation) {
		// insert code to detect obstacle here
		// if color == grey or something		
		// then call confirmObstacle(x,y)
	}
	
	public void confirmObstacle(MapGrid map, int x, int y) {
		
		if(map.getName().equals("map")) {
			map.grid[x][y].setBackground(CONFIRMOBSTACLE);
		}
		
		try {
			map.setMapDescObstacles(x, y);
			map.grid[x][y].setBackground(Color.red);
		}
		catch(ArrayIndexOutOfBoundsException e) {
			System.out.println("No such obstacle");
		}
		
		
	}
	
	public void setExplored(MapGrid map) {
		int x = this.getX();
		int y = this.getY();
		
		switch(this.getOrientation()) {
			case "N":
				if(map.grid[x+3][y].getBackground() != OBSTACLE && map.grid[x+3][y+1].getBackground() != OBSTACLE && map.grid[x+3][y+2].getBackground() != OBSTACLE) {
					map.grid[x+3][y].setBackground(EXPLORED);
					map.grid[x+3][y+1].setBackground(EXPLORED);
					map.grid[x+3][y+2].setBackground(EXPLORED);
					map.grid[x+3][y].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
					map.grid[x+3][y+1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
					map.grid[x+3][y+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
					map.setMapDesc(true, x+3, y);
					map.setMapDesc(true, x+3, y+1);
					map.setMapDesc(true, x+3, y+2);
				}
				break;
			case "S":
				if(map.grid[x-1][y].getBackground() != OBSTACLE && map.grid[x-1][y+1].getBackground() != OBSTACLE && map.grid[x-1][y+2].getBackground() != OBSTACLE) {
					map.grid[x-1][y].setBackground(EXPLORED);
					map.grid[x-1][y+1].setBackground(EXPLORED);
					map.grid[x-1][y+2].setBackground(EXPLORED);
					map.grid[x-1][y].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
					map.grid[x-1][y+1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
					map.grid[x-1][y+2].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
					map.setMapDesc(true, x-1, y);
					map.setMapDesc(true, x-1, y+1);
					map.setMapDesc(true, x-1, y+2);
				}
				break;
			case "E":
				if(map.grid[x][y-1].getBackground() != OBSTACLE && map.grid[x+1][y-1].getBackground() != OBSTACLE && map.grid[x+2][y-1].getBackground() != OBSTACLE) {
					map.grid[x][y-1].setBackground(EXPLORED);
					map.grid[x+1][y-1].setBackground(EXPLORED);
					map.grid[x+2][y-1].setBackground(EXPLORED);
					map.grid[x][y-1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
					map.grid[x+1][y-1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
					map.grid[x+2][y-1].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
					map.setMapDesc(true, x, y-1);
					map.setMapDesc(true, x+1, y-1);
					map.setMapDesc(true, x+2, y-1);
				}
				break;
			case "W":
				if(map.grid[x][y+3].getBackground() != OBSTACLE && map.grid[x+1][y+3].getBackground() != OBSTACLE && map.grid[x+2][y+3].getBackground() != OBSTACLE) {
					map.grid[x][y+3].setBackground(EXPLORED);
					map.grid[x+1][y+3].setBackground(EXPLORED);
					map.grid[x+2][y+3].setBackground(EXPLORED);
					map.grid[x][y+3].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
					map.grid[x+1][y+3].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
					map.grid[x+2][y+3].setBorder(BorderFactory.createLineBorder(GRIDBORDER, 1));
					map.setMapDesc(true, x, y+3);
					map.setMapDesc(true, x+1, y+3);
					map.setMapDesc(true, x+2, y+3);
				}
				break;
		}	
	}
}
