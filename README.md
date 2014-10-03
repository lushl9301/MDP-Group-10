##2014MDPGrp10

##Project Deliverable Checklist Assessment Form
####RPi Team
- [ ] A1. The Raspberry Pi board (RPi) is able to
    - [x] accessed via a PC/notebook over Wifi
    - [x] be wirelessly connected to the Nexus 7 tablet
    - [x] communicate with the Arduino board through over a USB->Serial connection
    - [X] multithread between 3 components
    - MORE DETAILED TASKS
    - [X] decode the data received
    - [X] robot deployment
    - [X] PC Java connection testing
    - [X] dropped connection from PC testing
    - [X] dropped connection from Android testing
    - [X] no initial connection from Arduino testing
    - FLOW TESTING
    - [X] remote control testing
    - [ ] exploration testing
    - [ ] shortest path testing

 
####Arduino Team
- [x] A2. Sensors calibrated to correctly return distance to obstacle
- [x] A3. Accurate straight line motion
- [ ] A4. Accurate rotation
    - [x] 90 degree rotation
    - [ ] Clock direction rotation (30 degree/mark)
    - [ ] Arbitory angle
- [ ] A5.
    - [x]  Obstacle avoidance
    - [x]  position recovery
- [ ] A6. Extension beyond the basics
    - [ ] Drifting
    - [ ] Demo.h
    - [ ] ...

####Algo & Simulator team
- [x] B1. Arena exploration simulator
- [x] B2. Time and coverage-limited exploration simulation
- [x] B3. Load map generator
- [x] B4. Generate map descriptor
    - [x] hexString MD1 & MD2
    - [x] MD3 checkObstacle for simulator
    - [x] MD3 checkObstacle for real-time robot
- [x] B5. Fastest path computation simulator
    - [x] Djikstra (BFS)
    - [x] String path to pass on to RPi
- [ ] B5. Extension beyond the basics

####Android Team
- [x] C1. The Android application (AA) is able to transmit and receive text strings over the Bluetooth serial communication link.
- [x] C2. Functional graphical user interface (GUI) that is able to initiate the scanning, selection and connection with a Bluetooth device.
- [x] C3. Functional GUI that provides interactive control of the robot movement via the Bluetooth link
- [x] C4. Functional GUI that indicates the current status of the robot
- [x] C5. 2D display of the maze environment and the robot’s location
- [x] C6. Functional GUI that provides the selection of Manual or Auto updating of graphical display of the maze environment
- [x] C7. Functional GUI that provides two buttons that supports persistent user reconfigurable string commands to the robot
- [x] C8. Robust connectivity with Bluetooth device
- [ ] C9. Extension beyond the basics


##Communication Format

###JSON Format (RPi communication with Android and PC)

####Received by RPi
```
{
	type: "command" / "movement" / "path"
	data (command)	: "S" (initiate robot) / "E" (exploration) / "P" (shortest path) / "R" (remote-control)
	data (movement)	: "L" / "R" / "1" / "G" (stop)
    data (path)     : "LR1" / "G" (stop)
}
```
####Sent from RPi
```
{
	type: "reading" / "status" / "map"
	data (reading): {
		U_F		    : #
		U_R	        : #
		U_L	        : #
		short_LF	: #
		short_RF	: #
		short_FR	: #
		long_BL		: #
        X           : #
        Y           : #
        direction   : #
	}
	data (status)   : "END_EXP" / "END_PATH" / "END_RMT"
    data (map)      : [](map matrix)
}

```

###RPi/PC -> Arduino
All ```Numbers``` and ```Chars```

Start command ```S```

number for go ahead ```x``` grids

Char for turning

```L``` for left 90

```R``` for right 90

```D``` for drift //for demo

####example
```
4 L 3 R R 1
```

####Arduino Send
```Arduino
    JsonObject<10> talk_Json;
    talk_Json["X"] = currentX;
    talk_Json["Y"] = currentY;
    talk_Json["direction"] = pwd;

    talk_Json["U_F"] = u_F_dis;
    talk_Json["U_R"] = u_R_dis;
    talk_Json["U_L"] = u_L_dis;

    talk_Json["short_LF"] = ir_lf_dis;
    talk_Json["short_RF"] = ir_rf_dis;

    talk_Json["short_FR"] = ir_r_dis;

    talk_Json["long_BL"] = ir_l_dis;
    Serial.print(talk_Json);
    Serial.println();
    //eg.
    //{"X":10,"Y":7,"direction":1,"U_F":5,"U_R":20,"U_L":231,"short_LF":659,"short_RF":608,"short_FR":354,"long_BL":216}
    //
```
####Arduino Get
```Arduino
while (!Serial.available() || Serial.read() != 'S'); //Start command

while (!Serial.available() || Serial.read() != 'P'); //Shortest Path
```

##Communication Scenarios

###Exploration
* Exploration Start:
    * Android -> (command to start exploration) -> RPi -> (command to start exploration) -> Arduino

* Exploration:
    * Arduino -> (sensor readings, direction, coordinate) -> RPi -> (sensor readings, direction, coordinate) -> PC & Android

* Exploration End:
    * Arduino -> (status exploration end) -> RPi -> (status exploration end) -> Android & PC 
    * PC -> (map matrix) -> RPi -> (map matrix) -> Android
    * PC -> (shortest path as path) -> RPi -> (shortest path as path) -> Android

###Shortest Path
* Shortest Path Start:
    * Android -> (command to start shortest path) -> RPi -> (command to start shortest path) -> Arduino
    * Android -> (shortest path as movement) -> RPi -> (shortest path as movement) -> Arduino

* Shortest Path:
    * Arduino -> (direction, coordinate) -> RPi -> (direction, coordinate) -> Android

* Shortest Path End:
    * Arduino -> (status shortest path end) -> RPi -> (status shortest path end) -> Android 

###Free Movement:
* Free Movement Start:
    * Android -> (command to start remote) -> RPi -> (command to start remote) -> Arduino

* Free Movement:
    * Android -> (movement command) -> RPi -> (movement command) -> Arduino

* Free Movement End:
    * Android -> (command to stop robot) -> RPi -> (command to stop robot) -> Arduino

