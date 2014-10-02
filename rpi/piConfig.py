# imports
import json

# CONSTANTS #

# commands
CMD_START_EXP = "E"
CMD_START_PATH = "P"
CMD_START_REMOTE = "R"

# status
ST_END_EXP = "END_EXP"
ST_END_PATH = "END_PATH"
ST_END_REMOTE = "END_RMT"

# json
JSON_START = {"type": "command", "data": "S"}
JSON_STOP = {"type": "movement", "data": "G"}  # currently not used by rpi

# network
WIFI_IP = "192.168.10.10"
WIFI_PORT = 8888
BT_UUID = "00001101-0000-1000-8000-00805F9B34FB"
BT_SERVER_NAME = "MDP10_BTServer"

# multithreading
SEMAPHORE_BUF = 3

# reusable methods


def receiveJSON(buff, senderName):
        json_string = buff.readline()
        if json_string is None:
            return None
        try:
            json_data = json.loads(json_string.strip())
            print "JSON from " + senderName + ": " + str(json_string)
            return json_data
        except ValueError:
            if len(json_string) <= 0:
                raise IOError()
            print "string from " + senderName + ": " + str(json_string)
            pass



# unused methods

# def receive(self):
#         if self.conn is None:
#             return
#         data = ""
#         completeJSON = False
#         while not completeJSON:
#             buff = self.conn.recv(1)  # receive the data per char
#             data += buff
#             if buff == "}":  # detects the end of a JSON in buffer
#                 completeJSON = True
#         # TODO: add catch block if decoding fails
#         json_data = json.loads(data)
#         print "Receive From Wifi: " + str(data)
#         return json_data

# def receive(self):
#         if self.client_sock is None:
#             return
#         # try:
#         data = ''
#         completeJSON = False
#         while not completeJSON:
#             # receive the data per char
#             buff = self.client_sock.recv(1)
#             data += buff
#             if buff == '}':  # detects the end of a JSON in buffer
#                 completeJSON = True
#         # TODO: add catch block if decoding fails
#         json_data = json.loads(data)
#         if completeJSON:
#             print "Received From Bluetooth: " + str(data)
#             return json_data
