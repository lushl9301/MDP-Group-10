void straighten() {
    adjustDistance();
    adjustDirection();
}

void adjustDirection() {
    //Ultrasonic go until 5cm
    for (int i = 0; i < 1000; i++) {
        if (shortIR_RF.getDis() > shortIR_LF.getDis()) {
            md.setSpeeds(-60, 60);
        } else if (shortIR_RF.getDis() < shortIR_LF.getDis()) {
            md.setSpeeds(60, -60);
        }
    }
    md.setBrakes(400, 400);
}

void adjustDistance() {
    int frontDis = max(shortIR_LF.getDis(), shortIR_RF.getDis());
    for (int i = 0; i < 1000; i++) {
        if (frontDis < 450) {
            md.setSpeeds(100, 100);
        } else if (frontDis > 500) {
            md.setSpeeds(-100, -100);
        } else {
            break;
        }
    }
    md.setBrakes(400, 400);
}