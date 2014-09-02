##2014MDPGrp10

##Project Deliverable Checklist Assessment Form
####RPi Team
- [ ] A1. The Raspberry Pi board (RPi) is able to
    - [x] accessed via a PC/notebook over Wifi
    - [x] be wirelessly connected to the Nexus 7 tablet
    - [ ] communicate with the Arduino board through over a USB->Serial connection

####Arduino Team
- [x] A2. Sensors calibrated to correctly return distance to obstacle
- [x] A3. Accurate straight line motion
- [ ] A4. Accurate rotation
    - [x] 90 degree rotation
    - [ ] Clock direction rotation (30 degree/mark)
    - [ ] Arbitory angle
- [ ] A5.
    - [x]  Obstacle avoidance
    - [ ]  position recovery
- [ ] A6. Extension beyond the basics
    - [ ] Drifting
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
- [ ] C1. The Android application (AA) is able to transmit and receive text strings over the Bluetooth serial communication link.
- [ ] C2. Functional graphical user interface (GUI) that is able to initiate the scanning, selection and connection with a Bluetooth device.
- [ ] C3. Functional GUI that provides interactive control of the robot movement via the Bluetooth link
- [ ] C4. Functional GUI that indicates the current status of the robot
- [ ] C5. 2D display of the maze environment and the robot’s location
- [ ] C6. Functional GUI that provides the selection of Manual or Auto updating of graphical display of the maze environment
- [ ] C7. Functional GUI that provides two buttons that supports persistent user reconfigurable string commands to the robot
- [ ] C8. Robust connectivity with Bluetooth device
- [ ] C9. Extension beyond the basics


##Communication Format
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
Serial.println("=============");
Serial.println("F30"); //30cm in front
Serial.print("L40"); //40cm on left
Serial.print("R5"); //5cm on right
```
####Arduino Get
```Arduino
while (!Serial.available() || Serial.read() != 'S'); //Start command

while (!Serial.available() || Serial.read() != 'P'); //Shortest Path
```

