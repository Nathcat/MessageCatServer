package com.nathcat.messagecat_server;

import com.nathcat.RSA.*;
import com.nathcat.messagecat_database.Result;
import com.nathcat.messagecat_database_entities.*;
import jdk.jshell.spi.ExecutionControl;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;

/**
 * Maintains a connection with the client device
 *
 * @author Nathan "Nathcat" Baines
 */
public class ConnectionHandler extends Handler {
    private JSONObject request;

    /**
     * Constructor method, assigns private and constant fields
     *
     * @param socket    The TCP/IP connection socket to be used by this handler
     * @param threadNum The Thread number of this handler, used in debug messages
     */
    public ConnectionHandler(Socket socket, int threadNum) throws NoSuchAlgorithmException, IOException {
        super(socket, threadNum, "ConnectionHandler");
    }

    /**
     * This method will be executed in a different thread
     */
    @Override
    public void run() {
        while (true) {
            this.busy = false;
            this.StopHandler();

            if (this.queueObject == null) {
                continue;
            }

            this.busy = true;
            this.authenticated = false;
            this.DebugLog("Assigned to task");

            this.socket = (Socket) ((CloneableObject) this.queueObject).object;

            try {
                this.InitializeIO();

            } catch (IOException e) {
                this.DebugLog("Failed to initialise I/O (" + e.getMessage() + ").");
                this.Close();
                continue;
            }

            // Perform handshake
            if (this.DoHandshake()) {
                // Start connection main loop
                this.MainLoop();
            }
            else {
                this.DebugLog("Handshake failed!");
                this.Close();
            }
        }
    }

    /**
     * Perform the handshake between the server and the client
     * @return Whether the handshake was successful or not
     */
    private boolean DoHandshake() {
        // Try to generate an RSA key pair
        try {
            this.keyPair = RSA.GenerateRSAKeyPair();
        } catch (NoSuchAlgorithmException e) {
            this.DebugLog("Failed to generate RSA key pair! (" + e.getMessage() + ")");
            this.queueObject = null;
            return false;
        }

        // Perform the handshake process
        boolean handshakeSuccessful = true;

        // Try to send the server's public key to the client
        try {
            this.Send(new KeyPair(this.keyPair.pub, null));

        } catch (IOException e) {
            this.DebugLog(e.getMessage());
            handshakeSuccessful = false;
        }

        // Try to receive the client's key pair
        try {
            this.clientKeyPair = (KeyPair) this.Receive();

        } catch (IOException | ClassNotFoundException e) {
            this.DebugLog(e.getMessage());
            handshakeSuccessful = false;
        }

        return handshakeSuccessful;
    }

    /**
     * The main handler loop for the connection
     */
    private void MainLoop() {
        while (true) {
            try {
                JSONObject request = (JSONObject) this.keyPair.decrypt((EncryptedObject) this.Receive());
                this.Send(this.clientKeyPair.encrypt(this.HandleRequest(request)));

            } catch (PrivateKeyException | PublicKeyException | IOException | ClassNotFoundException e) {
                this.DebugLog("Exception in main protocol: " + e.getMessage());
                this.Close();
                return;
            }
        }
    }

    /**
     * Handle a JSON request object
     * @param request The JSON request object
     * @return The response object
     */
    private Object HandleRequest(JSONObject request) {
        this.request = request;

        switch ((RequestType) request.get("type")) {
            case Authenticate -> {
                return this.Authenticate();
            }

            case GetUser -> {
                return this.GetUser();
            }

            case GetFriendship -> {
                return this.GetFriendship();
            }

            case GetFriendRequests -> {
                return this.GetFriendRequests();
            }

            case GetChat -> {
                return this.GetChat();
            }

            case GetChatInvite -> {
                return this.GetChatInvite();
            }

            case GetPublicKey -> {
                return this.GetPublicKey();
            }

            case AddUser -> {
                return this.AddUser();
            }

            case AddChat -> {
                return this.AddChat();
            }

            case AcceptFriendRequest -> {
                return this.AcceptFriendRequest();
            }

            case DeclineFriendRequest -> {
                return this.DeclineFriendRequest();
            }

            case AcceptChatInvite -> {
                return this.AcceptChatInvite();
            }

            case DeclineChatInvite -> {
                return this.DeclineChatInvite();
            }

            case SendMessage -> {
                return this.SendMessage();
            }

            case SendFriendRequest -> {
                return this.SendFriendRequest();
            }

            case SendChatInvite -> {
                return this.SendChatInvite();
            }
        }

        return null;
    }

    private Object Authenticate() {
        // Get the authentication data from the request
        User authData = (User) this.request.get("data");

        // Get the corresponding user from the database (by username)
        User user = this.server.db.GetUserByUsername(authData.Username);

        // Check if the user is null (i.e. the username is incorrect)
        if (user == null) {
            this.authenticated = false;
            return "failed";
        }
        else {  // Check the auth data is valid
            if (user.Password.contentEquals(authData.Password)) {
                this.authenticated = true;
                return user;
            }
            else {
                this.authenticated = false;
                return "failed";
            }
        }
    }

    private Object GetUser() {
        if (!this.authenticated) {
            return null;
        }

        // Get the user from the request and decrypt
        User requestedUser = (User) this.request.get("data");

        // Get the selector
        String selector = (String) this.request.get("selector");

        // Search the database and return the result
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
            return null;
        }

        // Remove the password from the result
        result = new User(((User) result).UserID, ((User) result).Username, null, ((User) result).DisplayName, ((User) result).DateCreated, ((User) result).ProfilePicturePath);

        return result;
    }

    private Object GetFriendship() {
        if (!this.authenticated) {
            return null;
        }

        // Get the friendship from the request and decrypt
        Friendship requestedFriendship = (Friendship) this.request.get("data");

        // Get the selector
        String selector = (String) this.request.get("selector");

        // Search the database and return the result
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
            return null;
        }

        return result;
    }

    private Object GetFriendRequests() {
        if (!this.authenticated) {
            return null;
        }

        // Get the friend request from the request and decrypt it
        FriendRequest requestedFriendRequest = (FriendRequest) this.request.get("data");

        // Get the selector
        String selector = (String) this.request.get("selector");

        // Search the database and return the result
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
            return null;
        }

        return result;
    }

    private Object GetChat() {
        if (!this.authenticated) {
            return null;
        }

        // Get the chat from the request and decrypt
        Chat requestedChat = (Chat) this.request.get("data");

        // Search the database and return the result
        Object result = this.server.db.GetChatByID(requestedChat.ChatID);

        if (result == null) {
            this.DebugLog("Invalid selector!");
            this.Close();
            return null;
        }

        return result;
    }

    private Object GetChatInvite() {
        if (!this.authenticated) {
            return null;
        }

        // Get the chat invite from the request and decrypt it
        ChatInvite requestedChatInvite = (ChatInvite) this.request.get("data");

        // Get the selector
        String selector = (String) this.request.get("selector");

        // Search the database and return the result
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
            return null;
        }

        return result;
    }

    private Object GetPublicKey() {
        if (!this.authenticated) {
            return null;
        }

        // Get the public key id from the request
        int keyID = (int) this.request.get("data");

        // Get the key pair from the database
        return this.server.db.GetKeyPair(keyID);
    }

    private Object AddUser() {
        // Get the user from the request and decrypt
        User newUser = (User) this.request.get("data");

        // Add the new user through the database
        this.server.db.AddUser(newUser);

        // Get the new user from the database and send back to the client
        return this.server.db.GetUserByUsername(newUser.Username);
    }

    private Object AddChat() {
        if (!this.authenticated) {
            return null;
        }

        // Get the public key from the request
        KeyPair chatKeyPair = (KeyPair) this.request.get("keyPair");

        // Get the chat from the request and decrypt
        Chat newChat = (Chat) this.request.get("data");

        // Add the chat and public key to the database
        this.server.db.AddChat(newChat);
        this.server.db.AddKeyPair(chatKeyPair);

        // Send the new chat back to the client
        return this.server.db.GetChatByPublicKeyID(chatKeyPair.hashCode());
    }

    private Object AcceptFriendRequest() {
        if (!this.authenticated) {
            return null;
        }

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
            return "failed";
        }

        // Reply to the client
        return "done";
    }

    private Object DeclineFriendRequest() {
        if (!this.authenticated) {
            return null;
        }

        // Get the friend request from the request, since the data contained are all integers we do not need to decrypt
        FriendRequest fr = (FriendRequest) this.request.get("data");

        // Delete the friend requests from the database
        if (this.server.db.DeleteFriendRequest(fr.FriendRequestID) == Result.FAILED) {
            return "failed";
        }

        // Reply to the client
        return "done";
    }

    private Object AcceptChatInvite() {
        if (!this.authenticated) {
            return null;
        }

        // Get the chat invite from the request and the private key from the database
        ChatInvite ci = (ChatInvite) this.request.get("data");
        KeyPair privateKey = this.server.db.GetKeyPair(ci.PrivateKeyID);

        // Delete the chat invite from the database
        if (this.server.db.DeleteChatInvite(ci.ChatInviteID) == Result.FAILED || this.server.db.RemoveKeyPair(ci.PrivateKeyID) == Result.FAILED) {
            return "failed";
        }

        return "done";
    }

    private Object DeclineChatInvite() {
        if (!this.authenticated) {
            return null;
        }

        // Get the chat invite from the request and the private key from the database
        ChatInvite ci = (ChatInvite) this.request.get("data");

        // Delete the chat invite from the database
        if (this.server.db.DeleteChatInvite(ci.ChatInviteID) == Result.FAILED || this.server.db.RemoveKeyPair(ci.PrivateKeyID) == Result.FAILED) {
            return "failed";
        }

        return "done";
    }

    private Object SendMessage() {
        if (!this.authenticated) {
            return null;
        }

        // Get the message from the database
        Message message = (Message) this.request.get("data");

        // Add the message to the database
        this.server.db.GetMessageQueue(message.ChatID).Push(message);

        // Reply to the client
        return "done";
    }

    private Object SendFriendRequest() {
        if (!this.authenticated) {
            return null;
        }

        // Get the friend request from the request
        FriendRequest fr = (FriendRequest) this.request.get("data");

        // Add the request to the database
        if (this.server.db.AddFriendRequest(fr) == Result.FAILED) {
            return "failed";
        }

        return "done";
    }

    private Object SendChatInvite() {
        if (!this.authenticated) {
            return null;
        }

        // Get the chat invite and public key from the request
        ChatInvite chatInvite = (ChatInvite) this.request.get("data");
        KeyPair privateKey = (KeyPair) this.request.get("keyPair");

        // Add the chat invite and private key to the database
        if (this.server.db.AddKeyPair(privateKey) == Result.FAILED || this.server.db.AddChatInvite(chatInvite) == Result.FAILED) {
            return "failed";
        }

        // Reply to the client
        return "done";
    }
}
