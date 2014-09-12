void straighten() {
    adjustDistance();
    delay(500);
    adjustDirection();
}

void adjustDirection() {
    //Ultrasonic go until 5cm
    int speed = 60;
    int l, r;
    for (int i = 0; i < 1000; i++) {
        l = shortIR_LF.getDis();
        r = shortIR_RF.getDis();
        delay(10);
        if (r > l) {
            md.setSpeeds(-60, 60);
        } else if (r < l) {
            md.setSpeeds(60, -60);
        }
    }
    md.setBrakes(400, 400);
}

void adjustDistance() {
    int speed = 100;
    int l, r, frontDis;
    for (int i = 0; i < 1000; i++) {
        l = shortIR_LF.getDis();
        r = shortIR_RF.getDis();
        delay(10);
        frontDis = max(l, r);
        
        if (frontDis < 500) {
            md.setSpeeds(speed, speed);
        } else if (frontDis > 550) {
            md.setSpeeds(-speed, -speed);
        } else {
            break;
        }
    }
    md.setBrakes(400, 400);
}