#include "MotorShield.h"
#include "Speed.h"
#include "avr/io.h"
#include "avr/interrupt.h"
#include "URM37.h"

#define motor_L 11  // encoder
#define motor_R 3  // encoder
#define urPWM_R A2
#define urTRIG_R A3

#define RisingEdgePerGrid 400
#define RisingEdgePerTurn_200 367

MotorShield md;
URM37 u_R;

volatile int direction;
volatile int delta;
volatile int rightMCtr, leftMCtr;
volatile long timer;

void setup() {
    pinMode(motor_R, INPUT);
    pinMode(motor_L, INPUT);
    Serial.begin(9600);
    u_R.init(urPWM_R, urTRIG_R);
}
void loop() {
  turn();
  Serial.print("distance = ");
  Serial.println(u_R.getDis());
  delay(1000);
}

void turn() {
    direction = 1;
    delta = 0;
    timer = 0;
    rightMCtr = leftMCtr = 367;

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
    Serial.println(delta);
    Serial.println(timer);
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
    md.setM2Speed((200 - delta*5) * direction);
    ++timer;
}
