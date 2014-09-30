from piConfig import *
import traceback
import threading
import socket
import sys
import json
import errno


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
                print "Wifi not established for sending data"

        def run(self):
            print "[ Wifi Thread Start ]"
            while 1:
                try:
                    self.piWifi = piWifi(WIFI_IP, WIFI_PORT)
                    self.connected = True
                    while 1:
                        receivedJSON = self.piWifi.receive()
                        # code to stop everything
                        if receivedJSON == JSON_STOP:
                            self.mainthread.flushCommandQueue()
                        else:
                            self.mainthread.addToQueue(receivedJSON)
                except IOError, e:
                    if e.errno == errno.ECONNRESET:
                        print "ERROR: WIFI disconnected. Try resuming.."
                    else:
                        print "Wifi Thread Receive Exception: " + e.message
                        print traceback.format_exc()
                        pass
                finally:
                    self.connected = False
                    self.piWifi.close()


class piWifi:
    host = 'localhost'
    port = 8888
    conn = None
    addr = None
    sock = None

    def __init__(self, host='localhost', port=8888):
        self.host = host
        self.port = port
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        print "[Socket Created]"

        try:
            self.sock.bind((self.host, self.port))

        except socket.error as msg:
            print "WIFI bind failed. " + str(msg[0]) + ". msg:" + str(msg[1])
            sys.exit()

        print "[Socket Bind complete]"

        self.sock.listen(5)  # 5 is the usual known max queued connection
        print "[Socket Now Listening]"

        (self.conn, self.addr) = self.sock.accept()
        print "WIFI connected with:" + self.addr[0] + ":" + str(self.addr[1])

    def send(self, data):  # data is a dictionary
        json_string = json.dumps(data)
        if self.conn is not None:
            try:
                print "Send To Wifi: " + str(data)
                self.conn.send(json_string + "\n")
            except IOError, e:
                print "Wifi sending exception. " + e.message
                print traceback.format_exc()
                pass

    def receive(self):
        if self.conn is None:
            return
        data = ""
        completeJSON = False
        while not completeJSON:
            buff = self.conn.recv(1)  # receive the data per char
            data += buff
            if buff == "}":  # detects the end of a JSON in buffer
                completeJSON = True
        # TODO: add catch block if decoding fails
        json_data = json.loads(data)
        print "Receive From Wifi: " + str(data)
        return json_data

    def close(self):
        self.conn.close()
        self.sock.close()
