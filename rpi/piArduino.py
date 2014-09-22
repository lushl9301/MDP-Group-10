import serial
import threading


class piArduino:
    def __init__(self):
        self.ser = serial.Serial('/dev/ttyACM0', 9600)
        print 'Arduino Connected'

    def send(self, json_data):
        command = json_data["data"]
        self.ser.write(command)
        print 'Send to Arduino: ' + command

    def receive(self):
        return self.ser.readline()


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
            # TODO: send start
            self.piArduino.send()

        def sendStop(self):
            # TODO: send stop
            self.piArduino.send()

        def run(self):
            print '[ Arduino Thread Start ]'
            self.piArduino = piArduino()

            while 1:
                receivedJSON = self.piArduino.receive()
                self.mainthread.addToQueue(receivedJSON)
