from piConfig import *
import socket
import json
from sys import stdin


sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)

sock.connect((WIFI_IP, WIFI_PORT))

while 1:
    print "input type: "
    data_type = stdin.readline().strip()
    print "input data: "
    data_data = stdin.readline().strip()
    data = {"type": data_type, "data": data_data}
    print ">>>>>>> SENDING >>>>>>>"
    json_string = json.dumps(data)
    sock.send(json_string + "\n")
