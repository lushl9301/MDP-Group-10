from bluetooth import *
import serial
import traceback

class piBlueTooth:
    uuid = "00001101-0000-1000-8000-00805F9B34FB"
    server_sock = None
    port = None
    client_sock = None
    client_info = None
    def __init__(self):
        self.server_sock = BluetoothSocket( RFCOMM )
        self.server_sock.bind(("",1))
        self.server_sock.listen(10)
        self.port = self.server_sock.getsockname()[1]
        advertise_service( self.server_sock, "SampleServer",
                           service_id = self.uuid,
                           service_classes = [ self.uuid,
                           SERIAL_PORT_CLASS ],
                           profiles = [ SERIAL_PORT_PROFILE ],
                           #protocols = [ OBEX_UUID ]
                        )
        print "Waiting for connection on RFCOMM channel %d" % self.port
        self.client_sock, self.client_info = self.server_sock.accept()
        print "Accepted connection from ", self.client_info

    def receive(self):
        if self.client_sock == None:
                return
        try:
                data = self.client_sock.recv(1024)
                if len(data) <> 0:
                        print "Received From Bluetooth [%s]" % data
                        return data
        except IOError:
                print "BlueTooth Receiving Exception"
		print traceback.format_exc()
                pass

    def send(self, data="Hello!"):
        if self.client_sock == None:
                return
        try:
                self.client_sock.send(str(data))
        except IOError:
                print "BlueTooth Sending Exception"
                pass
    def close(self):
        self.client_sock.close()
        self.server_sock.close()

piBT = piBlueTooth()

while 1:
	piBT.receive()
