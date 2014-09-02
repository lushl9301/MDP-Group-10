#ifndef Cornering_h
#define Cornering_h

#include "MotorShield.h"

class Cornering {
public:
    Cornering();
    Cornering(MotorShield& md);
    
    void init(MotorShield& md);
    
    void turnBack();
    void turnLeft();
    void turnRight();
    void clockyTurn(int hour);
    
    void RESTORE(); //restore robot to where it face

private:
    MotorShield _md;
};
#endif
