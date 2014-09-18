package mdpAlgorithm;

import java.awt.Color;
import javax.swing.JPanel;

class GridCell extends JPanel {
 
	private int row;
	private int col;

	public GridCell(int row, int col) {
		this.row = row;
	    this.col = col;
	    setBackground(Color.LIGHT_GRAY);
	}

	public int getRow() {
		return row;
	}
	
	public int getCol() {
		return col;
	}
}
