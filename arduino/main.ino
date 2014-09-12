#include "SharpA21.h"  // Short-distance
#include "SharpA02.h"  // Long-distance
#include "URM37.h"     // Ultrasonic
//#include "HMC5883L.h"  // Digital Compass

#include "movement.h"  // turn & goAhead
#include "autoFix.h"

//digital 0, 1 are not alow to use ==> serial port is using
//digital 2, 3, 4, 7, 8, 9, 10, 13 are occuppied by motor shield & encoder
//A0, A1, 6, 12 need to be kicked out before use

//5, 6, 11, 12

//PWM for reading ==> orange
//TRIG for writing ==> yellow  
#define urTRIG A3 //use one to write command

#define urPWM_F 12
#define urPWM_L A2
#define urPWM_R 5

#define motor_L 13  // encoder
#define motor_R 3     // encoder


#define shortIR_LF_in A4
#define shortIR_RF_in A5

#define longIR_L_in A1
#define shortIR_R_in A0

//#define longIR_F_in A4
/**********************/
#define RisingEdgePerGrid 400 // need testing
#define RisingEdgePerTurn_200 367 //for speed 200
#define stepToStraighten 5 //every 5 step make a auto adjust


volatile int currentDirection;
volatile int delta;
volatile int rightMCtr, leftMCtr;
volatile long timer;

int counter_for_straighten;
int currentX, currentY;
int goalX, goalY;

URM37 u_F, u_L, u_R;
SharpA21 shortIR_LF, shortIR_RF, shortIR_R;
SharpA02 longIR_L;

int u_F_dis, u_R_dis, u_L_dis;
int ir_rf_dis, ir_lf_dis, ir_l_dis, ir_r_dis;

MotorShield md;

void setPinsMode() {
    //analog pins no need
    //so IR sensor no need
    //digital pins are set in URM37.h
    //
    //TODO ===> set moto sensor in
    pinMode(motor_R, INPUT);
    pinMode(motor_L, INPUT);
}

void waitForCommand() {
    while (!Serial.available() || Serial.read() != 'S') {
    }
    delay(10);
}

void setup() {
    
    Serial.begin(9600);
    setPinsMode(); // not sure yet

    //set up motor
    md.init();
    sp.init(md);
    cn.init(md);
    delay(10);
    
    //set up 3 Ultrasonic
    u_F.init(urPWM_F, urTRIG);
    u_L.init(urPWM_L, urTRIG);
    u_R.init(urPWM_R, urTRIG);
    delay(10);
    
    //set up IR sensor
    shortIR_RF.init(shortIR_RF_in);
    shortIR_LF.init(shortIR_LF_in);

    shortIR_R.init(shortIR_R_in);
    longIR_L.init(longIR_L_in);
    delay(10);
}

void loop() {
    waitForCommand();

    currentX = 10;
    currentY = 7;
    pwd = 1;
    counter_for_straighten = stepToStraighten; //every 3 or 5 step do a straighten
    findWall();
    goalX = 1;
    goalY = 1;
    exploration();
    delay(1000);

    goalX = 20;
    goalY = 15;
    exploration();
    delay(1000);

    goalX = 1;
    goalY = 1;
    exploration();

    waitForCommand();
    getFRInstructions();
    currentX = 1;
    currentY = 1;
    bridesheadRevisited();
}

void sensorReading() {
    while ((u_F_dis = u_F.getDis()) == 0) {
        delay(50);
    }
    while ((u_L_dis = u_L.getDis()) == 0) {
        delay(50);
    }
    while ((u_R_dis = u_R.getDis()) == 0) {
        delay(50);
    }

    ir_rf_dis = shortIR_RF.getDis();
    ir_lf_dis = shortIR_LF.getDis();

    ir_r_dis = shortIR_R.getDis();
    ir_l_dis = longIR_L.getDis();

    thinkForAWhile();
}

void thinkForAWhile() {
    //think
    //send and delay
    Serial.println("UF " + String(u_F_dis));
    Serial.println("IRLF " + String(ir_lf_dis));
    Serial.println("IRRF " + String(ir_rf_dis));

    Serial.println("UL " + String(u_L_dis));
    Serial.println("IRL " + String(ir_l_dis));
    
    Serial.println("UR " + String(u_R_dis));
    Serial.println("IRR " + String(ir_r_dis));

    delay(500);
}

void exploration() {
    empty_space_R = 0;
    while (abs(goalX - currentX) >= 2 && abs(goalY - currentY) >= 2) {
        //check right
        
        //get all sensor data here.
        
        sensorReading();

        if (u_R_dis > 12) { //right got space
            ++empty_space_R;
            if (empty_space_R >= 2) {
                turn(1);
                continue;
            }
        } else {
            empty_space_R = 0;

            if (--counter_for_straighten == 0) {    //auto fix
                turn(1);    //turn right
                straighten();
                turn(-1);   //turn left
                counter_for_straighten = stepToStraighten;
            }
        }

        if (u_F_dis <= 6) {
            straighten();
            turn(-1);   //turn left
            continue;
        }

        //default go ahead
        goAhead(1);
    }
}

void findWall() {
    //find the closest obstacle
    //go and auto fix
    //go back
    //find farthest obstacle according to the distance
    //go that way
  
    /*
    HOWTO find closest obstacle
    360 turning. use sensor to see the distance
     */

    sensorReading();
    f_dis = min(ir_rf_dis, ir_lf_dis);
    
    int tempDis = f_dis;
    int tempMin = 0;
    
    for (int i = 1; i <= 4; ++i) {
        turn(1);
        sensorReading();
        f_dis = min(ir_rf_dis, ir_lf_dis);
        if (tempDis < f_dis && f_dis - tempDis > 50) {
            //ignore small difference
            tempMin = i;
            tempDis = f_dis;
        }
    }
    
    for (int i = tempMin; i > 0; --i) {
        turn(1);
    }

    sensorReading();
    farthestX = currentX;
    farthestY = currentY;
    farthestDis = max(u_L_dis, u_R_dis);

    while (1) {
        goAhead(1);
        sensorReading();
        if (u_L_dis > farthestDis) {
            farthestDis = u_L_dis;
            farthestX = currentX;
            farthestY = currentY;
        }
        if (u_R_dis > farthestDis) {
            farthestDis = u_R_dis;
            farthestX = -currentX;
            farthestY = -currentY;
        }
        if (u_F_dis <= 6) {
            break;
        }
    }

    straighten();
    //auto fix

    /*
    HOWTO find fasest obstacle
    1. go back
    2. find the farthest distance
    take this as the wall?
    3. go to the wall
     */
    turn(1);
    turn(1);

    grids2goback = abs(farthestX - currentX) + abs(farthestY - currentY);
    goAhead(grids2goback);

    if (farthestX < 0) {
        turn(1);
    } else {
        turn(-1);

    }
    //found where is the wall
    
    //go to the wall
    while (1) {
        sensorReading();
        if (u_F_dis <= 6) {
            break;
        }
        goAhead(1);
    }
    straighten();

    turn(-1);
    //turn left
    //start stick2TheWall & turn right
    //job done
}

void bridesheadRevisited() {
    //follow instruction
    //
    //turn(1); //right
    //turn(-1); //left
    //goAhead(l);
    getFRInstructions();
}

char getChar() {
    while (!Serial.available());
    return Serial.read();
}

void arriving() {
    //TODO
    //when near the end point
    //do nice stop
}

void getFRInstructions() {
    //get shortest path from RPi
    //then move

    while (1) {
        instrChar = getChar();
        while (isDigit(instrChar)) {
            instrString += instrChar;
            instrChar = getChar();
        }
        if (instrString.length() > 0) {
            goAhead(instrString.toInt());
            instrString = "";
        }
        if (instrChar == 'R') {
            turn(1);
        } else if (instrChar == 'L') {
            turn(-1);
        } else if (instrChar == 'G') {
            arriving();
        }
    }
}