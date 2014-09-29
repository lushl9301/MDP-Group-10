package mdpAlgorithm;

import java.awt.Color;
import java.util.Stack;

public class Exploration implements Runnable {
	private static final Color OBSTACLE = Color.RED;
	private static final Color WALL = new Color(160, 80, 70);
	private static final Color EXPLORED = new Color(0, 128, 255);
	private static final int TopWall = 0;
	private static final int LeftWall = 0;
	private static final int BottomWall = 16;
	private static final int RightWall = 21;
	private int distanceToNorthObs = 100;
	private int distanceToSouthObs = 100;
	private int distanceToWestObs = 100;
	private int distanceToEastObs = 100;
	public Stack<Robot> pathTravelled;
	public MapGrid map;
	public Robot rob;
	private int sleeptime;
	private double percentage;
	private boolean traversing = false;
	private String previousDirection = "";
	
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
		int faceFirstDir = 0;
		
		Robot firstDelay = new Robot(pathTravelled.peek());
		firstDelay(map, rob, sleeptime);
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
			
			
			if(rob.getX() == 1 && rob.getY() == 1 && !enteredGoal) enteredStart = true;
			else if(rob.getX() == 1 && rob.getY() == 1 && enteredGoal) completed = true;
			if(rob.getX() == 13 && rob.getY() == 18 && enteredStart) enteredGoal = true;
			
			currentPos = simulatorExplore2(map, rob, sleeptime);
			if (currentPos != null) {
				pathTravelled.push(currentPos);
			}
			else {
				currentPos = pathTravelled.pop();
			}
			checkCompleted(map, percentage);
			if (completed) rob.rotateRobot(map, "E");
		} while(!pathTravelled.isEmpty() && !completed);
		
		
		
		System.out.println(map.getMapDesc());
		System.out.println(map.getMapDesc2());
		int[][] md3 = map.getMapDesc3Testing(map.getMapDesc(), map.getMapDesc2());
		for (int j = 0; j< 15; j++) {
			for(int i = 0; i < 20; i++) {
			
				System.out.print(md3[j][i]);
			}
			System.out.println();
		}
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
		map.mapDescriptor1 = new int[15][20];
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
	
	public Robot goToFurthestObstacle(MapGrid map, Robot rob, int sleeptime){
	// This method calculates whether there are any obstacle in 3grids away from the robot and faces that direction
		
		int x = rob.getX();
		int y = rob.getY();
		
		//int distanceToNorthObs = -1;
		//int distanceToSouthObs = -1;
		//int distanceToWestObs = -1;
		//int distanceToEastObs = -1;

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
		
			
		
		if (distanceToNorthObs>=distanceToSouthObs && distanceToNorthObs>=distanceToWestObs && distanceToNorthObs>=distanceToEastObs){
			rob.rotateRobot(map, "N"); 
			previousDirection = "N";
		}
		else { 
			if (distanceToWestObs>distanceToNorthObs && distanceToWestObs>=distanceToSouthObs && distanceToWestObs>=distanceToEastObs){
				rob.rotateRobot(map, "W");
				previousDirection = "W";
			}
			else {
				if (distanceToSouthObs>distanceToNorthObs && distanceToSouthObs>distanceToWestObs && distanceToSouthObs>=distanceToEastObs){
					rob.rotateRobot(map, "S");
					previousDirection = "S";
				}
				else { 
					if (distanceToEastObs>distanceToSouthObs && distanceToEastObs>distanceToWestObs && distanceToEastObs>distanceToNorthObs){
					rob.rotateRobot(map, "E");
					previousDirection = "E";
				}
					
				}
			}
		}
		System.out.printf("Distance to north is %d",distanceToNorthObs);
		System.out.println();
		System.out.printf("Distance to south is %d",distanceToSouthObs);
		System.out.println();
		System.out.printf("Distance to west is %d",distanceToWestObs);
		System.out.println();
		System.out.printf("Distance to east is %d",distanceToEastObs);
		System.out.println();
		System.out.printf("Direction is %s",rob.getOrientation());
		System.out.println();
		System.out.printf("Previous Direction is %s",previousDirection);
		System.out.println();
		

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
		
		int distanceToNorthObs = 100;
		int distanceToSouthObs = 100;
		int distanceToWestObs = 100;
		int distanceToEastObs = 100;
		
		int LeftSideDistance = 9;
		int RightSideDistance = 10;
		int UpSideDistance = 7;
		int DownSideDistance = 7;
		
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
		    		   		
					if ((!blockGoingUp) && (map.grid[x-1][y].getBackground() != OBSTACLE && map.grid[x-1][y+1].getBackground() != OBSTACLE && map.grid[x-1][y+2].getBackground() != OBSTACLE)) {
						rob.moveRobot(map, 1);
						System.out.println("No obstacle. Move up.");
					}
					else if ((!blockGoingUp) && (map.grid[x-1][y].getBackground() == OBSTACLE || map.grid[x-1][y+1].getBackground() == OBSTACLE || map.grid[x-1][y+2].getBackground() == OBSTACLE)) {
						System.out.println("Got obstacle. Stop and check side distances.");
			    		
						if (previousDirection=="N") {
							System.out.printf("It comes in HEREEE!!");
							System.out.println();
							//Check right obs
							if (map.grid[x][y+11].getBackground() == OBSTACLE || map.grid[x+1][y+11].getBackground() == OBSTACLE || map.grid[x+2][y+11].getBackground() == OBSTACLE) {
								distanceToEastObs = 9;
							}
							if (map.grid[x][y+10].getBackground() == OBSTACLE || map.grid[x+1][y+10].getBackground() == OBSTACLE || map.grid[x+2][y+10].getBackground() == OBSTACLE) {
								distanceToEastObs = 8;
							}
							if (map.grid[x][y+9].getBackground() == OBSTACLE || map.grid[x+1][y+9].getBackground() == OBSTACLE || map.grid[x+2][y+9].getBackground() == OBSTACLE) {
								distanceToEastObs = 7;
							}
							if (map.grid[x][y+8].getBackground() == OBSTACLE || map.grid[x+1][y+8].getBackground() == OBSTACLE || map.grid[x+2][y+8].getBackground() == OBSTACLE) {
								distanceToEastObs = 6;
							}
							if (map.grid[x][y+7].getBackground() == OBSTACLE || map.grid[x+1][y+7].getBackground() == OBSTACLE || map.grid[x+2][y+7].getBackground() == OBSTACLE) {
								distanceToEastObs = 5;
							}
							if (map.grid[x][y+6].getBackground() == OBSTACLE || map.grid[x+1][y+6].getBackground() == OBSTACLE || map.grid[x+2][y+6].getBackground() == OBSTACLE) {
								distanceToEastObs = 4;
							}
							if (map.grid[x][y+5].getBackground() == OBSTACLE || map.grid[x+1][y+5].getBackground() == OBSTACLE || map.grid[x+2][y+5].getBackground() == OBSTACLE) {
								distanceToEastObs = 3;
							}
							if (map.grid[x][y+4].getBackground() == OBSTACLE || map.grid[x+1][y+4].getBackground() == OBSTACLE || map.grid[x+2][y+4].getBackground() == OBSTACLE) {
								distanceToEastObs = 2;
							}
							if (map.grid[x][y+3].getBackground() == OBSTACLE || map.grid[x+1][y+3].getBackground() == OBSTACLE || map.grid[x+2][y+3].getBackground() == OBSTACLE) {
								distanceToEastObs = 1;
							}
							
							//Check left obs
							if (map.grid[x][y-8].getBackground() == OBSTACLE || map.grid[x+1][y-8].getBackground() == OBSTACLE || map.grid[x+2][y-8].getBackground() == OBSTACLE) {
								distanceToWestObs = 8;
							}
							if (map.grid[x][y-7].getBackground() == OBSTACLE || map.grid[x+1][y-7].getBackground() == OBSTACLE || map.grid[x+2][y-7].getBackground() == OBSTACLE) {
								distanceToWestObs = 7;
							}
							if (map.grid[x][y-6].getBackground() == OBSTACLE || map.grid[x+1][y-6].getBackground() == OBSTACLE || map.grid[x+2][y-6].getBackground() == OBSTACLE) {
								distanceToWestObs = 6;
							}
							if (map.grid[x][y-5].getBackground() == OBSTACLE || map.grid[x+1][y-5].getBackground() == OBSTACLE || map.grid[x+2][y-5].getBackground() == OBSTACLE) {
								distanceToWestObs = 5;
							}
							if (map.grid[x][y-4].getBackground() == OBSTACLE || map.grid[x+1][y-4].getBackground() == OBSTACLE || map.grid[x+2][y-4].getBackground() == OBSTACLE) {
								distanceToWestObs = 4;
							}
							if (map.grid[x][y-3].getBackground() == OBSTACLE || map.grid[x+1][y-3].getBackground() == OBSTACLE || map.grid[x+2][y-3].getBackground() == OBSTACLE) {
								distanceToWestObs = 3;
							}
							if (map.grid[x][y-2].getBackground() == OBSTACLE || map.grid[x+1][y-2].getBackground() == OBSTACLE || map.grid[x+2][y-2].getBackground() == OBSTACLE) {
								distanceToWestObs = 2;
							}
							if (map.grid[x][y-1].getBackground() == OBSTACLE || map.grid[x+1][y-1].getBackground() == OBSTACLE || map.grid[x+2][y-1].getBackground() == OBSTACLE) {
								distanceToWestObs = 1;
							}
							

							//Where is the obs
				    		LeftSideDistance=distanceToWestObs;
				    		RightSideDistance=distanceToEastObs;
				    		
				    		System.out.printf("Distance to west is %d",LeftSideDistance);
							System.out.println();
							System.out.printf("Distance to east is %d",RightSideDistance);
							System.out.println();
							
							//Head where
							if (LeftSideDistance >= RightSideDistance) {
								rob.rotateRobot(map, "W");
								break;
							}
							else {
								rob.rotateRobot(map, "E");
								break;
							}
						}
						
						else rob.rotateRobot(map, previousDirection);
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

					if ((!blockGoingDown) && (map.grid[x+3][y].getBackground() != OBSTACLE && map.grid[x+3][y+1].getBackground() != OBSTACLE && map.grid[x+3][y+2].getBackground() != OBSTACLE)) {
						rob.moveRobot(map, 1);
					}
					else if ((!blockGoingDown) && (map.grid[x+3][y].getBackground() == OBSTACLE || map.grid[x+3][y+1].getBackground() == OBSTACLE || map.grid[x+3][y+2].getBackground() == OBSTACLE)) {
						/*
						if ((x-7)<0 && previousDirection=="W") {
			    			rob.rotateRobot(map, previousDirection);
			    		}
						else if ((x-7)<0 && previousDirection=="E") {
			    			rob.rotateRobot(map, previousDirection);
			    		}
						*/
			    		
						if (previousDirection=="S") {
							//Check right obs
							if (map.grid[x][y+11].getBackground() == OBSTACLE || map.grid[x+1][y+11].getBackground() == OBSTACLE || map.grid[x+2][y+11].getBackground() == OBSTACLE) {
								distanceToEastObs = 9;
							}
							if (map.grid[x][y+10].getBackground() == OBSTACLE || map.grid[x+1][y+10].getBackground() == OBSTACLE || map.grid[x+2][y+10].getBackground() == OBSTACLE) {
								distanceToEastObs = 8;
							}
							if (map.grid[x][y+9].getBackground() == OBSTACLE || map.grid[x+1][y+9].getBackground() == OBSTACLE || map.grid[x+2][y+9].getBackground() == OBSTACLE) {
								distanceToEastObs = 7;
							}
							if (map.grid[x][y+8].getBackground() == OBSTACLE || map.grid[x+1][y+8].getBackground() == OBSTACLE || map.grid[x+2][y+8].getBackground() == OBSTACLE) {
								distanceToEastObs = 6;
							}
							if (map.grid[x][y+7].getBackground() == OBSTACLE || map.grid[x+1][y+7].getBackground() == OBSTACLE || map.grid[x+2][y+7].getBackground() == OBSTACLE) {
								distanceToEastObs = 5;
							}
							if (map.grid[x][y+6].getBackground() == OBSTACLE || map.grid[x+1][y+6].getBackground() == OBSTACLE || map.grid[x+2][y+6].getBackground() == OBSTACLE) {
								distanceToEastObs = 4;
							}
							if (map.grid[x][y+5].getBackground() == OBSTACLE || map.grid[x+1][y+5].getBackground() == OBSTACLE || map.grid[x+2][y+5].getBackground() == OBSTACLE) {
								distanceToEastObs = 3;
							}
							if (map.grid[x][y+4].getBackground() == OBSTACLE || map.grid[x+1][y+4].getBackground() == OBSTACLE || map.grid[x+2][y+4].getBackground() == OBSTACLE) {
								distanceToEastObs = 2;
							}
							if (map.grid[x][y+3].getBackground() == OBSTACLE || map.grid[x+1][y+3].getBackground() == OBSTACLE || map.grid[x+2][y+3].getBackground() == OBSTACLE) {
								distanceToEastObs = 1;
							}
							
							//Check left obs
							if (map.grid[x][y-8].getBackground() == OBSTACLE || map.grid[x+1][y-8].getBackground() == OBSTACLE || map.grid[x+2][y-8].getBackground() == OBSTACLE) {
								distanceToWestObs = 8;
							}
							if (map.grid[x][y-7].getBackground() == OBSTACLE || map.grid[x+1][y-7].getBackground() == OBSTACLE || map.grid[x+2][y-7].getBackground() == OBSTACLE) {
								distanceToWestObs = 7;
							}
							if (map.grid[x][y-6].getBackground() == OBSTACLE || map.grid[x+1][y-6].getBackground() == OBSTACLE || map.grid[x+2][y-6].getBackground() == OBSTACLE) {
								distanceToWestObs = 6;
							}
							if (map.grid[x][y-5].getBackground() == OBSTACLE || map.grid[x+1][y-5].getBackground() == OBSTACLE || map.grid[x+2][y-5].getBackground() == OBSTACLE) {
								distanceToWestObs = 5;
							}
							if (map.grid[x][y-4].getBackground() == OBSTACLE || map.grid[x+1][y-4].getBackground() == OBSTACLE || map.grid[x+2][y-4].getBackground() == OBSTACLE) {
								distanceToWestObs = 4;
							}
							if (map.grid[x][y-3].getBackground() == OBSTACLE || map.grid[x+1][y-3].getBackground() == OBSTACLE || map.grid[x+2][y-3].getBackground() == OBSTACLE) {
								distanceToWestObs = 3;
							}
							if (map.grid[x][y-2].getBackground() == OBSTACLE || map.grid[x+1][y-2].getBackground() == OBSTACLE || map.grid[x+2][y-2].getBackground() == OBSTACLE) {
								distanceToWestObs = 2;
							}
							if (map.grid[x][y-1].getBackground() == OBSTACLE || map.grid[x+1][y-1].getBackground() == OBSTACLE || map.grid[x+2][y-1].getBackground() == OBSTACLE) {
								distanceToWestObs = 1;
							}
							
							//Where is the obs
				    		LeftSideDistance=distanceToWestObs;
				    		RightSideDistance=distanceToEastObs;
				    		
							
							//Head where
							if (LeftSideDistance >= RightSideDistance) {
								rob.rotateRobot(map, "W");
							}
							else {
								rob.rotateRobot(map, "E");
							}
						}
						else rob.rotateRobot(map, previousDirection);
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
					
					if ((!blockGoingRight) && (map.grid[x][y+3].getBackground() != OBSTACLE && map.grid[x+1][y+3].getBackground() != OBSTACLE && map.grid[x+2][y+3].getBackground() != OBSTACLE)) {
						rob.moveRobot(map, 1);
					}
					else if ((!blockGoingRight) && (map.grid[x][y+3].getBackground() == OBSTACLE || map.grid[x+1][y+3].getBackground() == OBSTACLE || map.grid[x+2][y+3].getBackground() == OBSTACLE)) {
						/*
						if ((y-9)<0 && previousDirection=="N") {
			    			rob.rotateRobot(map, previousDirection);
			    		}
						else if ((y-9)<0 && previousDirection=="S") {
			    			rob.rotateRobot(map, previousDirection);
			    		}
						*/
			    		
						if (previousDirection=="E") {
						
						// North obs
						if (map.grid[x-6][y].getBackground() == OBSTACLE || map.grid[x-6][y+1].getBackground() == OBSTACLE || map.grid[x-6][y+2].getBackground() == OBSTACLE) {
							distanceToNorthObs = 6;
						}
						if (map.grid[x-5][y].getBackground() == OBSTACLE || map.grid[x-5][y+1].getBackground() == OBSTACLE || map.grid[x-5][y+2].getBackground() == OBSTACLE) {
							distanceToNorthObs = 5;
						}
						if (map.grid[x-4][y].getBackground() == OBSTACLE || map.grid[x-4][y+1].getBackground() == OBSTACLE || map.grid[x-4][y+2].getBackground() == OBSTACLE) {
							distanceToNorthObs = 4;
						}
						if (map.grid[x-3][y].getBackground() == OBSTACLE || map.grid[x-3][y+1].getBackground() == OBSTACLE || map.grid[x-3][y+2].getBackground() == OBSTACLE) {
							distanceToNorthObs = 3;
						}
						if (map.grid[x-2][y].getBackground() == OBSTACLE || map.grid[x-2][y+1].getBackground() == OBSTACLE || map.grid[x-2][y+2].getBackground() == OBSTACLE) {
							distanceToNorthObs = 2;
						}
						if (map.grid[x-1][y].getBackground() == OBSTACLE || map.grid[x-1][y+1].getBackground() == OBSTACLE || map.grid[x-1][y+2].getBackground() == OBSTACLE) {
							distanceToNorthObs = 1;
						}
						
						// South obs
						if (map.grid[x+8][y].getBackground() == OBSTACLE || map.grid[x+8][y+1].getBackground() == OBSTACLE || map.grid[x+8][y+2].getBackground() == OBSTACLE) {
							distanceToSouthObs = 6;
						}
						if (map.grid[x+7][y].getBackground() == OBSTACLE || map.grid[x+7][y+1].getBackground() == OBSTACLE || map.grid[x+7][y+2].getBackground() == OBSTACLE) {
							distanceToSouthObs = 5;
						}
						if (map.grid[x+6][y].getBackground() == OBSTACLE || map.grid[x+6][y+1].getBackground() == OBSTACLE || map.grid[x+6][y+2].getBackground() == OBSTACLE) {
							distanceToSouthObs = 4;
						}
						if (map.grid[x+5][y].getBackground() == OBSTACLE || map.grid[x+5][y+1].getBackground() == OBSTACLE || map.grid[x+5][y+2].getBackground() == OBSTACLE) {
							distanceToSouthObs = 3;
						}
						if (map.grid[x+4][y].getBackground() == OBSTACLE || map.grid[x+4][y+1].getBackground() == OBSTACLE || map.grid[x+4][y+2].getBackground() == OBSTACLE) {
							distanceToSouthObs = 2;
						}
						if (map.grid[x+3][y].getBackground() == OBSTACLE || map.grid[x+3][y+1].getBackground() == OBSTACLE || map.grid[x+3][y+2].getBackground() == OBSTACLE) {
							distanceToSouthObs = 1;
						}
						
						// What is the obs distance
						UpSideDistance=distanceToNorthObs;
			    		DownSideDistance=distanceToSouthObs;
			    		
			    		System.out.printf("Distance to up is %d",UpSideDistance);
						System.out.println();
						System.out.printf("Distance to down is %d",DownSideDistance);
						System.out.println();
						
			    		// Move where
						if (UpSideDistance >= DownSideDistance) {
							rob.rotateRobot(map, "N");
						}
						else {
							rob.rotateRobot(map, "S");
						}
						}
						else rob.rotateRobot(map, previousDirection);
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
					
					if ((!blockGoingLeft) && (map.grid[x][y-1].getBackground() != OBSTACLE && map.grid[x+1][y-1].getBackground() != OBSTACLE && map.grid[x+2][y-1].getBackground() != OBSTACLE)) {
						rob.moveRobot(map, 1);
					}
					else if ((!blockGoingLeft) && (map.grid[x][y-1].getBackground() == OBSTACLE || map.grid[x+1][y-1].getBackground() == OBSTACLE || map.grid[x+2][y-1].getBackground() == OBSTACLE)) {
						/*
						if ((y-9)<0 && previousDirection=="N") {
			    			rob.rotateRobot(map, previousDirection);
			    		}
						else if ((y-9)<0 && previousDirection=="S") {
			    			rob.rotateRobot(map, previousDirection);
			    		}
			    		*/
						
						if (previousDirection=="W") {
							// North obs
							if (map.grid[x-6][y].getBackground() == OBSTACLE || map.grid[x-6][y+1].getBackground() == OBSTACLE || map.grid[x-6][y+2].getBackground() == OBSTACLE) {
								distanceToNorthObs = 6;
							}
							if (map.grid[x-5][y].getBackground() == OBSTACLE || map.grid[x-5][y+1].getBackground() == OBSTACLE || map.grid[x-5][y+2].getBackground() == OBSTACLE) {
								distanceToNorthObs = 5;
							}
							if (map.grid[x-4][y].getBackground() == OBSTACLE || map.grid[x-4][y+1].getBackground() == OBSTACLE || map.grid[x-4][y+2].getBackground() == OBSTACLE) {
								distanceToNorthObs = 4;
							}
							if (map.grid[x-3][y].getBackground() == OBSTACLE || map.grid[x-3][y+1].getBackground() == OBSTACLE || map.grid[x-3][y+2].getBackground() == OBSTACLE) {
								distanceToNorthObs = 3;
							}
							if (map.grid[x-2][y].getBackground() == OBSTACLE || map.grid[x-2][y+1].getBackground() == OBSTACLE || map.grid[x-2][y+2].getBackground() == OBSTACLE) {
								distanceToNorthObs = 2;
							}
							if (map.grid[x-1][y].getBackground() == OBSTACLE || map.grid[x-1][y+1].getBackground() == OBSTACLE || map.grid[x-1][y+2].getBackground() == OBSTACLE) {
								distanceToNorthObs = 1;
							}
							
							// South obs
							if (map.grid[x+8][y].getBackground() == OBSTACLE || map.grid[x+8][y+1].getBackground() == OBSTACLE || map.grid[x+8][y+2].getBackground() == OBSTACLE) {
								distanceToSouthObs = 6;
							}
							if (map.grid[x+7][y].getBackground() == OBSTACLE || map.grid[x+7][y+1].getBackground() == OBSTACLE || map.grid[x+7][y+2].getBackground() == OBSTACLE) {
								distanceToSouthObs = 5;
							}
							if (map.grid[x+6][y].getBackground() == OBSTACLE || map.grid[x+6][y+1].getBackground() == OBSTACLE || map.grid[x+6][y+2].getBackground() == OBSTACLE) {
								distanceToSouthObs = 4;
							}
							if (map.grid[x+5][y].getBackground() == OBSTACLE || map.grid[x+5][y+1].getBackground() == OBSTACLE || map.grid[x+5][y+2].getBackground() == OBSTACLE) {
								distanceToSouthObs = 3;
							}
							if (map.grid[x+4][y].getBackground() == OBSTACLE || map.grid[x+4][y+1].getBackground() == OBSTACLE || map.grid[x+4][y+2].getBackground() == OBSTACLE) {
								distanceToSouthObs = 2;
							}
							if (map.grid[x+3][y].getBackground() == OBSTACLE || map.grid[x+3][y+1].getBackground() == OBSTACLE || map.grid[x+3][y+2].getBackground() == OBSTACLE) {
								distanceToSouthObs = 1;
							}
							
							// What is the obs distance
	
				    		UpSideDistance=distanceToNorthObs;
				    		DownSideDistance=distanceToSouthObs;
				    		
				    		System.out.printf("Distance to west is %d",UpSideDistance);
							System.out.println();
							System.out.printf("Distance to east is %d",DownSideDistance);
							System.out.println();
							
				    		// Move where
							if (UpSideDistance >= DownSideDistance) {
								rob.rotateRobot(map, "N");
							}
							else {
								rob.rotateRobot(map, "S");
							}
						}
						else rob.rotateRobot(map, previousDirection);
					}
					
					else if (blockGoingLeft) {
						rob.rotateRobot(map, "S");
					}
					System.out.printf("Distance to north is %d",UpSideDistance);
					System.out.println();
					System.out.printf("Distance to south is %d",DownSideDistance);
					System.out.println();
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
		
				if ( ((map.grid[x-1][y].getBackground() == OBSTACLE || map.grid[x-1][y+1].getBackground() == OBSTACLE || map.grid[x-1][y+2].getBackground() == OBSTACLE))) {
					if ((map.grid[x][y+3].getBackground() != OBSTACLE && map.grid[x+1][y+3].getBackground() != OBSTACLE && map.grid[x+2][y+3].getBackground() != OBSTACLE)) {
						rob.rotateRobot(map, "E");
					}
					else if ((map.grid[x-1][y].getBackground() == OBSTACLE || map.grid[x-1][y+1].getBackground() == OBSTACLE || map.grid[x-1][y+2].getBackground() == OBSTACLE)) {
						rob.rotateRobot(map, "W");
					}
				}
				
				else if ((map.grid[x-1][y].getBackground() != OBSTACLE && map.grid[x-1][y+1].getBackground() != OBSTACLE && map.grid[x-1][y+2].getBackground() != OBSTACLE)) {
					if ((map.grid[x][y+3].getBackground() == OBSTACLE || map.grid[x+1][y+3].getBackground() == OBSTACLE || map.grid[x+2][y+3].getBackground() == OBSTACLE)) {
						if ((map.grid[x-1][y].getBackground().equals(WALL)|| map.grid[x-1][y+1].getBackground().equals(WALL) || map.grid[x-1][y+2].getBackground().equals(WALL))) {
							rob.rotateRobot(map, "W");
						}
						else {
							rob.moveRobot(map, 1);
						}			
					}
					else if ((map.grid[x][y+3].getBackground() != OBSTACLE && map.grid[x+1][y+3].getBackground() != OBSTACLE && map.grid[x+2][y+3].getBackground() != OBSTACLE)) {					
						if (map.grid[x+2][y+3].getBackground() != OBSTACLE) {
							rob.setRCount(3);
						}
						if (rob.getRCount()==3) {
							rob.rotateRobot(map, "E");
							rob.moveRobot(map, 1);
							rob.setRCount(0);
						}	
					}
					
				}

				break;	
				
			case "W":				
				if (y-1 > 1) {
					if ((map.grid[x][y-1].getBackground() == OBSTACLE) || (map.grid[x+1][y-1].getBackground() == OBSTACLE) || (map.grid[x+2][y-1].getBackground() == OBSTACLE) )
						rob.rotateRobot(map, "S");
					
					if (!blockGoingUp) {
						if (map.grid[x-1][y].getBackground() == OBSTACLE || map.grid[x-1][y+1].getBackground() == OBSTACLE || map.grid[x-1][y+2].getBackground() == OBSTACLE) {
							rob.moveRobot(map, 1);
						}
						else if ((map.grid[x-1][y].getBackground() != OBSTACLE && map.grid[x-1][y+1].getBackground() != OBSTACLE && map.grid[x-1][y+2].getBackground() != OBSTACLE)){
							if (map.grid[x-1][y+2].getBackground() != OBSTACLE) {
								rob.setRCount(3);
							}
							if (rob.getRCount()==3) {
								rob.rotateRobot(map, "N");
								rob.moveRobot(map, 1);
								rob.setRCount(0);
							}	
						}
					}
					
					else if (blockGoingUp) {
						if ((map.grid[x][y-1].getBackground() == OBSTACLE) || (map.grid[x+1][y-1].getBackground() == OBSTACLE) || (map.grid[x+2][y-1].getBackground() == OBSTACLE)) {
							rob.rotateRobot(map, "S");
						}
						else if ((map.grid[x][y-1].getBackground() != OBSTACLE) && (map.grid[x+1][y-1].getBackground() != OBSTACLE) && (map.grid[x+2][y-1].getBackground() != OBSTACLE)) {
							rob.moveRobot(map, 1);
						}
					}
				}
				else if(y-1 == 1) {
					if (map.grid[x-1][y].getBackground() == OBSTACLE || map.grid[x-1][y+1].getBackground() == OBSTACLE || map.grid[x-1][y+2].getBackground() == OBSTACLE) {
						rob.moveRobot(map, 1);
					}
					else if ((map.grid[x-1][y].getBackground().equals(WALL) || map.grid[x-1][y+1].getBackground().equals(WALL) || map.grid[x-1][y+2].getBackground().equals(WALL))) {
						rob.rotateRobot(map, "W");
					}
					else if ((map.grid[x-1][y].getBackground() != OBSTACLE && map.grid[x-1][y+1].getBackground() != OBSTACLE && map.grid[x-1][y+2].getBackground() != OBSTACLE)){
						if (map.grid[x-1][y+2].getBackground() != OBSTACLE) {
							rob.setRCount(3);
						}
						if (rob.getRCount()==3) {
							rob.rotateRobot(map, "N");
							rob.moveRobot(map, 1);
							rob.setRCount(0);
						}	
					}
				}
				else if (y == 1) {
					if (!blockGoingUp) {
						if ((map.grid[x-1][y].getBackground() != OBSTACLE && map.grid[x-1][y+1].getBackground() != OBSTACLE && map.grid[x-1][y+2].getBackground() != OBSTACLE)){
							if (map.grid[x-1][y+2].getBackground() != OBSTACLE) {
								rob.setRCount(3);
							}
							if (rob.getRCount()==3) {
								rob.rotateRobot(map, "N");
								rob.moveRobot(map, 1);
								rob.setRCount(0);
							}	
						}	
						
					}
					else if (blockGoingUp) {
						if ((map.grid[x][y-1].getBackground() == OBSTACLE) || (map.grid[x+1][y-1].getBackground() == OBSTACLE) || (map.grid[x+2][y-1].getBackground() == OBSTACLE)) {
							return rob;
						}
						else if ((map.grid[x][y-1].getBackground().equals(WALL) || map.grid[x+1][y-1].getBackground().equals(WALL) || map.grid[x+2][y-1].getBackground().equals(WALL))) {
							rob.rotateRobot(map, "W");
						}
						else if ((map.grid[x][y-1].getBackground() != OBSTACLE) && (map.grid[x+1][y-1].getBackground() != OBSTACLE) && (map.grid[x+2][y-1].getBackground() != OBSTACLE)) {
							rob.moveRobot(map, 1);
							rob.rotateRobot(map, "W");
						}
					}
					
				}	
				
				if (map.grid[x-1][y+2].getBackground().equals(WALL)) {
					reachedWall = true;
				}
				
				if (reachedWall == true) {
					rob.rotateRobot(map, "W");
					traversing = false;
					break;
				}
				 
				break;

			case "S":
				if ((map.grid[x+3][y].getBackground() == OBSTACLE) || (map.grid[x+3][y+1].getBackground() == OBSTACLE) || (map.grid[x+3][y+2].getBackground() == OBSTACLE)) {
					//if ((map.grid[x][y-1].getBackground() != OBSTACLE) && (map.grid[x+1][y-1].getBackground() != OBSTACLE) && (map.grid[x+2][y-1].getBackground() != OBSTACLE)) {
					//	rob.rotateRobot(map, "W");
					//}
					if ((map.grid[x][y-1].getBackground() == OBSTACLE) || (map.grid[x+1][y-1].getBackground() == OBSTACLE) || (map.grid[x+2][y-1].getBackground() == OBSTACLE) || (map.grid[x+3][y-1].getBackground() == OBSTACLE)) {
						rob.rotateRobot(map, "E");
					}
					if (map.grid[x][y-1].getBackground() != OBSTACLE && map.grid[x+1][y-1].getBackground() != OBSTACLE && map.grid[x+2][y-1].getBackground() != OBSTACLE) {
						rob.setRCount(3);
					}
					if (rob.getRCount()==3) {
						rob.rotateRobot(map, "W");
						rob.moveRobot(map, 1);
						rob.setRCount(0);
					}
					
				}
				else if ((map.grid[x][y-1].getBackground() == OBSTACLE) || (map.grid[x+1][y-1].getBackground() == OBSTACLE) || (map.grid[x+2][y-1].getBackground() == OBSTACLE) ) {
					rob.moveRobot(map, 1);
				}
			
				else if ((map.grid[x][y-1].getBackground() != OBSTACLE) && (map.grid[x+1][y-1].getBackground() != OBSTACLE) && (map.grid[x+2][y-1].getBackground() != OBSTACLE) ) {
					if (map.grid[x][y-1].getBackground() != OBSTACLE) {
						rob.setRCount(3);
					}
					if (rob.getRCount()==3) {
						rob.rotateRobot(map, "W");
						rob.moveRobot(map, 1);
						rob.setRCount(0);
					}		
					
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
					else if ((map.grid[x][y+3].getBackground() != OBSTACLE && map.grid[x+1][y+3].getBackground() != OBSTACLE && map.grid[x+2][y+3].getBackground() != OBSTACLE)){
						if (map.grid[x+3][y].getBackground() == OBSTACLE){
							rob.moveRobot(map, 1);
							
							if (map.grid[x+3][y].getBackground() != OBSTACLE) {
								rob.setRCount(3);
							}
						}
						else if ((map.grid[x+3][y].getBackground() == OBSTACLE || map.grid[x+3][y+1].getBackground() == OBSTACLE || map.grid[x+3][y+2].getBackground() == OBSTACLE)){
							rob.moveRobot(map, 1);
						}
					}
				}
				
				else if ((map.grid[x][y+3].getBackground() == OBSTACLE || map.grid[x+1][y+3].getBackground() == OBSTACLE || map.grid[x+2][y+3].getBackground() == OBSTACLE)){
					if ((map.grid[x+3][y].getBackground() != OBSTACLE && map.grid[x+3][y+1].getBackground() != OBSTACLE && map.grid[x+3][y+2].getBackground() != OBSTACLE)){
						if (map.grid[x+3][y].getBackground() != OBSTACLE) {
							rob.setRCount(3);
						}
						if (rob.getRCount()==3) {
							rob.rotateRobot(map, "S");
							rob.moveRobot(map, 1);
							rob.setRCount(0);
						}	
					}
					else {
						rob.rotateRobot(map, "N");
					}	
					
				}
				
				else if (map.grid[x][y+3].getBackground() != OBSTACLE && map.grid[x+1][y+3].getBackground() != OBSTACLE && map.grid[x+2][y+3].getBackground() != OBSTACLE){
					if (map.grid[x+3][y].getBackground() != OBSTACLE) {
						rob.setRCount(3);
					}
					if (rob.getRCount()==3) {
						rob.rotateRobot(map, "S");
						rob.moveRobot(map, 1);
						rob.setRCount(0);
					}			
						
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
				
				if ((map.grid[x+3][y].getBackground() == OBSTACLE || map.grid[x+3][y+1].getBackground() == OBSTACLE || map.grid[x+3][y+2].getBackground() == OBSTACLE)) {
					if (map.grid[x][y-1].getBackground() != OBSTACLE && map.grid[x+1][y-1].getBackground() != OBSTACLE && map.grid[x+2][y-1].getBackground() != OBSTACLE) {
						rob.rotateRobot(map, "W");
					}
					else if (map.grid[x+3][y].getBackground() == OBSTACLE || map.grid[x+3][y+1].getBackground() == OBSTACLE || map.grid[x+3][y+2].getBackground() == OBSTACLE) {
						rob.rotateRobot(map, "E");
					}
				}
				
				else if ((map.grid[x+3][y].getBackground() != OBSTACLE && map.grid[x+3][y+1].getBackground() != OBSTACLE && map.grid[x+3][y+2].getBackground() != OBSTACLE)) {
					if ((map.grid[x][y-1].getBackground() == OBSTACLE || map.grid[x+1][y-1].getBackground() == OBSTACLE || map.grid[x+2][y-1].getBackground() == OBSTACLE)) {
						if ((map.grid[x+3][y].getBackground().equals(WALL) || map.grid[x+3][y+1].getBackground().equals(WALL) || map.grid[x+3][y+2].getBackground().equals(WALL))) {
							rob.rotateRobot(map, "E");
						}
						else {
							rob.moveRobot(map, 1);
						}	
					}
					else if ((map.grid[x][y-1].getBackground() != OBSTACLE && map.grid[x+1][y-1].getBackground() != OBSTACLE && map.grid[x+2][y-1].getBackground() != OBSTACLE)) {
						if (map.grid[x][y-1].getBackground() != OBSTACLE) {
							rob.setRCount(3);
						}
						if (rob.getRCount()==3) {
							rob.rotateRobot(map, "W");
							rob.moveRobot(map, 1);
							rob.setRCount(0);
						}	
					}	
				}
				

				break;
				
			case "E":	
				if (y+1 < 18) {
					if ((map.grid[x][y+3].getBackground() == OBSTACLE) || (map.grid[x+1][y+3].getBackground() == OBSTACLE) || (map.grid[x+2][y+3].getBackground() == OBSTACLE)) {
						rob.rotateRobot(map, "N");
					}
					
					if (!blockGoingDown) {
						if (map.grid[x+3][y].getBackground() == OBSTACLE || map.grid[x+3][y+1].getBackground() == OBSTACLE || map.grid[x+3][y+2].getBackground() == OBSTACLE) {
							rob.moveRobot(map, 1);
						}
						else if ((map.grid[x+3][y].getBackground() != OBSTACLE && map.grid[x+3][y+1].getBackground() != OBSTACLE && map.grid[x+3][y+2].getBackground() != OBSTACLE)){
							if (map.grid[x+3][y].getBackground() != OBSTACLE) {
								rob.setRCount(3);
							}
							if (rob.getRCount()==3) {
								rob.rotateRobot(map, "S");
								rob.moveRobot(map, 1);
								rob.setRCount(0);
							}	
						}
					}
					
					else if (blockGoingDown) {
						if ((map.grid[x][y+3].getBackground() == OBSTACLE) || (map.grid[x+1][y+3].getBackground() == OBSTACLE) || (map.grid[x+2][y+3].getBackground() == OBSTACLE)) {
							rob.rotateRobot(map, "N");
						}
						else if ((map.grid[x][y+3].getBackground() != OBSTACLE) && (map.grid[x+1][y+3].getBackground() != OBSTACLE) && (map.grid[x+2][y+3].getBackground() != OBSTACLE)) {
							rob.moveRobot(map, 1);
						}
					}
				}
				else if(y+1 == 18) {
					if ((map.grid[x+3][y].getBackground() == OBSTACLE || map.grid[x+3][y+1].getBackground() == OBSTACLE || map.grid[x+3][y+2].getBackground() == OBSTACLE)) {
						rob.moveRobot(map, 1);
					}
					else if ((map.grid[x+3][y].getBackground().equals(WALL) || map.grid[x+3][y+1].getBackground().equals(WALL) || map.grid[x+3][y+2].getBackground().equals(WALL))) {
						rob.rotateRobot(map, "E");
					}
					else if ((map.grid[x+3][y].getBackground() != OBSTACLE && map.grid[x+3][y+1].getBackground() != OBSTACLE && map.grid[x+3][y+2].getBackground() != OBSTACLE)){
						if (map.grid[x+3][y].getBackground() != OBSTACLE) {
							rob.setRCount(3);
						}
						if (rob.getRCount()==3) {
							rob.rotateRobot(map, "S");
							rob.moveRobot(map, 1);
							rob.setRCount(0);
						}	
					}
				}
				else if (y == 18) {
					if (!blockGoingDown) {
						if ((map.grid[x+3][y].getBackground() != OBSTACLE && map.grid[x+3][y+1].getBackground() != OBSTACLE && map.grid[x+3][y+2].getBackground() != OBSTACLE)){
							if (map.grid[x+3][y].getBackground() != OBSTACLE) {
								rob.setRCount(3);
							}
							if (rob.getRCount()==3) {
								rob.rotateRobot(map, "S");
								rob.moveRobot(map, 1);
								rob.setRCount(0);
							}	
						}
					}
					else if (blockGoingDown) {
						if ((map.grid[x][y+3].getBackground() == OBSTACLE) || (map.grid[x+1][y+3].getBackground() == OBSTACLE) || (map.grid[x+2][y+3].getBackground() == OBSTACLE)) {
							return rob;
						}
						else if ((map.grid[x][y+3].getBackground().equals(WALL)) || (map.grid[x+1][y+3].getBackground().equals(WALL)) || (map.grid[x+2][y+3].getBackground().equals(WALL))) {
							rob.rotateRobot(map, "E");
						}
						else if ((map.grid[x][y+3].getBackground() != OBSTACLE) && (map.grid[x+1][y+3].getBackground() != OBSTACLE) && (map.grid[x+2][y+3].getBackground() != OBSTACLE)) {
							rob.moveRobot(map, 1);
							rob.rotateRobot(map, "E");
						}
					}
					
				}
				
				if (map.grid[x+3][y].getBackground().equals(WALL)) {
					reachedWall = true;
				}
				
				if (reachedWall == true) {
					rob.rotateRobot(map, "E");
					traversing = false;
					break;
				}
						
				break;

			case "N":				
				if ((map.grid[x-1][y].getBackground() == OBSTACLE) || (map.grid[x-1][y+1].getBackground() == OBSTACLE) || (map.grid[x-1][y+2].getBackground() == OBSTACLE)) {
					//if ((map.grid[x][y+3].getBackground() != OBSTACLE) && (map.grid[x+1][y+3].getBackground() != OBSTACLE) && (map.grid[x+2][y+3].getBackground() != OBSTACLE)) {
					//	rob.rotateRobot(map, "E");
					//}
					if ((map.grid[x][y+3].getBackground() == OBSTACLE) || (map.grid[x+1][y+3].getBackground() == OBSTACLE) || (map.grid[x+2][y+3].getBackground() == OBSTACLE) || (map.grid[x+3][y-1].getBackground() == OBSTACLE)) {
						rob.rotateRobot(map, "W");
					}
					if ((map.grid[x][y+3].getBackground() != OBSTACLE && map.grid[x+1][y+3].getBackground() != OBSTACLE && map.grid[x+2][y+3].getBackground() != OBSTACLE) ) {
						rob.setRCount(3);
					}
					if (rob.getRCount()==3) {
						rob.rotateRobot(map, "E");
						rob.moveRobot(map, 1);
						rob.setRCount(0);
					}	
					
				}
				else if ((map.grid[x][y+3].getBackground() == OBSTACLE) || (map.grid[x+1][y+3].getBackground() == OBSTACLE) || (map.grid[x+2][y+3].getBackground() == OBSTACLE) ) {
					rob.moveRobot(map, 1);
				}
			
				else if ((map.grid[x][y+3].getBackground() != OBSTACLE) && (map.grid[x+1][y+3].getBackground() != OBSTACLE) && (map.grid[x+2][y+3].getBackground() != OBSTACLE) ) {
					if (map.grid[x+2][y+3].getBackground() != OBSTACLE) {
						rob.setRCount(3);
					}
					if (rob.getRCount()==3) {
						rob.rotateRobot(map, "E");
						rob.moveRobot(map, 1);
						rob.setRCount(0);
					}		
					
				}
				else {
					rob.rotateRobot(map, "E");
				}
				
				break;
				
			case "W":			
				if ((map.grid[x-1][y].getBackground() == OBSTACLE || map.grid[x-1][y+1].getBackground() == OBSTACLE || map.grid[x-1][y+2].getBackground() == OBSTACLE)){
					if ((map.grid[x][y-1].getBackground() == OBSTACLE || map.grid[x+1][y-1].getBackground() == OBSTACLE || map.grid[x+2][y-1].getBackground() == OBSTACLE)){
						rob.rotateRobot(map, "S");
					}
					else if ((map.grid[x][y-1].getBackground() != OBSTACLE && map.grid[x+1][y-1].getBackground() != OBSTACLE && map.grid[x+2][y-1].getBackground() != OBSTACLE)){
						if (map.grid[x-1][y+2].getBackground() == OBSTACLE){
							rob.moveRobot(map, 1);
							if (map.grid[x-1][y+2].getBackground() != OBSTACLE) {
								rob.setRCount(3);
							}
						}
						else if ((map.grid[x-1][y].getBackground() == OBSTACLE || map.grid[x-1][y+1].getBackground() == OBSTACLE || map.grid[x-1][y+2].getBackground() == OBSTACLE)){
							rob.moveRobot(map, 1);
						}
					}
					
				}
				
				else if ((map.grid[x][y-1].getBackground() == OBSTACLE || map.grid[x+1][y-1].getBackground() == OBSTACLE || map.grid[x+2][y-1].getBackground() == OBSTACLE)){
					if ((map.grid[x-1][y].getBackground() != OBSTACLE && map.grid[x-1][y+1].getBackground() != OBSTACLE && map.grid[x-1][y+2].getBackground() != OBSTACLE)){
						if (map.grid[x-1][y+2].getBackground() != OBSTACLE) {
							rob.setRCount(3);
						}
						if (rob.getRCount()==3) {
							rob.rotateRobot(map, "N");
							rob.moveRobot(map, 1);
							rob.setRCount(0);
						}	
					}
					else {
						rob.rotateRobot(map, "S");
					}	
				}
				
				else if (map.grid[x][y-1].getBackground() != OBSTACLE && map.grid[x+1][y-1].getBackground() != OBSTACLE && map.grid[x+2][y-1].getBackground() != OBSTACLE){
					if (map.grid[x-1][y+2].getBackground() != OBSTACLE) {
						rob.setRCount(3);
					}
					if (rob.getRCount()==3) {
						rob.rotateRobot(map, "N");
						rob.moveRobot(map, 1);
						rob.setRCount(0);
					}			
						
				}

				else if ((map.grid[x-1][y].getBackground() != OBSTACLE && map.grid[x-1][y+1].getBackground() != OBSTACLE && map.grid[x-1][y+2].getBackground() != OBSTACLE)){
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
				
				if ((map.grid[x][y-1].getBackground() == OBSTACLE || map.grid[x+1][y-1].getBackground() == OBSTACLE || map.grid[x+2][y-1].getBackground() == OBSTACLE)) {
					if (map.grid[x-1][y].getBackground() != OBSTACLE && map.grid[x-1][y+1].getBackground() != OBSTACLE && map.grid[x-1][y+2].getBackground() != OBSTACLE) {
						rob.rotateRobot(map, "N");
					}
					else if (map.grid[x][y-1].getBackground() == OBSTACLE || map.grid[x+1][y-1].getBackground() == OBSTACLE || map.grid[x+2][y-1].getBackground() == OBSTACLE) {
						rob.rotateRobot(map, "S");
					}
				}
				
				else if ((map.grid[x][y-1].getBackground() != OBSTACLE && map.grid[x+1][y-1].getBackground() != OBSTACLE && map.grid[x+2][y-1].getBackground() != OBSTACLE)) {
					if ((map.grid[x-1][y].getBackground() == OBSTACLE || map.grid[x-1][y+1].getBackground() == OBSTACLE || map.grid[x-1][y+2].getBackground() == OBSTACLE)) {
						if ((map.grid[x][y-1].getBackground().equals(WALL) || map.grid[x+1][y-1].getBackground().equals(WALL) || map.grid[x+2][y-1].getBackground().equals(WALL))) {
							rob.rotateRobot(map, "S");
						}
						else {
							rob.moveRobot(map, 1);
							System.out.println("Do THIS IS IT??");
						}	
					}
					else if ((map.grid[x-1][y].getBackground() != OBSTACLE && map.grid[x-1][y+1].getBackground() != OBSTACLE && map.grid[x-1][y+2].getBackground() != OBSTACLE)) {
						if (map.grid[x-1][y+2].getBackground() != OBSTACLE) {
							rob.setRCount(3);
						}
						if (rob.getRCount()==3) {
							rob.rotateRobot(map, "N");
							rob.moveRobot(map, 1);
							rob.setRCount(0);
						}	
					}
				}
				
				
				break;
				
			case "S":	
				if (x+1 < 13) {
					if ((map.grid[x+3][y].getBackground() == OBSTACLE) || (map.grid[x+3][y+1].getBackground() == OBSTACLE) || (map.grid[x+3][y+2].getBackground() == OBSTACLE)) {
						rob.rotateRobot(map, "E");
					}
					
					if (!blockGoingLeft) {
						if (map.grid[x][y-1].getBackground() == OBSTACLE || map.grid[x+1][y-1].getBackground() == OBSTACLE || map.grid[x+2][y-1].getBackground() == OBSTACLE) {
							rob.moveRobot(map, 1);
						}
						else if ((map.grid[x][y-1].getBackground() != OBSTACLE && map.grid[x+1][y-1].getBackground() != OBSTACLE && map.grid[x+2][y-1].getBackground() != OBSTACLE)){
							if (map.grid[x][y-1].getBackground() != OBSTACLE) {
								rob.setRCount(3);
							}
							if (rob.getRCount()==3) {
								rob.rotateRobot(map, "W");
								rob.moveRobot(map, 1);
								rob.setRCount(0);
							}	
						}
					}
					
					else if (blockGoingLeft) {
						if ((map.grid[x+3][y].getBackground() == OBSTACLE) || (map.grid[x+3][y+1].getBackground() == OBSTACLE) || (map.grid[x+3][y+2].getBackground() == OBSTACLE)) {
							rob.rotateRobot(map, "E");
						}
						else if ((map.grid[x+3][y].getBackground() != OBSTACLE) && (map.grid[x+3][y+1].getBackground() != OBSTACLE) && (map.grid[x+3][y+2].getBackground() != OBSTACLE)) {
							rob.moveRobot(map, 1);
						}
					}
				}
				else if(x+1 == 13) {
					if ((map.grid[x+3][y].getBackground() == OBSTACLE) || (map.grid[x+3][y+1].getBackground() == OBSTACLE) || (map.grid[x+3][y+2].getBackground() == OBSTACLE)) {
						rob.rotateRobot(map, "E");
					}
					if ((map.grid[x][y-1].getBackground() == OBSTACLE || map.grid[x+1][y-1].getBackground() == OBSTACLE || map.grid[x+2][y-1].getBackground() == OBSTACLE || map.grid[x+3][y-1].getBackground() == OBSTACLE)) {
						rob.moveRobot(map, 1);
					}
					else if ((map.grid[x][y-1].getBackground().equals(WALL) || map.grid[x+1][y-1].getBackground().equals(WALL) || map.grid[x+2][y-1].getBackground().equals(WALL))) {
						rob.rotateRobot(map, "S");
					}
					else if ((map.grid[x][y-1].getBackground() != OBSTACLE && map.grid[x+1][y-1].getBackground() != OBSTACLE && map.grid[x+2][y-1].getBackground() != OBSTACLE)){
						if (map.grid[x][y-1].getBackground() != OBSTACLE) {
							rob.setRCount(3);
						}
						if (rob.getRCount()==3) {
							rob.rotateRobot(map, "W");
							rob.moveRobot(map, 1);
							rob.setRCount(0);
						}	
					}
				}
				else if (x == 13) {
					if (!blockGoingLeft) {
						if ((map.grid[x][y-1].getBackground() != OBSTACLE && map.grid[x+1][y-1].getBackground() != OBSTACLE && map.grid[x+2][y-1].getBackground() != OBSTACLE)){
							if (map.grid[x][y-1].getBackground() != OBSTACLE) {
								rob.setRCount(3);
							}
							if (rob.getRCount()==3) {
								rob.rotateRobot(map, "W");
								rob.moveRobot(map, 1);
								rob.setRCount(0);
							}	
						}
						else if (blockGoingDown){
							rob.rotateRobot(map, "S");
							traversing = false;
							break;
						}
					}
					else if (blockGoingLeft) {
						if ((map.grid[x+3][y].getBackground() == OBSTACLE) || (map.grid[x+3][y+1].getBackground() == OBSTACLE) || (map.grid[x+3][y+2].getBackground() == OBSTACLE)) {
							return rob;
						}
						else if ((map.grid[x+3][y].getBackground().equals(WALL)) || (map.grid[x+3][y+1].getBackground().equals(WALL)) || (map.grid[x+3][y+2].getBackground().equals(WALL))) {
							rob.rotateRobot(map, "S");
						}
						else if ((map.grid[x+3][y].getBackground() != OBSTACLE) && (map.grid[x+3][y+1].getBackground() != OBSTACLE) && (map.grid[x+3][y+2].getBackground() != OBSTACLE)) {
							rob.moveRobot(map, 1);
							rob.rotateRobot(map, "S");
						}
					}
					
				}
				
				if (map.grid[x][y-1].getBackground().equals(WALL)) { 
					reachedWall = true;
				}
				
				if (reachedWall == true) {
					rob.rotateRobot(map, "S");
					traversing = false;
					break;
				}
				
				break;

			case "E":		
				if ((map.grid[x][y+3].getBackground() == OBSTACLE) || (map.grid[x+1][y+3].getBackground() == OBSTACLE) || (map.grid[x+2][y+3].getBackground() == OBSTACLE)) {
					if (((map.grid[x+3][y].getBackground() == OBSTACLE) || (map.grid[x+3][y+1].getBackground() == OBSTACLE) || (map.grid[x+3][y+2].getBackground() == OBSTACLE) || (map.grid[x+3][y+3].getBackground() == OBSTACLE))) {
						rob.rotateRobot(map, "N");
					}
					if ((map.grid[x+3][y].getBackground() != OBSTACLE && map.grid[x+3][y+1].getBackground() != OBSTACLE && map.grid[x+3][y+2].getBackground() != OBSTACLE) ) {
						rob.setRCount(3);
					}
					if (rob.getRCount()==3) {
						rob.rotateRobot(map, "S");
						rob.moveRobot(map, 1);
						rob.setRCount(0);
					}	
					
				}
				else if ((map.grid[x+3][y].getBackground() == OBSTACLE) || (map.grid[x+3][y+1].getBackground() == OBSTACLE) || (map.grid[x+3][y+2].getBackground() == OBSTACLE) ) {
					rob.moveRobot(map, 1);
				}
				
				else if ((map.grid[x+3][y].getBackground() != OBSTACLE) && (map.grid[x+3][y+1].getBackground() != OBSTACLE) && (map.grid[x+3][y+2].getBackground() != OBSTACLE) ) {
					if (map.grid[x+3][y].getBackground() != OBSTACLE) {
						rob.setRCount(3);
					}
					if (rob.getRCount()==3) {
						rob.rotateRobot(map, "S");
						rob.moveRobot(map, 1);
						rob.setRCount(0);
					}		
				}
				
				else {
					rob.rotateRobot(map, "S");
				}
				break;
				
			case "N":
				x = rob.getX();
	    		y = rob.getY();	    		
				if ((map.grid[x][y+3].getBackground() == OBSTACLE || map.grid[x+1][y+3].getBackground() == OBSTACLE || map.grid[x+2][y+3].getBackground() == OBSTACLE)){
					if ((map.grid[x-1][y].getBackground() == OBSTACLE || map.grid[x-1][y+1].getBackground() == OBSTACLE || map.grid[x-1][y+2].getBackground() == OBSTACLE)){
						rob.rotateRobot(map, "W");
					}
					else if ((map.grid[x-1][y].getBackground() != OBSTACLE && map.grid[x-1][y+1].getBackground() != OBSTACLE && map.grid[x-1][y+2].getBackground() != OBSTACLE)){
						if (map.grid[x+2][y+3].getBackground() == OBSTACLE){
							rob.moveRobot(map, 1);
							if (map.grid[x+2][y+3].getBackground() != OBSTACLE) {
								rob.setRCount(3);
							}
						}
						else if ((map.grid[x][y+3].getBackground() == OBSTACLE || map.grid[x+1][y+3].getBackground() == OBSTACLE || map.grid[x+2][y+3].getBackground() == OBSTACLE)){
							rob.moveRobot(map, 1);
						}
					}
				}
				
				else if ((map.grid[x-1][y].getBackground() == OBSTACLE || map.grid[x-1][y+1].getBackground() == OBSTACLE || map.grid[x-1][y+2].getBackground() == OBSTACLE)){
					if ((map.grid[x][y+3].getBackground() != OBSTACLE && map.grid[x+1][y+3].getBackground() != OBSTACLE && map.grid[x+2][y+3].getBackground() != OBSTACLE)){
						if (map.grid[x+2][y+3].getBackground() != OBSTACLE) {
							rob.setRCount(3);
						}
						if (rob.getRCount()==3) {
							rob.rotateRobot(map, "E");
							rob.moveRobot(map, 1);
							rob.setRCount(0);
						}	
					}
					else {
						rob.rotateRobot(map, "W");
					}		
				}
				
				else if (map.grid[x-1][y].getBackground() != OBSTACLE && map.grid[x-1][y+1].getBackground() != OBSTACLE && map.grid[x-1][y+2].getBackground() != OBSTACLE){
					if (map.grid[x+2][y+3].getBackground() != OBSTACLE) {
						rob.setRCount(3);
					}
					if (rob.getRCount()==3) {
						rob.rotateRobot(map, "E");
						rob.moveRobot(map, 1);
						rob.setRCount(0);
					}			
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
				
				if ((map.grid[x][y+3].getBackground() == OBSTACLE || map.grid[x+1][y+3].getBackground() == OBSTACLE || map.grid[x+2][y+3].getBackground() == OBSTACLE)) {
					if (map.grid[x+3][y].getBackground() != OBSTACLE && map.grid[x+3][y+1].getBackground() != OBSTACLE && map.grid[x+3][y+2].getBackground() != OBSTACLE) {
						rob.rotateRobot(map, "S");
					}
					else if (map.grid[x][y+3].getBackground() == OBSTACLE || map.grid[x+1][y+3].getBackground() == OBSTACLE || map.grid[x+2][y+3].getBackground() == OBSTACLE) {
						rob.rotateRobot(map, "N");
					}
				}
				
				else if ((map.grid[x][y+3].getBackground() != OBSTACLE && map.grid[x+1][y+3].getBackground() != OBSTACLE && map.grid[x+2][y+3].getBackground() != OBSTACLE)) {
					if ((map.grid[x+3][y].getBackground() == OBSTACLE || map.grid[x+3][y+1].getBackground() == OBSTACLE || map.grid[x+3][y+2].getBackground() == OBSTACLE)) {
						if ((map.grid[x][y+3].getBackground().equals(WALL) || map.grid[x+1][y+3].getBackground().equals(WALL) || map.grid[x+2][y+3].getBackground().equals(WALL))) {
							rob.rotateRobot(map, "N");
						}
						else {
							rob.moveRobot(map, 1);
						}
					}
					else if ((map.grid[x+3][y].getBackground() != OBSTACLE && map.grid[x+3][y+1].getBackground() != OBSTACLE && map.grid[x+3][y+2].getBackground() != OBSTACLE)) {
						if (map.grid[x+3][y].getBackground() != OBSTACLE) {
							rob.setRCount(3);
						}
						if (rob.getRCount()==3) {
							rob.rotateRobot(map, "S");
							rob.moveRobot(map, 1);
							rob.setRCount(0);
						}	
					}
				}
						
				break;
				
			case "N":	
				if (x-1 > 1) {
					if ((map.grid[x-1][y].getBackground() == OBSTACLE) || (map.grid[x-1][y+1].getBackground() == OBSTACLE) || (map.grid[x-1][y+2].getBackground() == OBSTACLE)) {
						rob.rotateRobot(map, "W");
					}
					
					if (!blockGoingRight) {
						if (map.grid[x][y+3].getBackground() == OBSTACLE || map.grid[x+1][y+3].getBackground() == OBSTACLE || map.grid[x+2][y+3].getBackground() == OBSTACLE) {
							rob.moveRobot(map, 1);
						}
						else if ((map.grid[x][y+3].getBackground() != OBSTACLE && map.grid[x+1][y+3].getBackground() != OBSTACLE && map.grid[x+2][y+3].getBackground() != OBSTACLE)){
							if (map.grid[x+2][y+3].getBackground() != OBSTACLE) {
								rob.setRCount(3);
							}
							if (rob.getRCount()==3) {
								rob.rotateRobot(map, "E");
								rob.moveRobot(map, 1);
								rob.setRCount(0);
							}	
						}
					}
					
					else if (blockGoingRight) {
						if ((map.grid[x-1][y].getBackground() == OBSTACLE) || (map.grid[x-1][y+1].getBackground() == OBSTACLE) || (map.grid[x-1][y+2].getBackground() == OBSTACLE)) {
							rob.rotateRobot(map, "W");
						}
						else if ((map.grid[x-1][y].getBackground() != OBSTACLE) && (map.grid[x-1][y+1].getBackground() != OBSTACLE) && (map.grid[x-1][y+2].getBackground() != OBSTACLE)) {
							rob.moveRobot(map, 1);
						}
					}
				}
				else if(x-1 == 1) {
					if ((map.grid[x-1][y].getBackground() == OBSTACLE) || (map.grid[x-1][y+1].getBackground() == OBSTACLE) || (map.grid[x-1][y+2].getBackground() == OBSTACLE)) {
						rob.rotateRobot(map, "W");
					}
					if ((map.grid[x][y+3].getBackground() == OBSTACLE || map.grid[x+1][y+3].getBackground() == OBSTACLE || map.grid[x+2][y+3].getBackground() == OBSTACLE || map.grid[x-1][y+3].getBackground() == OBSTACLE)) {
						rob.moveRobot(map, 1);
					}
					else if ((map.grid[x][y+3].getBackground().equals(WALL) || map.grid[x+1][y+3].getBackground().equals(WALL) || map.grid[x+2][y+3].getBackground().equals(WALL))) {
						rob.rotateRobot(map, "N");
					}
					else if ((map.grid[x][y+3].getBackground() != OBSTACLE && map.grid[x+1][y+3].getBackground() != OBSTACLE && map.grid[x+2][y+3].getBackground() != OBSTACLE)){
						if (map.grid[x+2][y+3].getBackground() != OBSTACLE) {
							rob.setRCount(3);
						}
						if (rob.getRCount()==3) {
							rob.rotateRobot(map, "E");
							rob.moveRobot(map, 1);
							rob.setRCount(0);
						}	
					}
				}
				else if (x == 1) {
					if (!blockGoingRight) {
						if ((map.grid[x][y+3].getBackground() != OBSTACLE && map.grid[x+1][y+3].getBackground() != OBSTACLE && map.grid[x+2][y+3].getBackground() != OBSTACLE)){
							if (map.grid[x+2][y+3].getBackground() != OBSTACLE) {
								rob.setRCount(3);
							}
							if (rob.getRCount()==3) {
								rob.rotateRobot(map, "E");
								rob.moveRobot(map, 1);
								rob.setRCount(0);
							}	
						}

						else if (blockGoingUp){
							rob.rotateRobot(map, "N");
							traversing = false;
							break;
						}
					}
					else if (blockGoingRight) {
						if ((map.grid[x-1][y].getBackground() == OBSTACLE) || (map.grid[x-1][y+1].getBackground() == OBSTACLE) || (map.grid[x-1][y+2].getBackground() == OBSTACLE)) {
							return rob;
						}
						else if ((map.grid[x-1][y].getBackground().equals(WALL)) || (map.grid[x-1][y+1].getBackground().equals(WALL)) || (map.grid[x-1][y+2].getBackground().equals(WALL))) {
							rob.rotateRobot(map, "N");
						}
						else if ((map.grid[x-1][y].getBackground() != OBSTACLE) && (map.grid[x-1][y+1].getBackground() != OBSTACLE) && (map.grid[x-1][y+2].getBackground() != OBSTACLE)) {
							rob.moveRobot(map, 1);
							rob.rotateRobot(map, "N");
						}
					}
					
				}	
				
				if (map.grid[x+2][y+3].getBackground().equals(WALL)) { 
					reachedWall = true;
				}
				
				if (reachedWall == true) {
					rob.rotateRobot(map, "N");
					traversing = false;
					break;
				}
				
				break;

			case "W":				
				if ((map.grid[x][y-1].getBackground() == OBSTACLE) || (map.grid[x+1][y-1].getBackground() == OBSTACLE) || (map.grid[x+2][y-1].getBackground() == OBSTACLE)) {
					//if ((map.grid[x-1][y].getBackground() != OBSTACLE) && (map.grid[x-1][y+1].getBackground() != OBSTACLE) && (map.grid[x-1][y+2].getBackground() != OBSTACLE)) {
					//	rob.rotateRobot(map, "N");
					//}
					if ((map.grid[x-1][y].getBackground() == OBSTACLE) || (map.grid[x-1][y+1].getBackground() == OBSTACLE) || (map.grid[x-1][y+2].getBackground() == OBSTACLE) || (map.grid[x-1][y-1].getBackground() == OBSTACLE)) {
						rob.rotateRobot(map, "S");
					}
					if ((map.grid[x-1][y].getBackground() != OBSTACLE && map.grid[x-1][y+1].getBackground() != OBSTACLE && map.grid[x-1][y+2].getBackground() != OBSTACLE) ) {
						rob.setRCount(3);
					}
					if (rob.getRCount()==3) {
						rob.rotateRobot(map, "N");
						rob.moveRobot(map, 1);
						rob.setRCount(0);
					}	
					
				}
				else if ((map.grid[x-1][y].getBackground() == OBSTACLE) || (map.grid[x-1][y+1].getBackground() == OBSTACLE) || (map.grid[x-1][y+2].getBackground() == OBSTACLE) ) {
					rob.moveRobot(map, 1);
				}
				else if ((map.grid[x-1][y].getBackground() != OBSTACLE) && (map.grid[x-1][y+1].getBackground() != OBSTACLE) && (map.grid[x-1][y+2].getBackground() != OBSTACLE) ) {
					if (map.grid[x-1][y+2].getBackground() != OBSTACLE) {
						rob.setRCount(3);
					}
					if (rob.getRCount()==3) {
						rob.rotateRobot(map, "N");
						rob.moveRobot(map, 1);
						rob.setRCount(0);
					}	
				}
				
				else {
					rob.rotateRobot(map, "N");
				}
				break;
				
			case "S":				
				x = rob.getX();
	    		y = rob.getY();	    		
				if ((map.grid[x][y-1].getBackground() == OBSTACLE || map.grid[x+1][y-1].getBackground() == OBSTACLE || map.grid[x+2][y-1].getBackground() == OBSTACLE)){
					if ((map.grid[x+3][y].getBackground() == OBSTACLE || map.grid[x+3][y+1].getBackground() == OBSTACLE || map.grid[x+3][y+2].getBackground() == OBSTACLE)){
						rob.rotateRobot(map, "E");
					}
					if ((map.grid[x+3][y].getBackground() != OBSTACLE && map.grid[x+3][y+1].getBackground() != OBSTACLE && map.grid[x+3][y+2].getBackground() != OBSTACLE)){
						if (map.grid[x][y-1].getBackground() == OBSTACLE){
							rob.moveRobot(map, 1);
							if (map.grid[x][y-1].getBackground() != OBSTACLE) {
								rob.setRCount(3);
							}
						}
						else if ((map.grid[x][y-1].getBackground() == OBSTACLE || map.grid[x+1][y-1].getBackground() == OBSTACLE || map.grid[x+2][y-1].getBackground() == OBSTACLE)){
							rob.moveRobot(map, 1);
						}
					}
					
				}
				
				else if ((map.grid[x+3][y].getBackground() == OBSTACLE || map.grid[x+3][y+1].getBackground() == OBSTACLE || map.grid[x+3][y+2].getBackground() == OBSTACLE)){
					if ((map.grid[x][y-1].getBackground() != OBSTACLE && map.grid[x+1][y-1].getBackground() != OBSTACLE && map.grid[x+2][y-1].getBackground() != OBSTACLE)){
						if (map.grid[x][y-1].getBackground() != OBSTACLE) { 
							rob.setRCount(3);
						}
						if (rob.getRCount()==3) {
							rob.rotateRobot(map, "W");
							rob.moveRobot(map, 1);
							rob.setRCount(0);
						}	
					}
					else {
						rob.rotateRobot(map, "E");
					}	
					
				}
				
				else if (map.grid[x+3][y].getBackground() != OBSTACLE && map.grid[x+3][y+1].getBackground() != OBSTACLE && map.grid[x+3][y+2].getBackground() != OBSTACLE){
					if (map.grid[x][y-1].getBackground() != OBSTACLE) {
						rob.setRCount(3);
					}
					if (rob.getRCount()==3) {
						rob.rotateRobot(map, "W");
						rob.moveRobot(map, 1);
						rob.setRCount(0);
					}	
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
