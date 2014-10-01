from piConfig import *
from bluetooth import *
import json
import traceback
import threading
import Queue
import errno


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
                self.piBT.send(json_data)

        def run(self):
            print "[ BT Thread Start ]"
            while 1:
                self.piBT = piBT()
                self.connected = True
                try:
                    while 1:
                        receivedJSON = self.piBT.receive()
                        # code to stop everything
                        if receivedJSON == JSON_STOP:
                            self.mainthread.flushCommandQueue()
                        else:
                            self.mainthread.addToQueue(receivedJSON)
                except IOError, e:
                    if e.errno == errno.ECONNRESET:
                        print "ERROR: BT disconnected. Try resuming.."
                    else:
                        print "BT Thread Receive Exception: " + e.message
                        print traceback.format_exc()
                    pass
                finally:
                    self.connected = False
                    self.piBT.close()


class piBT:
    server_sock = None
    port = None
    client_sock = None
    client_info = None
    buff = None

    def __init__(self):
        self.server_sock = BluetoothSocket(RFCOMM)
        self.server_sock.bind(("", 1))
        self.server_sock.listen(10)
        self.port = self.server_sock.getsockname()[1]
        advertise_service(self.server_sock, BT_SERVER_NAME,
                          service_id=BT_UUID,
                          service_classes=[BT_UUID, SERIAL_PORT_CLASS],
                          profiles=[SERIAL_PORT_PROFILE],
                          )
        print "Waiting for BT connection on RFCOMM channel " + str(self.port)
        self.client_sock, self.client_info = self.server_sock.accept()
        print "Accepted BT connection from ", self.client_info

        # instantiate file buffer for receival
        if self.conn is not None:
            self.buff = self.client_sock.makefile("r")

    def receive(self):
        if self.conn is None:
            return
        return receiveJSON(self.buff, "BT")

    def send(self, data):
        if self.client_sock is not None:
            try:
                json_string = json.dumps(data)
                self.client_sock.send(json_string)
                print "Send to BT: " + str(data)
            except IOError, e:
                print "Bluetooth Sending Exception: " + e.message
                print traceback.format_exc()
                pass

    def close(self):
        self.client_sock.close()
        self.server_sock.close()
