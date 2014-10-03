#include "SharpA21.h"

SharpA21::SharpA21() {
    ;
}

void SharpA21::init(int inputPin) {
    _inputPin = inputPin;
    //init random data into circular queue
    randomSeed(analogRead(_inputPin));
    for (int i = 0; i < FilterLength; ++i) {
        _inputs[i] = random(295, 305);
    }
    _head = 6;
}

int SharpA21::getDis() {
    int _results[7], _temp;
    for (int k = 0; k < 7; k++) {
        for (int i = 0; i < 5; i++) {
            _inputs[_head] = analogRead(_inputPin);
            --_head;
            if (_head < 0) {
                _head = 6;
            }
            _ds.windowFilter7(_inputs, _head);
        }
        _results[k] = _inputs[(_head + 1) % 7];
    }

    for (int i = 0; i < 7; i++) {
        for (int j = 0; j < 7; j++) {
            if (_results[i] > _results[j]) {
                _temp = _results[i];
                _results[i] = _results[j];
                _results[j] = _temp;
            }
        }
    }
    return (_results[3]);
}
