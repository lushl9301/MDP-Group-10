#include "Cornering.h"

Cornering::Cornering() {
    ;
}

Cornering::Cornering(MotorShield& md) {
    _md = md;
}

void Cornering::init(MotorShield& md) {
    _md = md;
}

void Cornering::turnBack() {
    _md.brakeWithABS();
    delay(30);    
    _md.setSpeeds(400, -400);
    delay(200);
    _md.brakeWithABS();
}

void Cornering::turnLeft() {
    _md.brakeWithABS();
    delay(30);
    _md.setSpeeds(400, -400);
    delay(100);
    _md.brakeWithABS();
}

void Cornering::turnRight() {
    _md.brakeWithABS();
    delay(30);
    _md.setSpeeds(-400, 400);
    delay(100);
    _md.brakeWithABS();
}

void Cornering::clockyTurn(int hour) {
    //TODO add rotation with angle
    ;
}

void Cornering::RESTORE() {
    //TODO finish compass first
    ;
}
