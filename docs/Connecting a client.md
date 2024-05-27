# Connecting a client
This document covers the connection and handshake process of the MessageCat server.

1. Client initiates connection to the server on the port given in the server configuration
2. The server will then generate an RSA key pair and send this to the client, including only the public key.
3. The client should then generate an RSA key pair and send this to the server, including only public key.
4. The server then sends a single integer to the client, encrypted with the client's public key, this integer is the thread number of the connection handler that the client has been passed to.
5. The server will then send over another integer, this is port of the server socket listening for the listen rule socket connection. The client should create another socket to connect to this new socket. The listen rule socket will only send information to the client, the client will not send information to the server on this socket.
6. The client is now connected and can start sending requests.