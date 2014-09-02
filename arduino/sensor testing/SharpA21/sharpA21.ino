#include "DataSmoothing.h"
#define N 7

int inputs[N];
int head;
DataSmoothing ds;

void setup() {
    Serial.begin(9600);
    randomSeed(analogRead(0));
    for (int i = 0; i < N; ++i) {
        inputs[i] = random(295, 305);
    }
    head = 6;
}

void loop() {
    int distance;

    distance = analogRead(0);
    inputs[head] = distance;
    --head;
    if (head < 0) {
        head = 6;
    }
    delay(100);
    Serial.print(distance);
    Serial.print("        ");
    ds.windowFilter7(inputs, head);
    Serial.println(inputs[(head + 1) % 7]);
}

