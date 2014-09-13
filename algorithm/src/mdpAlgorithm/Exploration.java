package mdpAlgorithm;

import java.awt.Color;

public class Exploration {
	private static final Color OBSTACLE = Color.DARK_GRAY;
	private static final int TopWall = -1;
	private static final int LeftWall = -1;
	private static final int BottomWall = 15;
	private static final int RightWall = 20;

	
	public void robotExplore(){
		
	}
	
	public void simulatorExplore(MapGrid map, Robot rob){
		int x = rob.getX();
		int y = rob.getY();

		int distanceToNorthObs = 0;
		int distanceToSouthObs = 0;
		int distanceToWestObs = 0;
		int distanceToEastObs = 0;
		
		// Check whether there is an obstacle 3 grids away on top
		if (map.grid[x-3][y].getBackground() == OBSTACLE || map.grid[x-3][y+1].getBackground() == OBSTACLE || map.grid[x-3][y+2].getBackground() == OBSTACLE) {
			distanceToNorthObs = 3;
		}
		if (map.grid[x-2][y].getBackground() == OBSTACLE || map.grid[x-2][y+1].getBackground() == OBSTACLE || map.grid[x-2][y+2].getBackground() == OBSTACLE) {
			distanceToNorthObs = 2;
		}
		if (map.grid[x-1][y].getBackground() == OBSTACLE || map.grid[x-1][y+1].getBackground() == OBSTACLE || map.grid[x-1][y+2].getBackground() == OBSTACLE) {
			distanceToNorthObs = 1;
		}
		
		
		// Check whether there is an obstacle 3 grids away on left
		if (map.grid[x][y-3].getBackground() == OBSTACLE || map.grid[x+1][y-3].getBackground() == OBSTACLE || map.grid[x+2][y-3].getBackground() == OBSTACLE) {
			distanceToWestObs = 3;
		}
		if (map.grid[x][y-2].getBackground() == OBSTACLE || map.grid[x+1][y-2].getBackground() == OBSTACLE || map.grid[x+2][y-2].getBackground() == OBSTACLE) {
			distanceToWestObs = 2;
		}
		if (map.grid[x][y-1].getBackground() == OBSTACLE || map.grid[x+1][y-1].getBackground() == OBSTACLE || map.grid[x+2][y-1].getBackground() == OBSTACLE) {
			distanceToWestObs = 1;
		}
		
		
		
		// Check whether there is an obstacle 3 grids away on south
		if (map.grid[x+5][y].getBackground() == OBSTACLE || map.grid[x+5][y+1].getBackground() == OBSTACLE || map.grid[x+5][y+2].getBackground() == OBSTACLE) {
			distanceToSouthObs = 3;
		}
		if (map.grid[x+4][y].getBackground() == OBSTACLE || map.grid[x+4][y+1].getBackground() == OBSTACLE || map.grid[x+4][y+2].getBackground() == OBSTACLE) {
			distanceToSouthObs = 2;
		}
		if (map.grid[x+3][y].getBackground() == OBSTACLE || map.grid[x+3][y+1].getBackground() == OBSTACLE || map.grid[x+3][y+2].getBackground() == OBSTACLE) {
			distanceToSouthObs = 1;
		}
		
		
		// Check whether there is an obstacle 3 grids away on right			
		if (map.grid[x][y+5].getBackground() == OBSTACLE || map.grid[x+1][y+5].getBackground() == OBSTACLE || map.grid[x+2][y+5].getBackground() == OBSTACLE) {
			distanceToEastObs = 3;
		}
		if (map.grid[x][y+4].getBackground() == OBSTACLE || map.grid[x+1][y+4].getBackground() == OBSTACLE || map.grid[x+2][y+4].getBackground() == OBSTACLE) {
			distanceToEastObs = 2;
		}
		if (map.grid[x][y+3].getBackground() == OBSTACLE || map.grid[x+1][y+3].getBackground() == OBSTACLE || map.grid[x+2][y+3].getBackground() == OBSTACLE) {
			distanceToEastObs = 1;
		}
		
		//for debugging		
		System.out.format ("The north distance is: %d%n", distanceToNorthObs);
		System.out.format ("The south distance is: %d%n", distanceToSouthObs);
		System.out.format ("The west distance is: %d%n", distanceToWestObs);
		System.out.format ("The east distance is: %d%n", distanceToEastObs);
		
		
		// To detect the furthest obs and face that direction
		if (distanceToNorthObs>distanceToSouthObs && distanceToNorthObs>distanceToWestObs && distanceToNorthObs>distanceToEastObs){
			rob.rotateRobot(map, rob.getX(), rob.getY(), "N"); 
		}
		else { 
			if (distanceToEastObs>distanceToSouthObs && distanceToEastObs>distanceToWestObs && distanceToEastObs>distanceToNorthObs){
				rob.rotateRobot(map, rob.getX(), rob.getY(), "E");
			}
			else {
				if (distanceToSouthObs>distanceToNorthObs && distanceToSouthObs>distanceToWestObs && distanceToSouthObs>distanceToEastObs){
					rob.rotateRobot(map, rob.getX(), rob.getY(), "S");
				}
				else {
					if (distanceToWestObs>distanceToNorthObs && distanceToWestObs>distanceToSouthObs && distanceToWestObs>distanceToEastObs){
						rob.rotateRobot(map, rob.getX(), rob.getY(), "W");
					}
				}
			}
		}
		//for debugging
		System.out.format ("The direction is: %s%n", rob.getOrientation());
		
		
		int LeftSideDistance = 0;
		int RightSideDistance = 20;
		int UpSideDistance = 0;
		int DownSideDistance = 15;
		
		int count=0; 
		do { //DOWHILE loop until robot finds a wall
			switch (rob.getOrientation()) {
				case "N": 
					while ((x-1)!=TopWall) { // heading up north
						// if there's obstacle, always go WEST
						x = rob.getX();
			    		y = rob.getY();
						LeftSideDistance = (y+1) - 0;
						RightSideDistance = 20 - (y+1);
						if(map.grid[x-1][y].getBackground() == OBSTACLE || map.grid[x-1][y+1].getBackground() == OBSTACLE || map.grid[x-1][y+2].getBackground() == OBSTACLE) {
							if (LeftSideDistance >= RightSideDistance) {
								rob.rotateRobot(map, rob.getX(), rob.getY(), "W");
							}
							else {
								rob.rotateRobot(map, rob.getX(), rob.getY(), "E");
							}
							x = rob.getX();
				    		y = rob.getY();
			    			break;
			    		}
			            else{ // else continue moving
			            	rob.moveRobot(map, rob.getX(), rob.getY(), 1, rob.getOrientation());
			            }
						x = rob.getX();
			    		y = rob.getY();
					}
					if (((x-1)!=TopWall)) {
						break;
					}
					else if (((x-1)==TopWall) && ((y+3)==RightWall)) {
						rob.rotateRobot(map, rob.getX(), rob.getY(), "W");
					}
					else if ((x-1)==TopWall && ((y+3)!=LeftWall)) {
						rob.rotateRobot(map, rob.getX(), rob.getY(), "W");
					}
					else if (((y+3)==RightWall) && (map.grid[x-1][y].getBackground() == OBSTACLE || map.grid[x-1][y+1].getBackground() == OBSTACLE || map.grid[x-1][y+2].getBackground() == OBSTACLE)) {
						/*
						 * This Part should be where the Robot traverse the obstacle and follow the sensor on the robot's right
						 */
						rob.rotateRobot(map, rob.getX(), rob.getY(), "W");
					}		
					else {
						rob.rotateRobot(map, rob.getX(), rob.getY(), "N");
					}
					x = rob.getX();
		    		y = rob.getY();
		    		System.out.println();
		    		System.out.format ("The left distance is: %d%n", LeftSideDistance);
		    		System.out.format ("The right distance is: %d%n", RightSideDistance);
					break;
					
					
				case "S":
					while ((x+3)!=BottomWall) { // heading south
						// if there's obstacle, ...
						x = rob.getX();
			    		y = rob.getY();
						LeftSideDistance = (y+1) - 0;
						RightSideDistance = 20 - (y+1);
						if(map.grid[x+3][y].getBackground() == OBSTACLE || map.grid[x+3][y+1].getBackground() == OBSTACLE || map.grid[x+3][y+2].getBackground() == OBSTACLE) {
							if (LeftSideDistance >= RightSideDistance) {
								rob.rotateRobot(map, rob.getX(), rob.getY(), "W");
							}
							else {
								rob.rotateRobot(map, rob.getX(), rob.getY(), "E");
							}
							x = rob.getX();
				    		y = rob.getY();
			    			break;
			    		}
			            else{ // else continue moving
			            	rob.moveRobot(map, rob.getX(), rob.getY(), 1, rob.getOrientation());
			            }
						x = rob.getX();
			    		y = rob.getY();
					}
					if ((x+3)!=BottomWall) {
						break;
					}
					else if (((x+3)==BottomWall) && ((y-1)==LeftWall)) {
						rob.rotateRobot(map, rob.getX(), rob.getY(), "E");
					}
					else if (((x+3)==BottomWall) && ((y-1)!=RightWall)) {
						rob.rotateRobot(map, rob.getX(), rob.getY(), "E");
					}
					else if (((y-1)==LeftWall) && (map.grid[x+3][y].getBackground() == OBSTACLE || map.grid[x+3][y+1].getBackground() == OBSTACLE || map.grid[x+3][y+2].getBackground() == OBSTACLE)) {
						/*
						 * This Part should be where the Robot traverse the obstacle and follow the sensor on the robot's right
						 */
						rob.rotateRobot(map, rob.getX(), rob.getY(), "E");
					}
					else {
						rob.rotateRobot(map, rob.getX(), rob.getY(), "S");
					}
					x = rob.getX();
		    		y = rob.getY();
					break;
					
				case "E":
					while ((y+3)!=RightWall) { // heading east
						// if there's obstacle, ...
						x = rob.getX();
			    		y = rob.getY();
						UpSideDistance = x - 0;
						DownSideDistance = 15 - x;
						if(map.grid[x][y+3].getBackground() == OBSTACLE || map.grid[x+1][y+3].getBackground() == OBSTACLE || map.grid[x+2][y+3].getBackground() == OBSTACLE) {
							if (UpSideDistance >= DownSideDistance) {
								rob.rotateRobot(map, rob.getX(), rob.getY(), "N");
							}
							else {
								rob.rotateRobot(map, rob.getX(), rob.getY(), "S");
							}
							x = rob.getX();
				    		y = rob.getY();
			    			break;
			    		}
			            else{ // else continue moving
			            	rob.moveRobot(map, rob.getX(), rob.getY(), 1, rob.getOrientation());
			            }
						x = rob.getX();
			    		y = rob.getY();
					}
					if ((y+3)!=RightWall) {
						break;
					}
					else if (((y+3)==RightWall) && ((x-1)!=TopWall) ) {
						rob.rotateRobot(map, rob.getX(), rob.getY(), "N");
					}
					else if (((y+3)==RightWall) && ((x+3)==BottomWall) ) {
						rob.rotateRobot(map, rob.getX(), rob.getY(), "N");
					}
					else if (((x+3)==BottomWall) && (map.grid[x][y+3].getBackground() == OBSTACLE || map.grid[x+1][y+3].getBackground() == OBSTACLE || map.grid[x+2][y+3].getBackground() == OBSTACLE)) {
						/*
						 * This Part should be where the Robot traverse the obstacle and follow the sensor on the robot's right
						 */
						rob.rotateRobot(map, rob.getX(), rob.getY(), "N");
					}
					else {
						rob.rotateRobot(map, rob.getX(), rob.getY(), "E");
					}
					x = rob.getX();
		    		y = rob.getY();
					break;
					
				case "W":
					while ((y-1)!=LeftWall) { // heading west
						// if there's obstacle, ...
						x = rob.getX();
			    		y = rob.getY();
						UpSideDistance = x - 0;
						DownSideDistance = 15 - x;
						if(map.grid[x][y-1].getBackground() == OBSTACLE || map.grid[x+1][y-1].getBackground() == OBSTACLE || map.grid[x+2][y-1].getBackground() == OBSTACLE) {
							if (UpSideDistance >= DownSideDistance) {
								rob.rotateRobot(map, rob.getX(), rob.getY(), "N");
							}
							else {
								rob.rotateRobot(map, rob.getX(), rob.getY(), "S");
							}
							x = rob.getX();
				    		y = rob.getY();
			    			break;
			    		}
			            else{ // else continue moving
			            	rob.moveRobot(map, rob.getX(), rob.getY(), 1, rob.getOrientation());
			            }
						x = rob.getX();
			    		y = rob.getY();
					}
					if ((y-1)!=LeftWall) {
						break;
					}
					else if (((y-1)==LeftWall) && ((x-1)==TopWall)) {
						rob.rotateRobot(map, rob.getX(), rob.getY(), "S");
					}
					else if (((y-1)==LeftWall) && ((x+3)!=BottomWall)) {
						rob.rotateRobot(map, rob.getX(), rob.getY(), "S");
					}
					else if (((x-1)==TopWall) && (map.grid[x][y-1].getBackground() == OBSTACLE || map.grid[x+1][y-1].getBackground() == OBSTACLE || map.grid[x+2][y-1].getBackground() == OBSTACLE)) {
						/*
						 * This Part should be where the Robot traverse the obstacle and follow the sensor on the robot's right
						 */
						rob.rotateRobot(map, rob.getX(), rob.getY(), "S");
					}
					else {
						rob.rotateRobot(map, rob.getX(), rob.getY(), "W");
					}
					x = rob.getX();
		    		y = rob.getY();
					break;		
			}
			count++;
		} while (count<20);
		// while ( ((x-1)!=TopWall) || ((x+3)!=BottomWall) || ((y+3)!=RightWall) || ((y-1)!=LeftWall) );
		

	}
}
