#include "SharpA21.h"

SharpA21::SharpA21() {
    ;
}

void SharpA21::init(int inputPin) {
    _inputPin = inputPin;
    //init random data into circular queue
    randomSeed(analogRead(_inputPin));
    for (int i = 0; i < FilterLength; ++i) {
        _input[i] = random(295, 305);
    }
    _head = 6;
}

int SharpA21::getDis() {
    _inputs[_head] = analogRead(_inputPin);
    --_head;
    if (_head < 0) {
        _head = 6;
    }
    delay(100);
    //replace head with the filted value and return
    _ds.windowFilter7(_inputs, _head);
    return (inputs[(_head + 1) % 7]);
}
