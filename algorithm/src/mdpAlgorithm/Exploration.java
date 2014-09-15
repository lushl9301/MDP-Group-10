package mdpAlgorithm;

import java.awt.Color;
import java.util.Stack;

public class Exploration implements Runnable {
	private static final Color OBSTACLE = Color.DARK_GRAY;
	private static final int TopWall = -1;
	private static final int LeftWall = -1;
	private static final int BottomWall = 15;
	private static final int RightWall = 20;
	public Stack<Robot> pathTravelled;
	public MapGrid map;
	public Robot rob;
	private int sleeptime;
	
	public Exploration(MapGrid map, Robot rob, int sleeptime) {
		this.rob = rob;
		this.map = map;
		this.sleeptime = sleeptime;
	}
	
	@Override
	public void run() {

		boolean reachedWall = false;
		boolean completed = false;
		boolean enteredGoal = false;

		
		pathTravelled = new Stack<Robot>();
		pathTravelled.push(rob);
		
		int rotationCount = 0;
		int faceFirstDir = 0;
		
		do { // Fake 360 degree rotation to check distance
			Robot currentDir = new Robot(pathTravelled.peek());
			simulatorFirstRotation(map, rob, sleeptime); //rotate on the spot
			if (currentDir != null) {
				pathTravelled.push(currentDir);
			}
			rotationCount++;
		} while (rotationCount <4); //rotate on the spot 4 times
		
		
		do { // First direction (This is where the first obstacle distance is being counted)
			Robot goingDir = new Robot(pathTravelled.peek());
			simulatorFirstDirection(map, rob, sleeptime); //Face first direction
			if (goingDir != null) {
				pathTravelled.push(goingDir);
			}
			faceFirstDir++;
		} while (faceFirstDir <1); //straight away face the direction with the obstacle
		
		
		do { //path to find wall
			Robot currentPost = new Robot(pathTravelled.peek());
			if (((rob.getX()-1)==TopWall) || ((rob.getX()+3)==BottomWall) || ((rob.getY()+3)==RightWall) || ((rob.getY()-1)==LeftWall)) reachedWall = true;
			currentPost = simulatorExplore(map, rob, sleeptime);
			if (currentPost != null) {
				pathTravelled.push(currentPost);
			}
			else {
				currentPost = pathTravelled.pop();
			}
			
		} while(!pathTravelled.isEmpty() && !reachedWall);
	
		
		do { // traverse wall
			Robot currentPos = new Robot(pathTravelled.peek());
			
			if(rob.getX() == 12 && rob.getY() == 17) enteredGoal = true;
			if(rob.getX() == 0 && rob.getY() == 0 && enteredGoal) completed = true;
			currentPos = simulatorExplore2(map, rob, sleeptime);
			if (currentPos != null) {
				pathTravelled.push(currentPos);
			}
			else {
				currentPos = pathTravelled.pop();
			}
			
		} while(!pathTravelled.isEmpty() && !completed);

//		
//		Stack<Robot> pathTravelled = new Stack<Robot>();
//		pathTravelled.push(rob);
	}
	
	public void robotExplore(){
		
	}
	
	public Robot simulatorFirstRotation(MapGrid map, Robot rob, int sleeptime){
		// This method is a fake method to simulate the robot turning 360 degrees to check the distance from all sides
		switch (rob.getOrientation()) {
			case "N":
				rob.rotateRobot(map, rob, "W");
				break;
			case "W":
				rob.rotateRobot(map, rob, "S");
				break;
			case "S":
				rob.rotateRobot(map, rob, "E");
				break;
			case "E":
				rob.rotateRobot(map, rob, "N");
				break;
		}
		try {
			Thread.sleep(sleeptime);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rob;
	}
	
	public Robot simulatorFirstDirection(MapGrid map, Robot rob, int sleeptime){
	// This method calculates whether there are any obstacle in 3grids away from the robot and faces that direction
		
		int x = rob.getX();
		int y = rob.getY();
		
		int distanceToNorthObs = 0;
		int distanceToSouthObs = 0;
		int distanceToWestObs = 0;
		int distanceToEastObs = 0;
		
		// Check whether there is an obstacle 3 grids away on top (NORTH)
		if (map.grid[x-3][y].getBackground() == OBSTACLE || map.grid[x-3][y+1].getBackground() == OBSTACLE || map.grid[x-3][y+2].getBackground() == OBSTACLE) {
			distanceToNorthObs = 3;
		}
		if (map.grid[x-2][y].getBackground() == OBSTACLE || map.grid[x-2][y+1].getBackground() == OBSTACLE || map.grid[x-2][y+2].getBackground() == OBSTACLE) {
			distanceToNorthObs = 2;
		}
		if (map.grid[x-1][y].getBackground() == OBSTACLE || map.grid[x-1][y+1].getBackground() == OBSTACLE || map.grid[x-1][y+2].getBackground() == OBSTACLE) {
			distanceToNorthObs = 1;
		}
		
		
		// Check whether there is an obstacle 3 grids away on left (WEST)
		if (map.grid[x][y-3].getBackground() == OBSTACLE || map.grid[x+1][y-3].getBackground() == OBSTACLE || map.grid[x+2][y-3].getBackground() == OBSTACLE) {
			distanceToWestObs = 3;
		}
		if (map.grid[x][y-2].getBackground() == OBSTACLE || map.grid[x+1][y-2].getBackground() == OBSTACLE || map.grid[x+2][y-2].getBackground() == OBSTACLE) {
			distanceToWestObs = 2;
		}
		if (map.grid[x][y-1].getBackground() == OBSTACLE || map.grid[x+1][y-1].getBackground() == OBSTACLE || map.grid[x+2][y-1].getBackground() == OBSTACLE) {
			distanceToWestObs = 1;
		}
		
		
		
		// Check whether there is an obstacle 3 grids away on south (SOUTH)
		if (map.grid[x+5][y].getBackground() == OBSTACLE || map.grid[x+5][y+1].getBackground() == OBSTACLE || map.grid[x+5][y+2].getBackground() == OBSTACLE) {
			distanceToSouthObs = 3;
		}
		if (map.grid[x+4][y].getBackground() == OBSTACLE || map.grid[x+4][y+1].getBackground() == OBSTACLE || map.grid[x+4][y+2].getBackground() == OBSTACLE) {
			distanceToSouthObs = 2;
		}
		if (map.grid[x+3][y].getBackground() == OBSTACLE || map.grid[x+3][y+1].getBackground() == OBSTACLE || map.grid[x+3][y+2].getBackground() == OBSTACLE) {
			distanceToSouthObs = 1;
		}
		
		
		// Check whether there is an obstacle 3 grids away on right	(EAST)
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
		System.out.format ("The direction is: %s%n", rob.getOrientation());

		// To detect the furthest obs and face that direction
		if (distanceToNorthObs>distanceToSouthObs && distanceToNorthObs>distanceToWestObs && distanceToNorthObs>distanceToEastObs){
			rob.rotateRobot(map, rob, "N"); 
		}
		else { 
			if (distanceToEastObs>distanceToSouthObs && distanceToEastObs>distanceToWestObs && distanceToEastObs>distanceToNorthObs){
				rob.rotateRobot(map, rob, "E");
			}
			else {
				if (distanceToSouthObs>distanceToNorthObs && distanceToSouthObs>distanceToWestObs && distanceToSouthObs>distanceToEastObs){
					rob.rotateRobot(map, rob, "S");
				}
				else {
					if (distanceToWestObs>distanceToNorthObs && distanceToWestObs>distanceToSouthObs && distanceToWestObs>distanceToEastObs){
						rob.rotateRobot(map, rob, "W");
					}
				}
			}
		}
		
		try {
			Thread.sleep(sleeptime);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rob;
		
	}
	
	public Robot simulatorExplore(MapGrid map, Robot rob, int sleeptime){
		int x = rob.getX();
		int y = rob.getY();

		int LeftSideDistance = 0;
		int RightSideDistance = 20;
		int UpSideDistance = 0;
		int DownSideDistance = 15;
		//int count = 0;
		//do {
			switch (rob.getOrientation()) {
				case "N": 
					x = rob.getX();
		    		y = rob.getY();
					LeftSideDistance = (y+1) - 0;
					RightSideDistance = 20 - (y+1);
					if (((x-1)!=TopWall) && (map.grid[x-1][y].getBackground() != OBSTACLE && map.grid[x-1][y+1].getBackground() != OBSTACLE && map.grid[x-1][y+2].getBackground() != OBSTACLE)) {
						rob.moveRobot(map, rob, 1);
					}
					else if (((x-1)!=TopWall) && (map.grid[x-1][y].getBackground() == OBSTACLE || map.grid[x-1][y+1].getBackground() == OBSTACLE || map.grid[x-1][y+2].getBackground() == OBSTACLE)) {
						if (LeftSideDistance >= RightSideDistance) {
							rob.rotateRobot(map, rob, "W");
							break;
						}
						else {
							rob.rotateRobot(map, rob, "E");
							break;
						}
					}
					else if (((x-1)==TopWall)) {
						rob.rotateRobot(map, rob, "W");
					}
					x = rob.getX();
		    		y = rob.getY();
					break;
					
					/* OLD WORKING CODE BELOW
					while ((x-1)!=TopWall) { // heading up north
						// if there's obstacle..
						x = rob.getX();
			    		y = rob.getY();
						LeftSideDistance = (y+1) - 0;
						RightSideDistance = 20 - (y+1);
						if(map.grid[x-1][y].getBackground() == OBSTACLE || map.grid[x-1][y+1].getBackground() == OBSTACLE || map.grid[x-1][y+2].getBackground() == OBSTACLE) {
							if (LeftSideDistance >= RightSideDistance) {
								rob.rotateRobot(map, rob, "W");
							}
							else {
								rob.rotateRobot(map, rob, "E");
							}
							x = rob.getX();
				    		y = rob.getY();
			    			break;
			    		}
			            else{ // else continue moving
			            	rob.moveRobot(map, rob, 1);
			            }
						x = rob.getX();
			    		y = rob.getY();
					}
					x = rob.getX();
		    		y = rob.getY();
		    		System.out.println();
		    		System.out.format ("The left distance is: %d%n", LeftSideDistance);
		    		System.out.format ("The right distance is: %d%n", RightSideDistance);
					break;
					*/
					
				case "S":
					x = rob.getX();
		    		y = rob.getY();
					LeftSideDistance = (y+1) - 0;
					RightSideDistance = 20 - (y+1);
					if (((x+3)!=BottomWall) && (map.grid[x+3][y].getBackground() != OBSTACLE && map.grid[x+3][y+1].getBackground() != OBSTACLE && map.grid[x+3][y+2].getBackground() != OBSTACLE)) {
						rob.moveRobot(map, rob, 1);
					}
					else if (((x+3)!=BottomWall) && (map.grid[x+3][y].getBackground() == OBSTACLE || map.grid[x+3][y+1].getBackground() == OBSTACLE || map.grid[x+3][y+2].getBackground() == OBSTACLE)) {
						if (LeftSideDistance >= RightSideDistance) {
							rob.rotateRobot(map, rob, "W");
						}
						else {
							rob.rotateRobot(map, rob, "E");
						}
					}
					else if (((x+3)==BottomWall)) {
						rob.rotateRobot(map, rob, "E");
					}
					x = rob.getX();
		    		y = rob.getY();
					break;
					
					/* OLD WORKING CODE BELOW
					while ((x+3)!=BottomWall) { // heading south
						// if there's obstacle, ...
						x = rob.getX();
			    		y = rob.getY();
						LeftSideDistance = (y+1) - 0;
						RightSideDistance = 20 - (y+1);
						if (map.grid[x+3][y].getBackground() == OBSTACLE || map.grid[x+3][y+1].getBackground() == OBSTACLE || map.grid[x+3][y+2].getBackground() == OBSTACLE) {
							if (LeftSideDistance >= RightSideDistance) {
								rob.rotateRobot(map, rob, "W");
							}
							else {
								rob.rotateRobot(map, rob, "E");
							}
							x = rob.getX();
				    		y = rob.getY();
			    			break;
			    		}
			            else{ // else continue moving
			            	rob.moveRobot(map, rob, 1);
			            }
						x = rob.getX();
			    		y = rob.getY();
					}
					x = rob.getX();
		    		y = rob.getY();
					break;
					*/
					
					
				case "E":
					x = rob.getX();
		    		y = rob.getY();
					UpSideDistance = x - 0;
					DownSideDistance = 15 - x;
					if (((y+3)!=RightWall) && (map.grid[x][y+3].getBackground() != OBSTACLE && map.grid[x+1][y+3].getBackground() != OBSTACLE && map.grid[x+2][y+3].getBackground() != OBSTACLE)) {
						rob.moveRobot(map, rob, 1);
					}
					else if (((y+3)!=RightWall) && (map.grid[x][y+3].getBackground() == OBSTACLE || map.grid[x+1][y+3].getBackground() == OBSTACLE || map.grid[x+2][y+3].getBackground() == OBSTACLE)) {
						if (UpSideDistance >= DownSideDistance) {
							rob.rotateRobot(map, rob, "N");
						}
						else {
							rob.rotateRobot(map, rob, "S");
						}
					}
					else if (((y+3)==RightWall)) {
						rob.rotateRobot(map, rob, "N");
					}
					x = rob.getX();
		    		y = rob.getY();
					break;
					
					/* OLD WORKING CODE BELOW
					while ((y+3)!=RightWall) { // heading east
						// if there's obstacle, ...
						x = rob.getX();
			    		y = rob.getY();
						UpSideDistance = x - 0;
						DownSideDistance = 15 - x;
						if (map.grid[x][y+3].getBackground() == OBSTACLE || map.grid[x+1][y+3].getBackground() == OBSTACLE || map.grid[x+2][y+3].getBackground() == OBSTACLE) {
							if (UpSideDistance >= DownSideDistance) {
								rob.rotateRobot(map, rob, "N");
							}
							else {
								rob.rotateRobot(map, rob, "S");
							}
							x = rob.getX();
				    		y = rob.getY();
			    			break;
			    		}
			            else{ // else continue moving
			            	rob.moveRobot(map, rob, 1);
			            }
						x = rob.getX();
			    		y = rob.getY();
					}
					x = rob.getX();
		    		y = rob.getY();
					break;
					*/
					
				case "W":
					x = rob.getX();
		    		y = rob.getY();
					UpSideDistance = x - 0;
					DownSideDistance = 15 - x;
					if (((y-1)!=LeftWall) && (map.grid[x][y-1].getBackground() != OBSTACLE && map.grid[x+1][y-1].getBackground() != OBSTACLE && map.grid[x+2][y-1].getBackground() != OBSTACLE)) {
						rob.moveRobot(map, rob, 1);
					}
					else if (((y-1)!=LeftWall) && (map.grid[x][y-1].getBackground() == OBSTACLE || map.grid[x+1][y-1].getBackground() == OBSTACLE || map.grid[x+2][y-1].getBackground() == OBSTACLE)) {
						if (UpSideDistance >= DownSideDistance) {
							rob.rotateRobot(map, rob, "N");
						}
						else {
							rob.rotateRobot(map, rob, "S");
						}
					}
					else if (((y-1)==LeftWall)) {
						rob.rotateRobot(map, rob, "S");
					}
					x = rob.getX();
		    		y = rob.getY();
					break;
					
					/* OLD WORKING CODE BELOW
					while ((y-1)!=LeftWall) { // heading west
						// if there's obstacle, ...
						x = rob.getX();
			    		y = rob.getY();
						UpSideDistance = x - 0;
						DownSideDistance = 15 - x;
						if (map.grid[x][y-1].getBackground() == OBSTACLE || map.grid[x+1][y-1].getBackground() == OBSTACLE || map.grid[x+2][y-1].getBackground() == OBSTACLE) {
							if (UpSideDistance >= DownSideDistance) {
								rob.rotateRobot(map, rob, "N");
							}
							else {
								rob.rotateRobot(map, rob, "S");
							}
							x = rob.getX();
				    		y = rob.getY();
			    			break;
			    		}
			            else{ // else continue moving
			            	rob.moveRobot(map, rob, 1);
			            }
						x = rob.getX();
			    		y = rob.getY();
					}
					x = rob.getX();
		    		y = rob.getY();
					break;	
					*/
			}
			
			//count++;
		 //while(count<20);
			
		try {
			Thread.sleep(sleeptime);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rob;	
	}

	
	
	
	
	
	
	
	
	public Robot simulatorExplore2(MapGrid map, Robot rob, int sleeptime){
		int x = rob.getX();
		int y = rob.getY();
		int steps = 0;

		switch (rob.getOrientation()) {
			case "N": 
				steps = 0;
				// If robot reaches TOP RIGHT corner wall
				if (((x-1)==TopWall) && ((y+3)==RightWall)) {
					rob.rotateRobot(map, rob, "W");
				}
				// If robot reaches TOP WALL but somewhere in the middle
				else if ((x-1)==TopWall && ((y+3)!=LeftWall)) {
					rob.rotateRobot(map, rob, "W");
				}
				// Going NORTH on the RIGHT WALL, but in front of robot got obstacle, then traverse obs
				else if (((y+3)==RightWall) && (map.grid[x-1][y].getBackground() == OBSTACLE || map.grid[x-1][y+1].getBackground() == OBSTACLE || map.grid[x-1][y+2].getBackground() == OBSTACLE)) {
					rob.rotateRobot(map, rob, "W");
					do {
						rob.moveRobot(map, rob, 1);
						x = rob.getX();
			    		y = rob.getY();
						steps++;
					} while ((map.grid[x-1][y].getBackground() == OBSTACLE) || (map.grid[x-1][y+1].getBackground() == OBSTACLE) || (map.grid[x-1][y+2].getBackground() == OBSTACLE));
					
					rob.rotateRobot(map, rob, "N");
					do {
						rob.moveRobot(map, rob, 1);
						x = rob.getX();
			    		y = rob.getY();
					} while ((map.grid[x][y+3].getBackground() == OBSTACLE || map.grid[x+1][y+3].getBackground() == OBSTACLE || map.grid[x+2][y+3].getBackground() == OBSTACLE));
					
					rob.rotateRobot(map, rob, "E");
					for (int i=0; i<steps;i++) {
						rob.moveRobot(map, rob, 1);
					}
					rob.rotateRobot(map, rob, "N");		
				}		
				else if (((y+3)==RightWall) && (map.grid[x-1][y].getBackground() != OBSTACLE || map.grid[x-1][y+1].getBackground() != OBSTACLE || map.grid[x-1][y+2].getBackground() != OBSTACLE)) {
					rob.moveRobot(map, rob, 1);
					rob.rotateRobot(map, rob, "N");
				}
				else {
					rob.rotateRobot(map, rob, "N");
				}
				x = rob.getX();
	    		y = rob.getY();
				break;
				
				
			case "S":
				steps = 0;
				// If robot reaches BOTTOM LEFT corner wall
				if (((x+3)==BottomWall) && ((y-1)==LeftWall)) {
					rob.rotateRobot(map, rob, "E");
				}
				// If robot reaches BOTTOM WALL but somewhere in the middle
				else if (((x+3)==BottomWall) && ((y-1)!=RightWall)) {
					rob.rotateRobot(map, rob, "E");
				}
				// Going SOUTH on the LEFT WALL, but in front of robot got obstacle, then traverse obs
				else if (((y-1)==LeftWall) && (map.grid[x+3][y].getBackground() == OBSTACLE || map.grid[x+3][y+1].getBackground() == OBSTACLE || map.grid[x+3][y+2].getBackground() == OBSTACLE)) {
					rob.rotateRobot(map, rob, "E");
					do {
						rob.moveRobot(map, rob, 1);
						x = rob.getX();
			    		y = rob.getY();
						steps++;
					} while ((map.grid[x+3][y].getBackground() == OBSTACLE) || (map.grid[x+3][y+1].getBackground() == OBSTACLE) || (map.grid[x+3][y+2].getBackground() == OBSTACLE));
					
					rob.rotateRobot(map, rob, "S");
					do {
						rob.moveRobot(map, rob, 1);
						x = rob.getX();
			    		y = rob.getY();
					} while ((map.grid[x][y-1].getBackground() == OBSTACLE || map.grid[x+1][y-1].getBackground() == OBSTACLE || map.grid[x+2][y-1].getBackground() == OBSTACLE));
					
					rob.rotateRobot(map, rob, "W");
					for (int i=0; i<steps;i++) {
						rob.moveRobot(map, rob, 1);
					}
					rob.rotateRobot(map, rob, "S");		
				}		
				else if (((y-1)==LeftWall) && (map.grid[x+3][y].getBackground() != OBSTACLE || map.grid[x+3][y+1].getBackground() != OBSTACLE || map.grid[x+3][y+2].getBackground() != OBSTACLE)) {
					rob.moveRobot(map, rob, 1);
					rob.rotateRobot(map, rob, "S");
				}
				else {
					rob.rotateRobot(map, rob, "S");
				}
				x = rob.getX();
	    		y = rob.getY();
				break;
				
			case "E":
				steps = 0;
				// If robot reaches BOTTOM RIGHT corner wall
				if (((y+3)==RightWall) && ((x+3)==BottomWall) ) {
					rob.rotateRobot(map, rob, "N");
				}
				// If robot reaches RIGHT WALL but somewhere in the middle
				else if (((y+3)==RightWall) && ((x-1)!=TopWall) ) {
					rob.rotateRobot(map, rob, "N");
				}
				// Going EAST on the BOTTOM WALL, but in front of robot got obstacle, then traverse obs	
				else if (((x+3)==BottomWall) && (map.grid[x][y+3].getBackground() == OBSTACLE || map.grid[x+1][y+3].getBackground() == OBSTACLE || map.grid[x+2][y+3].getBackground() == OBSTACLE)) {
					rob.rotateRobot(map, rob, "N");
					do {
						rob.moveRobot(map, rob, 1);
						x = rob.getX();
			    		y = rob.getY();
						steps++;
					} while ((map.grid[x][y+3].getBackground() == OBSTACLE) || (map.grid[x+1][y+3].getBackground() == OBSTACLE) || (map.grid[x+2][y+3].getBackground() == OBSTACLE));
					
					rob.rotateRobot(map, rob, "E");
					do {
						rob.moveRobot(map, rob, 1);
						x = rob.getX();
			    		y = rob.getY();
					} while ((map.grid[x+3][y].getBackground() == OBSTACLE || map.grid[x+3][y+1].getBackground() == OBSTACLE || map.grid[x+3][y+2].getBackground() == OBSTACLE));
					
					rob.rotateRobot(map, rob, "S");
					for (int i=0; i<steps;i++) {
						rob.moveRobot(map, rob, 1);
					}
					rob.rotateRobot(map, rob, "E");		
				}		
				else if (((x+3)==BottomWall) && (map.grid[x][y+3].getBackground() != OBSTACLE || map.grid[x+1][y+3].getBackground() != OBSTACLE || map.grid[x+2][y+3].getBackground() != OBSTACLE)) {
					rob.moveRobot(map, rob, 1);
					rob.rotateRobot(map, rob, "E");
				}
				else {
					rob.rotateRobot(map, rob, "E");
				}
				x = rob.getX();
	    		y = rob.getY();
				break;
				
			case "W":
				steps = 0;
				// If robot reaches TOP LEFT corner wall
				if (((y-1)==LeftWall) && ((x-1)==TopWall)) {
					rob.rotateRobot(map, rob, "S");
				}
				// If robot reaches LEFT WALL but somewhere in the middle
				else if (((y-1)==LeftWall) && ((x+3)!=BottomWall)) {
					rob.rotateRobot(map, rob, "S");
				}
				// Going WEST on the TOP WALL, but in front of robot got obstacle, then traverse obs				
				else if (((x-1)==TopWall) && (map.grid[x][y-1].getBackground() == OBSTACLE || map.grid[x+1][y-1].getBackground() == OBSTACLE || map.grid[x+2][y-1].getBackground() == OBSTACLE)) {
					rob.rotateRobot(map, rob, "S");
					do {
						rob.moveRobot(map, rob, 1);
						x = rob.getX();
			    		y = rob.getY();
						steps++;
					} while ((map.grid[x][y-1].getBackground() == OBSTACLE) || (map.grid[x+1][y-1].getBackground() == OBSTACLE) || (map.grid[x+2][y-1].getBackground() == OBSTACLE));
					
					rob.rotateRobot(map, rob, "W");
					do {
						rob.moveRobot(map, rob, 1);
						x = rob.getX();
			    		y = rob.getY();
					} while ((map.grid[x-1][y].getBackground() == OBSTACLE || map.grid[x-1][y+1].getBackground() == OBSTACLE || map.grid[x-1][y+2].getBackground() == OBSTACLE));
					
					rob.rotateRobot(map, rob, "N");
					for (int i=0; i<steps;i++) {
						rob.moveRobot(map, rob, 1);
					}
					rob.rotateRobot(map, rob, "W");		
				}		
				else if (((x-1)==TopWall) && (map.grid[x][y-1].getBackground() != OBSTACLE || map.grid[x+1][y-1].getBackground() != OBSTACLE || map.grid[x+2][y-1].getBackground() != OBSTACLE)) {
					rob.moveRobot(map, rob, 1);
					rob.rotateRobot(map, rob, "W");
				}
				
				else {
					rob.rotateRobot(map, rob, "W");
				}
				x = rob.getX();
	    		y = rob.getY();
				break;		
			}
		
		try {
			Thread.sleep(sleeptime);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rob;
	}
	
}
