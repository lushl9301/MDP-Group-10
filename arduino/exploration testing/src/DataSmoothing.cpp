#include "DataSmoothing.h"

DataSmoothing::DataSmoothing() {
    ;
}
DataSmoothing::~DataSmoothing() {
    ;
}

const int coef[7] = {
    13, 10, 7, 4, 1, -2, -5
};

void DataSmoothing::windowFilter7(int *ptr2RawData, int head) {
    // init
    // a[0,1,2,3,4,5,6]
    // head = 6; tail = 0;
    // new come in
    // a[head] = new;
    // head = head - 1; if head < 0 : head = 6;
    int t = 0;
    int tail = head + 1;
    if (tail > 6) {
        tail = 0;
    }
    int out = 0;
    while (head != tail) {
        out += *(ptr2RawData + (tail++)) * coef[t++];
        if (tail > 6) {
            tail = 0;
        }
    }
    out += *(ptr2RawData + tail) * coef[t];
    ++tail;
    if (tail > 6) {
      tail = 0;
    }
    if (out < 0) {
      *(ptr2RawData + tail) = 50;
    } else {
      *(ptr2RawData + tail) = out / 28;
    }
}
