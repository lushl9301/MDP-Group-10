import traceback
import threading
import socket
import sys
import json


class wifiThread (threading.Thread):
        mainthread = None
        connected = False
        piWifi = None

        def __init__(self, threadID, name):
            threading.Thread.__init__(self)
            self.threadID = threadID
            self.name = name

        def assignMainThread(self, mainThread):
            self.mainthread = mainThread

        def isConnected(self):
            return self.connected

        def send(self, json_data):
            if self.connected:
                    self.piWifi.send(json_data)
            else:
                print 'Wifi not established for sending data'

        def run(self):
            print "[ Wifi Thread Start ]"
            while 1:
                try:
                    self.piWifi = piWifi('192.168.10.10', 8888)
                    self.connected = True
                    while 1:
                        receivedJSON = self.piWifi.receive()
                        # code to stop everything
                        if receivedJSON["type"] == "CLEAR":
                            self.mainthread.flushCommandQueue()
                        else:
                            self.mainthread.addToQueue(receivedJSON)
                except IOError:
                    print "Wifi Receiving Exception"
                    print traceback.format_exc()
                finally:
                    self.piWifi.close()
                    self.connected = 0


class piWifi:
    # HOST = '192.168.10.10'
    HOST = 'localhost'
    PORT = 8888
    conn = None
    addr = None
    sock = None

    def __init__(self, HOST='localhost', PORT=8888):
        self.HOST = HOST
        self.PORT = PORT
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        print '[Socket Created]'

        try:
            self.sock.bind((self.HOST, self.PORT))

        except socket.error as msg:
            print 'Bind failed, Error Code' + str(msg[0]) + ' Message' + msg[1]
            sys.exit()

        print '[Socket Bind complete]'

        self.sock.listen(5)  # 5 is the usual known max queued connection
        print '[Socket Now Listening]'

        (self.conn, self.addr) = self.sock.accept()
        print 'Connected with:' + self.addr[0] + ':' + str(self.addr[1])

    def send(self, data):  # data is a dictionary
        json_string = json.dumps(data)
        if self.conn is not None:
            print 'Send To PC: ' + str(data)
            self.conn.send(json_string)

    def receive(self):
        if self.conn is None:
            return
        data = ''
        completeJSON = False
        while not completeJSON:
            buff = self.conn.recv(1)  # receive the data per char
            data += buff
            if buff == '}':  # detects the end of a JSON in buffer
                completeJSON = True
        # TODO: add catch block if decoding fails
        json_data = json.loads(data)
        print 'Receive From PC: ' + str(data)
        return json_data

    def close(self):
        self.conn.close()
        self.sock.close()

piWifi = piWifi()

while 1:
        print piWifi.receive()
