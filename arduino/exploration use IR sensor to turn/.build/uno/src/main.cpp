#include <Arduino.h>
#include "avr/io.h"    // hmm
#include "avr/interrupt.h" //
#include "SharpA21.h"  // Short-distance
#include "SharpA02.h"  // Long-distance
#include "URM37.h"     // Ultrasonic
#include "MotorShield.h"
#include "JsonGenerator/JsonGenerator.h" //adding Json generator https://github.com/bblanchon/ArduinoJson
void setPinsMode();
void waitForCommand();
void setup();
void dailyTuning();
void sptuning();
void turnAndGoTuning();
void loop();
void goToStart();
void goToGoal();
void explorationFLow();
void sensorReading();
void thinkForAWhile();
void exploration();
bool findWall();
void bridesheadRevisited();
void remote();
void parking_U();
void parking();
void arriving(int endPoint);
void getFRInstructions();
void goAhead(int grids);
void turn(int turnRight);
void countRight();
void setTimerInterrupt();
void detachTimerInterrupt();
void straighten();
void adjustDirection();
void adjustDistance();
bool isGoodObstacle();
bool isWithWall();
bool obstacleInFront();
char getChar();
void brakeForGoAhead();
void brakeForRotation();
#line 1 "src/main.ino"
//jiayou yidingyaochenggong
//#include "avr/io.h"    // hmm
//#include "avr/interrupt.h" //

//#include "SharpA21.h"  // Short-distance
//#include "SharpA02.h"  // Long-distance
//#include "URM37.h"     // Ultrasonic
//#include "MotorShield.h"

//#include "JsonGenerator/JsonGenerator.h" //adding Json generator https://github.com/bblanchon/ArduinoJson

using namespace ArduinoJson::Generator;

//digital 0, 1 are not alow to use ==> serial port is using
//digital 2, 3, 4, 7, 8, 9, 10, 13 are occuppied by motor shield & encoder
//A0, A1, 6, 12 need to be kicked out before use

//5, 6, 11, 12

//PWM for reading ==> orange
//TRIG for writing ==> yellow
#define urTRIG A3 //use one to write command

#define urPWM_F 12
#define urPWM_L 6
#define urPWM_R 5

#define motor_L 13  // encoder
#define motor_R 3     // encoder


#define shortIR_LF_in A4
#define shortIR_RF_in A5

#define longIR_L_in A1
#define shortIR_R_in A0
#define shortIR_L_in A2

//#define longIR_F_in A4
/**********************/

#define RisingEdgePerTurnRight_200 387 // 392
#define RisingEdgePerTurnLeft_200 392  // 394
#define RisingEdgePerGrid_300 276
#define RisingEdgeForSP 295
//Speed
#define slowSpeed 200
#define speedModeSpeed 300
#define fastRunSpeed 380
#define adjustDirectionSpeed 50
#define adjustDistanceSpeed 90
//auto align frequence
#define stepToStraighten 4 //every 3 step make a auto adjust 3 OCT


#define shortSensorToCM(ir_dis) (6787 / (ir_dis - 3) - 4)
#define longSensorToCM(ir_dis) (16667 / (ir_dis + 15) - 10)


volatile int direction;
volatile int delta;
volatile int rightMCtr, leftMCtr;
volatile int MODE;  //2 for fast run; 1 for speedMode(300), 0 for rotation(200)

volatile int grids;
volatile int currentSpeed;

volatile int counter_for_straighten;
volatile int empty_space_R;

int pwd = 1; // current direction
//1 north
//2 east
//3 south
//4 west

int currentX, currentY;
int goalX, goalY;

URM37 u_F, u_L, u_R;
SharpA21 shortIR_LF, shortIR_RF, shortIR_R, shortIR_L;
SharpA02 longIR_L;

int u_F_dis, u_R_dis, u_L_dis;
int ir_rf_dis, ir_lf_dis, ir_l_dis, ir_r_dis, ir_l2_dis;

MotorShield md;

void setPinsMode() {
    //analog pins no need
    //so IR sensor no need
    //digital pins are set in URM37.h
    //
    pinMode(motor_R, INPUT);
    pinMode(motor_L, INPUT);
}

void waitForCommand() {
    while (!Serial.available() || Serial.read() != 'S') {
        ;
    }
    delay(100);
}

void setup() {

    Serial.begin(9600);
    Serial.println("CLEAR");
    setPinsMode();

    //set up motor
    md.init();

    //set up 3 Ultrasonic
    u_F.init(urPWM_F, urTRIG);
    u_L.init(urPWM_L, urTRIG);
    u_R.init(urPWM_R, urTRIG);

    //set up IR sensor
    shortIR_RF.init(shortIR_RF_in);
    shortIR_LF.init(shortIR_LF_in);

    shortIR_R.init(shortIR_R_in);
    shortIR_L.init(shortIR_L_in);
    longIR_L.init(longIR_L_in);
}

void dailyTuning() {
    int i;
    
    // i = 10;
    // delay(1000);
    // while (--i) {
    //     turn(1);
    //     straighten();
    //     delay(1000);
    //     turn(-1);
    //     delay(1000);
    // }

    delay(1000);
    i = 13;
    while (--i) {
        turn(1);
        delay(200);
    }

    // delay(1000);
    // i = 13;
    // while (--i) {
    //     turn(-1);
    //     delay(200);
    // }

}

void sptuning() {

    turn(-1);
    straighten();
    turn(1);
    delay(500);
    int grids = 17;
    // while (grids-- != 0) {
    //     goAhead(1);
    //     delay(1000);
    // }
    while (1) {
        goAhead(11);
        turn(1);
        turn(1);
    }
    // turn(-1);
    // straighten();
    // turn(-1);
}

void turnAndGoTuning() {
    while (1) {
        turn(1);
        goAhead(1);
    }
}

void loop() {
    //sptuning();
    //dailyTuning();
    //turnAndGoTuning();
    //delay(1000);
    
    // while (1) {
    //     pwd = 4;
    //     parking();
    //     arriving(0);
    //     delay(1000);
    // }
    
    //explorationFLow();
    
    // while (1) {
    //     sensorReading();
    //     // delay(300);
    //     // if (isGoodObstacle()) {
    //     //     Serial.println("YES");
    //     // } else {
    //     //     Serial.println("NO");
    //     // }
    //     delay(300);
    // }

    waitForCommand();

    switch (getChar()) {
        case 'E': {
            explorationFLow();
            break;
        }
        case 'P': {
            Serial.println("START Shortest Path");
            bridesheadRevisited();
            break; 
        }
        case 'R': {
            remote();
            break;
        }
        default:
            // error
            break;
    }
}

void goToStart() {
    goalX = 1;
    goalY = 1;
    exploration();
    //Serial.println("I reach START");
    currentX = 2;
    currentY = 2;
    arriving(0);
}

void goToGoal() {
    goalX = 20;
    goalY = 15;
    exploration();
    //Serial.println("I reach GOAL");
    currentX = 19;
    currentY = 14;
    arriving(1);
}

void explorationFLow() {

    currentX = 10;
    currentY = 8;
    pwd = 1; //north
    while (!findWall());


    //START Exploration flow
    counter_for_straighten = stepToStraighten;
    //every 3 or 5 step do a straighten

    //CASE #1
    //West Wall
    
    if (currentX <= 2) {
        goToGoal();
        goToStart();
        goto FinishExploration;
    } else

    //CASE #2
    //South Wall
    if (currentY >= 14) {
        goToGoal();
        goToStart();
        turn(1);
        goToGoal();
        goToStart();
        goto FinishExploration;
    } else

    //CASE #3
    //East Wall
    if (currentX >= 19) {
        goToStart();
        turn(1);
        goToGoal();
        goToStart();
        goto FinishExploration;
    } else

    //CASE #4
    //North Wall
    if (currentY <= 2) {
        goToStart();
        turn(1);
        goToGoal();
        goToStart();
        goto FinishExploration;
    } else {
        goToStart();
        goto FinishExploration;
    }

    FinishExploration:

    JsonObject<2> toRPi;
    toRPi["type"] = "status";
    toRPi["data"] = "END_EXP";
    Serial.println(toRPi);
}

void sensorReading() {

    int i;

    i = 5;
    while (--i > 0 && ((u_F_dis = u_F.getDis()) == 0 || u_F_dis > 200)) {
        delay(2);
    }

    ir_rf_dis = shortIR_RF.getDis();
    ir_lf_dis = shortIR_LF.getDis();
    
    i = 5;
    while (--i > 0 && ((u_L_dis = u_L.getDis()) == 0 || u_L_dis > 200)) {
        delay(2);
    }

    ir_r_dis = shortIR_R.getDis();
    ir_l_dis = longIR_L.getDis();
    ir_l2_dis = shortIR_L.getDis();

    i = 5;
    while (--i > 0 && ((u_R_dis = u_R.getDis()) == 0 || u_R_dis > 200)) {
        delay(2);
    }

    thinkForAWhile();
}

void thinkForAWhile() {
    //think
    //send
    
    JsonObject<11> talk_Json;
    talk_Json["X"] = currentX;
    talk_Json["Y"] = currentY;
    talk_Json["direction"] = pwd;

    talk_Json["U_F"] = u_F_dis;
    talk_Json["U_R"] = u_R_dis;
    talk_Json["U_L"] = u_L_dis;

    talk_Json["short_LF"] = shortSensorToCM(ir_lf_dis);
    talk_Json["short_RF"] = shortSensorToCM(ir_rf_dis);

    talk_Json["short_FR"] = shortSensorToCM(ir_r_dis);
    talk_Json["short_FL"] = shortSensorToCM(ir_l2_dis);
    talk_Json["long_BL"] = longSensorToCM(ir_l_dis);
    
    JsonObject<2> toRPi;
    toRPi["type"] = "reading";
    toRPi["data"] = talk_Json;

    Serial.println(toRPi);
}

void exploration() {
    empty_space_R = 0;  
    
    while (abs(goalX - currentX) >= 3 || abs(goalY - currentY) >= 3) { 
        //get all sensor data here.
        sensorReading();
        if (shortSensorToCM(ir_r_dis) > 11) { //right got space
            if (++empty_space_R >= 3) {
                turn(1);
                empty_space_R = -1;
                counter_for_straighten = stepToStraighten - 1;
                continue;
            }
        } else {
            empty_space_R = 0;
            if (--counter_for_straighten == 0) {    //auto fix
                turn(1);    //turn right
                straighten();
                turn(-1);   //turn left
                counter_for_straighten = stepToStraighten;
                sensorReading();
            }
        }

        if (obstacleInFront()) {
            //Serial.println("something in front");
            straighten();
            if (u_F_dis > 12 && ir_lf_dis < 400 && ir_rf_dis > 400) {
                empty_space_R = 1;
            }
            turn(-1);   //turn left
            counter_for_straighten = stepToStraighten - 1;
            continue;
        }

        //default go ahead
        //if (u_F_dis > 12 && ir_rf_dis < 400 && ir_lf_dis < 400)
        //    then go
        goAhead(1);
    }
}

bool findWall() {
    //find the furthest obstacle
    //go and auto fix
    //go back
    //find farthest obstacle according to the distance
    //go that way
  
    /*
    HOWTO find furthest obstacle
    360 turning. use sensor to see the distance
     */
    // Serial.println("finding wall");
    sensorReading();
    int f_dis = min(shortSensorToCM(ir_rf_dis), shortSensorToCM(ir_lf_dis));
    if (u_F_dis > f_dis) {
        f_dis = u_F_dis;
    }
    
    int tempDis = f_dis * 5;
    int tempMin = 1;
    
    for (int i = 2; i <= 4; ++i) {
        turn(1);
        sensorReading();
        f_dis = min(shortSensorToCM(ir_rf_dis), shortSensorToCM(ir_lf_dis));
        if (u_F_dis > f_dis) {
            f_dis = u_F_dis;
        }
        if (i < 4) {
            f_dis *= 3;
        } else {
            f_dis *= 5;
        }
        if (tempDis < f_dis) {
            tempMin = i;
            tempDis = f_dis;
        }
    }
    // turn(1);
    // for (int i = 1; i < tempMin; ++i) {
    //     turn(1);
    // }
    switch (tempMin) {
        case 4:
            break;
        case 3:
            turn(-1);
            break;
        case 2:
            turn(1);
            turn(1);
        default:
            turn(1);
    }

    sensorReading();
    int farthestX = currentX;
    int farthestY = currentY;
    int farthestDis = 3;
    // Serial.println("Found furthest one");

    while (1) {
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
        if (obstacleInFront()) {
            break;
        }
        goAhead(1);
        sensorReading();
    }

    straighten();
    //auto fix
    if (isWithWall()) {
        turn(-1);
        return true;
    }

    /*
    HOWTO find fasest obstacle
    1. go back
    2. find the farthest distance
    take this as the wall?
    3. go to the wall
     */
    turn(1);
    turn(1);

    int grids2goback = abs(abs(farthestX) - currentX) + abs(abs(farthestY) - currentY)  ;
    //Serial.println("Go back =======>");
    //Serial.println(grids2goback);
    while (grids2goback > 0) {
        sensorReading();
        goAhead(1);
        grids2goback--;
    }

    if (farthestX < 0) {
        turn(-1); //was on the right. go back. turn left
    } else {
        turn(1);
    }
    //Serial.println("I found the wall");
    //found where is the wall
    
    //go to the wall
    while (1) {
        sensorReading();
        if (obstacleInFront()) {
            break;
        }
        goAhead(1);
    }

    straighten();

    turn(-1);
    //turn left
    //start stick2TheWall & turn right
    //job done
    if (isWithWall()) {
        return true;
    }
    return false;
}

void bridesheadRevisited() {
    //follow instruction

    currentX = 2;
    currentY = 2;
    getFRInstructions();

    JsonObject<2> toRPi;
    toRPi["type"] = "status";
    toRPi["data"] = "END_PATH";
    Serial.println(toRPi);
}

void remote() {
    char instrChar;
    int grids;

    while (1) {
        if (isDigit(instrChar = getChar())) {
            grids = instrChar - '0';
            goAhead(grids);
        }
        if (instrChar == 'R') {
            turn(1);
        } else if (instrChar == 'L') {
            turn(-1);
        } else if (instrChar == 'G') {
            break;
        }
    }

    JsonObject<2> toRPi;
    toRPi["type"] = "status";
    toRPi["data"] = "END_RMT";
    Serial.println(toRPi);
}

void parking_U() {
    int u_F_dis = u_F.getDis();
    while (u_F_dis > 10) {
        md.setSpeeds(190, 190);
        delay(3);
        u_F_dis = u_F.getDis();
    }
    brakeForRotation();
}

void parking() {
    int ir_dis = min(shortSensorToCM(shortIR_LF.getDis()), shortSensorToCM(shortIR_RF.getDis()));
    Serial.println(ir_dis);
    while (ir_dis > 15) {
        md.setSpeeds(190, 190);
        delay(3);
        ir_dis = min(shortSensorToCM(shortIR_LF.getDis()), shortSensorToCM(shortIR_RF.getDis()));
        Serial.println(ir_dis);
    }
    brakeForRotation();
}

void arriving(int endPoint) {
    //when near the end point
    //do nice stop
    if (endPoint) {
        if (pwd == 3) {
            parking();
            straighten();
            turn(-1); //face down/3
        }
        parking();
        straighten();
    
        //now face 3
        turn(1); // turn right
        parking();
        straighten();
        turn(1);
        turn(1);
    } else {
        if (pwd == 1) {
            parking();
            straighten();
            turn(-1); //face up/1
        }
        parking();
        straighten();

        //now face up 1
        turn(1); //turn right
        parking();
        straighten();
        turn(1);
    }
    //face to
    //==========
    //|| o -> 2
    //|| (1, 1)
    //|| 

    //
    //OR
    //
    //        1 ||
    //        ^ ||
    //        | ||
    //(20, 15)o ||
    //============

}


void getFRInstructions() {
    //get shortest path from RPi
    //then move

    int instrChar;
    grids = 0;
    int auto_alignment_counter = 5;

    while (1) {
        
        grids = 0;
        while (isDigit(instrChar = getChar())) {
            grids = grids * 10 + (char)instrChar - '0';
        }
        if (grids != 0) {
            goAhead(grids);
        }

        if (instrChar == 'R') {
            turn(1);
        } else if (instrChar == 'L') {
            turn(-1);
        } else if (instrChar == 'G') {
            parking();
            return;
        }
    }
}


/**************************************************************/
/***************************movement***************************/
/**************************************************************/



void goAhead(int grids) {
    MODE = 1;
    delta = 0;
    currentSpeed = speedModeSpeed;
    if (grids == 1) {
        rightMCtr = leftMCtr = RisingEdgePerGrid_300;
    } else {
        MODE = 2;
        currentSpeed = fastRunSpeed;
        rightMCtr = leftMCtr = RisingEdgeForSP * grids;
    }

    setTimerInterrupt();
    attachInterrupt(1, countRight, RISING);

    md.init();
    // md.setM2Speed(currentSpeed);
    // delay(4);
    // md.setM1Speed(currentSpeed);
    md.setSpeeds(currentSpeed, currentSpeed);
    while (--leftMCtr) {
        while (digitalRead(motor_L));
        while (!digitalRead(motor_L));
        //wait for one cycle
    }

    detachTimerInterrupt();
    detachInterrupt(1);

    brakeForGoAhead();
    
    //update direciton
    switch (pwd) {
        case 1: currentY -=grids;

                if (currentY < 2) {
                    currentY = 2;
                }
                break;
        case 2: currentX +=grids;
                if (currentX > 19) {
                    currentX = 19;
                }
                break;
        case 3: currentY +=grids;
                if (currentY > 14) {
                    currentY = 14;
                }
                break;
        case 4: currentX -=grids;
                if (currentX < 2) {
                    currentX = 2;
                }
                break;
        default: break;
    }
    //if (MODE != 2) {
        delay(50);
    //}
}

void turn(int turnRight) {
    MODE = 0;
    direction = turnRight;
    delta = 0;
    if (turnRight == 1) {
        rightMCtr = leftMCtr = RisingEdgePerTurnRight_200;
    } else {
        rightMCtr = leftMCtr = RisingEdgePerTurnLeft_200;
    }

    setTimerInterrupt();
    attachInterrupt(1, countRight, RISING);

    //sp.setSpeedLvls(3, 3);
    md.init();
    md.setSpeeds(-200 * direction, 200 * direction);
    while (--leftMCtr) {
        while (digitalRead(motor_L));
        while (!digitalRead(motor_L));
        //wait for one cycle
    }

    detachTimerInterrupt();
    detachInterrupt(1);

    brakeForRotation();
    
    //update direction
    pwd += turnRight;
    if (pwd == 5) {
        pwd = 1;
    } else if (pwd == 0) {
        pwd = 4;
    }
    delay(30);
}



void countRight() {
    --rightMCtr;
    delta = rightMCtr - leftMCtr;
}

void setTimerInterrupt() {
  cli();          // disable global interrupts
  // Timer/Counter Control Registers
  TCCR1A = 0;     // set entire TCCR1A register to 0
  TCCR1B = 0;     // same for TCCR1B

  // set compare match register to desired timer count:
  OCR1A = 1562;   // scale = 1024, OCR1A = (xxx / 64 / 1024)

  // turn on CTC mode:
  TCCR1B |= (1 << WGM12);
  // Set CS10 and CS12 bits for 1024 prescaler:
  TCCR1B |= (1 << CS10);
  TCCR1B |= (1 << CS12);

  //  enable timer compare interrupt:
  //  Timer/Counter Interrupt Mask Register
  //  Compare A&B interrupts 
  TIMSK1 |= (1 << OCIE1A);
  sei();          // enable global interrupts
}
void detachTimerInterrupt() {
  cli();
  TIMSK1 &= 0; // disable
  sei();
}
ISR(TIMER1_COMPA_vect) {
    if (MODE == 2) {
        md.setSpeeds(fastRunSpeed + delta, fastRunSpeed - delta);
    } else if (MODE == 1) {
        md.setM2Speed(speedModeSpeed - delta);
    } else {
        md.setM2Speed((slowSpeed - delta) * direction);
    }
}



/**************************************************************/
/***************************auto fix***************************/
/**************************************************************/


void straighten() {
    if (!isGoodObstacle()) {
        return;
    }
    adjustDistance();
    delay(50);
    adjustDirection();
    delay(80);
}

void adjustDirection() {
    //IR sensor make robot shake
    int l, r;
    for (int i = 0; i < 350; i++) {
        l = shortIR_LF.getDis();
        r = shortIR_RF.getDis();
        //delay(1);
        if (r > l) {
            md.setSpeeds(-adjustDirectionSpeed, adjustDirectionSpeed);
        } else if (r < l) {
            md.setSpeeds(adjustDirectionSpeed, -adjustDirectionSpeed);
        }
    }
    brakeForRotation();
}

void adjustDistance() {
    //IR sensor correct position
    int l, r, frontDis;
    for (int i = 0; i < 1200; i++) {
        l = shortIR_LF.getDis();
        r = shortIR_RF.getDis();
        delay(7);
        frontDis = max(l, r);
        
        if (frontDis < 505) {
            md.setSpeeds(adjustDistanceSpeed, adjustDistanceSpeed);
        } else if (frontDis > 510) {
            md.setSpeeds(-adjustDistanceSpeed, -adjustDistanceSpeed);
        } else {
            break;
        }
    }
    brakeForRotation();
}


/**************************************************************/
/*********************Functional Function**********************/
/**************************************************************/


bool isGoodObstacle() {
    sensorReading();
    int ir_rf_dis_cm = shortSensorToCM(ir_rf_dis);
    int ir_lf_dis_cm = shortSensorToCM(ir_lf_dis);

    return(ir_lf_dis_cm < 23
           && ir_rf_dis_cm < 23
           && abs(ir_rf_dis_cm - ir_lf_dis_cm) <= 7
           && abs(ir_rf_dis_cm - u_F_dis) <= 10);
}

bool isWithWall() {
    return(abs(currentX) <= 2 || currentX >= 19
           || abs(currentY) <= 2 || currentY >= 14);
}

bool obstacleInFront() {
    return(u_F_dis <= 14 || ir_lf_dis > 400 || ir_rf_dis > 400);
}

char getChar() {
    while (!Serial.available());
    return Serial.read();
}

void brakeForGoAhead() {
    for (int i = 3; i > 0; i--) {
        md.setBrakes(370, 400);
        //motor not start at the same time
        //not stop at the same time
        //make right motor skip a bit
        //to be same as left motor
    }
}

void brakeForRotation() {
    for (int i = 3; i > 0; i--) {
        md.setBrakes(400, 400);
    }
}