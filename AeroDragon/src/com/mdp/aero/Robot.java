package com.mdp.aero;

public class Robot {
	private int[][] position;
	private int direction;
	
	public int NORTH=1; //Left
	public int EAST=2; //Right
	public int SOUTH=3; //Down
	public int WEST=4; //Left
	
	public Robot () {}
	public Robot(int[][] position, int direction)
	{
		//this.position = position;
		
		position[0][0] = 0;
		position[0][1] = 0;
		position[0][2] = 0;
		position[1][0] = 0;
		position[1][1] = 0;
		position[1][2] = 0;
		position[2][0] = 0;
		position[2][1] = 0;
		position[2][2] = 0;
		
		this.direction = direction;
	}
	
	public void setPosition(int [][] position)
	{
		this.position = position;
		
	}
	
	public int[][] getPosition()
	{
		/*position[0][0] = 0;
		position[0][1] = 1;
		position[0][2] = 2;
		position[1][0] = 20;
		position[1][1] = 21;
		position[1][2] = 22;
		position[2][0] = 40;
		position[2][1] = 41;
		position[2][2] = 42;*/
		
		return position;
	}
	
	public void setDirection(int  direction)
	{
		this.direction = direction;
	}
	
	public int getDirection()
	{
		return direction;
	}
}
