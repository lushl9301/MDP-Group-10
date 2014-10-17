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
            if self.piArduino is None:
                print "Cannot send data to robot. Robot is gone"
                return
            self.piArduino.send(json_data)

        def sendStart(self):
            if self.piArduino is None:
                print "Cannot send START to robot. Robot is gone"
                return
            self.piArduino.send(JSON_START)

        def sendStop(self):
            if self.piArduino is None:
                print "Cannot send STOP to robot. Robot is gone"
                return
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

    sensorLog = None

    def __init__(self):
        while 1:
            try:
                arduinoPort = glob.glob("/dev/ttyACM*")[0]
                self.ser = serial.Serial(arduinoPort, 9600)
                print "Arduino Connected"
                break
            except IndexError:
                print "Trying to reconnect Arduino.."
                pass

    def send(self, json_data):
        command = json_data["data"]
        try:
            # if command == "E":
            #     self.sensorLog = open("sensor.log", "w")
            self.ser.write(command)
            print "Send to Arduino: " + command
        except AttributeError:
            print "WARNING! Arduino still not connected."
            pass

    def receive(self):
        try:
            json_string = self.ser.readline().strip()
            try:
                json_data = json.loads(json_string)
                # if (json_data["type"] == "reading"):
                #     logJSON(json_data, self.sensorLog)
                # elif (json_data["data"] == "END_EXP"):
                #     self.sensorLog.close()
                print "ROBOT:\n" + json.dumps(json_data, indent=4) + "\n"
                return json_data
            except (ValueError, TypeError):
                print "From robot: " + json_string
                pass
        except (serial.SerialException, AttributeError):
            pass
