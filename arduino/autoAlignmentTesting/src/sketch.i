#include "SharpA21.h"  // Short-distance
//#include "Arduino.h"

/**********************/
#define shortIR_LF_in A4
#define shortIR_RF_in A5

SharpA21 shortIR_LF, shortIR_RF;

void setup() {
    
    Serial.begin(9600);
    shortIR_LF.init(shortIR_LF_in);
    shortIR_RF.init(shortIR_RF_in);
}

void loop() {
    Serial.println("L" + String(shortIR_LF.getDis()) + "    R" + String(shortIR_RF.getDis()));
    delay(200);
}