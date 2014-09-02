#include <Arduino.h>
#include "MotorShield.h"
#include "Speed.h"
void stopIfFault();
void setup();
void loop();
#line 1 "src/sketch.ino"
//#include "MotorShield.h"
//#include "Speed.h"

MotorShield md;
Speed sp;
void stopIfFault() {
  if (md.getM1Fault()) {
    Serial.println("M1 fault");
    while(1);
  }
  if (md.getM2Fault()) {
    Serial.println("M2 fault");
    while(1);
  }
}

void setup() {
  Serial.begin(115200);
  Serial.println("Dual VNH5019 Motor Shield");
  md.init();
  sp.init(md);
}

void loop() {
  for (int i = 1; i <= 4; i++) {
    sp.setM1SpeedLvl(i);
    //md.setM1Speed(i);
    stopIfFault();
    Serial.print("M1 current: ");
    Serial.println(md.getM1CurrentMilliamps());
    delay(1000);
  }
  
  for (int i = 4; i >= 1; i--) {
    sp.setM1SpeedLvl(0 - i);
    //md.setM1Speed(i);
    stopIfFault();
    Serial.print("M1 current: ");
    Serial.println(md.getM1CurrentMilliamps());
    delay(1000);
  }
  
  for (int i = -400; i <= 0; i++) {
    md.setM1Speed(i);
    stopIfFault();
    if (i%200 == 100)
    {
      Serial.print("M1 current: ");
      Serial.println(md.getM1CurrentMilliamps());
    }
    delay(2);
  }

  for (int i = 0; i <= 400; i++)
  {
    md.setM2Speed(i);
    stopIfFault();
    if (i%200 == 100)
    {
      Serial.print("M2 current: ");
      Serial.println(md.getM2CurrentMilliamps());
    }
    delay(2);
  }
  
  for (int i = 400; i >= -400; i--)
  {
    md.setM2Speed(i);
    stopIfFault();
    if (i%200 == 100)
    {
      Serial.print("M2 current: ");
      Serial.println(md.getM2CurrentMilliamps());
    }
    delay(2);
  }
  
  for (int i = -400; i <= 0; i++)
  {
    md.setM2Speed(i);
    stopIfFault();
    if (i%200 == 100)
    {
      Serial.print("M2 current: ");
      Serial.println(md.getM2CurrentMilliamps());
    }
    delay(2);
  }
}
