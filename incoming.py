import socket

HOST = 'localhost'  # The remote server's hostname or IP address
PORT = 8081         # The port used by the remote server

# Create a socket object
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# Connect to the remote server
s.connect((HOST, PORT))
print('Connected to ' + HOST + ':' + str(PORT))

# Receive data in chunks of 1024 bytes
while True:
    data = s.recv(1024)
    if not data:
        break
    # Print "1" every time there is incoming data
    print("1")

# Close the socket
s.close()
