package mdpAlgorithm;

import java.awt.Color;

public class Exploration {
	private static final Color OBSTACLE = Color.DARK_GRAY;
	private static final int TopWall = -1;
	private static final int LeftWall = -1;
	private static final int BottomWall = 15;
	private static final int RightWall = 20;
	
	final MapGrid map = new MapGrid();
	Robot rob = new Robot(map);
	int x = rob.getX();
	int y = rob.getY();
	
	public void robotExplore(){
		
	}
	
	public void simulatorExplore(){
		x = rob.getX();
		y = rob.getY();
		
		while ((x-1)!=TopWall) {
			// if there's obstacle, always go WEST
			if(map.grid[x-1][y].getBackground() == OBSTACLE || map.grid[x-1][y+1].getBackground() == OBSTACLE || map.grid[x-1][y+2].getBackground() == OBSTACLE) {
            	rob.rotateRobot(map, rob.getX(), rob.getY(), "W");
    			rob.moveRobot(map, rob.getX(), rob.getY(), 1, rob.getOrientation());
    		}
            else{ // else continue moving
            	rob.moveRobot(map, rob.getX(), rob.getY(), 1, rob.getOrientation());
            }
			x = rob.getX();
    		y = rob.getY();
		}
		rob.rotateRobot(map, rob.getX(), rob.getY(), "W");

		
		while ((y-1)!=LeftWall) {
			// if there's obstacle, ...
			if(map.grid[x][y-1].getBackground() == OBSTACLE || map.grid[x+1][y-1].getBackground() == OBSTACLE || map.grid[x+2][y-1].getBackground() == OBSTACLE) {
            	rob.rotateRobot(map, rob.getX(), rob.getY(), "S");
    			rob.moveRobot(map, rob.getX(), rob.getY(), 1, rob.getOrientation());
    		}
            else{ // else continue moving
            	rob.moveRobot(map, rob.getX(), rob.getY(), 1, rob.getOrientation());
            }
			x = rob.getX();
    		y = rob.getY();
		}
		rob.rotateRobot(map, rob.getX(), rob.getY(), "S");
		
		while ((x+3)!=BottomWall) {
			// if there's obstacle, ...
			if(map.grid[x+3][y].getBackground() == OBSTACLE || map.grid[x+3][y+1].getBackground() == OBSTACLE || map.grid[x+3][y+2].getBackground() == OBSTACLE) {
            	rob.rotateRobot(map, rob.getX(), rob.getY(), "S");
    			rob.moveRobot(map, rob.getX(), rob.getY(), 1, rob.getOrientation());
    		}
            else{ // else continue moving
            	rob.moveRobot(map, rob.getX(), rob.getY(), 1, rob.getOrientation());
            }
			x = rob.getX();
    		y = rob.getY();
		}
		rob.rotateRobot(map, rob.getX(), rob.getY(), "E");
		
		while ((y+3)!=RightWall) {
			// if there's obstacle, ...
			if(map.grid[x][y+3].getBackground() == OBSTACLE || map.grid[x+1][y+3].getBackground() == OBSTACLE || map.grid[x+2][y+3].getBackground() == OBSTACLE) {
            	rob.rotateRobot(map, rob.getX(), rob.getY(), "S");
    			rob.moveRobot(map, rob.getX(), rob.getY(), 1, rob.getOrientation());
    		}
            else{ // else continue moving
            	rob.moveRobot(map, rob.getX(), rob.getY(), 1, rob.getOrientation());
            }
			x = rob.getX();
    		y = rob.getY();
		}
		rob.rotateRobot(map, rob.getX(), rob.getY(), "E");
		
		/*
		 Things to note for the above exploration:
		 - order of path should be:
		 		if (while walking up got obstacle) then turn left-west
		 		if (while walking left got obstacle) then turn up-north
		 			but if (up-north also got obstacle) then turn down-south (since left-west got obs)
		 				but if (down-south also got obstacle) then right-east (since left-west and down-south got obs)
		 		and so on...
		 */
		
		
		
		/*
        rob.moveRobot(map, rob.getX(), rob.getY(), 3, rob.getOrientation());				
		rob.rotateRobot(map, rob.getX(), rob.getY(), "E");
		rob.moveRobot(map, rob.getX(), rob.getY(), 4, rob.getOrientation());
		rob.rotateRobot(map, rob.getX(), rob.getY(), "S");
		rob.moveRobot(map, rob.getX(), rob.getY(), 4, rob.getOrientation());
		rob.rotateRobot(map, rob.getX(), rob.getY(), "E");
		rob.moveRobot(map, rob.getX(), rob.getY(), 3, rob.getOrientation());
		*/
	}
	
	
	
}
