#include "SharpA02.h"

SharpA02::SharpA02() {
    ;
}

void SharpA02::init(int inputPin) {
    _inputPin = inputPin;
    //init random data into circular queue
    randomSeed(analogRead(_inputPin));
    for (int i = 0; i < FilterLength; ++i) {
        _inputs[i] = random(295, 305);
    }
    _head = 6;
}

int SharpA02::getDis() {
    for (int i = 0; i < 3; i++) {
        _inputs[_head] = analogRead(_inputPin);
        --_head;
        if (_head < 0) {
            _head = 6;
        }
        _ds.windowFilter7(_inputs, _head);
    }
    return (_inputs[(_head + 1) % 7]);
}
