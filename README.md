# MessageCat Server
This is the server for the MessageCat IM service by Nathcat 2022. All communications are encrypted using an implementation of RSA asymmetric encryption and are handled concurrently.

## Environment
The file structure for this program should follow this pattern:
```
MessageCatServer.jar
Assets/
  Data/
    ...
  MySQL_Config.json
  Server_Config.json
  ```
There are no specific system requirements, although a higher end machine will obviously be capable of handling more concurrent connections.

## Config files
### MySQL_Config.json
This is the config file which specifies the configuration for the MySQL program to be used by the server. Following are the allowed fields
 - "connection_url": "<jdbc url to MySQL server>"
 - "username": "<username>"
 - "password": "<password>"
  
### Server_Config.json
This file specifies the configuration for the MessageCat server program. Following are the allowed fields
 - "port": "<port>"
 - "maxThreadCount": "<number of handler threads>"
  
## Requests
There are set request types which you should use when making a request to the server.

Upon connection the client should sent their RSA public key to the server, after this the server will send it's public key to the client. After this the client will be able to send requests to the server (the server assumes all communications from this point on are encrypted).
