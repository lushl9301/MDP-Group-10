package mdpAlgorithm;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.Timer;

public class Robot {
	private static final Color ROBOT = new Color(153, 204, 255);
	private static final Color FRONTROBOT = new Color(146, 208, 80);
	private static final Color BORDER = new Color(153, 204, 255);
	private static final Color EXPLORED = new Color(0, 128, 255);
	private static final Color CONFIRMOBSTACLE = new Color(255, 30, 30);
	private int robotX, robotY;
	private String robotOrientation;
	
	public Robot (MapGrid map) {
		initRobot(map);
	}
	
	public int getX() {
		return robotX;
	}
	
	public int getY() {
		return robotY;
	}
	
	public String getOrientation() {
		return robotOrientation;
	}
	
	private void initRobot(MapGrid map) {
		setRobotXY(map, 6, 8, "ExploreStart", "N"); // 6,8 top left of robot
		robotX = 6;
		robotY = 8;
		robotOrientation = "N";
	}
	
	public static void setRobotXY(MapGrid map, int x, int y, String text, String orientation) {

		for (int i = x; i < x+3; i++) {
			for (int j = y; j < y+3; j++) {
				map.grid[i][j].setBackground(ROBOT);
				map.grid[i][j].setBorder(BorderFactory.createLineBorder(BORDER, 1));
			}
		}
		
		switch(orientation) {
			case "N": 
				map.grid[x][y+1].setBackground(FRONTROBOT);
				break;
			case "S":
				map.grid[x+2][y+1].setBackground(FRONTROBOT);
				break;
			case "E":
				map.grid[x+1][y+2].setBackground(FRONTROBOT);
				break;
			case "W":
				map.grid[x+1][y].setBackground(FRONTROBOT);
				break;		
		}	
	}
	
	public void moveRobot(MapGrid map, int x, int y, int noOfSteps, String orientation) {
		robotOrientation = orientation;
		switch(orientation) {
			case "N": 
				while (noOfSteps > 0) {
					x--;
					setRobotXY(map, x, y, "Move", orientation); //move north
					map.grid[x+3][y].setBackground(EXPLORED);
					map.grid[x+3][y+1].setBackground(EXPLORED);
					map.grid[x+3][y+2].setBackground(EXPLORED);
					noOfSteps--;
					robotX = x;
					robotY = y;
				}
				break;
			case "S": 
				while (noOfSteps > 0) {
					x++;
					setRobotXY(map, x, y, "Move", orientation); //move south
					map.grid[x-1][y].setBackground(EXPLORED);
					map.grid[x-1][y+1].setBackground(EXPLORED);
					map.grid[x-1][y+2].setBackground(EXPLORED);
					noOfSteps--;
					robotX = x;
					robotY = y;
				}
				break;
			case "E": 
				while (noOfSteps > 0) {
					y++;
					setRobotXY(map, x, y, "Move", orientation); //move east
					map.grid[x][y-1].setBackground(EXPLORED);
					map.grid[x+1][y-1].setBackground(EXPLORED);
					map.grid[x+2][y-1].setBackground(EXPLORED);
					noOfSteps--;
					robotX = x;
					robotY = y;
				}
				break;
			case "W": 				
				while (noOfSteps > 0) {
					y--;
					setRobotXY(map, x, y, "Move", orientation); //move west
					map.grid[x][y+3].setBackground(EXPLORED);
					map.grid[x+1][y+3].setBackground(EXPLORED);
					map.grid[x+2][y+3].setBackground(EXPLORED);
					noOfSteps--;
					robotX = x;
					robotY = y;
				}
				break;
		}
	}
	
	public void rotateRobot(MapGrid map, int x, int y, String turnWhere) {
		robotOrientation = turnWhere;
		
		for (int i = x; i < x+3; i++) {
			for (int j = y; j < y+3; j++) {
				map.grid[i][j].setBackground(ROBOT);
				map.grid[i][j].setBorder(BorderFactory.createLineBorder(BORDER, 1));
			}
		}
		
		switch(turnWhere) {
			case "N": 
				map.grid[x][y+1].setBackground(FRONTROBOT);
				break;
			case "S":
				map.grid[x+2][y+1].setBackground(FRONTROBOT);
				break;
			case "E":
				map.grid[x+1][y+2].setBackground(FRONTROBOT);
				break;
			case "W":
				map.grid[x+1][y].setBackground(FRONTROBOT);
				break;	
		}
	}
	
	public void detectObstacle(MapGrid map, int x, int y, String orientation) {
		// insert code to detect obstacle here
		// if color == grey or something		
		// then call confirmObstacle(x,y)
	}
	
	public void confirmObstacle(MapGrid map, int x, int y) {
		map.grid[x][y].setBackground(CONFIRMOBSTACLE);
	}
}
