#ifndef SharpA02_h
#define SharpA02_h

#define FilterLength 7

#include "DataSmoothing.h"
#include "Arduino.h"

class SharpA02 {
public:
    
    SharpA02();
    
    void init(int inputPin);
    
    int getDis();

private:
    int _inputPin;
    int _inputs[FilterLength];
    int _head;
    DataSmoothing _ds;
};

#endif

