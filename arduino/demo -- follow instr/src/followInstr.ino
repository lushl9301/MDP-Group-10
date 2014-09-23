#include "avr/io.h"    // hmm
#include "avr/interrupt.h" //

#include "SharpA21.h"  // Short-distance
#include "SharpA02.h"  // Long-distance
#include "URM37.h"     // Ultrasonic
#include "MotorShield.h"

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
#define RisingEdgePerGrid 380 // need testing
#define RisingEdgePerTurn_200 412 //for speed 200
#define stepToStraighten 5 //every 5 step make a auto adjust


volatile int direction;
volatile int delta;
volatile int rightMCtr, leftMCtr;
volatile int speedMode;  //1 for fast(400), 0 for slow(200)

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
    pinMode(motor_R, INPUT);
    pinMode(motor_L, INPUT);
}

void waitForCommand() {
    /**/
    delay(1000);
    return;
    /**/
    while (!Serial.available() || Serial.read() != 'S') {
        ;
    }
    delay(10);
}

void setup() {
    
    Serial.begin(9600);
    setPinsMode();

    //set up motor
    md.init();
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
    // while (1) {
    //     turn(1);
    //     delay(400);
    // }
    waitForCommand();
    //getXY()?
    followInstr();
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
    Serial.println("==========================");
    Serial.println("UF " + String(u_F_dis));
    Serial.println("IRLF " + String(ir_lf_dis));
    Serial.println("IRRF " + String(ir_rf_dis));

    Serial.println("UL " + String(u_L_dis));
    Serial.println("IRL " + String(ir_l_dis));
    
    Serial.println("UR " + String(u_R_dis));
    Serial.println("IRR " + String(ir_r_dis));
    Serial.println("___________________________");
    delay(200);
}

void exploration() {
    empty_space_R = 0;
    while (abs(goalX - currentX) >= 2 && abs(goalY - currentY) >= 2) {
        //check right
        
        //get all sensor data here.
        sensorReading();

        if (u_R_dis > 12) { //right got space
            ++empty_space_R;
            Serial.println("RRRRRRR");
            if (empty_space_R >= 2) {
                turn(1);
                empty_space_R = 0;
                continue;
            }
        } else {
            empty_space_R = 0;
            Serial.println("right no space");
            if (--counter_for_straighten == 0) {    //auto fix
                turn(1);    //turn right
                straighten();
                turn(-1);   //turn left
                counter_for_straighten = 5;
            }
        }

        if (u_F_dis <= 6) {
            Serial.println("shit in front");
            //straighten();
            turn(-1);   //turn left
            empty_space_R = 0;
            continue;
        }

        //TODO testing
        //default go ahead
        //if (u_F_dis > 12 && ir_rf_dis < 400 && ir_lf_dis < 400) {
            //can go
            goAhead(1);
        //}
    }
}

char getChar() {
    while (!Serial.available());
    return Serial.read();
}

void arriving(int endPoint) {
    //TODO
    //when near the end point
    //do nice stop
    if (!endPoint) {
        if (pwd == 4) {
            turn(-1); //face down/3
        }
        straighten();
    
        //now face 3
        turn(1); // turn right
        ++pwd;
        straighten();
        turn(1);
        turn(1);
    } else {
        if (pwd == 2) {
            turn(-1); //face up/1
        }
        straighten();

        //now face up 1
        turn(1); //turn right
        ++pwd;
        straighten();
        turn(1);
        turn(1);
    }
    //face to
    //||
    //|| (1, 1)
    //|| o -> 2
    //==========
    //
    //OR
    //
    //===========
    //  4 <- o ||
    // (20, 15)||
    //         ||
    //

}

void followInstr() {
    //get shortest path from RPi
    //then move

    char instrChar;
    int grids;
    while (1) {
        sensorReading();
        while (isDigit(instrChar = getChar())) {
            grids = grids * 10 + instrChar - '0';
        }
        while (grids > 0) {
            goAhead(1);
            --grids;
        }
        if (instrChar == 'R') {
            turn(1);
        } else if (instrChar == 'L') {
            turn(-1);
        } else if (instrChar == 'G') {
            arriving(1);
        } else if (instrChar == 'A') {
            straighten();
        } else if (instrChar == 'S') {
            return;
        }
    }
}


/**************************************************************/
/***************************movement***************************/
/**************************************************************/



void goAhead(int grids) {
    speedMode = 1;
    direction = 1;
    delta = 0;
    rightMCtr = leftMCtr = RisingEdgePerGrid * grids;

    setTimerInterrupt();
    attachInterrupt(1, countRight, RISING);

    md.init();
    md.setSpeeds(250, 250);
    while (--leftMCtr) {
        while (digitalRead(motor_L));
        while (!digitalRead(motor_L));
        //wait for one cycle
    }

    detachTimerInterrupt();
    detachInterrupt(1);

    md.brakeWithABS();
    
    //update direciton
    switch (pwd) {
        case 1: currentY +=grids;
                break;
        case 2: currentX +=grids;
                break;
        case 3: currentY -=grids;
                break;
        case 4: currentX -=grids;
                break;
        default: break;
    }
    delay(3000);
}

void turn(int turnRight) {
    speedMode = 0;
    direction = turnRight;
    delta = 0;
    rightMCtr = leftMCtr = RisingEdgePerTurn_200;

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

    md.brakeWithABS();
    
    //update direction
    pwd += turnRight;
    if (pwd == 5) {
        pwd = 1;
    } else if (pwd == 0) {
        pwd = 4;
    }
    delay(3000);
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
    if (speedMode) {
        md.setM2Speed((250 - delta*5) * direction);
    } else {
        md.setM2Speed((200 - delta*5) * direction);
    }
}



/**************************************************************/
/***************************auto fix***************************/
/**************************************************************/


void straighten() {
    adjustDistance();
    delay(50);
    adjustDirection();
    delay(50);
}

void adjustDirection() {
    //Ultrasonic go until 5cm
    int speed = 60;
    int l, r;
    for (int i = 0; i < 800; i++) {
        l = shortIR_LF.getDis();
        r = shortIR_RF.getDis();
        delay(10);
        if (r > l + 20) { //TODO WARNING
            md.setSpeeds(-60, 60);
        } else if (r < l) {
            md.setSpeeds(60, -60);
        }
    }
    md.setBrakes(400, 400);
}

void adjustDistance() {
    int speed = 100;
    int l, r, frontDis;
    for (int i = 0; i < 500; i++) {
        l = shortIR_LF.getDis();
        r = shortIR_RF.getDis();
        delay(6);
        frontDis = max(l, r);
        
        if (frontDis < 450) {
            md.setSpeeds(speed, speed);
        } else if (frontDis > 500) {
            md.setSpeeds(-speed, -speed);
        } else {
            break;
        }
    }
    md.setBrakes(400, 400);
}