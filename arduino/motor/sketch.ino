#include "MotorShield.h"
#include "Speed.h"

MotorShield md;
Speed sp;

void setup() {
  Serial.begin(115200);
  Serial.println("Dual VNH5019 Motor Shield");
  md.init();
  sp.init(md);
}

void loop() {
  for (int i = 1; i <= 4; i++) {
    sp.setM1SpeedLvl(i);
    sp.setM2SpeedLvl(i);
    //md.setM1Speed(i);
    Serial.print("M1 current: ");
    Serial.println(md.getM1CurrentMilliamps());
    delay(1000);
  }
  
  for (int i = 4; i >= 1; i--) {
    sp.setM1SpeedLvl(0 - i);
    sp.setM2SpeedLvl(0 - i);
    //md.setM1Speed(i);
    Serial.print("M1 current: ");
    Serial.println(md.getM1CurrentMilliamps());
    delay(1000);
  }
}
