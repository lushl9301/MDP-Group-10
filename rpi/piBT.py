from bluetooth import *
import json
import traceback
import threading
import Queue


class btThread(threading.Thread):
        mainthread = None
        connected = False
        piBT = None
        dataDropped = Queue.Queue()

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
                self.piBT.send(data)

        def run(self):
            print "[ BT Thread Start ]"
            while 1:
                    self.piBT = piBT()
                    self.connected = True
                    try:
                        while 1:
                            receivedJSON = self.piBT.receive()
                            # code to stop everything
                            if receivedJSON["type"] == "STOP":
                                self.mainthread.flushCommandQueue()
                            else:
                                self.mainthread.addToQueue(receivedJSON)
                    except IOError, e:
                            print "BT Thread Receive Exception: " + e.message
                            print traceback.format_exc()
                            pass
                    finally:
                        self.piBT.close()
                        self.connected = False


class piBT:
    uuid = "00001101-0000-1000-8000-00805F9B34FB"
    server_sock = None
    port = None
    client_sock = None
    client_info = None

    def __init__(self):
        self.server_sock = BluetoothSocket(RFCOMM)
        self.server_sock.bind(("", 1))
        self.server_sock.listen(10)
        self.port = self.server_sock.getsockname()[1]
        advertise_service(self.server_sock, "MDP10_BTServer",
                          service_id=self.uuid,
                          service_classes=[self.uuid, SERIAL_PORT_CLASS],
                          profiles=[SERIAL_PORT_PROFILE],
                          )
        print "Waiting for BT connection on RFCOMM channel " + str(self.port)
        self.client_sock, self.client_info = self.server_sock.accept()
        print "Accepted BT connection from ", self.client_info

    def receive(self):
        if self.client_sock is None:
                return
        try:
                data = ''
                completeJSON = False
                while not completeJSON:
                    # receive the data per char
                    buff = self.client_sock.recv(1)
                    data += buff
                    if buff == '}':  # detects the end of a JSON in buffer
                        completeJSON = True
                # TODO: add catch block if decoding fails
                json_data = json.loads(data)
                if completeJSON:
                        print "Received From Bluetooth: " + str(data)
                        return json_data
        except IOError, e:
                print "Bluetooth Receive Exception: " + e.message
                print traceback.format_exc()
                pass

    def send(self, data):
        if self.client_sock is None:
                return
        try:
                json_string = json.dumps(data)
                self.client_sock.send(json_string)
        except IOError, e:
                print "Bluetooth Sending Exception: " + e.message
                print traceback.format_exc()
                pass

    def close(self):
        self.client_sock.close()
        self.server_sock.close()
