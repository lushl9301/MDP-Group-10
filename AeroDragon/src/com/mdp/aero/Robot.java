package com.mdp.aero;

public class Robot {
	private int[][] position;
	private int direction;
	
	public int U=0; //Left
	public int R=1; //Right
	public int D=2; //Down
	public int L=3; //Left
	
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
