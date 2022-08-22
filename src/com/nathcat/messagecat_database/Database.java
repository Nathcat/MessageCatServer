package com.nathcat.messagecat_database;

import com.nathcat.RSA.KeyPair;
import com.nathcat.messagecat_database_entities.*;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Wrapper which combines the different database systems into one unit.
 *
 * @author Nathan "Nathcat" Baines
 */
public class Database {
    protected MySQLHandler mySQLHandler = null;   // MySQLHandler instance
    protected MessageStore messageStore = null;   // MessageStore instance
    protected KeyStore keyStore = null;           // KeyStore instance
    private final ExpirationManager expirationManager;  // The expiration manager

    /**
     * Default constructor
     */
    public Database() {
        // Try to create instances of the three database systems
        try {
            this.mySQLHandler = new MySQLHandler();
            this.keyStore = new KeyStore();
            this.messageStore = new MessageStore();

        } catch (ParseException | SQLException | IOException e) {
            e.printStackTrace();
        }

        // Ensure that all the systems have been initialised correctly
        assert this.mySQLHandler != null && this.messageStore != null && this.keyStore != null;

        // Start the expiration manager
        expirationManager = new ExpirationManager(this);
        expirationManager.setDaemon(true);
        expirationManager.start();
    }

    /**
     * @see com.nathcat.messagecat_database.MySQLHandler#GetUserByID(int)
     */
    public User GetUserByID(int UserID) {
        try {
            return this.mySQLHandler.GetUserByID(UserID);

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @see com.nathcat.messagecat_database.MySQLHandler#GetUserByUsername(String)
     */
    public User GetUserByUsername(String Username) {
        try {
            return this.mySQLHandler.GetUserByUsername(Username);

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @see com.nathcat.messagecat_database.MySQLHandler#GetUserByDisplayName(String)
     */
    public User[] GetUserByDisplayName(String DisplayName) {
        try {
            return this.mySQLHandler.GetUserByDisplayName(DisplayName);

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @see com.nathcat.messagecat_database.MySQLHandler#GetFriendshipByID(int)
     */
    public Friendship GetFriendshipByID(int FriendshipID) {
        try {
            return this.mySQLHandler.GetFriendshipByID(FriendshipID);

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @see com.nathcat.messagecat_database.MySQLHandler#GetFriendshipByUserID(int)
     */
    public Friendship[] GetFriendshipByUserID(int UserID) {
        try {
            return this.mySQLHandler.GetFriendshipByUserID(UserID);

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @see com.nathcat.messagecat_database.MySQLHandler#GetFriendshipByUserIDAndFriendID(int, int)
     */
    public Friendship GetFriendshipByUserIDAndFriendID(int UserID, int FriendID) {
        try {
            return this.mySQLHandler.GetFriendshipByUserIDAndFriendID(UserID, FriendID);

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @see com.nathcat.messagecat_database.MySQLHandler#GetFriendRequestsBySenderID(int)
     */
    public FriendRequest[] GetFriendRequestsBySenderID(int SenderID) {
        try {
            return this.mySQLHandler.GetFriendRequestsBySenderID(SenderID);

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @see com.nathcat.messagecat_database.MySQLHandler#GetFriendRequestsByRecipientID(int)
     */
    public FriendRequest[] GetFriendRequestsByRecipientID(int RecipientID) {
        try {
            return this.mySQLHandler.GetFriendRequestsByRecipientID(RecipientID);

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @see com.nathcat.messagecat_database.MySQLHandler#DeleteFriendRequest(int) 
     */
    public Result DeleteFriendRequest(int FriendRequestID) {
        try {
            this.mySQLHandler.DeleteFriendRequest(FriendRequestID);
            return Result.SUCCESS;

        } catch (SQLException e) {
            return Result.FAILED;
        }
    }

    /**
     * @see com.nathcat.messagecat_database.MySQLHandler#GetChatByID(int)
     */
    public Chat GetChatByID(int ChatID) {
        try {
            return this.mySQLHandler.GetChatByID(ChatID);

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @see com.nathcat.messagecat_database.MySQLHandler#GetChatByPublicKeyID(int) 
     */
    public Chat GetChatByPublicKeyID(int PublicKeyID) {
        try {
            return this.mySQLHandler.GetChatByPublicKeyID(PublicKeyID);
            
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @see com.nathcat.messagecat_database.MySQLHandler#GetChatInviteByID(int)
     */
    public ChatInvite GetChatInviteByID(int ChatInviteID) {
        try {
            return this.mySQLHandler.GetChatInviteByID(ChatInviteID);

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @see com.nathcat.messagecat_database.MySQLHandler#GetChatInvitesBySenderID(int)
     */
    public ChatInvite[] GetChatInvitesBySenderID(int SenderID) {
        try {
            return this.mySQLHandler.GetChatInvitesBySenderID(SenderID);

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @see com.nathcat.messagecat_database.MySQLHandler#GetChatInvitesByRecipientID(int)
     */
    public ChatInvite[] GetChatInvitesByRecipientID(int RecipientID) {
        try {
            return this.mySQLHandler.GetChatInvitesByRecipientID(RecipientID);

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Result DeleteChatInvite(int ChatInviteID) {
        try {
            this.mySQLHandler.DeleteChatInvite(ChatInviteID);
            return Result.SUCCESS;

        } catch (SQLException e) {
            return Result.FAILED;
        }
    }

    /**
     * @see com.nathcat.messagecat_database.MySQLHandler#AddUser(User)
     */
    public Result AddUser(User user) {
        try {
            this.mySQLHandler.AddUser(user);
            return Result.SUCCESS;

        } catch (SQLException e) {
            e.printStackTrace();
            return Result.FAILED;
        }
    }

    /**
     * @see com.nathcat.messagecat_database.MySQLHandler#AddFriendship(Friendship)
     */
    public Result AddFriendship(Friendship friendship) {
        try {
            this.mySQLHandler.AddFriendship(friendship);
            return Result.SUCCESS;

        } catch (SQLException e) {
            e.printStackTrace();
            return Result.FAILED;
        }
    }

    /**
     * @see com.nathcat.messagecat_database.MySQLHandler#AddFriendRequest(FriendRequest)
     */
    public Result AddFriendRequest(FriendRequest friendRequest) {
        try {
            this.mySQLHandler.AddFriendRequest(friendRequest);
            return Result.SUCCESS;

        } catch (SQLException e) {
            e.printStackTrace();
            return Result.FAILED;
        }
    }

    /**
     * @see com.nathcat.messagecat_database.MySQLHandler#AddChat(Chat)
     */
    public Result AddChat(Chat chat) {
        try {
            this.mySQLHandler.AddChat(chat);
            return Result.SUCCESS;

        } catch (SQLException e) {
            e.printStackTrace();
            return Result.FAILED;
        }
    }

    /**
     * @see com.nathcat.messagecat_database.MySQLHandler#AddChatInvite(ChatInvite)
     */
    public Result AddChatInvite(ChatInvite chatInvite) {
        try {
            this.mySQLHandler.AddChatInvite(chatInvite);
            return Result.SUCCESS;

        } catch (SQLException e) {
            e.printStackTrace();
            return Result.FAILED;
        }
    }

    /**
     * @see com.nathcat.messagecat_database.MessageStore#GetMessageQueue(int)
     */
    public MessageQueue GetMessageQueue(int ChatID) {
        return this.messageStore.GetMessageQueue(ChatID);
    }

    /**
     * @see com.nathcat.messagecat_database.MessageStore#AddMessageQueue(MessageQueue)
     */
    public Result AddMessageQueue(MessageQueue messageQueue) {
        return this.messageStore.AddMessageQueue(messageQueue);
    }

    /**
     * Add a key pair to the key store
     * @param pair The key pair to add
     * @return The result code
     * @see com.nathcat.messagecat_database.KeyStore#AddKeyPair(KeyPair)
     */
    public Result AddKeyPair(KeyPair pair) {
        return this.keyStore.AddKeyPair(pair);
    }

    /**
     * Get a key pair from the key store
     * @param keyID The ID of the kay pair
     * @return The key pair found at this ID
     */
    public KeyPair GetKeyPair(int keyID) {
        return this.keyStore.GetKeyPair(keyID);
    }

    /**
     * Remove a key pair from the key store
     * @param id The id of the key pair to remove
     * @return The result code
     * @see com.nathcat.messagecat_database.KeyStore#RemoveKeyPair(int)
     */
    public Result RemoveKeyPair(int id) {
        return this.keyStore.RemoveKeyPair(id);
    }
}
