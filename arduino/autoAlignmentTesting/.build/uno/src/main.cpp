#include <Arduino.h>
#include "SharpA21.h"  // Short-distance
#include "MotorShield.h"
void straighten();
void adjustDirection();
void adjustDistance();
void setup();
void loop();
#line 1 "src/main.ino"
//#include "SharpA21.h"  // Short-distance
//#include "MotorShield.h"
//#include "Arduino.h"

#define shortIR_LF_in A4
#define shortIR_RF_in A5

SharpA21 shortIR_LF, shortIR_RF;

MotorShield md;

void straighten() {
    adjustDistance();
    delay(500);
    adjustDirection();
}

void adjustDirection() {
    //Ultrasonic go until 5cm
    int speed = 60;
    int l, r;
    for (int i = 0; i < 1000; i++) {
        l = shortIR_LF.getDis();
        r = shortIR_RF.getDis();
        delay(10);
        //Serial.println("LF" + String(l) + "     RF" + String(r));
        if (r > l) {
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
    for (int i = 0; i < 1000; i++) {
        l = shortIR_LF.getDis();
        r = shortIR_RF.getDis();
        delay(10);
        frontDis = max(l, r);
        //Serial.println("LF" + String(l) + "     RF" + String(r));
        
        if (frontDis < 500) {
            md.setSpeeds(speed, speed);
        } else if (frontDis > 550) {
            md.setSpeeds(-speed, -speed);
        } else {
            break;
        }
    }
    md.setBrakes(400, 400);
}

void setup() {
    
    Serial.begin(9600);
    //set up motor
    md.init();
    delay(10);

    //set up IR sensor
    shortIR_RF.init(shortIR_RF_in);
    shortIR_LF.init(shortIR_LF_in);

    delay(10);
}

void loop() {
    straighten();
    delay(5000);
}
