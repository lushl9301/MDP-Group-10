from piConfig import *


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
                   "movement": self.doMovement,
                   "path": self.doPath
                   }
        if options.get(json_data["type"]):
            return options[json_data["type"]](json_data, lock)
        else:
            print "ERROR: invalid JSON type key"

    def sendCommand(self, json_data, lock):
        if json_data["data"] == CMD_START_EXP:
            # start robot
            lock.acquire()
            self.robot.sendStart()
            lock.release()

            # inform PC
            lock.acquire()
            self.pc.send(json_data)
            lock.release()

            # begin exp
            lock.acquire()
            self.robot.send(json_data)
            lock.release()
            print "..starting exploration.."
        elif json_data["data"] == CMD_START_PATH:
            # start robot
            lock.acquire()
            self.robot.sendStart()
            lock.release()

            # inform PC
            lock.acquire()
            self.pc.send(json_data)
            lock.release()

            # begin shortest path
            lock.acquire()
            self.robot.send(json_data)
            lock.release()
            print "..starting shortest path.."
        elif json_data["data"] == CMD_START_REMOTE:
            # start robot
            lock.acquire()
            self.robot.sendStart()
            lock.release()

            # begin remote control
            lock.acquire()
            self.robot.send(json_data)
            lock.release()
        else:
            print "ERROR: unknown command data - cannot process"

    def sendReading(self, json_data, lock):
        lock.acquire()
        self.pc.send(json_data)
        lock.release()
        print "..sending robot data to PC.."

        lock.acquire()
        self.android.send(json_data)
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
            self.pc.send(json_data)
            lock.release()

            lock.acquire()
            self.android.send(json_data)
            lock.release()
            print "..sending end shortest path.."
        elif json_data["data"] == ST_END_REMOTE:
            lock.acquire()
            self.android.send(json_data)
            lock.release()
        else:
            print "ERROR: unknown status data - cannot process"

    def doMovement(self, json_data, lock):
        lock.acquire()
        self.robot.send(json_data)
        lock.release()
        print "..sending robot movement.."

    def doPath(self, json_data, lock):
        lock.acquire()
        self.robot.send(json_data)
        lock.release()
