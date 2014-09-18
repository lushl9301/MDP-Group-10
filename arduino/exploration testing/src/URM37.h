#ifndef URM37_h
#define URM37_h

#include "Arduino.h"

class URM37 {
public:
    URM37();
    
    void init(int URPWM, int URTRIG);
    
    int getDis();
    
private:
    int _URPWM;
    int _URTRIG;
};

#endif

