import threading
import traceback
import Queue

from piArduino import arduinoThread
from piWifi import wifiThread
from piBT import btThread

# define constants
CMD_START_EXP = "START_EXP"
CMD_START_PATH = "START_PATH"
CMD_STOP = "STOP"
ST_END_EXP = "END_EXP"
ST_END_PATH = "END_PATH"


class protocolHandler:
    def __init__(self, wifi, bt, arduino):
        self.pc = wifi
        self.android = bt
        self.robot = arduino

    # what to do with the JSON data
    def decodeCommand(self, json_data):
        options = {"command": self.sendCommand,
                   "reading": self.sendReading,
                   "map": self.sendMap,
                   "status": self.sendStatus,
                   "movement": self.doMovement
                   }
        if options.get(json_data["type"]):
            return options[json_data["type"]](json_data)
        else:
            print "ERROR: invalid JSON type key"

    def sendCommand(self, json_data):
        if json_data["data"] == CMD_START_EXP:
            self.arduino.send(json_data)
            print "..starting exploration.."
        elif json_data["data"] == CMD_START_PATH:
            self.arduino.send(json_data)
            self.pc.send(json_data)
            print "..starting shortest path.."
        else:
            print "ERROR: unknown command data - cannot process"

    def sendReading(self, json_data):
        self.pc.send(json_data)
        self.android.send(json_data)
        print "..sending robot data to PC - Android.."

    def sendMap(self, json_data):
        self.android.send(json_data)
        print "..sending map data to Android.."

    def sendStatus(self, json_data):
        if json_data["data"] == ST_END_EXP:
            self.pc.send(json_data)
            self.android.send(json_data)
            print "..sending end exploration.."
        elif json_data["data"] == ST_END_PATH:
            self.android.send(json_data)
            print "..sending end shortest path.."
        else:
            print "ERROR: unknown status data - cannot process"

    def doMovement(self, json_data):
        self.robot.send(json_data)
        print "..sending robot movement.."


class coreThread(threading.Thread):

    commandQueue = None
    lock = None
    BUFFER = 3

    def __init__(self, threadID, name, wifi, bt, arduino):
        threading.Thread.__init__(self)
        self.threadID = threadID
        self.name = name

        # set the command queue
        self.lock = threading.BoundedSemaphore(self.BUFFER)
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
        self.lock = threading.BoundedSemaphore(self.BUFFER)
        self.arduino.sendStop()
        with self.commandQueue.mutex:
            self.commandQueue.queue.clear()
        print "[ Flushed command queue ]"

    def processCommand(self):
        if not self.commandQueue.empty():
            command = self.commandQueue.get()

            self.lock.acquire()
            self.protocolHandler.decodeCommand(command)
            self.lock.release()

    def run(self):
        while not self.wifi.isConnected():
            continue

        # send start signal to robot
        # self.arduino.sendStart()

        print "==========================="
        print "PROJECT: DRAGON - BOOT UP /"
        print "==========================="

        while 1:
            try:
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
