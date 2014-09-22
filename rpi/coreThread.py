import threading
import traceback
import Queue

from piArduino import piArduino
from piWifi import wifiThread


class protocolHandler:
    def __init__(self, wifiThread, btThread, arduinoThread):
        self.pc = wifiThread
        self.bt = btThread
        self.robot = arduinoThread

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

    def __init__(self, threadID, name, wifiThread, btThread, arduinoThread):
        threading.Thread.__init__(self)
        self.threadID = threadID
        self.name = name

        # set the command queue
        self.lock = threading.BoundedSemaphore(self.totalBuff)
        self.commandQueue = Queue.Queue()

        # assign thread
        self.wifiThread = wifiThread
        self.btThread = btThread
        self.arduinoThread = arduinoThread

        # assign handler for command
        self.protocolHandler = protocolHandler(wifiThread, btThread, arduinoThread)

    def flushCommandQueue(self):
        with self.commandQueue.mutex:
            self.commandQueue.queue.clear()

    def processCommand(self):
        if not self.commandQueue.empty():
            command = self.commandQueue.get()

            self.lock.acquire()
            self.protocolHandler.decodeCommand(command)

    def run(self):
        while not self.wifiThread.isConnected():
            continue

        self.arduinoThread.sendStart()
        print "==========================="
        print "PROJECT: DRAGON - BOOT UP /"
        print "==========================="

        while 1:
                self.processCommand()
                try:
                    self.processCommand()
                except Exception:
                        print "Unable to execute main thread"
