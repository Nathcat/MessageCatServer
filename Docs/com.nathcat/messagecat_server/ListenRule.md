# com.nathcat.messagecat_server.ListenRule

## Public nested classes

### ```IDAlreadySetException extends Exception```
An exception class thrown if a program attempts to set the ID of a listen rule which has already had its ID set.

## Public fields

### connectionHandlerId
Defaults to -1, this is the ID of the connection handler that the request should be sent through.

-1 will be changed to the ID of the connection handler the client is connected to.

## Constructors

### ```ListenRule(ConnectionHandler handler, RequestType listenForType, String fieldNameToMatch, Object objectToMatch)```

### ```ListenRule(int connectionHandlerId, RequestType listenForType, String fieldNameToMatch, Object objectToMatch)```

### ```ListenRule(RequestType listenForType, String fieldNameToMatch, Object objectToMatch)```

### ```ListenRule(RequestType listenForType, String fieldNameToMatch, Object[] objectsToMatch)```

### ```ListenRule(RequestType listenForType)```

### ```ListenRule(int connectionHandlerId, RequestType listenForType)```

## Public methods

### ```int getId()```
Gets the ID value of this listen rule.

### ```void setId(int id) throws IDAlreadySetException```
Sets the ID value of this listen rule, unless it is already set.

### ```boolean CheckRequest(RequestType type, Object data) throws NoSuchFieldException, IllegalAccessException```
Checks if a request meets the listen rules criteria.

This check is based on three values, supplied in the constructor of this class:

 - ```listenForType```
 - ```fieldNameToMatch```
 - ```objectToMatch```
 - ```objectsToMatch```

First the method checks if ```fieldNameToMatch``` is not set, in which case the result of the boolean expression ```type == listenForType``` is returned.

Next the method checks if ```objectToMatch``` is set, in which case the method checks if the type of the request matches ```listenForType```, and if the object in the field specified by ```fieldNameToMatch``` in the ```data``` object matches ```objectToMatch```, if it does then ```true``` is returned, otherwise ```false``` is returned.

If ```objectToMatch``` is not set, then ```objectsToMatch``` must be set, so the method checks if ANY of the objects in ```objectsToMatch``` meets the criteria above, if so then ```true``` is returned, otherwise ```false``` is returned.