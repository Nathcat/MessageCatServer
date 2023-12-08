package com.nathcat.messagecat_server;

/**
 * Used to determine how a request should be interpreted by the RequestHandler.
 * The descriptions of each enum value here show the data format required by each of the request types:
 * {
 *     "type": RequestType,
 *     "data": some.java.object,
 *     "selector": ["selector_value" -> return type]
 *     "keyPair": com.nathcat.RSA.KeyPair
 * }
 * @author Nathan "Nathcat" Baines
 */
public enum RequestType {
    /**
     * Authenticate a connection using login details.
     * {
     *     "type": Authenticate,
     *     "data": com.nathcat.messagecat_database_entities.User
     * } -> com.nathcat.messagecat_database_entities.User
     */
    Authenticate,
    /**
     * Get a user from the database.
     * {
     *     "type": GetUser,
     *     "data": com.nathcat.messagecat_database_entities.User,
     *     "selector": ["id" -> User, "username" -> User, "displayname" -> User[]]
     * } -> com.nathcat.messagecat_database_entities.User / []
     */
    GetUser,
    /**
     * Get a friendship record from the database
     * {
     *     "type": GetFriendship,
     *     "data": com.nathcat.messagecat_database_entities.Friendship,
     *     "selector": ["id" -> Friendship, "userID" -> Friendship[], "userID&friendID" -> Friendship]
     * } -> com.nathcat.messagecat_database_entities.Friendship / []
     */
    GetFriendship,
    /**
     * Get a friendship request record from the database
     * {
     *     "type": GetFriendRequests,
     *     "data": com.nathcat.messagecat_database_entities.FriendRequest,
     *     "selector": ["senderID" -> FriendRequest[], "recipientID" -> FriendRequest[]]
     * } -> com.nathcat.messagecat_database_entities.FriendRequest / []
     */
    GetFriendRequests,
    /**
     * Get a chat record from the database, can only be done by chat ID
     * {
     *     "type": GetChat,
     *     "data": com.nathcat.messagecat_database_entities.Chat,
     * } -> com.nathcat.messagecat_database_entities.Chat
     */
    GetChat,
    /**
     * Get a chat invite record from the database
     * {
     *     "type": GetChatInvite,
     *     "data": com.nathcat.messagecat_database_entities.ChatInvite,
     *     "selector": ["id" -> ChatInvite, "senderID" -> ChatInvite[], "recipientID" -> ChatInvite[]]
     * } -> com.nathcat.messagecat_database_entities.ChatInvite / []
     */
    GetChatInvite,
    /**
     * Get a public key from the database
     * {
     *     "type": GetPublicKey,
     *     "data": int keyID
     * } -> com.nathcat.RSA.KeyPair
     */
    GetPublicKey,
    /**
     * Get a message queue from the database
     * {
     *     "type": GetMessageQueue,
     *     "data": int chatID
     * } -> com.nathcat.messagecat_database.MessageQueue
     */
    GetMessageQueue,
    /**
     * Add a user to the database
     * {
     *     "type": AddUser,
     *     "data": com.nathcat.messagecat_database_entities.User
     * } -> com.nathcat.messagecat_database_entities.User
     */
    AddUser,
    /**
     * Add a chat to the database
     * {
     *     "type": AddChat,
     *     "data": com.nathcat.messagecat_database_entities.Chat,
     *     "keyPair": com.nathcat.RSA.KeyPair
     * } -> com.nathcat.messagecat_database_entities.Chat
     */
    AddChat,
    /**
     * Register a listen rule with the server
     * {
     *     "type": AddListenRule,
     *     "data": com.nathcat.messagecat_server.ListenRule
     * } -> int listenRuleID
     */
    AddListenRule,
    /**
     * Remove a registered listen rule from the server
     * {
     *     "type": RemoveListenRule,
     *     "data": int listenRuleID
     * } -> "done" / "failed"
     */
    RemoveListenRule,
    /**
     * Accept a friend request
     * {
     *     "type": AcceptFriendRequest,
     *     "data": com.nathcat.messagecat_database_entities.FriendRequest
     * } -> "done" / "failed"
     */
    AcceptFriendRequest,
    /**
     * Decline a friend request
     * {
     *     "type": DeclineFriendRequest,
     *     "data": com.nathcat.messagecat_database_entities.FriendRequest
     * } -> "done" / "failed"
     */
    DeclineFriendRequest,
    /**
     * Accept a chat invite
     * {
     *     "type": AcceptChatInvite,
     *     "data": com.nathcat.messagecat_database_entities.ChatInvite,
     * } -> com.nathcat.RSA.KeyPair privateKey
     */
    AcceptChatInvite,
    /**
     * Decline a chat invite
     * {
     *     "type": DeclineChatInvite,
     *     "data": com.nathcat.messagecat_database_entities.ChatInvite
     * } -> "done" / "failed"
     */
    DeclineChatInvite,
    /**
     * Send a message to a chat
     * {
     *     "type": SendMessage,
     *     "data": com.nathcat.messagecat_database_entities.Message
     * } -> "done" / "failed"
     */
    SendMessage,
    /**
     * Send a friend request
     * {
     *     "type": SendFriendRequest,
     *     "data": com.nathcat.messagecat_database_entities.FriendRequest
     * } -> "done" / "failed"
     */
    SendFriendRequest,
    /**
     * Send a chat invite
     * {
     *     "type": SendChatInvite,
     *     "data": com.nathcat.messagecat_database_entities.ChatInvite,
     *     "keyPair": com.nathcat.RSA.KeyPair privateKey
     * } -> "done" / "failed"
     */
    SendChatInvite
}
