#include "SharpA02.h"

SharpA02::SharpA02() {
    ;
}

void SharpA02::init(int inputPin) {
    _inputPin = inputPin;
    for (int i = 0; i < FilterLength; ++i) {
        _inputs[i] = 500;
    }
}

int SharpA02::getDis() {
    int _temp;

    analogRead(_inputPin);
    analogRead(_inputPin);
    analogRead(_inputPin);

    for (int i = 0; i < 7; i++) {
        _inputs[i] = analogRead(_inputPin);
    }

    for (int i = 0; i < 7; i++) {
        for (int j = 0; j < 7; j++) {
            if (_inputs[i] > _inputs[j]) {
                _temp = _inputs[i];
                _inputs[i] = _inputs[j];
                _inputs[j] = _temp;
            }
        }
    }
    return (_inputs[3]);
}

int SharpA02::getDisCM() {
    int _raw = getDis();
    float voltFromRaw=map(_raw, 0, 1023, 0, 5000);
    return (int)(61.573*pow(voltFromRaw/1000, -1.1068));
}