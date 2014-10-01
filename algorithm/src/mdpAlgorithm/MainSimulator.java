package mdpAlgorithm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
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
	private static final Color DEFAULTCELL = Color.LIGHT_GRAY;
	private static final Color OBSTACLE = Color.DARK_GRAY;
	private static final Color STARTGOAL= new Color(48, 208, 0);
	private static final Color CONFIRMOBSTACLE = Color.RED;
	private static final Color EXPLORE = new Color(146, 208, 80);
	private static final Color MIDEXPLORE = new Color(255, 30, 30);
	private static final Color BORDER = new Color(225, 225, 225);
	private static final Color EXPLORED = new Color(0, 128, 255);
	private static final Color ROBOT = new Color(153, 204, 255);
	private static final Color FRONTROBOT = new Color(146, 208, 80);
	private static Timer t;
	private static Thread exploreThread;
	private static Exploration explore;
	private static boolean exploreStart = false;
	private static JFrame frame;
	public static void main(String[] args) {

		final JButton clearObs;
		final JButton loadMap;
		final JButton exploreMap;
		final JButton solveMap = new JButton("Solve Map!");
		final JButton terminateEx = new JButton("Terminate Explore");
		final JToggleButton addObs;
		final JToggleButton realTime = new JToggleButton("Real Time");
		final JCheckBox md1;
		final JCheckBox md2 = new JCheckBox("MD 2");
		final JCheckBox md3 = new JCheckBox("MD 3");
		GridCell newCell;
		frame = new JFrame();
		frame.setTitle("Group 10 - Maze Simulator");
		frame.setSize(new Dimension(950, 600)); // length by breadth (950x600) 950x700

		Container contentPanel = frame.getContentPane(); // initialize content panel
		
		JPanel mapPanel = new JPanel();
		mapPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		mapPanel.setPreferredSize(new Dimension(700, 525));
		
		final MapGrid map = new MapGrid(); // initialize map
		map.setBorder(new EmptyBorder(10, 20, 0, 20) );
		map.setPreferredSize(new Dimension(700, 525));
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
		
        final JLabel timerLabel = new JLabel("06:00", JLabel.CENTER); 
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
                	for (int i = 1; i < 16; i++) {
            			for (int j = 1; j < 21; j++) {
            				final int i2 = i;
            				final int j2 = j;
            				if(map.grid[i][j].getBackground() == DEFAULTCELL || map.grid[i][j].getBackground().equals(CONFIRMOBSTACLE) || map.grid[i][j].getBackground() == OBSTACLE) {
	            				map.grid[i][j].addMouseListener(new MouseAdapter() {
	                    			@Override
	                    			public void mousePressed(MouseEvent e) {
	                    				if(map.grid[i2][j2].getBackground() == OBSTACLE && !exploreStart) {
	                    					map.grid[i2][j2].setBackground(DEFAULTCELL);
	                    				}
	                    				else if(map.grid[i2][j2].getBackground() != OBSTACLE && !exploreStart){                    					
		                    				map.grid[i2][j2].setBackground(OBSTACLE);
	                    				}
	                    			}  
	                    		});
            				}
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
				for (int i = 1; i < 16; i++) {
        			for (int j = 1; j < 21; j++) {
        				map.grid[i][j].setBackground(DEFAULTCELL);
        				map.grid[i][j].setBorder(BorderFactory.createLineBorder(BORDER, 1));
        				map.grid[i][j].clearLabels();
        			}
				}
				
				map.initLandmarks();
			}  
		});
		buttonPanel.add(clearObs, c);
		
		c.gridx = 0;
		c.gridy = 4;
		loadMap = new JButton("Load Map");
		final JFileChooser fc = new JFileChooser();
		loadMap.addActionListener(new ActionListener() {
			 
            public void actionPerformed(ActionEvent e) {
            	try {
	                int retVal = fc.showOpenDialog(frame);
	                if (retVal == JFileChooser.APPROVE_OPTION) {
	                	
	                	//reset map
	                	for (int i = 1; i < 16; i++) {
	            			for (int j = 1; j < 21; j++) {
	            				map.grid[i][j].setBackground(DEFAULTCELL);
	            				map.grid[i][j].setBorder(BorderFactory.createLineBorder(BORDER, 1));
	            				map.grid[i][j].clearLabels();
	            			}
	    				}
	    				
	    				map.initLandmarks();
	                	
	    				// get select file and load obstacles in
	                    File selectedFile = fc.getSelectedFile();
	                    FileInputStream fis = new FileInputStream(selectedFile);
	                    InputStreamReader in =  new InputStreamReader(fis, Charset.forName("UTF-8")); 
	                    char[] buffer = new char[1024];
	                    int n = in.read(buffer);
	                    String text = new String(buffer, 0, n);
	                    
	                    // get obstacles
	                    String[] obstacles = text.split(";");
	                    for(int i=0; i< obstacles.length; i++) {
	                    	String[] obsXY = obstacles[i].split(",");
	                    	map.grid[Integer.parseInt(obsXY[1])+1][Integer.parseInt(obsXY[0])+1].setBackground(OBSTACLE);
	                    }

	                    in.close();
	                }
            	} catch (Exception f) {
                 	 f.printStackTrace();
            	}
            }
        });
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
		final JTextField timeField = new JTextField("06:00", 5);
		buttonPanel.add(timeField, c);
		
		c.gridx = 0;
		c.gridy = 8;
		c.insets = new Insets(5,0,0,0);
		md1 = new JCheckBox("MD 1");
	    md1.addItemListener(new ItemListener() {
	        public void itemStateChanged(ItemEvent e) {
	            if(md1.isSelected()) {
	            	md2.setEnabled(false);
	            	map.setMD(1, true);
	            }
	            else {
	            	md2.setEnabled(true);
	            	map.setMD(1, false);
	            	
	            }
	          }
        });
		buttonPanel.add(md1, c);
		
		c.gridx = 1;
		c.gridy = 8;
		md2.addItemListener(new ItemListener() {
	        public void itemStateChanged(ItemEvent e) {
	            if(md2.isSelected()) {
	            	md1.setEnabled(false);
	            	map.setMD(2, true);
	            }
	            else {
	            	md1.setEnabled(true);
	            	map.setMD(2, false);
	            }
	          }
        });
		buttonPanel.add(md2, c);
	
		c.gridx = 0;
		c.gridy = 9;
		c.ipady = 30;
		c.insets = new Insets(5,0,0,0);
		exploreMap = new JButton("Explore!");
		
		final MouseAdapter exploreListener = new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				exploreStart = true;

				// insert timer countdown code here
				//if(!timeField.getText().equals("MM:SS")) test = (Integer.parseInt(timeField.getText().split(":")[0]) * 60000) + (Integer.parseInt(timeField.getText().split(":")[1])*1000);
				timerLabel.setText(timeField.getText());
				t = new Timer(1000, new ActionListener() {

		 			private long time = (Integer.parseInt(timeField.getText().split(":")[0]) * 60000) + (Integer.parseInt(timeField.getText().split(":")[1])*1000) - 1000;
		 			private String sPadding, mPadding;
		 			
		 		    public void actionPerformed(ActionEvent e) {
		 		    						
		 		        if (time >= 0 && exploreThread.isAlive()) {
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
		 		        	solveMap.setEnabled(true);
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
				md1.setEnabled(false);
				md2.setEnabled(false);
				
				// insert robot
				Robot rob = new Robot(map);
				
				// insert exploration code here
				double percentage = Double.parseDouble(percentObstacles.getText())/100;
				int time =  (1000 / Integer.parseInt(stepsPerSec.getText()));
				explore = new Exploration(map, rob, time, percentage);
				exploreThread = new Thread(explore);
				exploreThread.start();
				
			}
		};
		exploreMap.addMouseListener(exploreListener);
		buttonPanel.add(exploreMap, c);
		
		c.gridx = 1;
		c.gridy = 9;
		realTime.setPreferredSize(new Dimension(43,realTime.getPreferredSize().height));
		realTime.setMargin(new Insets(0,0,0,0));
		realTime.setFont(realTime.getFont().deriveFont(11.0f));
		buttonPanel.add(realTime, c);
			
		c.gridx = 0;
		c.gridy = 10;
		c.ipady = 5;
		c.insets = new Insets(0,0,0,0);
		c.gridwidth = 2;
		
		terminateEx.setEnabled(false);
		terminateEx.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				exploreStart = false;
				ButtonModel terminateButtonModel = terminateEx.getModel();
                boolean selected = terminateButtonModel.isEnabled();
                
                if(selected) {
					// insert timer countdown code here
					t.stop();
					
			        timerLabel.setText(timeField.getText()); 
			        
					// disable other buttons
					addObs.setEnabled(true);
					clearObs.setEnabled(true);
					loadMap.setEnabled(true);
					percentObstacles.setEnabled(true);
					stepsPerSec.setEnabled(true);
					terminateEx.setEnabled(false);
					solveMap.setEnabled(false);
					timeField.setEnabled(true);
					exploreMap.setEnabled(true);
					exploreMap.addMouseListener(exploreListener);
					realTime.setEnabled(true);
					if(md1.isSelected())
						md1.setEnabled(true);
					else if(md2.isSelected())
						md2.setEnabled(true);
					else {
						md1.setEnabled(true);
						md2.setEnabled(true);
					}
					
					for (int i = 1; i < 16; i++) {
            			for (int j = 1; j < 21; j++) {
            				if(map.grid[i][j].getBackground() == DEFAULTCELL || map.grid[i][j].getBackground().equals(EXPLORED) || map.grid[i][j].getBackground().equals(ROBOT)  || map.grid[i][j].getBackground().equals(FRONTROBOT)) {
								map.grid[i][j].setBackground(DEFAULTCELL);
								map.grid[i][j].setBorder(BorderFactory.createLineBorder(BORDER, 1));
            				}
            				else if(map.grid[i][j].getBackground().equals(CONFIRMOBSTACLE)) {
            					map.grid[i][j].setBackground(OBSTACLE);
            				}
            				map.grid[i][j].clearLabels();
	        			}
					}
					
					Dijkstra.route = new boolean[300];
					map.initLandmarks();
					addObs.addChangeListener(addObsListener);
					map.initMD2();
					map.toConfirmObstacle = new int[15][20];
					exploreThread.stop();
				}
			}  
		});
		buttonPanel.add(terminateEx, c);
		
		c.gridx = 0;
		c.gridy = 11;
		c.ipady = 45;
		c.insets = new Insets(20,0,0,0);
		solveMap.setEnabled(false);
		solveMap.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int whichCounter = 0;
				int midCounter = 0;
				String[] midroute = new String[300];
				new Dijkstra(map.getMapDesc(), map.getMapDesc2());
				for (int i = 1; i < 16; i++) {
        			for (int j = 1; j < 21; j++) {
        				if(Dijkstra.route[whichCounter]) {
        					for (int x = 0; x < 3; x++) {
        	        			for (int y = 0; y < 3; y++) {
        	        				map.grid[i+x][j+y].setBackground(ROBOT);
        	        				map.grid[i+x][j+y].setBorder(BorderFactory.createLineBorder(BORDER, 1));
        	        				
        	        				if(x == 1 && y == 1) {
        	        					midroute[midCounter] = (i+x) + "," + (j+y);
        	        					midCounter++;
        	        				}
        	        			}
        					}
        				}
        				whichCounter++;
        			}
				}
				int x;
				int y;
				for(int i = 0; i< midCounter; i++) {
					x = Integer.parseInt(midroute[i].split(",")[0]);
					y = Integer.parseInt(midroute[i].split(",")[1]);
					map.grid[x][y].setBackground(EXPLORE);
				}
			}
		});

		buttonPanel.add(solveMap, c);
		
		c.gridx = 0;
		c.gridy = 12;
		md3.addItemListener(new ItemListener() {
	        public void itemStateChanged(ItemEvent e) {
	        	if(md3.isSelected()) {
	        		map.setMD(3, true);
	        	}
	        	else
	        		map.setMD(3, false);
	        }
        });
		md3.setVisible(false);
		buttonPanel.add(md3, c);

		
		JPanel legendPanel = new JPanel(new GridBagLayout()); // initialize panel for all buttons		
		GridBagConstraints legendC = new GridBagConstraints();
		legendC.fill = GridBagConstraints.HORIZONTAL;
		legendC.gridx = 0;
		legendC.gridy = 0;
		legendC.insets = new Insets(0,30,0,0);
		newCell = new GridCell(0,0,"");
		newCell.setBorder(BorderFactory.createLineBorder(BORDER, 1));
		newCell.setBackground(STARTGOAL);
		legendPanel.add(newCell, legendC);
		
		legendC.gridx = 1;
		legendC.gridy = 0;
		legendC.insets = new Insets(0,0,0,0);
		JLabel startlabel = new JLabel("Start/Goal");
		legendPanel.add(startlabel, legendC);
		
		legendC.gridx = 2;
		legendC.gridy = 0;
		legendC.insets = new Insets(0,32,0,0);
		newCell = new GridCell(0,0,"");
		newCell.setBorder(BorderFactory.createLineBorder(EXPLORE, 3));
		newCell.setPreferredSize(new Dimension(12, 12));
		newCell.setBackground(MIDEXPLORE);
		legendPanel.add(newCell, legendC);
		
		legendC.gridx = 3;
		legendC.gridy = 0;
		legendC.insets = new Insets(0,2,0,0);
		JLabel exploreStart = new JLabel("Exploration Start");
		legendPanel.add(exploreStart, legendC);
		
		legendC.gridx = 4;
		legendC.gridy = 0;
		legendC.insets = new Insets(0,32,0,0);
		newCell = new GridCell(0,0,"");
		newCell.setBorder(BorderFactory.createLineBorder(BORDER, 1));
		newCell.setBackground(OBSTACLE);
		legendPanel.add(newCell, legendC);
		
		legendC.gridx = 5;
		legendC.gridy = 0;
		legendC.insets = new Insets(0,0,0,0);
		JLabel unconfirmObs = new JLabel("Unconfirmed Obstacle");
		legendPanel.add(unconfirmObs, legendC);
		
		legendC.gridx = 6;
		legendC.gridy = 0;
		legendC.insets = new Insets(0,32,0,0);
		newCell = new GridCell(0,0,"");
		newCell.setBorder(BorderFactory.createLineBorder(BORDER, 1));
		newCell.setBackground(CONFIRMOBSTACLE);
		legendPanel.add(newCell, legendC);
		
		legendC.gridx = 7;
		legendC.gridy = 0;
		legendC.insets = new Insets(0,0,0,0);
		JLabel confirmObs = new JLabel("Confirmed Obstacle");
		legendPanel.add(confirmObs, legendC);
		
		legendC.gridx = 8;
		legendC.gridy = 0;
		legendC.insets = new Insets(0,32,0,0);
		newCell = new GridCell(0,0,"");
		newCell.setBorder(BorderFactory.createLineBorder(BORDER, 1));
		newCell.setBackground(EXPLORED);
		legendPanel.add(newCell, legendC);
		
		legendC.gridx = 9;
		legendC.gridy = 0;
		legendC.insets = new Insets(0,0,0,0);
		JLabel explored = new JLabel("Explored");
		legendPanel.add(explored, legendC);
		
		mapPanel.add(map);
		mapPanel.add(legendPanel);
		
		contentPanel.add(mapPanel, BorderLayout.WEST);
		contentPanel.add(buttonPanel, BorderLayout.EAST);
        
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		
	}	
}
