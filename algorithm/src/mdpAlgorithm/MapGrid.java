package mdpAlgorithm;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
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
	int[][] mapDescriptor2 = new int[15][20];

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
				// mapDescriptor2[i][j] = ;
			}
		}
		initLandmarks();
		initWalls();
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
	
	public void setMapDesc(int x, int y) {
		mapDescriptor1[x-1][y-1] = 1;
		mapDescriptor2[x-1][y-1] = 1;
		setMapDescLabel(x, y);
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
	}
	
	public void setMD(int which, boolean onOff) {
		switch(which) {
			case 1:
				md1 = onOff;
				break;
			case 2:
				md2 = onOff;
				break;
		}
	}
	
	public String getMapDesc() {
		String strMapDesc = "";
		for(int i = 0; i < 20; i++) {
			for (int j = 15; j> 0; j--) {	
				strMapDesc += mapDescriptor1[j-1][i];
			}
			strMapDesc += "\n"; // comment this out if require a long string
		}
		return strMapDesc;
	}
	
	public String getMapDesc2(String strMapDesc) {
		
		
		return strMapDesc;
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
