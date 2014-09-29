##2014MDPGrp10

##Project Deliverable Checklist Assessment Form
####RPi Team
- [ ] A1. The Raspberry Pi board (RPi) is able to
    - [x] accessed via a PC/notebook over Wifi
    - [x] be wirelessly connected to the Nexus 7 tablet
    - [x] communicate with the Arduino board through over a USB->Serial connection
    - [ ] multithread between 3 components
    - [ ] decode the data received (need testing)
 
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
- [ ] B1. Arena exploration simulator
- [ ] B2. Fastest path computation simulator
    - [ ] Algorithm
    - [ ] ...
- [ ] B3. Generate map descriptor
- [ ] B4. Time and coverage-limited exploration simulation
- [ ] B5. Extension beyond the basics

####Android Team
- [x] C1. The Android application (AA) is able to transmit and receive text strings over the Bluetooth serial communication link.
- [x] C2. Functional graphical user interface (GUI) that is able to initiate the scanning, selection and connection with a Bluetooth device.
- [ ] C3. Functional GUI that provides interactive control of the robot movement via the Bluetooth link
- [x] C4. Functional GUI that indicates the current status of the robot
- [ ] C5. 2D display of the maze environment and the robot’s location
- [x] C6. Functional GUI that provides the selection of Manual or Auto updating of graphical display of the maze environment
- [x] C7. Functional GUI that provides two buttons that supports persistent user reconfigurable string commands to the robot
- [x] C8. Robust connectivity with Bluetooth device
- [ ] C9. Extension beyond the basics


##Communication Format

###JSON Format (RPi communication with Android and PC)

####Received by RPi
```
{
	type: "command" / "movement"
	data (command)	: "START_EXP" / "START_PATH" / "STOP"
	data (movement)	: "LRL4RRL"
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
	data (status)   : "END_EXP" / "END_PATH"
    data (map)      : []
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
    Serial.println("UF " + String(u_F_dis));
    Serial.println("IRLF " + String(ir_lf_dis));
    Serial.println("IRRF " + String(ir_rf_dis));

    Serial.println("UL " + String(u_L_dis));
    Serial.println("IRL " + String(ir_l_dis));
    
    Serial.println("UR " + String(u_R_dis));
    Serial.println("IRR " + String(ir_r_dis));
```
####Arduino Get
```Arduino
while (!Serial.available() || Serial.read() != 'S'); //Start command

while (!Serial.available() || Serial.read() != 'P'); //Shortest Path
```

##Communication Scenarios

###Exploration
* Exploration Start:
Android -> (command to start exploration) -> RPi -> (command to start exploration) -> Arduino

* Exploration:
Arduino -> (sensor readings, direction, coordinate) -> RPi -> (sensor readings, direction, coordinate) -> PC & Android

* Exploration End:
Arduino -> (status exploration end) -> RPi -> (status exploration end) -> Android & PC 
PC -> (map matrix) -> RPi -> (map matrix) -> Android

###Shortest Path
* Shortest Path Start:
Android -> (command to start shortest path) -> RPi -> (command to start shortest path) -> Arduino & PC

PC -> (shortest path movement) -> RPi -> (shortest path movement) -> Arduino

* Shortest Path:
Arduino -> (direction, coordinate) -> RPi -> (direction, coordinate) -> Android

* Shortest Path End:
Arduino -> (status shortest path end) -> RPi -> (status shortest path end) -> Android 

###Free Movement:
* Free Movement:
Android -> (movement command) -> RPi -> (movement command) -> Arduino
