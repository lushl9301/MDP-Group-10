from piConfig import *
import threading
import traceback
import Queue
from piArduino import arduinoThread
from piWifi import wifiThread
from piBT import btThread


class protocolHandler:
    def __init__(self, wifi, bt, arduino):
        self.pc = wifi
        self.android = bt
        self.robot = arduino

    # what to do with the JSON data
    def decodeCommand(self, json_data, lock):
        options = {"command": self.sendCommand,
                   "reading": self.sendReading,
                   "map": self.sendMap,
                   "status": self.sendStatus,
                   "movement": self.doMovement
                   }
        try:
            if options.get(json_data["type"]):
                return options[json_data["type"]](json_data, lock)
            else:
                print "ERROR: invalid JSON type key"
        except Exception:
            print "Invalid JSON"
            pass

    def sendCommand(self, json_data, lock):
        if json_data["data"] == CMD_START_EXP:
            lock.acquire()
            self.robot.send(json_data)
            lock.release()
            print "..starting exploration.."
        elif json_data["data"] == CMD_START_PATH:
            lock.acquire()
            self.robot.send(json_data)
            lock.acquire()
            self.pc.send(json_data)
            lock.release()
            lock.release()
            print "..starting shortest path.."
        else:
            print "ERROR: unknown command data - cannot process"

    def sendReading(self, json_data, lock):
        lock.acquire()
        self.pc.send(json_data)
        print "..sending robot data to PC.."
        lock.acquire()
        self.android.send(json_data)
        lock.release()
        lock.release()
        print "..sending robot data to Android.."

    def sendMap(self, json_data, lock):
        lock.acquire()
        self.android.send(json_data)
        lock.release()
        print "..sending map data to Android.."

    def sendStatus(self, json_data, lock):
        if json_data["data"] == ST_END_EXP:
            lock.acquire()
            self.pc.send(json_data)
            lock.release()
            lock.acquire()
            self.android.send(json_data)
            lock.release()
            print "..sending end exploration.."
        elif json_data["data"] == ST_END_PATH:
            lock.acquire()
            self.android.send(json_data)
            lock.release()
            print "..sending end shortest path.."
        else:
            print "ERROR: unknown status data - cannot process"

    def doMovement(self, json_data, lock):
        lock.acquire()
        self.robot.send(json_data)
        lock.release()
        print "..sending robot movement.."


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

            # self.lock.acquire()
            self.protocolHandler.decodeCommand(command, self.lock)
            # self.lock.release()

    def run(self):
        while not self.bt.isConnected() or not self.wifi.isConnected():
            continue

        # send start signal to robot
        # self.arduino.sendStart()

        print "==========================="
        print "PROJECT: DRAGON - BOOT UP /"
        print "==========================="

        while 1:
            try:
                if self.bt.isConnected() and self.wifi.isConnected():
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
