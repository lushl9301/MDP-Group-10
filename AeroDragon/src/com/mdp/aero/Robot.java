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
		position[1][0] = 0;
		position[1][1] = 0;
		
		this.direction = direction;
	}
	
	public void setPosition(int [][] position)
	{
		this.position = position;
	}
	
	public int[][] getPosition()
	{
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
