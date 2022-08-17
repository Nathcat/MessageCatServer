package com.nathcat.messagecat_server;

import com.nathcat.RSA.KeyPair;
import com.nathcat.RSA.PublicKeyException;
import com.nathcat.messagecat_database.Result;
import com.nathcat.messagecat_database_entities.*;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

/**
 * Handles any requests received by the connection handler
 *
 * @author Nathan "Nathcat" Baines
 * @deprecated No longer used in the single handler thread design
 */
public class RequestHandler extends Handler {
    private User user;
    private JSONObject request;

    /**
     * Constructor method, assigns private and constant fields
     *
     * @param socket    The TCP/IP connection socket to be used by this handler
     * @param threadNum The Thread number of this handler, used in debug messages
     */
    public RequestHandler(Socket socket, int threadNum) throws NoSuchAlgorithmException, IOException {
        super(socket, threadNum, "RequestHandler");
    }

    /**
     * This method will be executed in a different thread
     */
    @Override
    public void run() {
        while (true) {
            this.busy = false;
            // Stop this handler process
            this.StopHandler();

            // Check if the queue object is null or not
            if (this.queueObject == null) {
                continue;
            }

            this.busy = true;

            JSONObject data = (JSONObject) ((CloneableObject) this.queueObject).object;
            this.socket = (Socket) data.get("socket");
            this.oos = (ObjectOutputStream) data.get("oos");
            this.ois = (ObjectInputStream) data.get("ois");
            this.keyPair = (KeyPair) data.get("keyPair");
            this.clientKeyPair = (KeyPair) data.get("clientKeyPair");
            this.user = (User) data.get("user");
            this.request = (JSONObject) data.get("request");

            switch ((RequestType) request.get("type")) {
                case GetUser:
                    this.GetUser();
                    break;

                case GetFriendship:
                    this.GetFriendship();
                    break;

                case GetFriendRequests:
                    this.GetFriendRequests();
                    break;

                case GetChat:
                    this.GetChat();
                    break;

                case GetChatInvite:
                    this.GetChatInvite();
                    break;

                case GetPublicKey:
                    this.GetPublicKey();
                    break;

                case AddUser:
                    this.AddUser();
                    break;

                case AddChat:
                    this.AddChat();
                    break;

                case AcceptFriendRequest:
                    this.AcceptFriendRequest();
                    break;

                case DeclineFriendRequest:
                    this.DeclineFriendRequest();
                    break;

                case AcceptChatInvite:
                    this.AcceptChatInvite();
                    break;

                case DeclineChatInvite:
                    this.DeclineChatInvite();
                    break;

                case SendMessage:
                    this.SendMessage();
                    break;

                case SendFriendRequest:
                    this.SendFriendRequest();
                    break;

                case SendChatInvite:
                    this.SendChatInvite();
                    break;
            }
        }
    }

    private void GetUser() {
        // Get the user from the request and decrypt
        User requestedUser = (User) this.request.get("data");

        // Get the selector
        String selector = (String) this.request.get("selector");

        // Search the database and return the result
        try {
            Object result = null;

            if (selector.contentEquals("id")) {
                result = this.server.db.GetUserByID(requestedUser.UserID);
            }
            else if (selector.contentEquals("username")) {
                result = this.server.db.GetUserByUsername(requestedUser.Username);
            }
            else if (selector.contentEquals("displayName")) {
                result = this.server.db.GetUserByDisplayName(requestedUser.DisplayName);
            }

            if (result == null) {
                this.DebugLog("Invalid selector!");
                this.Close();
                return;
            }

            this.Send(this.clientKeyPair.encrypt(result));

        } catch (IOException | PublicKeyException e) {
            this.DebugLog("Failed to respond! (" + e.getMessage() + ")");
            this.Close();
        }
    }

    private void GetFriendship() {
        // Get the friendship from the request and decrypt
        Friendship requestedFriendship = (Friendship) this.request.get("data");

        // Get the selector
        String selector = (String) this.request.get("selector");

        // Search the database and return the result
        try {
            Object result = null;
            if (selector.contentEquals("id")) {
                result = this.server.db.GetFriendshipByID(requestedFriendship.FriendshipID);
            }
            else if (selector.contentEquals("userID")) {
                result = this.server.db.GetFriendshipByUserID(requestedFriendship.FriendshipID);
            }
            else if (selector.contentEquals("userID&FriendID")) {
                result = this.server.db.GetFriendshipByUserIDAndFriendID(requestedFriendship.UserID, requestedFriendship.FriendshipID);
            }

            if (result == null) {
                this.DebugLog("Invalid selector!");
                this.Close();
                return;
            }

            this.Send(this.clientKeyPair.encrypt(result));

        } catch (IOException | PublicKeyException e) {
            this.DebugLog("Failed to respond! (" + e.getMessage() + ")");
            this.Close();
        }
    }

    private void GetFriendRequests() {
        // Get the friend request from the request and decrypt it
        FriendRequest requestedFriendRequest = (FriendRequest) this.request.get("data");

        // Get the selector
        String selector = (String) this.request.get("selector");

        // Search the database and return the result
        try {
            Object result = null;

            if (selector.contentEquals("senderID")) {
                result = this.server.db.GetFriendRequestsBySenderID(requestedFriendRequest.SenderID);
            }
            else if (selector.contentEquals("recipientID")) {
                result = this.server.db.GetFriendRequestsByRecipientID(requestedFriendRequest.RecipientID);
            }

            if (result == null) {
                this.DebugLog("Invalid selector!");
                this.Close();
                return;
            }

            this.Send(this.clientKeyPair.encrypt(result));

        } catch (IOException | PublicKeyException e) {
            this.DebugLog("Failed to respond! (" + e.getMessage() + ")");
            this.Close();
        }
    }

    private void GetChat() {
        // Get the chat from the request and decrypt
        Chat requestedChat = (Chat) this.request.get("data");

        // Search the database and return the result
        try {
            Object result = this.server.db.GetChatByID(requestedChat.ChatID);

            if (result == null) {
                this.DebugLog("Invalid selector!");
                this.Close();
                return;
            }

            this.Send(this.clientKeyPair.encrypt(result));

        } catch (IOException | PublicKeyException e) {
            this.DebugLog("Failed to respond! (" + e.getMessage() + ")");
            this.Close();
        }
    }

    private void GetChatInvite() {
        // Get the chat invite from the request and decrypt it
        ChatInvite requestedChatInvite = (ChatInvite) this.request.get("data");

        // Get the selector
        String selector = (String) this.request.get("selector");

        // Search the database and return the result
        try {
            Object result = null;

            if (selector.contentEquals("id")) {
                result = this.server.db.GetChatInviteByID(requestedChatInvite.ChatInviteID);
            }
            else if (selector.contentEquals("senderID")) {
                result = this.server.db.GetChatInvitesBySenderID(requestedChatInvite.SenderID);
            }
            else if (selector.contentEquals("recipientID")) {
                result = this.server.db.GetChatInvitesByRecipientID(requestedChatInvite.RecipientID);
            }

            if (result == null) {
                this.DebugLog("Invalid selector!");
                this.Close();
                return;
            }

            this.Send(this.clientKeyPair.encrypt(result));

        } catch (IOException | PublicKeyException e) {
            this.DebugLog("Failed to respond! (" + e.getMessage() + ")");
            this.Close();
        }
    }

    private void GetPublicKey() {
        // Get the public key id from the request
        int keyID = (int) this.request.get("data");

        // Get the key pair from the database
        KeyPair publicKey = this.server.db.GetKeyPair(keyID);

        try {
            this.Send(this.clientKeyPair.encrypt(publicKey));

        } catch (IOException | PublicKeyException e) {
            this.DebugLog("Failed to respond! (" + e.getMessage() + ")");
            this.Close();
        }
    }

    private void AddUser() {
        // Get the user from the request and decrypt
        User newUser = (User) this.request.get("data");

        // Add the new user through the database
        this.server.db.AddUser(newUser);

        // Get the new user from the database and send back to the client
        User user = this.server.db.GetUserByUsername(newUser.Username);

        try {
            this.Send(this.clientKeyPair.encrypt(user));

        } catch (IOException | PublicKeyException e) {
            this.DebugLog("Failed to respond! (" + e.getMessage() + ")");
            this.Close();
        }
    }

    private void AddChat() {
        // Get the public key from the request
        KeyPair chatKeyPair = (KeyPair) this.request.get("keyPair");

        // Get the chat from the request and decrypt
        Chat newChat = (Chat) this.request.get("data");

        // Add the chat and public key to the database
        this.server.db.AddChat(newChat);
        this.server.db.AddKeyPair(chatKeyPair);

        // Send the new chat back to the client
        Chat chat = this.server.db.GetChatByPublicKeyID(chatKeyPair.hashCode());

        try {
            this.Send(this.clientKeyPair.encrypt(chat));

        } catch (IOException | PublicKeyException e) {
            this.DebugLog("Failed to respond! (" + e.getMessage() + ")");
            this.Close();
        }
    }

    private void AcceptFriendRequest() {
        // Get the friend request from the request, since the data contained are all integers we do not need to decrypt
        FriendRequest fr = (FriendRequest) this.request.get("data");

        // Create the friend objects
        Friendship friendA = new Friendship(-1, fr.SenderID, fr.RecipientID, "");
        Friendship friendB = new Friendship(-1, fr.RecipientID, fr.SenderID, "");

        // Add the friendships to the database
        this.server.db.AddFriendship(friendA);
        this.server.db.AddFriendship(friendB);

        // Delete the friend requests from the database
        if (this.server.db.DeleteFriendRequest(fr.FriendRequestID) == Result.FAILED) {
            try {
                this.Send(this.clientKeyPair.encrypt("failed"));
                return;

            } catch (IOException | PublicKeyException e) {
                this.DebugLog("Failed to respond! (" + e.getMessage() + ")");
                this.Close();
                return;
            }
        }

        // Reply to the client
        try {
            this.Send(this.clientKeyPair.encrypt("done"));

        } catch (IOException | PublicKeyException e) {
            this.DebugLog("Failed to respond! (" + e.getMessage() + ")");
            this.Close();
        }
    }

    private void DeclineFriendRequest() {
        // Get the friend request from the request, since the data contained are all integers we do not need to decrypt
        FriendRequest fr = (FriendRequest) this.request.get("data");

        // Delete the friend requests from the database
        if (this.server.db.DeleteFriendRequest(fr.FriendRequestID) == Result.FAILED) {
            try {
                this.Send(this.clientKeyPair.encrypt("failed"));
                return;

            } catch (IOException | PublicKeyException e) {
                this.DebugLog("Failed to respond! (" + e.getMessage() + ")");
                this.Close();
                return;
            }
        }

        // Reply to the client
        try {
            this.Send(this.clientKeyPair.encrypt("done"));

        } catch (IOException | PublicKeyException e) {
            this.DebugLog("Failed to respond! (" + e.getMessage() + ")");
            this.Close();
        }
    }

    private void AcceptChatInvite() {
        // Get the chat invite from the request and the private key from the database
        ChatInvite ci = (ChatInvite) this.request.get("data");
        KeyPair privateKey = this.server.db.GetKeyPair(ci.PrivateKeyID);

        // Delete the chat invite from the database
        if (this.server.db.DeleteChatInvite(ci.ChatInviteID) == Result.FAILED || this.server.db.RemoveKeyPair(ci.PrivateKeyID) == Result.FAILED) {
            try {
                this.Send(this.clientKeyPair.encrypt("failed"));
                return;

            } catch (IOException | PublicKeyException e) {
                this.DebugLog("Failed to respond! (" + e.getMessage() + ")");
                this.Close();
                return;
            }
        }

        try {
            this.Send(this.clientKeyPair.encrypt(privateKey));

        } catch (IOException | PublicKeyException e) {
            this.DebugLog("Failed to respond! (" + e.getMessage() + ")");
            this.Close();
        }
    }

    private void DeclineChatInvite() {
        // Get the chat invite from the request and the private key from the database
        ChatInvite ci = (ChatInvite) this.request.get("data");

        // Delete the chat invite from the database
        if (this.server.db.DeleteChatInvite(ci.ChatInviteID) == Result.FAILED || this.server.db.RemoveKeyPair(ci.PrivateKeyID) == Result.FAILED) {
            try {
                this.Send(this.clientKeyPair.encrypt("failed"));

            } catch (IOException | PublicKeyException e) {
                this.DebugLog("Failed to respond! (" + e.getMessage() + ")");
                this.Close();
            }
        }
    }

    private void SendMessage() {
        // Get the message from the database
        Message message = (Message) this.request.get("data");

        // Add the message to the database
        this.server.db.GetMessageQueue(message.ChatID).Push(message);

        // Reply to the client
        try {
            this.Send(this.clientKeyPair.encrypt("done"));

        } catch (IOException | PublicKeyException e) {
            this.DebugLog("Failed to respond! (" + e.getMessage() + ")");
            this.Close();
        }
    }

    private void SendFriendRequest() {
        // Get the friend request from the request
        FriendRequest fr = (FriendRequest) this.request.get("data");

        // Add the request to the database
        if (this.server.db.AddFriendRequest(fr) == Result.FAILED) {
            try {
                this.Send(this.clientKeyPair.encrypt("failed"));
                return;

            } catch (IOException | PublicKeyException e) {
                this.DebugLog("Failed to respond! (" + e.getMessage() + ")");
                this.Close();
                return;
            }
        }

        try {
            this.Send(this.clientKeyPair.encrypt("done"));

        } catch (IOException | PublicKeyException e) {
            this.DebugLog("Failed to respond! (" + e.getMessage() + ")");
            this.Close();
        }
    }

    private void SendChatInvite() {
        // Get the chat invite and public key from the request
        Chat chat = (Chat) this.request.get("data");
        KeyPair publicKey = (KeyPair) this.request.get("keyPair");

        // Add the chat and private key to the database
        chat = new Chat(-1, chat.Name, chat.Description, publicKey.hashCode());
        if (this.server.db.AddKeyPair(publicKey) == Result.FAILED || this.server.db.AddChat(chat) == Result.FAILED) {
            try {
                this.Send(this.clientKeyPair.encrypt("failed"));
                return;

            } catch (IOException | PublicKeyException e) {
                this.DebugLog("Failed to respond! (" + e.getMessage() + ")");
                this.Close();
                return;
            }
        }

        // Reply to the client
        try {
            this.Send(this.clientKeyPair.encrypt(this.server.db.GetChatByPublicKeyID(publicKey.hashCode())));

        } catch (IOException | PublicKeyException e) {
            this.DebugLog("Failed to respond! (" + e.getMessage() + ")");
            this.Close();
        }
    }
}

