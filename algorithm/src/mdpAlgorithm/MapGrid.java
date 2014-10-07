package mdpAlgorithm;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.math.BigInteger;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class MapGrid extends JPanel {
	private static final int MAP_COL = 22;
	private static final int MAP_ROW = 17;
	private static final Color BORDER = new Color(225, 225, 225);
	private static final Color STARTGOAL= new Color(48, 208, 0);
	private static final Color EXPLORE = new Color(146, 208, 80);
	private static final Color MIDEXPLORE = new Color(255, 30, 30);
	private static final Color WALL = new Color(160, 80, 70);
	public boolean md1 = false;
	public boolean md2 = false;
	public boolean md3 = true;
	
	GridCell[][] grid = new GridCell[MAP_ROW][MAP_COL];

	// =========== method 1 for map descriptor =========== 
	//		String mapDescriptor1 = "";
	//		for(int i = 1; i <= 20; i++) {
	//			for (int j = 15; j> 0; j--) {	
	//				mapDescriptor1 += "0";
	//			}
	//			mapDescriptor1 += "\n";
	//		}
	//		
	//		StringBuilder modMapDescriptor1 = new StringBuilder(mapDescriptor1);
	//
	//		modMapDescriptor1.setCharAt(15, 'x');
	//		System.out.println(modMapDescriptor1);
	// =================================================== 
	
	// =========== method 2 for map descriptor ===========
	int[][] mapDescriptor1 = new int[15][20];
	String[][] mapDescriptor2 = new String[15][20];
	public int[][] toConfirmObstacle = new int[15][20];

	//	mapDescriptor1[14][0] = 1; // this represents grid (15,1)
	//	mapDescriptor1[0][19] = 1; // this represents grid (1,20)
	
	public MapGrid() {
		initMaze();
	}
	
	private void initMaze() {
		setLayout (new GridLayout (MAP_ROW, MAP_COL));
		for (int i = 0; i < MAP_ROW; i++) {
			for (int j = 0; j < MAP_COL; j++) {
				GridCell newCell = new GridCell(i, j, "0");
				newCell.setBorder(BorderFactory.createLineBorder(BORDER, 1));
				grid[i][j] = newCell;
				add(newCell);
			}
		}
		initLandmarks();
		initWalls();
		initMD2();
		//init landmark label
		grid[2][2].setLabel("Start");
		grid[14][19].setLabel("Goal");
	}
	
	public void initLandmarks() {
		changeColour(1, 1, "Start", STARTGOAL);
		changeColour(13, 18, "Goal", STARTGOAL);
		changeColour(7, 9, "", EXPLORE);
		this.grid[8][10].setBackground(MIDEXPLORE);
	}
	
	public void initMD2() {
		for(int i = 0; i < 20; i++) {
			for (int j = 14; j>= 0; j--) {	
				mapDescriptor2[j][i] = "";
			}
		}
		
	}
	
	public void initWalls() {
		for(int i = 0; i<17; i++) {
			this.grid[i][0].setBackground(WALL);
			this.grid[i][0].setBorder(BorderFactory.createLineBorder(WALL, 1));
			this.grid[i][21].setBackground(WALL);
			this.grid[i][21].setBorder(BorderFactory.createLineBorder(WALL, 1));
		}
		
		for(int i = 0; i<22; i++) {
			this.grid[0][i].setBackground(WALL);
			this.grid[0][i].setBorder(BorderFactory.createLineBorder(WALL, 1));
			this.grid[16][i].setBackground(WALL);
			this.grid[16][i].setBorder(BorderFactory.createLineBorder(WALL, 1));
			this.grid[16][i].setPreferredSize(new Dimension(12, 12));
		}
	}
	
	public void setMapDesc(boolean explored,int x, int y) {
			mapDescriptor1[x-1][y-1] = 1;
			mapDescriptor2[x-1][y-1] = "0";
			if(!explored) {
				toConfirmObstacle[x-1][y-1]--;
			}
			else if(explored) {
				toConfirmObstacle[x-1][y-1] = toConfirmObstacle[x-1][y-1]-10;
			}
			setMapDescLabel(x, y);
	}

	public void setMapDescObstacles(int x, int y) {
			mapDescriptor1[x-1][y-1] = 1;
			mapDescriptor2[x-1][y-1] = "1";
			toConfirmObstacle[x-1][y-1]++;
			setMapDescLabelObstacles(x, y);
	}
	
	public void setMapDescLabel(int x, int y) {
		if(md1) {
			if(grid[x][y].getLabel() == "0" && !grid[x][y].label1.getText().equals("Start") && !grid[x][y].label1.getText().equals("Goal"))
				grid[x][y].setLabel("1");
		}
		else if(md2) {
			if(grid[x][y].getLabel() == "0" && !grid[x][y].label1.getText().equals("Start") && !grid[x][y].label1.getText().equals("Goal"))
				grid[x][y].setLabel("0");
		}
		else if(md3) {
			if(!grid[x][y].label1.getText().equals("Start") && !grid[x][y].label1.getText().equals("Goal"))			
				grid[x][y].setLabel(Integer.toString(toConfirmObstacle[x-1][y-1]));
			//System.out.print(toConfirmObstacle[x-1][y-1]+" ");
		}
	}
	
	public void setMapDescLabelObstacles(int x, int y) {
		if(md1) {
			if(grid[x][y].getLabel() == "0" && !grid[x][y].label1.getText().equals("Start") && !grid[x][y].label1.getText().equals("Goal"))
				grid[x][y].setLabel("1");
		}
		else if(md2) {
			if(grid[x][y].getLabel() == "0" && !grid[x][y].label1.getText().equals("Start") && !grid[x][y].label1.getText().equals("Goal"))
				grid[x][y].setLabel("1");
		}
		else if(md3) {
			if(!grid[x][y].label1.getText().equals("Start") && !grid[x][y].label1.getText().equals("Goal"))	
				grid[x][y].setLabel(Integer.toString(toConfirmObstacle[x-1][y-1]));
		}
	}
	
	public void setMD(int which, boolean onOff) {
		switch(which) {
			case 1:
				md1 = onOff;
				break;
			case 2:
				md2 = onOff;
				break;
			case 3:
				md3 = onOff;
				break;
		}
	}
	
	public String getMapDesc() {
		String strMapDesc = "11";
		System.out.println("");
		System.out.println("1 means explored; 0 means unexplored");
		//strMapDesc += "\n"; // comment this out if require a long string
		for(int i = 0; i < 20; i++) {
			for (int j = 0; j< 15; j++) {
				strMapDesc += mapDescriptor1[j][i];
			}
			//strMapDesc += "\n"; // comment this out if require a long string
		}
		strMapDesc += "11";
		return toHex(strMapDesc); // comment either one
		//return strMapDesc; // comment this out if require a long string
	}
	
	public String getMapDesc2() {
		System.out.println("");
		System.out.println("1 means obstacle; 0 means no obstacle; Unexplored grids not shown");
		String strMapDesc = "";
		int strLength = 0;
		boolean padEnough = false;
		
		for(int i = 0; i < 20; i++) {
			for (int j = 0; j< 15; j++) {
				if(!mapDescriptor2[j][i].equals("")) {
					strLength++;
					strMapDesc += mapDescriptor2[j][i];
				}
			}
			//strMapDesc += "\n"; // comment this out if require a long string
		}

		while (!padEnough) {
			if(strLength % 8 != 0) {
				strMapDesc += "0";
				strLength++;
			}
			else padEnough = true;
		}
		//return strMapDesc; // comment this out if require long string
		return toHex(strMapDesc); // comment either one
	}
	
	public int[][] getMapDesc3(String md1, String md2) {
		int[][] mapDescriptor3 = new int[15][20];
		
		String newMd1 = toBinary(md1);
		String newMd2 = toBinary(md2);
		
		// process md1
		newMd1 = newMd1.substring(2, newMd1.length()-2);	
		String[] md1Array = toStringArr(newMd1);
		String[] md2Array = toStringArr(newMd2);

		// make md3
		int md1Counter = 1;
		int md2Counter = 1;
		for(int i = 0; i < 20; i++) {
			for (int j = 0; j< 15; j++) {
				if(md1Array[md1Counter].equals("1")) {
					mapDescriptor3[j][i]++;
					
					if(md2Array[md2Counter].equals("1")) {
						mapDescriptor3[j][i]++;
					}
					md2Counter++;
				}
				md1Counter++;
			}
		}
		//return toHex(strMapDesc); // comment either one
		return mapDescriptor3; // comment this out if require a long string
	}
	
	//For real time
	public int[][] getMapDescRealTime() {
		
		int[][] strMapDescRealTime = new int[15][20];
		System.out.println();

		for(int i = 0; i < 20; i++) {
			for (int j = 0; j< 15; j++) {				
				if(toConfirmObstacle[j][i] >= 2) {
					strMapDescRealTime[j][i] = 2;
				}
				else if(toConfirmObstacle[j][i] > 0 && toConfirmObstacle[j][i] < 2) {
					strMapDescRealTime[j][i] = 1;
				}
				else if(toConfirmObstacle[j][i] < 0) {
					strMapDescRealTime[j][i] = 1;
				}
				else if(toConfirmObstacle[j][i] == 0) {
					strMapDescRealTime[j][i] = 0;
				}
			}
		}
		//return toHex(strMapDesc); // comment either one
		return strMapDescRealTime; // comment this out if require a long string
	}
	
	
	public String toHex(String bin){
		//System.out.println(bin);
		BigInteger b = new BigInteger(bin, 2);
		String hexNum = b.toString(16);
		
		int binaryLength = bin.length() * 2;
		int hexLength = hexNum.length() * 8;
		if(binaryLength - hexLength > 0) {
			for(int i = 0; i < ((binaryLength - hexLength)/8); i++) {
				hexNum = "0"+hexNum;
			}
		}
		return hexNum;
	}
	
	
	public String toBinary(String hex) {
		return new BigInteger("1" + hex, 16).toString(2).substring(1);
	}
	
	public String[] toStringArr(String bin) {
		return bin.split("");
	}
	
	public void changeColour(int x, int y, String text, Color color) {
		for (int i = x; i < x+3; i++) {
			for (int j = y; j < y+3; j++) {
				this.grid[i][j].setBackground(color);
				this.grid[i][j].setBorder(BorderFactory.createLineBorder(color, 1));
				if(color == EXPLORE) {
					this.grid[i][j].setBorder(BorderFactory.createLineBorder(BORDER, 1));
				}
			}
		}
	}
}
