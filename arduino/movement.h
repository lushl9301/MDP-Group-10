#include "Arduino.h"
#include "avr/io.h"    // hmm
#include "avr/interrupt.h" //

void goAhead(int grids) {
    direction = 1;
    delta = 0;
    timer = 0;
    rightMCtr = leftMCtr = RisingEdgePerGrid * grids;

    setTimerInterrupt();
    attachInterrupt(1, countRight, RISING);

    //sp.setSpeedLvls(3, 3);
    md.init();
    md.setSpeeds(400 * direction, 400 * direction);
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

void turn(int direction) {
    delta = 0;
    timer = 0;
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
  TIMSK1 = 0; // disable
  sei();
}

ISR(TIMER1_COMPA_vect) {
  if (speedMode == 0)
    md.setM1Speed((200 + leftCompensate) * neg);
  else
    md.setM1Speed((350 + leftCompensate) * neg);
}