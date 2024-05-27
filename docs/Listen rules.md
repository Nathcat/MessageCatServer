# Listen rules
If the client wishes to be alerted when certain events happen within the system, such as a message being sent to a chat, or a friend request / chat invite being sent, they should create a listen rule and register it with the server, once this is done, the server will forward a copy of any requests which trigger a listen rule to the client that register the rule that was triggered.

## Register a listen rule
To register a listen rule you should create a request with the type ```AddListenRule```.

The data field should contain a [```ListenRule```](com.nathcat/messagecat_server/ListenRule.md) object, with the ID unset. The server will then determine an appropriate ID and add it to the list of listen rules, finally sending the integer ID of the listen rule back to the client.

If the ID of the listen rule sent to the server is already set, the server will respond with ```"failed"```.

## Removing a listen rule
Send the ID of the listen rule to the server in the data field of a request with type ```RemoveListenRule``` and the server will attempt to remove the listen rule from its list.

If the listen rule is found and removed the server will respond with ```"done"```, otherwise it will respond with ```"failed"```.

## Receiving listen rule triggers
During the connection process the client will have created a listen rule socket. Any requests that trigger a listen rule created by a client will be copied and sent to the client that created the rule that was triggered through this socket.

The received request will include an extra field ```"triggerID"```, which contains the integer ID of the listen rule that was triggered.