from piConfig import *
import socket
import json
from sys import stdin


def modeReading():
    while 1:
        print "input x: "
        data_x = stdin.readline().strip()
        if data_x == "-1":
            print "=======[ EXIT ]======="
            break
        print "input y: "
        data_y = stdin.readline().strip()
        print "input direction: "
        data_direction = stdin.readline().strip()
        data_data = {"X": data_x, "Y": data_y, "direction": data_direction}
        data = {"type": "reading", "data": data_data}
        print ">>>>>>> SENDING >>>>>>>"
        json_string = json.dumps(data)
        sock.send(json_string + "\n")

f = open("try.log", "w")
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)

sock.connect((WIFI_IP, WIFI_PORT))

while 1:
    print "input type: "
    data_type = stdin.readline().strip()
    if data_type == "reading":
        modeReading()
    elif data_type == "-1":
        print "EXIT"
        f.close()
        break
    else:
        print "input data: "
        data_data = stdin.readline().strip()
        data = {"type": data_type, "data": data_data}
        print ">>>>>>> SENDING >>>>>>>"
        json_string = json.dumps(data)
        f.write(json.dumps(data, indent=4)+"\n")
        sock.send(json_string + "\n")
