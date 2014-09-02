#ifndef SharpA21_h
#define SharpA21_h

#define FilterLength 7

#include "DataSmoothing.h"

class SharpA21 {
public:
    
    SharpA21();
    
    init(int inputPin);
    
    int getDis();

private:
    int _inputPin;
    int _inputs[FilterLength];
    int _head;
    DataSmoothing _ds;
};

#endif

