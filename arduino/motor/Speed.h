#ifndef Speed_h
#define Speed_h

#include "MotorShield.h"

class Speed {
public:
    Speed();
    Speed(MotorShield& md);
    
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
