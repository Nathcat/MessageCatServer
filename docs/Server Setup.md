# Setup

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

## Building
This project is built with Intellij IDEA. Either use the .jar contained in the repository under ```out/artifacts/MessageCatServer_jar```, or build using Intellij IDEA.

## Creating clients
You can use the library .jar located in ```out/artifacts/MessageCatServerLib_jar``` to have access to classes required to make requests to the server within your client applications without having to copy - paste the source code classes. You may have to generate your own build for this depending on the JDK you are using to build your client.

This project has been built to language level 11.