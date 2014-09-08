#ifndef Wheels_h
#define Wheels_h

#include "MotorShield.h"

class Wheels {
public:
    Wheels();
    Wheels(MotorShield& md);
    
    void init(MotorShield& md);
    void setM1SpeedLvl(int lvl);
    void setM2SpeedLvl(int lvl);
    void setSpeedLvls(int m1Lvl, int m2Lvl);
    void setM1BrakeLvls(int lvl);
    void setM2BrakeLvls(int lvl);
    void setBrakeLvls(int m1Lvl, int m2Lvl);
    
private:
    MotorShield _md;
    
};

#endif
