#include "Wheels.h"

Wheels::Wheels() {
    ;
}

Wheels::Wheels(MotorShield& md) {
    _md = md;
}

void Wheels::init(MotorShield& md) {
    _md = md;
}

void Wheels::setM1SpeedLvl(int lvl) {
    switch (lvl) {
        case 1:
            _md.setM1Speed(100);
            break;
        case 2:
            _md.setM1Speed(200);
            break;
        case 3:
            _md.setM1Speed(300);
            break;
        case 4:
            _md.setM1Speed(400);
            break;
        case -1:
            _md.setM1Speed(-100);
            break;
        case -2:
            _md.setM1Speed(-200);
            break;
        case -3:
            _md.setM1Speed(-300);
            break;
        case -4:
            _md.setM1Speed(-400);
            break;
        default:
            _md.setM1Speed(100);
            break;
    }
}

void Wheels::setM2SpeedLvl(int lvl) {
    switch (lvl) {
        case 1:
            _md.setM2Speed(100);
            break;
        case 2:
            _md.setM2Speed(200);
            break;
        case 3:
            _md.setM2Speed(300);
            break;
        case 4:
            _md.setM2Speed(400);
            break;
        case -1:
            _md.setM2Speed(-100);
            break;
        case -2:
            _md.setM2Speed(-200);
            break;
        case -3:
            _md.setM2Speed(-300);
            break;
        case -4:
            _md.setM2Speed(-400);
            break;
        default:
            _md.setM2Speed(100);
            break;
    }
}

void Wheels::setSpeedLvls(int m1Lvl, int m2Lvl) {
    setM1SpeedLvl(m1Lvl);
    setM2SpeedLvl(m2Lvl);
}

void Wheels::setM1BrakeLvls(int lvl) {
    ;
}

void Wheels::setM2BrakeLvls(int lvl) {
    ;
}

void Wheels::setBrakeLvls(int m1Lvl, int m2Lvl) {
    ;    
}
