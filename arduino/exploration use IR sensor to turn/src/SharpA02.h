#ifndef SharpA02_h
#define SharpA02_h

#define FilterLength 7

#include "Arduino.h"

class SharpA02 {
public:
    
    SharpA02();
    
    void init(int inputPin);
    
    int getDis();
    int getDisCM();

private:
    int _inputPin;
    int _inputs[FilterLength];
};

#endif

