from piConfig import *
import threading
import traceback
import Queue
from protocolHandler import protocolHandler
from piArduino import arduinoThread
from piWifi import wifiThread
from piBT import btThread


class coreThread(threading.Thread):

    commandQueue = None
    lock = None

    def __init__(self, threadID, name, wifi, bt, arduino):
        threading.Thread.__init__(self)
        self.threadID = threadID
        self.name = name

        # set the command queue
        self.lock = threading.BoundedSemaphore(SEMAPHORE_BUF)
        self.commandQueue = Queue.Queue()

        # assign thread
        self.wifi = wifi
        self.bt = bt
        self.arduino = arduino

        # assign handler for command
        self.protocolHandler = protocolHandler(wifi, bt, arduino)

    def addToQueue(self, json_data):
        if json_data is not None:
            print "[ Adding to queue: " + str(json_data) + " ]"
            self.commandQueue.put(json_data)

    def flushCommandQueue(self):
        self.lock = threading.BoundedSemaphore(SEMAPHORE_BUF)
        self.arduino.sendStop()
        with self.commandQueue.mutex:
            self.commandQueue.queue.clear()
        print "[ Flushed command queue ]"

    def processCommand(self):
        if not self.commandQueue.empty():
            command = self.commandQueue.get()
            self.protocolHandler.decodeCommand(command, self.lock)

    def run(self):
        while not self.wifi.isConnected() or not self.bt.isConnected():
            continue

        print "==========================="
        print "PROJECT: DRAGON - BOOT UP /"
        print "==========================="

        while 1:
            try:
                if self.wifi.isConnected() and self.bt.isConnected():
                    self.processCommand()
            except Exception, e:
                print "Unable to decode JSON: " + e.message
                print traceback.format_exc()
                pass

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
