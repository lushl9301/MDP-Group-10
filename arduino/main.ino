#include "SharpA21.h"  // Short-distance
#include "SharpA02.h"  // Long-distance
#include "URM37.h"     // Ultrasonic
#include "HMC5883L.h"  // Digital Compass
#include "Speed.h"     // Set Speed
#include "Cornering.h" // Turning
#include "avr/io.h"    // hmm
#include "avr/interrupt.h" //

//PWM for reading ==> orange
//TRIG for writing ==> yellow  
#define urPWM_F 15
#define urTRIG_F 14

#define urPWM_L 1
#define urTRIG_L 0

#define urPWM_R 13
#define urTRIG_R 11

#define motor_L 3  // encoder
#define motor_R 5  // encoder

/**********************/
#define shortIR_LF_in A4
#define shortIR_LR_in A5
//#define longIR_F_in A4
/**********************/
#define RisingEdgePerGrid 400 // need testing
#define RisingEdgePerTurn 400 // need testing


URM37 u_F, u_L, u_R;
SharpA02 longIR_F;
SharpA21 shortIR_LF, shortIR_LR;

MotorShield md;
Speed sp;
Cornering cn;

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
    u_F.init(urPWM_F, urTRIG_F);
    u_L.init(urPWM_L, urTRIG_L);
    u_R.init(urPWM_R, urTRIG_R);
    delay(10);
    
    //set up IR sensor
    shortIR_LR.init(shortIR_LR_in);
    shortIR_LF.init(shortIR_LF_in);
    //longIR_F.init(longIR_F_in);
    delay(10);
}

void loop() {
    waitForCommand();
    exploration();
    waitForCommand();
    //memoryLane();
    bridesheadRevisited();
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
  TIMSK1 = 0; // disable
  sei();
}
ISR(TIMER1_COMPA_vect) {
  if (speedMode == 0)
    md.setM1Speed((200 + leftCompensate) * neg);
  else
    md.setM1Speed((350 + leftCompensate) * neg);
}