package mdpAlgorithm;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MapGrid extends JPanel {
	private static final int MAP_COL = 20;
	private static final int MAP_ROW = 15;
	private static final Color BORDER = new Color(225, 225, 225);
	private static final Color STARTGOAL= new Color(48, 208, 0);
	private static final Color EXPLORE = new Color(146, 208, 80);
	private static final Color MIDEXPLORE = new Color(255, 30, 30);
	
	JPanel[][] grid = new JPanel[MAP_ROW][MAP_COL];
	
	public MapGrid() {
		initMaze();
	}
	
	private void initMaze() {
		setLayout (new GridLayout (MAP_ROW, MAP_COL));
		for (int i = 0; i < MAP_ROW; i++) {
			for (int j = 0; j < MAP_COL; j++) {
				GridCell newCell = new GridCell(i,j);
				newCell.setBorder(BorderFactory.createLineBorder(BORDER, 1));
				grid[i][j] = newCell;
				add(newCell);
			}
		}
		initLandmarks(this);
		//init landmark label
		grid[1][1].add(new JLabel("Start"), JLabel.CENTER);
		grid[13][18].add(new JLabel("Goal"), JLabel.CENTER);
	}
	
	public void initLandmarks(MapGrid map) {
		changeColour(map, 0, 0, "Start", STARTGOAL);
		changeColour(map, 12, 17, "Goal", STARTGOAL);
		changeColour(map, 6, 8, "", EXPLORE);
	}
	
	public static void changeColour(MapGrid map, int x, int y, String text, Color color) {
		for (int i = x; i < x+3; i++) {
			for (int j = y; j < y+3; j++) {
				map.grid[i][j].setBackground(color);
				map.grid[i][j].setBorder(BorderFactory.createLineBorder(color, 1));
				if(color == EXPLORE) {
					map.grid[i][j].setBorder(BorderFactory.createLineBorder(BORDER, 1));
				}
			}
		}
		if(color == EXPLORE) map.grid[x+1][y+1].setBackground(MIDEXPLORE);
	}
}
