#include <Arduino.h>
#include "SharpA21.h"  // Short-distance
#include "URM37.h"     // Ultrasonic
void setup();
void loop();
#line 1 "src/tgt.ino"
//#include "SharpA21.h"  // Short-distance
//#include "SharpA02.h"  // Long-distance
//#include "URM37.h"     // Ultrasonic

//digital 0, 1 are not alow to use ==> serial port is using
//digital 2, 3, 4, 7, 8, 9, 10, 13 are occuppied by motor shield
//

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

URM37 u_F, u_L, u_R;
SharpA21 shortIR_LF, shortIR_RF, shortIR_R, longIR_L;

int u_F_dis, u_R_dis, u_L_dis;
int ir_rf_dis, ir_lf_dis, ir_l_dis, ir_r_dis;

void setup() {
    Serial.begin(9600);

    u_F.init(urPWM_F, urTRIG);
    u_L.init(urPWM_L, urTRIG);
    u_R.init(urPWM_R, urTRIG);
    delay(10);
    
    //set up IR sensor
    shortIR_RF.init(shortIR_RF_in);
    shortIR_LF.init(shortIR_LF_in);

    shortIR_R.init(shortIR_R_in);
    longIR_L.init(longIR_L_in);
}

void loop() {

    u_F_dis = u_F.getDis();
    u_L_dis = u_L.getDis();
    u_R_dis = u_R.getDis();
    
    ir_rf_dis = shortIR_RF.getDis();
    ir_lf_dis = shortIR_LF.getDis();

    ir_r_dis = shortIR_R.getDis();
    ir_l_dis = longIR_L.getDis();

    Serial.println("UF " + String(u_F_dis));
    Serial.println("IRLF " + String(ir_lf_dis));
    Serial.println("IRRF " + String(ir_rf_dis));

    Serial.println("UL " + String(u_L_dis));
    Serial.println("IRL " + String(ir_l_dis));
    
    Serial.println("UR " + String(u_R_dis));
    Serial.println("IRR " + String(ir_r_dis));
    Serial.println("\n\n\n");

    delay(1200);
}