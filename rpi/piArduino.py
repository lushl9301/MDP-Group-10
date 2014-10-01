from piConfig import *
import serial
import threading
import json
import traceback
import glob


class arduinoThread(threading.Thread):
        mainthread = None
        piArduino = None

        def __init__(self, threadID, name):
            threading.Thread.__init__(self)
            self.threadID = threadID
            self.name = name

        def assignMainThread(self, mainThread):
            self.mainthread = mainThread

        def send(self, json_data):
            self.piArduino.send(json_data)

        def sendStart(self):
            self.piArduino.send(JSON_START)

        def sendStop(self):
            self.piArduino.send(JSON_STOP)

        def run(self):
            print "[ Arduino Thread Start ]"
            self.piArduino = piArduino()

            while 1:
                try:
                    receivedJSON = self.piArduino.receive()
                    self.mainthread.addToQueue(receivedJSON)
                except IOError, e:
                    print "Arduino Thread Receive Exception: " + e.message
                    print traceback.format_exc()
                    pass


class piArduino:
    def __init__(self):
        arduinoPort = glob.glob("/dev/ttyACM*")[0]
        self.ser = serial.Serial(arduinoPort, 9600)
        print "Arduino Connected"

    def send(self, json_data):
        command = json_data["data"]
        self.ser.write(command)
        print "Send to Arduino: " + command

    def receive(self):
        try:
            json_string = self.ser.readline().strip()
            try:
                json_data = json.loads(json_string)
                print "[ From Arduino receive: " + str(json_data) + " ]"
                return json_data
            except ValueError:
                # print "ERROR: decoding JSON from arduino. " + e.message
                print "From robot: " + json_string
                pass
        except SerialException:
            print "Disconnected from Arduino"
            pass
