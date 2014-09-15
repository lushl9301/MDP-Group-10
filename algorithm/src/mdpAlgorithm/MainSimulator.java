package mdpAlgorithm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MainSimulator {
	private static final Color DEFAULTCELL = new Color(180, 180, 180);
	private static final Color OBSTACLE = Color.DARK_GRAY;
	private static final Color STARTGOAL= new Color(48, 208, 0);
	private static final Color EXPLORE = new Color(146, 208, 80);
	private static final Color BORDER = new Color(225, 225, 225);
	private static Timer t;
	private static Thread exploreThread;
	
	public static void main(String[] args) {
		
		final JButton clearObs;
		final JButton loadMap;
		final JButton exploreMap;
		JButton solveMap;
		final JButton terminateEx = new JButton("Terminate Explore");
		final JToggleButton addObs;
		final JToggleButton realTime = new JToggleButton("Real Time");
		
		JFrame frame = new JFrame();
		frame.setTitle("Group 10 - Maze Simulator");
		frame.setSize(new Dimension(930, 620)); // length by breadth

		Container contentPanel = frame.getContentPane(); // initialize content panel
		
		JPanel mapPanel = new JPanel();
		mapPanel.setPreferredSize(new Dimension(690, 520));
		
		final MapGrid map = new MapGrid(); // initialize map
		map.setBorder(new EmptyBorder(20, 20, 0, 20) );
		map.setPreferredSize(new Dimension(690, 520));
		JPanel buttonPanel = new JPanel(new GridBagLayout()); // initialize panel for all buttons		
		buttonPanel.setBorder(new EmptyBorder(0, 0, 0, 20) );
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(10,0,0,0);
		c.ipady = 10;
		c.ipadx = 18;
		
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 0;
		
        final JLabel timerLabel = new JLabel("08:00", JLabel.CENTER); 
 		timerLabel.setFont(timerLabel.getFont().deriveFont(50.0f));
		Color color = new Color(211,211,211);
		timerLabel.setBackground(color);
		timerLabel.setOpaque(true);
		buttonPanel.add(timerLabel, c);
		
		c.gridx = 0;
		c.gridy = 1;
		addObs = new JToggleButton("Add Obstacles");

		
		final ChangeListener addObsListener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                ButtonModel buttonModel = addObs.getModel();
                boolean selected = buttonModel.isEnabled();
                
                if (selected) {
                	for (int i = 0; i < 15; i++) {
            			for (int j = 0; j < 20; j++) {
            				final int i2 = i;
            				final int j2 = j;
            				map.grid[i][j].addMouseListener(new MouseAdapter() {
                    			@Override
                    			public void mousePressed(MouseEvent e) {
                    				if(map.grid[i2][j2].getBackground() == OBSTACLE) {
                    					map.grid[i2][j2].setBackground(DEFAULTCELL);
                    				}
                    				else {                    					
	                    				map.grid[i2][j2].setBackground(OBSTACLE);
                    				}
                    			}  
                    		});
            			}
            		}	
                }                
            }
        };
		addObs.addChangeListener(addObsListener);
		
		buttonPanel.add(addObs, c);
		
		c.gridx = 0;
		c.gridy = 3;
		clearObs = new JButton("Clear Obstacles");
		clearObs.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				for (int i = 0; i < 15; i++) {
        			for (int j = 0; j < 20; j++) {
						map.grid[i][j].setBackground(DEFAULTCELL);
        			}
				}
				
				MapGrid.initLandmarks(map);
			}  
		});
		buttonPanel.add(clearObs, c);
		
		c.gridx = 0;
		c.gridy = 4;
		loadMap = new JButton("Load Map");
		buttonPanel.add(loadMap, c);
		
		c.gridx = 0;
		c.gridy = 5;
		c.insets = new Insets(20,0,0,0);
		c.gridwidth = 1;
		c.ipady = 3;
		JLabel stepsLabel = new JLabel("Steps Per Second: "); 
		buttonPanel.add(stepsLabel, c);
		
		c.gridx = 1;
		c.gridy = 5;
		final JTextField stepsPerSec = new JTextField("1", 2);
		buttonPanel.add(stepsPerSec, c);
		
		c.gridx = 0;
		c.gridy = 6;
		c.insets = new Insets(0,0,0,0);
		c.gridwidth = 1;
		JLabel percentObstaclesLabel = new JLabel("<html>Percentage of<br>maze to explore: </html>"); 
		buttonPanel.add(percentObstaclesLabel, c);
		
		c.gridx = 1;
		c.gridy = 6;
		final JTextField percentObstacles = new JTextField("100", 3);
		buttonPanel.add(percentObstacles, c);
		
		c.gridx = 0;
		c.gridy = 7;
		c.gridwidth = 1;
		JLabel duration = new JLabel("<html>Duration of<br>Exploration:</html>"); 
		buttonPanel.add(duration, c);
		
		c.gridx = 1;
		c.gridy = 7;
		final JTextField timeField = new JTextField("08:00", 5);
		buttonPanel.add(timeField, c);
		
		c.gridx = 0;
		c.gridy = 8;
		c.ipady = 30;
		c.insets = new Insets(15,0,0,0);
		exploreMap = new JButton("Explore!");
		
		final MouseAdapter exploreListener = new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				// insert timer countdown code here
				//if(!timeField.getText().equals("MM:SS")) test = (Integer.parseInt(timeField.getText().split(":")[0]) * 60000) + (Integer.parseInt(timeField.getText().split(":")[1])*1000);
				timerLabel.setText(timeField.getText());
				t = new Timer(1000, new ActionListener() {

		 			private long time = (Integer.parseInt(timeField.getText().split(":")[0]) * 60000) + (Integer.parseInt(timeField.getText().split(":")[1])*1000) - 1000;
		 			private String sPadding, mPadding;
		 			
		 		    public void actionPerformed(ActionEvent e) {
		 		    						
		 		        if (time >= 0) {
		 		        	long s = ((time / 1000) % 60);
		 		            long m = (((time / 1000) / 60) % 60);
		 		            
		 		            if (s < 10) sPadding = "0";
		 		            else sPadding = "";
		 		            if (m < 10) mPadding = "0";
		 		            else mPadding = "";
		 		            
		 		            timerLabel.setText(mPadding + m + ":"+ sPadding + s);
		 		            time -= 1000;
		 		        }
		 		        else {
		 		        	exploreThread.stop();
		 		        }
		 		    }
		 		});
				t.start();
	
				// disable other buttons
				addObs.setSelected(false);
				addObs.setEnabled(false);
				addObs.removeChangeListener(addObsListener);
				exploreMap.setEnabled(false);
				exploreMap.removeMouseListener(this);
				realTime.setEnabled(false);
				clearObs.setEnabled(false);
				loadMap.setEnabled(false);
				percentObstacles.setEnabled(false);
				stepsPerSec.setEnabled(false);
				terminateEx.setEnabled(true);
				timeField.setEnabled(false);
				
				// insert robot
				Robot rob = new Robot(map);

				// insert exploration code here
				/*
				rob.moveRobot(map, rob.getX(), rob.getY(), 3, rob.getOrientation());
				rob.rotateRobot(map, rob.getX(), rob.getY(), "W");
				rob.moveRobot(map, rob.getX(), rob.getY(), 2, rob.getOrientation());
				rob.rotateRobot(map, rob.getX(), rob.getY(), "S");
				rob.moveRobot(map, rob.getX(), rob.getY(), 4, rob.getOrientation());
				rob.rotateRobot(map, rob.getX(), rob.getY(), "E");
				*/
				
//				Exploration explore = new Exploration();
//				explore.simulatorExplore(map, rob);
//				explore.simulatorExplore2(map, rob);
				int time =  (1000 / Integer.parseInt(stepsPerSec.getText()));
				Exploration explore = new Exploration(map, rob, time);
				exploreThread = new Thread(explore);
				exploreThread.start();
			}
		};
		exploreMap.addMouseListener(exploreListener);

		buttonPanel.add(exploreMap, c);
		
		c.gridx = 1;
		c.gridy = 8;
		realTime.setPreferredSize(new Dimension(43,realTime.getPreferredSize().height));
		realTime.setMargin(new Insets(0,0,0,0));
		realTime.setFont(realTime.getFont().deriveFont(11.0f));
		buttonPanel.add(realTime, c);
			
		c.gridx = 0;
		c.gridy = 9;
		c.ipady = 5;
		c.insets = new Insets(0,0,0,0);
		c.gridwidth = 2;
		
		terminateEx.setEnabled(false);
		terminateEx.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				ButtonModel terminateButtonModel = terminateEx.getModel();
                boolean selected = terminateButtonModel.isEnabled();
                
                if(selected) {
					// insert timer countdown code here
					t.stop();
					
			        timerLabel.setText("08:00"); 
			        
					// disable other buttons
					addObs.setEnabled(true);
					clearObs.setEnabled(true);
					loadMap.setEnabled(true);
					percentObstacles.setEnabled(true);
					stepsPerSec.setEnabled(true);
					terminateEx.setEnabled(false);
					timeField.setEnabled(true);
					exploreMap.setEnabled(true);
					exploreMap.addMouseListener(exploreListener);
					realTime.setEnabled(true);
					
					for (int i = 0; i < 15; i++) {
	        			for (int j = 0; j < 20; j++) {
							map.grid[i][j].setBackground(DEFAULTCELL);
							map.grid[i][j].setBorder(BorderFactory.createLineBorder(BORDER, 1));
	        			}
					}
					
					MapGrid.initLandmarks(map);
					addObs.addChangeListener(addObsListener);
					exploreThread.stop();
				}
			}  
		});
		buttonPanel.add(terminateEx, c);
		
		c.gridx = 0;
		c.gridy = 10;
		c.ipady = 45;
		c.insets = new Insets(20,0,0,0);
		solveMap = new JButton("Solve Map!");
		buttonPanel.add(solveMap, c);

		mapPanel.add(map);
		contentPanel.add(mapPanel, BorderLayout.WEST);
		contentPanel.add(buttonPanel, BorderLayout.EAST);
        
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}	

}
