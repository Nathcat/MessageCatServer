# Making Requests
Requests should be made to the server by passing an ```org.json.simple.JSONObject``` object through a socket connected
to the server. The object should be encrypted using the server's key pair, and the response will be encrypted with your
client's key pair.

The data contained within this object should be structured as follows:

 - "type": [```RequestType```](com.nathcat/messagecat_server/RequestType.md)
 - "data": ```Object``` The data that the server requires to fulfil this request.
 - "keyPair": [```KeyPair```](com.nathcat/RSA/KeyPair.md) Only required for some kinds of requests, does not need to be specified for other types.

## Types of requests
The type of request is specified by an enum called [```RequestType```](com.nathcat/messagecat_server/RequestType.md). Following is a list of the values in this enum and the data
that request requires.

### Authenticate

```json
{
  "type": RequestType.Authenticate,
  "data": com.nathcat.messagecat_database_entities.User // This an instance of the user to be authenticated. 
}
```


### GetUser

```json
{
  "type": RequestType.GetUser,
  "data": com.nathcat.messagecat_database_entities.User, // Contains only the field specified in the selector field
  "selector": "id" or "username" or "displayName"
}
```

### GetFriendship

```json
{
  "type": RequestType.GetFriendship,
  "data": com.nathcat.messagecat_database_entities.Friendship, // Contains only the field specified in the selector field
  "selector": "id" or "userID" or "userID&FriendID"
}
```

If the selector is `userID` then the return value is an array.

### GetFriendRequests

```json
{
  "type": RequestType.GetFriendRequests,
  "data": com.nathcat.messagecat_database_entities.FriendRequest, // Contains only the field specified in the selector field
  "selector": "senderID" or "recipientID"
}
```

### GetChat
### GetChatInvite
### GetPublicKey
### GetMessageQueue
### AddUser
### AddChat
### AddListenRule
### RemoveListenRule
### AcceptFriendRequest
### DeclineFriendRequest
### AcceptChatInvite
### DeclineChatInvite
### SendMessage
### SendFriendRequest
### SendChatInvite