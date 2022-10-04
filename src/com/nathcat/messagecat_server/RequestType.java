package com.nathcat.messagecat_server;

/**
 * Used to determine how a request should be interpreted by the RequestHandler
 *
 * @author Nathan "Nathcat" Baines
 */
public enum RequestType {
    Authenticate,
    GetUser,
    GetFriendship,
    GetFriendRequests,
    GetChat,
    GetChatInvite,
    GetPublicKey,
    GetMessageQueue,
    AddUser,
    AddChat,
    AddListenRule,
    RemoveListenRule,
    AcceptFriendRequest,
    DeclineFriendRequest,
    AcceptChatInvite,
    DeclineChatInvite,
    SendMessage,
    SendFriendRequest,
    SendChatInvite
}
