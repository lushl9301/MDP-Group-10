import threading
import traceback
import Queue

from piArduino import arduinoThread
from piWifi import wifiThread
from piBT import btThread


class protocolHandler:
    def __init__(self, wifi, bt, arduino):
        self.pc = wifi
        self.bt = bt
        self.robot = arduino

    # what to do with the JSON data
    def decodeCommand(self, json_data):
        options = {"command": self.sendCommand,
                   "map": self.sendMap,
                   "status": self.sendStatus,
                   "movement": self.doMovement
                   }
        if options.get(json_data["type"]):
            return options[json_data["type"]](json_data)
        else:
            print "Error: invalid JSON type key"

    def sendCommand(self, json_data):
        self.pc.send(json_data)
        print "send command to PC"

    def sendMap(self, json_data):
        self.pc.send(json_data)
        self.bt.send(json_data)
        print "send map data to PC"

    def sendStatus(self, json_data):
        self.pc.send(json_data)
        self.bt.send(json_data)
        print "send status update to PC"

    def doMovement(self, json_data):
        self.robot.send(json_data)
        self.bt.send(json_data)
        print "do robot movement"


class coreThread(threading.Thread):

    commandQueue = None
    lock = None

    def __init__(self, threadID, name, wifi, bt, arduino):
        threading.Thread.__init__(self)
        self.threadID = threadID
        self.name = name

        # set the command queue
        self.lock = threading.BoundedSemaphore(self.totalBuff)
        self.commandQueue = Queue.Queue()

        # assign thread
        self.wifi = wifi
        self.bt = bt
        self.arduino = arduino

        # assign handler for command
        self.protocolHandler = protocolHandler(wifi, bt, arduino)

    def flushCommandQueue(self):
        with self.commandQueue.mutex:
            self.commandQueue.queue.clear()

    def processCommand(self):
        if not self.commandQueue.empty():
            command = self.commandQueue.get()

            self.lock.acquire()
            self.protocolHandler.decodeCommand(command)

    def run(self):
        while not self.wifi.isConnected():
            continue

        self.arduino.sendStart()
        print "==========================="
        print "PROJECT: DRAGON - BOOT UP /"
        print "==========================="

        while 1:
                self.processCommand()
                try:
                    self.processCommand()
                except Exception:
                        print "Unable to execute main thread"
                        print traceback.format_exc()

wifi = wifiThread(1, "WIFI")
arduino = arduinoThread(2, "ARDUINO")
bt = btThread(3, "BT")
core = coreThread(0, "CORE", wifi, bt, arduino)

wifi.assignMainThread(core)
arduino.assignMainThread(core)
bt.assignMainThread(core)

wifi.start()
arduino.start()
bt.start()
core.start()
