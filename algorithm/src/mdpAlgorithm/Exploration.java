package mdpAlgorithm;

import java.awt.Color;
import java.util.Stack;

public class Exploration implements Runnable {
	private static final Color OBSTACLE = Color.RED;
	private static final Color EXPLORED = new Color(0, 128, 255);
	private static final int TopWall = -1;
	private static final int LeftWall = -1;
	private static final int BottomWall = 15;
	private static final int RightWall = 20;
	public Stack<Robot> pathTravelled;
	public MapGrid map;
	public Robot rob;
	private int sleeptime;
	private double percentage;
	private boolean traversing = false;
	private boolean backToPosition = false;
	
	public Exploration(MapGrid map, Robot rob, int sleeptime, double percentage) {
		this.rob = rob;
		this.map = map;
		this.sleeptime = sleeptime;
		this.percentage = percentage;
	}
	
	@Override
	public void run() {

		boolean reachedWall = false;
		boolean completed = false;
		boolean enteredGoal = false;
		boolean enteredStart = false;
		
		pathTravelled = new Stack<Robot>();
		pathTravelled.push(rob);
		
		int rotationCount = 0;
		int alignmentCount = 0;
		int faceFirstDir = 0;
		
		Robot firstDelay = new Robot(pathTravelled.peek());
		firstDelay(map, rob, sleeptime); //rotate on the spot
		if (firstDelay != null) {
			pathTravelled.push(firstDelay);
		}
		
		do { // Fake 360 degree rotation to check distance
			Robot currentDir = new Robot(pathTravelled.peek());
			simulatorFirstRotation(map, rob, sleeptime); //rotate on the spot
			if (currentDir != null) {
				pathTravelled.push(currentDir);
			}
			rotationCount++;
			checkCompleted(map, percentage);
		} while (rotationCount <4); //rotate on the spot 4 times
		
		/*
		do { // First direction towards the nearest obstacle
			Robot alignmentDir = new Robot(pathTravelled.peek());
			goToNearestObstacle(map, rob, sleeptime); //rotate on the spot
			if (alignmentDir != null) {
				pathTravelled.push(alignmentDir);
			}
			//if (rob.getX()==6 && rob.getY()==8) backToPosition = true;
			checkCompleted(map, percentage);
		} while (!backToPosition); //rotate on the spot 4 times
		*/
		
		do { // First direction towards the furthest obstacle
			Robot goingDir = new Robot(pathTravelled.peek());
			goToFurthestObstacle(map, rob, sleeptime); //Face first direction
			if (goingDir != null) {
				pathTravelled.push(goingDir);
			}
			faceFirstDir++;
			checkCompleted(map, percentage);
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
			checkCompleted(map, percentage);
		} while(!pathTravelled.isEmpty() && !reachedWall);
	
		
		do { // traverse wall
			Robot currentPos = new Robot(pathTravelled.peek());
			
			
			if(rob.getX() == 0 && rob.getY() == 0 && !enteredGoal) enteredStart = true;
			else if(rob.getX() == 0 && rob.getY() == 0 && enteredGoal) completed = true;
			if(rob.getX() == 12 && rob.getY() == 17 && enteredStart) enteredGoal = true;
			
			currentPos = simulatorExplore2(map, rob, sleeptime);
			if (currentPos != null) {
				pathTravelled.push(currentPos);
			}
			else {
				currentPos = pathTravelled.pop();
			}
			checkCompleted(map, percentage);
		} while(!pathTravelled.isEmpty() && !completed);

//		
//		Stack<Robot> pathTravelled = new Stack<Robot>();
//		pathTravelled.push(rob);
	}
	
	public void checkCompleted(MapGrid map, double percentage) {
		int num = 0;
		for (int i = 0; i < 15; i++) {
			for (int j = 0; j < 20; j++) {
				if(map.grid[i][j].getBackground().equals(EXPLORED)) num++;
			}
		}

		if(num >= (percentage*300)-9 && percentage < 1.0) {
			Thread.currentThread().stop();
		}
	}
	
	public Robot firstDelay(MapGrid map, Robot rob, int sleeptime) {
		rob.moveRobot(map, 0);
		try {
			Thread.sleep(sleeptime);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rob;
	}
	
	public Robot simulatorFirstRotation(MapGrid map, Robot rob, int sleeptime){
		// This method is a fake method to simulate the robot turning 360 degrees to check the distance from all sides
		switch (rob.getOrientation()) {
			case "N":
				rob.rotateRobot(map, "W");
				break;
			case "W":
				rob.rotateRobot(map, "S");
				break;
			case "S":
				rob.rotateRobot(map, "E");
				break;
			case "E":
				rob.rotateRobot(map, "N");
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
	
	/*
	public Robot goToNearestObstacle(MapGrid map, Robot rob, int sleeptime){
		// This method is find the nearest obs to do auto fix with the wall
		
		int x = rob.getX();
		int y = rob.getY();
		
		int distanceToNorthObs = -1;
		int distanceToSouthObs = -1;
		int distanceToWestObs = -1;
		int distanceToEastObs = -1;
		
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
		
		
		//To check where is the nearest obstacle
		if (distanceToNorthObs == 1)
			rob.rotateRobot(map, "N"); 
		
		else if (distanceToNorthObs == -1) {
			if (distanceToWestObs == 1)
				rob.rotateRobot(map, "W"); 
			else {
				if (distanceToSouthObs == 1)
					rob.rotateRobot(map, "S");
				else {
					rob.rotateRobot(map, "E");
				}
			}
		}
			
		else { // distanceToNorthObs == 2 or 3
			if (distanceToWestObs < distanceToNorthObs)
				rob.rotateRobot(map, "W");
			else if (distanceToSouthObs < distanceToWestObs)
				rob.rotateRobot(map, "S");
			else if (distanceToEastObs < distanceToSouthObs)
				rob.rotateRobot(map, "E");
		}		
		
		//for debugging		
		System.out.format ("The north distance is: %d%n", distanceToNorthObs);
		System.out.format ("The south distance is: %d%n", distanceToSouthObs);
		System.out.format ("The west distance is: %d%n", distanceToWestObs);
		System.out.format ("The east distance is: %d%n", distanceToEastObs);
		System.out.format ("The direction is: %s%n", rob.getOrientation());	

		int i = 1;
		int j = 1;
				
		switch (rob.getOrientation()) {
			case "N": 
				x = rob.getX();
				y = rob.getY();
				
				if (i<distanceToNorthObs) {
					rob.moveRobot(map, 1);
					i++;
				}
				
				else if (rob.getOrientation()=="N") {rob.rotateRobot(map, "W");}
				
				else if (rob.getOrientation()=="W") {rob.rotateRobot(map, "S");}

				else if ((rob.getOrientation()=="S") && (j<i)) {
					rob.moveRobot(map, 1);
					j++;
					if (j==i) {backToPosition = true;}		
				}
				
				
				break;
				
			case "W": 
				x = rob.getX();
				y = rob.getY();
				
				if (i<distanceToWestObs) {
					rob.moveRobot(map, 1);
					i++;
				}
				
				else if (rob.getOrientation()=="W") {rob.rotateRobot(map, "S");}
				
				else if (rob.getOrientation()=="S") {rob.rotateRobot(map, "E");}

				else if ((rob.getOrientation()=="E") && (j<i)) {
					rob.moveRobot(map, 1);
					j++;
					if (j==i) {backToPosition = true;}		
				}
				break;
			
			case "S": 
				x = rob.getX();
				y = rob.getY();
				
				if (i<distanceToSouthObs) {
					rob.moveRobot(map, 1);
					i++;
				}
				
				else if (rob.getOrientation()=="S") {rob.rotateRobot(map, "E");}
				
				else if (rob.getOrientation()=="E") {rob.rotateRobot(map, "N");}

				else if ((rob.getOrientation()=="N") && (j<i)) {
					rob.moveRobot(map, 1);
					j++;
					if (j==i) {backToPosition = true;}		
				}
				break;
				
			case "E": 
				x = rob.getX();
				y = rob.getY();
				
				if (i<distanceToEastObs) {
					rob.moveRobot(map, 1);
					i++;
				}
				
				else if (rob.getOrientation()=="E") {rob.rotateRobot(map, "N");}
				
				else if (rob.getOrientation()=="N") {rob.rotateRobot(map, "W");}

				else if ((rob.getOrientation()=="W") && (j<i)) {
					rob.moveRobot(map, 1);
					j++;
					if (j==i) {backToPosition = true;}		
				}
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
	*/
	
	public Robot goToFurthestObstacle(MapGrid map, Robot rob, int sleeptime){
	// This method calculates whether there are any obstacle in 3grids away from the robot and faces that direction
		
		int x = rob.getX();
		int y = rob.getY();
		
		int distanceToNorthObs = -1;
		int distanceToSouthObs = -1;
		int distanceToWestObs = -1;
		int distanceToEastObs = -1;
		
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
		
			
		
		if (distanceToNorthObs>distanceToSouthObs && distanceToNorthObs>distanceToWestObs && distanceToNorthObs>distanceToEastObs){
			rob.rotateRobot(map, "N"); 
		}
		else { 
			if (distanceToEastObs>distanceToSouthObs && distanceToEastObs>distanceToWestObs && distanceToEastObs>distanceToNorthObs){
				rob.rotateRobot(map, "E");
			}
			else {
				if (distanceToSouthObs>distanceToNorthObs && distanceToSouthObs>distanceToWestObs && distanceToSouthObs>distanceToEastObs){
					rob.rotateRobot(map, "S");
				}
				else {
					if (distanceToWestObs>distanceToNorthObs && distanceToWestObs>distanceToSouthObs && distanceToWestObs>distanceToEastObs){
						rob.rotateRobot(map, "W");
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
		
		boolean blockGoingUp = false;
		boolean blockGoingDown = false;
		boolean blockGoingLeft = false;
		boolean blockGoingRight = false;
		
		if ((x-1)==TopWall) blockGoingUp = true;
		if ((x+3)==BottomWall) blockGoingDown = true;
		if ((y-1)==LeftWall) blockGoingLeft = true;
		if ((y+3)==RightWall) blockGoingRight = true;

			switch (rob.getOrientation()) {
				case "N": 
					x = rob.getX();
		    		y = rob.getY();
					LeftSideDistance = (y+1) - 0;
					RightSideDistance = 20 - (y+1);
					
					if ((!blockGoingUp) && (map.grid[x-1][y].getBackground() != OBSTACLE && map.grid[x-1][y+1].getBackground() != OBSTACLE && map.grid[x-1][y+2].getBackground() != OBSTACLE)) {
						rob.moveRobot(map, 1);
					}
					else if ((!blockGoingUp) && (map.grid[x-1][y].getBackground() == OBSTACLE || map.grid[x-1][y+1].getBackground() == OBSTACLE || map.grid[x-1][y+2].getBackground() == OBSTACLE)) {
						if (LeftSideDistance >= RightSideDistance) {
							rob.rotateRobot(map, "W");
							break;
						}
						else {
							rob.rotateRobot(map, "E");
							break;
						}
					}
					else if (blockGoingUp) {
						rob.rotateRobot(map, "W");
					}
					x = rob.getX();
		    		y = rob.getY();
					break;
					
				case "S":
					x = rob.getX();
		    		y = rob.getY();
					LeftSideDistance = (y+1) - 0;
					RightSideDistance = 20 - (y+1);
					
					if ((!blockGoingDown) && (map.grid[x+3][y].getBackground() != OBSTACLE && map.grid[x+3][y+1].getBackground() != OBSTACLE && map.grid[x+3][y+2].getBackground() != OBSTACLE)) {
						rob.moveRobot(map, 1);
					}
					else if ((!blockGoingDown) && (map.grid[x+3][y].getBackground() == OBSTACLE || map.grid[x+3][y+1].getBackground() == OBSTACLE || map.grid[x+3][y+2].getBackground() == OBSTACLE)) {
						if (LeftSideDistance >= RightSideDistance) {
							rob.rotateRobot(map, "W");
						}
						else {
							rob.rotateRobot(map, "E");
						}
					}
					else if (blockGoingDown) {
						rob.rotateRobot(map, "E");
					}
					x = rob.getX();
		    		y = rob.getY();
					break;
					
					
				case "E":
					x = rob.getX();
		    		y = rob.getY();
					UpSideDistance = x - 0;
					DownSideDistance = 15 - x;
					
					if ((!blockGoingRight) && (map.grid[x][y+3].getBackground() != OBSTACLE && map.grid[x+1][y+3].getBackground() != OBSTACLE && map.grid[x+2][y+3].getBackground() != OBSTACLE)) {
						rob.moveRobot(map, 1);
					}
					else if ((!blockGoingRight) && (map.grid[x][y+3].getBackground() == OBSTACLE || map.grid[x+1][y+3].getBackground() == OBSTACLE || map.grid[x+2][y+3].getBackground() == OBSTACLE)) {
						if (UpSideDistance >= DownSideDistance) {
							rob.rotateRobot(map, "N");
						}
						else {
							rob.rotateRobot(map, "S");
						}
					}
					else if (blockGoingRight) {
						rob.rotateRobot(map, "N");
					}
					x = rob.getX();
		    		y = rob.getY();
					break;
					
					
				case "W":
					x = rob.getX();
		    		y = rob.getY();
					UpSideDistance = x - 0;
					DownSideDistance = 15 - x;
					
					if ((!blockGoingLeft) && (map.grid[x][y-1].getBackground() != OBSTACLE && map.grid[x+1][y-1].getBackground() != OBSTACLE && map.grid[x+2][y-1].getBackground() != OBSTACLE)) {
						rob.moveRobot(map, 1);
					}
					else if ((!blockGoingLeft) && (map.grid[x][y-1].getBackground() == OBSTACLE || map.grid[x+1][y-1].getBackground() == OBSTACLE || map.grid[x+2][y-1].getBackground() == OBSTACLE)) {
						if (UpSideDistance >= DownSideDistance) {
							rob.rotateRobot(map, "N");
						}
						else {
							rob.rotateRobot(map, "S");
						}
					}
					else if (blockGoingLeft) {
						rob.rotateRobot(map, "S");
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


	
	public Robot simulatorExplore2(MapGrid map, Robot rob, int sleeptime) {
		int x = rob.getX();
		int y = rob.getY();
		
		boolean blockGoingUp = false;
		boolean blockGoingDown = false;
		boolean blockGoingLeft = false;
		boolean blockGoingRight = false;
		
		if ((x-1)==TopWall) blockGoingUp = true;
		if ((x+3)==BottomWall) blockGoingDown = true;
		if ((y-1)==LeftWall) blockGoingLeft = true;
		if ((y+3)==RightWall) blockGoingRight = true;
		
		switch (rob.getOrientation()) {
		
			case "N": 
				// If robot reaches TOP RIGHT corner wall
				if (blockGoingUp && blockGoingRight) {
					rob.rotateRobot(map, "W");
				}
				// If robot reaches TOP WALL but somewhere in the middle
				else if (blockGoingUp && !blockGoingLeft) {
					rob.rotateRobot(map, "W");
				}
				// Going NORTH on the RIGHT WALL, but in front of robot got obstacle, then traverse obs
				else if (blockGoingRight && (map.grid[x-1][y].getBackground() == OBSTACLE || map.grid[x-1][y+1].getBackground() == OBSTACLE || map.grid[x-1][y+2].getBackground() == OBSTACLE)) {
					rob.rotateRobot(map, "W");
					traversing = true;
					
					do {
						obstacleFrontNorth(map, rob, sleeptime);
					} while (traversing);
				}		
				
				else if (blockGoingRight && (map.grid[x-1][y].getBackground() != OBSTACLE || map.grid[x-1][y+1].getBackground() != OBSTACLE || map.grid[x-1][y+2].getBackground() != OBSTACLE)) {
					rob.moveRobot(map, 1);
					rob.rotateRobot(map, "N");
				}
				else {
					rob.rotateRobot(map, "N");
				}
				x = rob.getX();
	    		y = rob.getY();
				break;
				
				
			case "S":
				// If robot reaches BOTTOM LEFT corner wall
				if (blockGoingDown && blockGoingLeft) {
					rob.rotateRobot(map, "E");
				}
				// If robot reaches BOTTOM WALL but somewhere in the middle
				else if (blockGoingDown && !blockGoingRight) {
					rob.rotateRobot(map, "E");
				}
				// Going SOUTH on the LEFT WALL, but in front of robot got obstacle, then traverse obs
				else if (blockGoingLeft && (map.grid[x+3][y].getBackground() == OBSTACLE || map.grid[x+3][y+1].getBackground() == OBSTACLE || map.grid[x+3][y+2].getBackground() == OBSTACLE)) {
					rob.rotateRobot(map, "E");
					traversing = true;
					
					do {
						obstacleFrontSouth(map, rob, sleeptime);
					} while (traversing);
					
				}		
				else if (blockGoingLeft && (map.grid[x+3][y].getBackground() != OBSTACLE || map.grid[x+3][y+1].getBackground() != OBSTACLE || map.grid[x+3][y+2].getBackground() != OBSTACLE)) {
					rob.moveRobot(map, 1);
					rob.rotateRobot(map, "S");
				}
				else {
					rob.rotateRobot(map, "S");
				}
				x = rob.getX();
	    		y = rob.getY();
				break;
				
			case "E":
				// If robot reaches BOTTOM RIGHT corner wall
				if (blockGoingRight && blockGoingDown ) {
					rob.rotateRobot(map, "N");
				}
				// If robot reaches RIGHT WALL but somewhere in the middle
				else if (blockGoingRight && !blockGoingUp ) {
					rob.rotateRobot(map, "N");
				}
				// Going EAST on the BOTTOM WALL, but in front of robot got obstacle, then traverse obs	
				else if (blockGoingDown && (map.grid[x][y+3].getBackground() == OBSTACLE || map.grid[x+1][y+3].getBackground() == OBSTACLE || map.grid[x+2][y+3].getBackground() == OBSTACLE)) {
					rob.rotateRobot(map, "N");
					traversing = true;
					
					do {
						obstacleFrontEast(map, rob, sleeptime);
						//if ((rob.getX()+3)==BottomWall) break;
					} while (traversing);

				}		
				
				
				else if (blockGoingDown && (map.grid[x][y+3].getBackground() != OBSTACLE || map.grid[x+1][y+3].getBackground() != OBSTACLE || map.grid[x+2][y+3].getBackground() != OBSTACLE)) {
					rob.moveRobot(map, 1);
					rob.rotateRobot(map, "E");
				}
				else {
					rob.rotateRobot(map, "E");
				}
				x = rob.getX();
	    		y = rob.getY();
				break;
				
			case "W":
				// If robot reaches TOP LEFT corner wall
				if (blockGoingLeft && blockGoingUp) {
					rob.rotateRobot(map, "S");
				}
				// If robot reaches LEFT WALL but somewhere in the middle
				else if (blockGoingLeft && !blockGoingDown) {
					rob.rotateRobot(map, "S");
				}
				// Going WEST on the TOP WALL, but in front of robot got obstacle, then traverse obs				
				else if (blockGoingUp && (map.grid[x][y-1].getBackground() == OBSTACLE || map.grid[x+1][y-1].getBackground() == OBSTACLE || map.grid[x+2][y-1].getBackground() == OBSTACLE)) {
					rob.rotateRobot(map, "S");
					traversing = true;
					
					do {
						obstacleFrontWest(map, rob, sleeptime);
					} while (traversing);
					
				}		
				
				// going west but infront got no obstacle
				else if (blockGoingUp && (map.grid[x][y-1].getBackground() != OBSTACLE && map.grid[x+1][y-1].getBackground() != OBSTACLE && map.grid[x+2][y-1].getBackground() != OBSTACLE)) {
					rob.moveRobot(map, 1);
				}
				
				else {
					rob.rotateRobot(map, "W");
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
	

	
	public Robot obstacleFrontWest(MapGrid map, Robot rob, int sleeptime) {
		int x = rob.getX();
		int y = rob.getY();
		boolean reachedWall = false;
		
		boolean blockGoingUp = false;
		boolean blockGoingDown = false;
		boolean blockGoingLeft = false;
		boolean blockGoingRight = false;
		
		if ((x-1)==TopWall) blockGoingUp = true;
		if ((x+3)==BottomWall) blockGoingDown = true;
		if ((y-1)==LeftWall) blockGoingLeft = true;
		if ((y+3)==RightWall) blockGoingRight = true;
		
		// This is when traversing WEST and infront there's an obstacle
		switch (rob.getOrientation()) {
			case "N":

				if (x==0) {
					System.out.println("ReachTOP");
					reachedWall = true;
				}
				
				//ADD THIS OT OTHER PARTS
				else if ((map.grid[x-1][y].getBackground() == OBSTACLE || map.grid[x-1][y+1].getBackground() == OBSTACLE || map.grid[x-1][y+2].getBackground() == OBSTACLE)) {
					if (map.grid[x][y+3].getBackground() != OBSTACLE && map.grid[x+1][y+3].getBackground() != OBSTACLE && map.grid[x+2][y+3].getBackground() != OBSTACLE) {
						rob.rotateRobot(map, "E");
					}
					else if (map.grid[x-1][y].getBackground() == OBSTACLE || map.grid[x-1][y+1].getBackground() == OBSTACLE || map.grid[x-1][y+2].getBackground() == OBSTACLE) {
						rob.rotateRobot(map, "W");
					}
				}
				
				//ADD THIS OT OTHER PARTS
				else if ((map.grid[x-1][y].getBackground() != OBSTACLE && map.grid[x-1][y+1].getBackground() != OBSTACLE && map.grid[x-1][y+2].getBackground() != OBSTACLE)) {
					if(map.grid[x-1][y+3].getBackground() == OBSTACLE ) {
						rob.moveRobot(map, 1);
					}
					else if (map.grid[x][y+3].getBackground() != OBSTACLE && map.grid[x+1][y+3].getBackground() != OBSTACLE && map.grid[x+2][y+3].getBackground() != OBSTACLE) {
						rob.rotateRobot(map, "E");
					}
					else
						rob.moveRobot(map, 1);
						
					
				}
				
				//THIS PART THE ELSE IS CHANGED
				 if (reachedWall == true) {
					rob.rotateRobot(map, "W");
					traversing = false;
					break;
				 }

				break;	
				
			case "W":
							
				// If on the left got obs
				if (y-1 > 0) {
					if (map.grid[x-1][y-1].getBackground() != OBSTACLE && map.grid[x-1][y].getBackground() != OBSTACLE &&  map.grid[x-1][y+1].getBackground() != OBSTACLE && map.grid[x-1][y+2].getBackground() != OBSTACLE) {
						rob.rotateRobot(map, "N");
					}
					else if ((map.grid[x][y-1].getBackground() == OBSTACLE) || (map.grid[x+1][y-1].getBackground() == OBSTACLE) || (map.grid[x+2][y-1].getBackground() == OBSTACLE) )
						rob.rotateRobot(map, "S");
					
					// traverse wall using sensors on robots right
					else if (!blockGoingUp) {
						if (map.grid[x-1][y-1].getBackground() == OBSTACLE || map.grid[x-1][y].getBackground() == OBSTACLE || map.grid[x-1][y+1].getBackground() == OBSTACLE || map.grid[x-1][y+2].getBackground() == OBSTACLE) {
							rob.moveRobot(map, 1);
						}
						else if ((map.grid[x-1][y].getBackground() != OBSTACLE && map.grid[x-1][y+1].getBackground() != OBSTACLE && map.grid[x-1][y+2].getBackground() != OBSTACLE)){
							rob.rotateRobot(map, "N");
						}
					}
					else if (blockGoingUp) {
						if ((map.grid[x][y-1].getBackground() == OBSTACLE) || (map.grid[x+1][y-1].getBackground() == OBSTACLE) || (map.grid[x+2][y-1].getBackground() == OBSTACLE)) {
							return rob;
						}
						else if ((map.grid[x][y-1].getBackground() != OBSTACLE) && (map.grid[x+1][y-1].getBackground() != OBSTACLE) && (map.grid[x+2][y-1].getBackground() == OBSTACLE)) {
							rob.moveRobot(map, 1);
							rob.rotateRobot(map, "W");
						}
					}
				}
				else if(y-1 == 0) {
					if (map.grid[x-1][y-1].getBackground() == OBSTACLE || map.grid[x-1][y].getBackground() == OBSTACLE || map.grid[x-1][y+1].getBackground() == OBSTACLE || map.grid[x-1][y+2].getBackground() == OBSTACLE) {
						rob.moveRobot(map, 1);
					}
					else if ((map.grid[x-1][y].getBackground() != OBSTACLE && map.grid[x-1][y+1].getBackground() != OBSTACLE && map.grid[x-1][y+2].getBackground() != OBSTACLE)){
						rob.rotateRobot(map, "N");
					}
				}
				else if (y == 0) {
					// traverse wall using sensors on robots right
					if (!blockGoingUp) {
						if ((map.grid[x-1][y].getBackground() != OBSTACLE && map.grid[x-1][y+1].getBackground() != OBSTACLE && map.grid[x-1][y+2].getBackground() != OBSTACLE)){
							rob.rotateRobot(map, "N");
						}	
						
					}
					else if (blockGoingUp) {
						if ((map.grid[x][y-1].getBackground() == OBSTACLE) || (map.grid[x+1][y-1].getBackground() == OBSTACLE) || (map.grid[x+2][y-1].getBackground() == OBSTACLE)) {
							return rob;
						}
						else if ((map.grid[x][y-1].getBackground() != OBSTACLE) && (map.grid[x+1][y-1].getBackground() != OBSTACLE) && (map.grid[x+2][y-1].getBackground() != OBSTACLE)) {
							rob.moveRobot(map, 1);
							rob.rotateRobot(map, "W");
						}
					}
					
				}	
				break;

			case "S":
				if ((map.grid[x+3][y].getBackground() == OBSTACLE) || (map.grid[x+3][y+1].getBackground() == OBSTACLE) || (map.grid[x+3][y+2].getBackground() == OBSTACLE)) {
					if ((map.grid[x][y-1].getBackground() != OBSTACLE) && (map.grid[x+1][y-1].getBackground() != OBSTACLE) && (map.grid[x+2][y-1].getBackground() != OBSTACLE)) {
						rob.rotateRobot(map, "W");
					}
					else if ((map.grid[x][y-1].getBackground() == OBSTACLE) || (map.grid[x+1][y-1].getBackground() == OBSTACLE) || (map.grid[x+2][y-1].getBackground() == OBSTACLE) || (map.grid[x+3][y-1].getBackground() == OBSTACLE)) {
						rob.rotateRobot(map, "E");
					}
					
				}
				else if ((map.grid[x][y-1].getBackground() == OBSTACLE) || (map.grid[x+1][y-1].getBackground() == OBSTACLE) || (map.grid[x+2][y-1].getBackground() == OBSTACLE) || (map.grid[x+3][y-1].getBackground() == OBSTACLE)) {
					rob.moveRobot(map, 1);
				}
				else {
					rob.rotateRobot(map, "W");
				}
				break;
				
			case "E":
				x = rob.getX();
	    		y = rob.getY();
				if ((map.grid[x+3][y].getBackground() == OBSTACLE || map.grid[x+3][y+1].getBackground() == OBSTACLE || map.grid[x+3][y+2].getBackground() == OBSTACLE)){
					if ((map.grid[x][y+3].getBackground() == OBSTACLE || map.grid[x+1][y+3].getBackground() == OBSTACLE || map.grid[x+2][y+3].getBackground() == OBSTACLE)){
						rob.rotateRobot(map, "N");
					}
					if ((map.grid[x][y+3].getBackground() != OBSTACLE && map.grid[x+1][y+3].getBackground() != OBSTACLE && map.grid[x+2][y+3].getBackground() != OBSTACLE)){
						rob.moveRobot(map, 1);
					}
					
				}
				
				else if ((map.grid[x][y+3].getBackground() == OBSTACLE || map.grid[x+1][y+3].getBackground() == OBSTACLE || map.grid[x+2][y+3].getBackground() == OBSTACLE)){
					rob.rotateRobot(map, "N");
				}
				
				//ADD THIS TO OTHER PARTS
				
				
				else if (rob.getOrientation()=="E" && (map.grid[x][y+3].getBackground() != OBSTACLE && map.grid[x+1][y+3].getBackground() != OBSTACLE && map.grid[x+2][y+3].getBackground() != OBSTACLE)){
					if (map.grid[x+3][y+3].getBackground() == OBSTACLE) {
							rob.moveRobot(map, 1);
					}
					else rob.rotateRobot(map, "S");
				}

				else if ((map.grid[x+3][y].getBackground() != OBSTACLE && map.grid[x+3][y+1].getBackground() != OBSTACLE && map.grid[x+3][y+2].getBackground() != OBSTACLE)){
					rob.rotateRobot(map, "S");
				}
				
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
	
	
	public Robot obstacleFrontEast(MapGrid map, Robot rob, int sleeptime) {
		int x = rob.getX();
		int y = rob.getY();
		boolean reachedWall = false;
		
		boolean blockGoingUp = false;
		boolean blockGoingDown = false;
		boolean blockGoingLeft = false;
		boolean blockGoingRight = false;
		
		if ((x-1)==TopWall) blockGoingUp = true;
		if ((x+3)==BottomWall) blockGoingDown = true;
		if ((y-1)==LeftWall) blockGoingLeft = true;
		if ((y+3)==RightWall) blockGoingRight = true;
		
		// This is when traversing EAST and infront there's an obstacle
		switch (rob.getOrientation()) {
		
			case "S":
				if (blockGoingDown) { 
					reachedWall = true;
				}
				
				else if ((map.grid[x+3][y].getBackground() == OBSTACLE || map.grid[x+3][y+1].getBackground() == OBSTACLE || map.grid[x+3][y+2].getBackground() == OBSTACLE)) {
					if (map.grid[x-1][y].getBackground() != OBSTACLE && map.grid[x-1][y+1].getBackground() != OBSTACLE && map.grid[x-1][y+2].getBackground() != OBSTACLE) {
						rob.rotateRobot(map, "W");
					}
					else if (map.grid[x][y+3].getBackground() == OBSTACLE || map.grid[x+1][y+3].getBackground() == OBSTACLE || map.grid[x+2][y+3].getBackground() == OBSTACLE) {
						rob.rotateRobot(map, "E");
					}
				}
				
				
				/*
				else if ((map.grid[x-1][y].getBackground() == OBSTACLE || map.grid[x-1][y+1].getBackground() == OBSTACLE || map.grid[x-1][y+2].getBackground() == OBSTACLE)) {
					if (map.grid[x][y+3].getBackground() != OBSTACLE && map.grid[x+1][y+3].getBackground() != OBSTACLE && map.grid[x+2][y+3].getBackground() != OBSTACLE) {
						rob.rotateRobot(map, "E");
					}
					else if (map.grid[x-1][y].getBackground() == OBSTACLE || map.grid[x-1][y+1].getBackground() == OBSTACLE || map.grid[x-1][y+2].getBackground() == OBSTACLE) {
						rob.rotateRobot(map, "W");
					}
				}
				
				else if ((map.grid[x-1][y].getBackground() != OBSTACLE && map.grid[x-1][y+1].getBackground() != OBSTACLE && map.grid[x-1][y+2].getBackground() != OBSTACLE)) {
					if(map.grid[x-1][y+3].getBackground() == OBSTACLE) {
						rob.moveRobot(map, 1);
					}
					else if (map.grid[x][y+3].getBackground() != OBSTACLE && map.grid[x+1][y+3].getBackground() != OBSTACLE && map.grid[x+2][y+3].getBackground() != OBSTACLE) {
						rob.rotateRobot(map, "E");
					}	
					
				}
				*/
				
				
				
				
				if (reachedWall == false) {
					rob.moveRobot(map, 1);
					x = rob.getX();
		    		y = rob.getY();
				}
				else if (reachedWall == true) {
					rob.rotateRobot(map, "E");
					traversing = false;
					break;
				}
				break;
				
			case "E":				
				if (y+1 < 17) {
					if (map.grid[x+3][y+3].getBackground() != OBSTACLE && map.grid[x+3][y].getBackground() != OBSTACLE &&  map.grid[x+3][y+1].getBackground() != OBSTACLE && map.grid[x+3][y+2].getBackground() != OBSTACLE) {
						rob.rotateRobot(map, "S");
					}
					else if ((map.grid[x][y+3].getBackground() == OBSTACLE) || (map.grid[x+1][y+3].getBackground() == OBSTACLE) || (map.grid[x+2][y+3].getBackground() == OBSTACLE)) {
						rob.rotateRobot(map, "N");
					}
					// traverse wall using sensors on robots right
					else if (!blockGoingDown) {
						if ((map.grid[x+3][y+3].getBackground() == OBSTACLE || map.grid[x+3][y].getBackground() == OBSTACLE || map.grid[x+3][y+1].getBackground() == OBSTACLE || map.grid[x+3][y+2].getBackground() == OBSTACLE)) {
							rob.moveRobot(map, 1);
						}
						else if ((map.grid[x+3][y].getBackground() != OBSTACLE && map.grid[x+3][y+1].getBackground() != OBSTACLE && map.grid[x+3][y+2].getBackground() != OBSTACLE) && !blockGoingRight){
							rob.rotateRobot(map, "S");
						}	
					}
					else if (blockGoingDown) {
						if ((map.grid[x][y+3].getBackground() == OBSTACLE) || (map.grid[x+1][y+3].getBackground() == OBSTACLE) || (map.grid[x+2][y+3].getBackground() == OBSTACLE)) {
							return rob;
						}
						else if ((map.grid[x][y+3].getBackground() != OBSTACLE) && (map.grid[x+1][y+3].getBackground() != OBSTACLE) && (map.grid[x+2][y+3].getBackground() == OBSTACLE)) {
							rob.moveRobot(map, 1);
							rob.rotateRobot(map, "E");
						}
					}
				}
				else if(y+1 == 17) {
					if ((map.grid[x+3][y+3].getBackground() == OBSTACLE || map.grid[x+3][y].getBackground() == OBSTACLE || map.grid[x+3][y+1].getBackground() == OBSTACLE || map.grid[x+3][y+2].getBackground() == OBSTACLE)) {
						rob.moveRobot(map, 1);
					}
					else if ((map.grid[x+3][y].getBackground() != OBSTACLE && map.grid[x+3][y+1].getBackground() != OBSTACLE && map.grid[x+3][y+2].getBackground() != OBSTACLE) && !blockGoingRight){
						rob.rotateRobot(map, "S");
					}	
				}
				else if (y == 17) {
					// traverse wall using sensors on robots right
					if (!blockGoingDown) {
						if ((map.grid[x+3][y].getBackground() != OBSTACLE && map.grid[x+3][y+1].getBackground() != OBSTACLE && map.grid[x+3][y+2].getBackground() != OBSTACLE)){
							rob.rotateRobot(map, "S");
						}	
					}
					else if (blockGoingDown) {
						if ((map.grid[x][y+3].getBackground() == OBSTACLE) || (map.grid[x+1][y+3].getBackground() == OBSTACLE) || (map.grid[x+2][y+3].getBackground() == OBSTACLE)) {
							return rob;
						}
						else if ((map.grid[x][y+3].getBackground() != OBSTACLE) && (map.grid[x+1][y+3].getBackground() != OBSTACLE) && (map.grid[x+2][y+3].getBackground() != OBSTACLE)) {
							rob.moveRobot(map, 1);
							rob.rotateRobot(map, "E");
						}
					}
					
				}
				break;

			case "N":				
				if ((map.grid[x-1][y].getBackground() == OBSTACLE) || (map.grid[x-1][y+1].getBackground() == OBSTACLE) || (map.grid[x-1][y+2].getBackground() == OBSTACLE)) {
					if ((map.grid[x][y+3].getBackground() != OBSTACLE) && (map.grid[x+1][y+3].getBackground() != OBSTACLE) && (map.grid[x+2][y+3].getBackground() != OBSTACLE)) {
						rob.rotateRobot(map, "E");
					}
					else if ((map.grid[x][y+3].getBackground() == OBSTACLE) || (map.grid[x+1][y+3].getBackground() == OBSTACLE) || (map.grid[x+2][y+3].getBackground() == OBSTACLE) || (map.grid[x-1][y+3].getBackground() == OBSTACLE)) {
						rob.rotateRobot(map, "W");
					}
					
				}
				else if ((map.grid[x][y+3].getBackground() == OBSTACLE) || (map.grid[x+1][y+3].getBackground() == OBSTACLE) || (map.grid[x+2][y+3].getBackground() == OBSTACLE) || (map.grid[x-1][y+3].getBackground() == OBSTACLE)) {
					rob.moveRobot(map, 1);
					x = rob.getX();
		    		y = rob.getY();
				}
				else {
					rob.rotateRobot(map, "E");
				}
				break;
				
			case "W":				
				if ((map.grid[x-1][y].getBackground() == OBSTACLE || map.grid[x-1][y+1].getBackground() == OBSTACLE || map.grid[x-1][y+2].getBackground() == OBSTACLE)){
					rob.moveRobot(map, 1);
				}
				else {
					rob.rotateRobot(map, "N");
				}
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
	
	
	public Robot obstacleFrontSouth(MapGrid map, Robot rob, int sleeptime) {
		int x = rob.getX();
		int y = rob.getY();
		boolean reachedWall = false;
		
		boolean blockGoingUp = false;
		boolean blockGoingDown = false;
		boolean blockGoingLeft = false;
		boolean blockGoingRight = false;
		
		if ((x-1)==TopWall) blockGoingUp = true;
		if ((x+3)==BottomWall) blockGoingDown = true;
		if ((y-1)==LeftWall) blockGoingLeft = true;
		if ((y+3)==RightWall) blockGoingRight = true;
		
		// This is when traversing SOUTH and infront there's an obstacle
		switch (rob.getOrientation()) {
		
			case "W":
				if (blockGoingLeft) { 
					reachedWall = true;
				}
				
				if (reachedWall == false) {
					rob.moveRobot(map, 1);
					x = rob.getX();
		    		y = rob.getY();
				}
				else if (reachedWall == true) {
					rob.rotateRobot(map, "S");
					traversing = false;
					break;
				}
				break;
				
			case "S":				
				if (x+1 < 12) {
					if (map.grid[x][y-1].getBackground() != OBSTACLE && map.grid[x+1][y-1].getBackground() != OBSTACLE &&  map.grid[x+2][y-1].getBackground() != OBSTACLE && map.grid[x+3][y-1].getBackground() != OBSTACLE) {
						rob.rotateRobot(map, "W");
					}
					else if ((map.grid[x+3][y].getBackground() == OBSTACLE) || (map.grid[x+3][y+1].getBackground() == OBSTACLE) || (map.grid[x+3][y+2].getBackground() == OBSTACLE)) {
						if ((map.grid[x][y-1].getBackground() != OBSTACLE && map.grid[x+1][y-1].getBackground() != OBSTACLE && map.grid[x+2][y-1].getBackground() != OBSTACLE) && !blockGoingDown){
							rob.rotateRobot(map, "W");
						}	
						else if ((map.grid[x][y-1].getBackground() == OBSTACLE || map.grid[x+1][y-1].getBackground() == OBSTACLE || map.grid[x+2][y-1].getBackground() == OBSTACLE || map.grid[x+3][y-1].getBackground() == OBSTACLE)) {
							rob.rotateRobot(map, "E");
						}
						
					}
					// traverse wall using sensors on robots right
					else if (!blockGoingLeft) {
						if ((map.grid[x][y-1].getBackground() == OBSTACLE || map.grid[x+1][y-1].getBackground() == OBSTACLE || map.grid[x+2][y-1].getBackground() == OBSTACLE || map.grid[x+3][y-1].getBackground() == OBSTACLE)) {
							rob.moveRobot(map, 1);
						}
						else if ((map.grid[x][y-1].getBackground() != OBSTACLE && map.grid[x+1][y-1].getBackground() != OBSTACLE && map.grid[x+2][y-1].getBackground() != OBSTACLE) && !blockGoingDown){
							rob.rotateRobot(map, "W");
						}	
					}
					else if (blockGoingLeft) {
						if ((map.grid[x+3][y].getBackground() == OBSTACLE) || (map.grid[x+3][y+1].getBackground() == OBSTACLE) || (map.grid[x+3][y+2].getBackground() == OBSTACLE)) {
							return rob;
						}
						else if ((map.grid[x+3][y].getBackground() != OBSTACLE) && (map.grid[x+3][y+1].getBackground() != OBSTACLE) && (map.grid[x+3][y+2].getBackground() == OBSTACLE)) {
							rob.moveRobot(map, 1);
							rob.rotateRobot(map, "S");
						}
					}
				}
				else if(x+1 == 12) {
					if ((map.grid[x][y-1].getBackground() == OBSTACLE || map.grid[x+1][y-1].getBackground() == OBSTACLE || map.grid[x+2][y-1].getBackground() == OBSTACLE || map.grid[x+3][y-1].getBackground() == OBSTACLE)) {
						rob.moveRobot(map, 1);
					}
					else if ((map.grid[x][y-1].getBackground() != OBSTACLE && map.grid[x+1][y-1].getBackground() != OBSTACLE && map.grid[x+2][y-1].getBackground() != OBSTACLE) && !blockGoingDown){
						rob.rotateRobot(map, "W");
					}	
				}
				else if (x == 12) {
					// traverse wall using sensors on robots right
					if (!blockGoingLeft) {
						if ((map.grid[x][y-1].getBackground() != OBSTACLE && map.grid[x+1][y-1].getBackground() != OBSTACLE && map.grid[x+2][y-1].getBackground() != OBSTACLE)){
							rob.rotateRobot(map, "W");
						}	
					}
					else if (blockGoingLeft) {
						if ((map.grid[x+3][y].getBackground() == OBSTACLE) || (map.grid[x+3][y+1].getBackground() == OBSTACLE) || (map.grid[x+3][y+2].getBackground() == OBSTACLE)) {
							return rob;
						}
						else if ((map.grid[x+3][y].getBackground() != OBSTACLE) && (map.grid[x+3][y+1].getBackground() != OBSTACLE) && (map.grid[x+3][y+2].getBackground() != OBSTACLE)) {
							rob.moveRobot(map, 1);
							rob.rotateRobot(map, "S");
						}
					}
					
				}
				break;

			case "E":				
				if ((map.grid[x][y+3].getBackground() == OBSTACLE) || (map.grid[x+1][y+3].getBackground() == OBSTACLE) || (map.grid[x+2][y+3].getBackground() == OBSTACLE)) {
					if ((map.grid[x+3][y].getBackground() != OBSTACLE) && (map.grid[x+3][y+1].getBackground() != OBSTACLE) && (map.grid[x+3][y+2].getBackground() != OBSTACLE)) {
						rob.rotateRobot(map, "S");
					}
					else if (((map.grid[x+3][y].getBackground() == OBSTACLE) || (map.grid[x+3][y+1].getBackground() == OBSTACLE) || (map.grid[x+3][y+2].getBackground() == OBSTACLE) || (map.grid[x+3][y+3].getBackground() == OBSTACLE))) {
						rob.rotateRobot(map, "N");
					}
					
				}
				else if ((map.grid[x+3][y].getBackground() == OBSTACLE) || (map.grid[x+3][y+1].getBackground() == OBSTACLE) || (map.grid[x+3][y+2].getBackground() == OBSTACLE) || (map.grid[x+3][y+3].getBackground() == OBSTACLE)) {
					rob.moveRobot(map, 1);
					x = rob.getX();
		    		y = rob.getY();
				}
				else {
					rob.rotateRobot(map, "S");
				}
				break;
				
			case "N":
				if ((map.grid[x][y+3].getBackground() == OBSTACLE) || (map.grid[x+1][y+3].getBackground() == OBSTACLE) || (map.grid[x+2][y+3].getBackground() == OBSTACLE)) {
					rob.moveRobot(map, 1);
				}
				else {
					rob.rotateRobot(map, "E");
				}
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
	
	
	public Robot obstacleFrontNorth(MapGrid map, Robot rob, int sleeptime) {
		int x = rob.getX();
		int y = rob.getY();
		boolean reachedWall = false;
		
		boolean blockGoingUp = false;
		boolean blockGoingDown = false;
		boolean blockGoingLeft = false;
		boolean blockGoingRight = false;
		
		if ((x-1)==TopWall) blockGoingUp = true;
		if ((x+3)==BottomWall) blockGoingDown = true;
		if ((y-1)==LeftWall) blockGoingLeft = true;
		if ((y+3)==RightWall) blockGoingRight = true;
		
		// This is when traversing NORTH and infront there's an obstacle
		switch (rob.getOrientation()) {
		
			case "E":
				if (blockGoingRight) { 
					reachedWall = true;
				}
				
				if (reachedWall == false) {
					rob.moveRobot(map, 1);
					x = rob.getX();
		    		y = rob.getY();
				}
				else if (reachedWall == true) {
					rob.rotateRobot(map, "N");
					traversing = false;
					break;
				}
				break;
				
			case "N":				
				if (x-1 > 0) {
					if (map.grid[x][y+3].getBackground() != OBSTACLE && map.grid[x+1][y+3].getBackground() != OBSTACLE &&  map.grid[x+2][y+3].getBackground() != OBSTACLE && map.grid[x-1][y+3].getBackground() != OBSTACLE) {
						rob.rotateRobot(map, "E");
					}
					else if ((map.grid[x-1][y].getBackground() == OBSTACLE) || (map.grid[x-1][y+1].getBackground() == OBSTACLE) || (map.grid[x-1][y+2].getBackground() == OBSTACLE)) {
						rob.rotateRobot(map, "W");
					}
					// traverse wall using sensors on robots right
					else if (!blockGoingRight) {
						if ((map.grid[x][y+3].getBackground() == OBSTACLE || map.grid[x+1][y+3].getBackground() == OBSTACLE || map.grid[x+2][y+3].getBackground() == OBSTACLE || map.grid[x-1][y+3].getBackground() == OBSTACLE)) {
							rob.moveRobot(map, 1);
						}
						else if ((map.grid[x][y+3].getBackground() != OBSTACLE && map.grid[x+1][y+3].getBackground() != OBSTACLE && map.grid[x+2][y+3].getBackground() != OBSTACLE) && !blockGoingUp){
							rob.rotateRobot(map, "E");
						}	
					}
					else if (blockGoingRight) {
						if ((map.grid[x-1][y].getBackground() == OBSTACLE) || (map.grid[x-1][y+1].getBackground() == OBSTACLE) || (map.grid[x-1][y+2].getBackground() == OBSTACLE)) {
							return rob;
						}
						else if ((map.grid[x-1][y].getBackground() != OBSTACLE) && (map.grid[x-1][y+1].getBackground() != OBSTACLE) && (map.grid[x-1][y+2].getBackground() == OBSTACLE)) {
							rob.moveRobot(map, 1);
							rob.rotateRobot(map, "N");
						}
					}
				}
				else if(x-1 == 0) {
					if ((map.grid[x][y+3].getBackground() == OBSTACLE || map.grid[x+1][y+3].getBackground() == OBSTACLE || map.grid[x+2][y+3].getBackground() == OBSTACLE || map.grid[x-1][y+3].getBackground() == OBSTACLE)) {
						rob.moveRobot(map, 1);
					}
					else if ((map.grid[x][y+3].getBackground() != OBSTACLE && map.grid[x+1][y+3].getBackground() != OBSTACLE && map.grid[x+2][y+3].getBackground() != OBSTACLE) && !blockGoingUp){
						rob.rotateRobot(map, "E");
					}	
				}
				else if (x == 0) {
					// traverse wall using sensors on robots right
					if (!blockGoingRight) {
						if ((map.grid[x][y+3].getBackground() != OBSTACLE && map.grid[x+1][y+3].getBackground() != OBSTACLE && map.grid[x+2][y+3].getBackground() != OBSTACLE)){
							rob.rotateRobot(map, "E");
						}	
					}
					else if (blockGoingRight) {
						if ((map.grid[x-1][y].getBackground() == OBSTACLE) || (map.grid[x-1][y+1].getBackground() == OBSTACLE) || (map.grid[x-1][y+2].getBackground() == OBSTACLE)) {
							return rob;
						}
						else if ((map.grid[x-1][y].getBackground() != OBSTACLE) && (map.grid[x-1][y+1].getBackground() != OBSTACLE) && (map.grid[x-1][y+2].getBackground() != OBSTACLE)) {
							rob.moveRobot(map, 1);
							rob.rotateRobot(map, "N");
						}
					}
					
				}
				
				break;

			case "W":				
				if ((map.grid[x][y-1].getBackground() == OBSTACLE) || (map.grid[x+1][y-1].getBackground() == OBSTACLE) || (map.grid[x+2][y-1].getBackground() == OBSTACLE)) {
					if ((map.grid[x-1][y].getBackground() != OBSTACLE) && (map.grid[x-1][y+1].getBackground() != OBSTACLE) && (map.grid[x-1][y+2].getBackground() != OBSTACLE)) {
						rob.rotateRobot(map, "N");
					}
					else if ((map.grid[x-1][y].getBackground() == OBSTACLE) || (map.grid[x-1][y+1].getBackground() == OBSTACLE) || (map.grid[x-1][y+2].getBackground() == OBSTACLE) || (map.grid[x-1][y-1].getBackground() == OBSTACLE)) {
						rob.rotateRobot(map, "S");
					}
					
				}
				else if ((map.grid[x-1][y].getBackground() == OBSTACLE) || (map.grid[x-1][y+1].getBackground() == OBSTACLE) || (map.grid[x-1][y+2].getBackground() == OBSTACLE) || (map.grid[x-1][y-1].getBackground() == OBSTACLE)) {
					rob.moveRobot(map, 1);
					x = rob.getX();
		    		y = rob.getY();
				}
				else {
					rob.rotateRobot(map, "N");
				}
				break;
				
			case "S":				
				if ((map.grid[x][y-1].getBackground() == OBSTACLE) || (map.grid[x+1][y-1].getBackground() == OBSTACLE) || (map.grid[x+2][y-1].getBackground() == OBSTACLE)) {
					rob.moveRobot(map, 1);
				}
				else {
					rob.rotateRobot(map, "W");
				}
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
