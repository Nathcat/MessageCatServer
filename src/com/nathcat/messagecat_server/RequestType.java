package com.nathcat.messagecat_server;

/**
 * Used to determine how a request should be interpreted by the RequestHandler
 *
 * @author Nathan "Nathcat" Baines
 */
public enum RequestType {
    GetUser,
    GetFriendship,
    GetFriendRequests,
    GetChat,
    GetChatInvite,
    GetPublicKey,
    AddUser,
    AddChat,
    AcceptFriendRequest,
    DeclineFriendRequest,
    AcceptChatInvite,
    DeclineChatInvite,
    SendMessage,
    SendFriendRequest,
    SendChatInvite
}
