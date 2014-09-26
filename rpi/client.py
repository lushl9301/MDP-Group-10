import socket
import json
from sys import stdin


sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)

sock.connect(('192.168.10.10', 8888))

while 1:
    print "input status: "
    data_type = stdin.readline().strip()
    print "input data: "
    data_data = stdin.readline().strip()
    data = data = {"type": data_type, "data": data_data}
    print ">>>>>>> SENDING >>>>>>>"
    json_data = json.dumps(data)
    sock.send(json_data)
