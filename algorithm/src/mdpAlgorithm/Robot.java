package mdpAlgorithm;

import java.awt.Color;

import javax.swing.BorderFactory;

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
		setRobotXY(map, 7, 9, "ExploreStart", "N"); // 7,9 top left of robot
		robotX = 7;
		robotY = 9;
		robotOrientation = "N";
		rCount = 0;
	}
	
	public void setRobotXY(MapGrid map, int x, int y, String text, String orientation) {
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
		else if(map.getName().equals("map2"))
			setRTSensors(map);
	}
	
	public void setRTSensors(MapGrid map){
		int x = this.getX();
		int y = this.getY();
		String orientation = this.getOrientation();

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
							map.setMapDesc(x-i, y);
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
							map.setMapDesc(x-i, y+1);
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
							map.setMapDesc(x-i, y+2);
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
							map.setMapDesc(x+2, y-i);
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
							map.setMapDesc(x+1, y-i);
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
							map.setMapDesc(x, y+2+i);
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
							map.setMapDesc(x+1, y+2+i);
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
							map.setMapDesc(x+i+2, y);
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
							map.setMapDesc(x+i+2, y+1);
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
							map.setMapDesc(x+i+2, y+2);
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
							map.setMapDesc(x, y+i+2);
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
							map.setMapDesc(x+1, y+i+2);
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
							map.setMapDesc(x+2, y-i);
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
							map.setMapDesc(x+1, y-i);
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
							map.setMapDesc(x, y+i+2);
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
							map.setMapDesc(x+1, y+i+2);
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
							map.setMapDesc(x+2, y+i+2);
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
							map.setMapDesc(x-i, y);
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
							map.setMapDesc(x-i, y+1);
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
							map.setMapDesc(x+2+i, y+1);
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
							map.setMapDesc(x+2+i, y+2);
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
							map.setMapDesc(x, y-i);
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
							map.setMapDesc(x+1, y-i);
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
							map.setMapDesc(x+2, y-i);
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
							map.setMapDesc(x+i+2, y+2);
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
							map.setMapDesc(x+i+2, y+1);
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
							map.setMapDesc(x-i, y);
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
							map.setMapDesc(x-i, y+1);
						}
					}
				}
				break;
			}

	}
	
	public void moveRobot(MapGrid map, int noOfSteps) {
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
			setRobotXY(map, x, y, "Move", orientation);
			setExplored(map);
			noOfSteps--;
		}
	}
	
	public void rotateRobot(MapGrid map, String turnWhere) {
		setRobotXY(map, this.getX(), this.getY(), "Rotate", turnWhere);
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
		map.grid[x][y].setBackground(CONFIRMOBSTACLE);
		map.setMapDescObstacles(x, y);
		
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
					map.setMapDesc(x+3, y);
					map.setMapDesc(x+3, y+1);
					map.setMapDesc(x+3, y+2);
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
					map.setMapDesc(x-1, y);
					map.setMapDesc(x-1, y+1);
					map.setMapDesc(x-1, y+2);
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
					map.setMapDesc(x, y-1);
					map.setMapDesc(x+1, y-1);
					map.setMapDesc(x+2, y-1);
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
					map.setMapDesc(x, y+3);
					map.setMapDesc(x+1, y+3);
					map.setMapDesc(x+2, y+3);
				}
				break;
		}	
	}
}
