#ifndef SharpA21_h
#define SharpA21_h

#define FilterLength 7

#include "Arduino.h"

class SharpA21 {
public:
    
    SharpA21();
    
    void init(int inputPin);
    
    int getDis();
    int getDisCM();

private:
    int _inputPin;
    int _inputs[FilterLength];
};

#endif

