#include "URM37.h"

URM37::URM37() {
}

uint8_t EnPwmCmd[4]={0x44,0x02,0xbb,0x01};
void URM37::init(int URPWM, int URTRIG) {
    _URPWM = URPWM;
    _URTRIG = URTRIG;
    
    pinMode(_URTRIG,OUTPUT);
    digitalWrite(_URTRIG,HIGH);
    
    pinMode(_URPWM, INPUT);
    
    // Sending Enable PWM mode command
    for(int i=0;i<4;i++){
        Serial.write(EnPwmCmd[i]);
    }
}

int URM37::getDis() {
    digitalWrite(_URTRIG, LOW);
    digitalWrite(_URTRIG, HIGH);
    
    return pulseIn(_URPWM, LOW) / 50;
}

