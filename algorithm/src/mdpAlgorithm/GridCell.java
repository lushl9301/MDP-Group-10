package mdpAlgorithm;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;

class GridCell extends JPanel {
 
	private int row;
	private int col;
	private String label;
	JLabel label1 = new JLabel("", JLabel.CENTER);
	
	public GridCell(int row, int col, String label) {
		this.row = row;
	    this.col = col;
	    this.label = label;
	    if(!label.equals("0"))
	    	label1.setText(label);
	    this.add(label1);
	    setBackground(Color.LIGHT_GRAY);
	}

	public int getRow() {
		return row;
	}
	
	public int getCol() {
		return col;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
		label1.setText(label);
	}
	
	public void clearLabels() {
		this.label = "0";
		if(!label1.getText().equals("Start") &&  !label1.getText().equals("Goal"))
			label1.setText("");
	}
}
