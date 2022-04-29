#!/usr/bin/env python3

# Prerequisits for Ubuntu
#     sudo apt-get install python3-wheel
#     sudo pip3 install python-osc

from pythonosc import dispatcher, osc_server
from socket import AF_INET, SOCK_DGRAM, socket

def get_ip_address():
    s = socket(AF_INET, SOCK_DGRAM)
    s.connect(("8.8.8.8", 80))
    address = s.getsockname()[0]
    s.close()
    return address

def handler(addr, *message):
    output = '{:32}'.format(addr)
    for i in range(len(message)):
        output += ' {:8.3f}'.format(message[i])
    print(output)

if __name__ == "__main__":
    address = get_ip_address()
    port = 9000
    
    dispatcher = dispatcher.Dispatcher()
    dispatcher.map('/*', handler)
    
    server = osc_server.ThreadingOSCUDPServer((address, port), dispatcher)
    print('OSC server started on {}:{}'.format(address, port))
    server.serve_forever()
