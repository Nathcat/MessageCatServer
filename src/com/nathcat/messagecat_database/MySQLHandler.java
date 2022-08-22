package com.nathcat.messagecat_database;

import com.nathcat.messagecat_database_entities.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.Scanner;

/**
 * This class handles calls to the MySQL database made through the Database class.
 *
 * @author Nathan "Nathcat" Baines
 */
public class MySQLHandler {
    private final Connection conn;  // The connection to the MySQL database.

    /**
     * Default constructor, creates a connection to the database.
     */
    public MySQLHandler() throws FileNotFoundException, ParseException, SQLException {
        // Instead of catching those exceptions, we should leave them to be caught elsewhere.
        // If any of them are thrown, this object shouldn't be allowed to be created.

        // Get the MySQL config file
        JSONObject config = this.GetMySQLConfig();

        // Create a connection to the MySQL database.
        conn = DriverManager.getConnection((String) config.get("connection_url"), (String) config.get("username"), (String) config.get("password"));
    }

    /**
     * Get the MySQL config JSON file.
     * @return A JSON Object containing MySQL config data.
     * @throws FileNotFoundException Thrown if the config file cannot be found.
     * @throws ParseException Thrown if the data in the config file contains a syntax error.
     */
    private JSONObject GetMySQLConfig() throws FileNotFoundException, ParseException {
        Scanner reader = new Scanner(new File("Assets/MySQL_Config.json"));
        StringBuilder sb = new StringBuilder();

        while (reader.hasNextLine()) {
            sb.append(reader.nextLine());
        }

        return (JSONObject) new JSONParser().parse(sb.toString());
    }

    /**
     * Perform a Select query on the database.
     * @param query The query to be executed
     * @return The ResultSet returned from the query
     * @throws SQLException Thrown by SQL errors.
     */
    protected ResultSet Select(String query) throws SQLException {
        // Create and execute the statement
        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        stmt.execute(query);
        // Get the result set and close the statement
        ResultSet rs = stmt.getResultSet();

        // Return the result set
        return rs;
    }

    /**
     * Perform an update query on the database (or any query that does not have a result set)
     * @param query The query to be executed
     * @throws SQLException Thrown by SQL errors
     */
    protected void Update(String query) throws SQLException {
        // Create and execute the statement
        Statement stmt = conn.createStatement();
        stmt.execute(query);

        // Close the statement
        stmt.close();
    }

    /**
     * Get a User by their ID.
     * @param UserID The UserID to search for.
     * @return The User that was found, or null if none were found.
     * @throws SQLException Thrown by SQL errors.
     */
    public User GetUserByID(int UserID) throws SQLException {
        // Get the result set from the query
        ResultSet rs = this.Select("SELECT * FROM `Users` WHERE `UserID` like " + UserID);

        // Check if there are no results
        rs.last();
        if (rs.getRow() == 0) {
            return null;
        }

        // There must be 1 result, since we are searching for a primary key.
        // Get the data from this 1 result, close the result set, and return the user.
        rs.first();
        User result = new User(
                rs.getInt("UserID"),
                rs.getString("Username"),
                rs.getString("Password"),
                rs.getString("DisplayName"),
                rs.getString("DateCreated"),
                rs.getString("ProfilePicturePath")
        );

        rs.close();
        return result;
    }

    /**
     * Get a user by their username.
     * @param Username The username to search for
     * @return The User that is found, or null, if none are found.
     * @throws SQLException Thrown by SQL errors.
     */
    public User GetUserByUsername(String Username) throws SQLException {
        // Get the result set from the query
        ResultSet rs = this.Select("SELECT * FROM `Users` WHERE `Username` like '" + Username + "'");

        // Check if there are no results
        rs.last();
        if (rs.getRow() == 0) {
            return null;
        }

        // There must be 1 result, since we are searching for a unique value.
        // Get the data from this 1 result, close the result set, and return the user.
        rs.first();
        User result = new User(
                rs.getInt("UserID"),
                rs.getString("Username"),
                rs.getString("Password"),
                rs.getString("DisplayName"),
                rs.getString("DateCreated"),
                rs.getString("ProfilePicturePath")
        );

        rs.close();
        return result;
    }

    /**
     * Get a list of users by their display name.
     * @param DisplayName The display name to search for
     * @return A list of users whose display names start with DisplayName
     * @throws SQLException Thrown by SQL errors.
     */
    public User[] GetUserByDisplayName(String DisplayName) throws SQLException {
        // Get the result set from the query, include a wildcard character in the query so that we get the users whose
        // display names start with DisplayName.
        ResultSet rs = this.Select("SELECT * FROM `Users` WHERE `DisplayName` like '" + DisplayName + "%'");

        // Check if there are no results
        rs.last();
        if (rs.getRow() == 0) {
            return new User[0];
        }

        // There must be at least one result, so create an array of Users.
        User[] results = new User[rs.getRow()];

        rs.beforeFirst();

        while (rs.next()) {
            results[rs.getRow() - 1] = new User(
                    rs.getInt("UserID"),
                    rs.getString("Username"),
                    rs.getString("Password"),
                    rs.getString("DisplayName"),
                    rs.getString("DateCreated"),
                    rs.getString("ProfilePicturePath")
            );
        }

        rs.close();
        return results;
    }

    /**
     * Get a friendship record from the database
     * @param FriendshipID The ID of the record
     * @return The record found, or null if none are found
     * @throws SQLException Thrown by SQL errors
     */
    public Friendship GetFriendshipByID(int FriendshipID) throws SQLException {
        // Get the result set from the query
        ResultSet rs = this.Select("SELECT * FROM `Friendships` WHERE `FriendshipID` like " + FriendshipID);

        // Check if there are no results
        rs.last();
        if (rs.getRow() == 0) {
            return null;
        }

        // There must be 1 result, since we are searching for a unique value.
        // Get the data from this 1 result, close the result set, and return the friendship.
        rs.first();
        Friendship result = new Friendship(
                rs.getInt("FriendshipID"),
                rs.getInt("UserID"),
                rs.getInt("FriendID"),
                rs.getString("DateEstablished")
        );

        rs.close();
        return result;
    }

    /**
     * Get a friendship record by the UserID
     * @param UserID The UserID to search for
     * @return The records found, or an empty array if none are found
     * @throws SQLException Thrown by SQL errors
     */
    public Friendship[] GetFriendshipByUserID(int UserID) throws SQLException {
        // Get the result set from the query
        ResultSet rs = this.Select("SELECT * FROM `Friendships` WHERE `UserID` like " + UserID);

        // Check if there are no results
        rs.last();
        if (rs.getRow() == 0) {
            return new Friendship[0];
        }

        // There must be at least one result, so create an array of friendships.
        Friendship[] results = new Friendship[rs.getRow()];

        rs.beforeFirst();

        while (rs.next()) {
            results[rs.getRow() - 1] = new Friendship(
                    rs.getInt("FriendshipID"),
                    rs.getInt("UserID"),
                    rs.getInt("FriendID"),
                    rs.getString("DateEstablished")
            );
        }

        rs.close();
        return results;
    }

    /**
     * Get a friendship record by its UserID and FriendID
     * @param UserID The UserID
     * @param FriendID The FriendID
     * @return The Friendship record that is found, or null if none are found
     * @throws SQLException Thrown by SQL errors
     */
    public Friendship GetFriendshipByUserIDAndFriendID(int UserID, int FriendID) throws SQLException {
        // Get the result set from the query
        ResultSet rs = this.Select("SELECT * FROM `Friendships` WHERE `UserID` like " + UserID + " AND `FriendID` like " + FriendID);

        // Check if there are no results
        rs.last();
        if (rs.getRow() == 0) {
            return null;
        }

        // There must be 1 result, since we are searching for a unique value.
        // Get the data from this 1 result, close the result set, and return the friendship.
        rs.first();
        Friendship result = new Friendship(
                rs.getInt("FriendshipID"),
                rs.getInt("UserID"),
                rs.getInt("FriendID"),
                rs.getString("DateEstablished")
        );

        rs.close();
        return result;
    }

    /**
     * Get a User's friend requests (requests where they are the recipient)
     * @param RecipientID The UserID of the recipient
     * @return An array of friend requests
     * @throws SQLException Thrown by SQL errors
     */
    public FriendRequest[] GetFriendRequestsByRecipientID(int RecipientID) throws SQLException {
        // Get the result set from the query
        ResultSet rs = this.Select("SELECT * FROM `FriendRequests` WHERE `RecipientID` like " + RecipientID);

        // Check if there are no results
        rs.last();
        if (rs.getRow() == 0) {
            return new FriendRequest[0];
        }

        // There must be at least one result, so create an array of requests.
        FriendRequest[] results = new FriendRequest[rs.getRow()];

        rs.beforeFirst();

        while (rs.next()) {
            results[rs.getRow() - 1] = new FriendRequest(
                    rs.getInt("FriendRequestID"),
                    rs.getInt("SenderID"),
                    rs.getInt("RecipientID"),
                    rs.getLong("TimeSent")
            );
        }

        rs.close();
        return results;
    }

    /**
     * Get a User's friend requests (requests they have sent)
     * @param SenderID The UserID of the sender
     * @return An array of friend requests
     * @throws SQLException Thrown by SQL errors
     */
    public FriendRequest[] GetFriendRequestsBySenderID(int SenderID) throws SQLException {
        // Get the result set from the query
        ResultSet rs = this.Select("SELECT * FROM `FriendRequests` WHERE `SenderID` like " + SenderID);

        // Check if there are no results
        rs.last();
        if (rs.getRow() == 0) {
            return new FriendRequest[0];
        }

        // There must be at least one result, so create an array of requests.
        FriendRequest[] results = new FriendRequest[rs.getRow()];

        rs.beforeFirst();

        while (rs.next()) {
            results[rs.getRow() - 1] = new FriendRequest(
                    rs.getInt("FriendRequestID"),
                    rs.getInt("SenderID"),
                    rs.getInt("RecipientID"),
                    rs.getLong("TimeSent")
            );
        }

        rs.close();
        return results;
    }

    /**
     * Delete a friend request
     * @param FriendRequestID The ID of the friend request to delete
     * @throws SQLException Thrown by SQL errors
     */
    public void DeleteFriendRequest(int FriendRequestID) throws SQLException {
        this.Update("DELETE FROM `FriendRequests` WHERE `FriendRequestID` like " + FriendRequestID);
    }

    /**
     * Get a chat record from the database
     * @param ChatID The ID of the record
     * @return The record found, or null if none are found
     * @throws SQLException Thrown by SQL errors
     */
    public Chat GetChatByID(int ChatID) throws SQLException {
        // Get the result set from the query
        ResultSet rs = this.Select("SELECT * FROM `Chats` WHERE `ChatID` like " + ChatID);

        // Check if there are no results
        rs.last();
        if (rs.getRow() == 0) {
            return null;
        }

        // There must be 1 result, since we are searching for a unique value.
        // Get the data from this 1 result, close the result set, and return the chat.
        rs.first();
        Chat result = new Chat(
                rs.getInt("ChatID"),
                rs.getString("Name"),
                rs.getString("Description"),
                rs.getInt("PublicKeyID")
        );

        rs.close();
        return result;
    }

    /**
     * Get a chat record from the database by the public key id associated with it
     * @param PublicKeyID The public key id to search for
     * @return The Chat that is found
     * @throws SQLException Thrown by SQL errors
     */
    public Chat GetChatByPublicKeyID(int PublicKeyID) throws SQLException {
        // Get the result set from the query
        ResultSet rs = this.Select("SELECT * FROM `Chats` WHERE `PublicKeyID` like " + PublicKeyID);

        // Check if there are no results
        rs.last();
        if (rs.getRow() == 0) {
            return null;
        }

        // There must be 1 result, since we are searching for a unique value.
        // Get the data from this 1 result, close the result set, and return the chat.
        rs.first();
        Chat result = new Chat(
                rs.getInt("ChatID"),
                rs.getString("Name"),
                rs.getString("Description"),
                rs.getInt("PublicKeyID")
        );

        rs.close();
        return result;
    }

    /**
     * Get a chat invite record from the database
     * @param ChatInviteID The ID of the record
     * @return The record found, or null if none are found
     * @throws SQLException Thrown by SQL errors
     */
    public ChatInvite GetChatInviteByID(int ChatInviteID) throws SQLException {
        // Get the result set from the query
        ResultSet rs = this.Select("SELECT * FROM `ChatInvitations` WHERE `ChatInviteID` like " + ChatInviteID);

        // Check if there are no results
        rs.last();
        if (rs.getRow() == 0) {
            return null;
        }

        // There must be 1 result, since we are searching for a unique value.
        // Get the data from this 1 result, close the result set, and return the invite
        rs.first();
        ChatInvite result = new ChatInvite(
                rs.getInt("ChatInviteID"),
                rs.getInt("ChatID"),
                rs.getInt("SenderID"),
                rs.getInt("RecipientID"),
                rs.getLong("TimeSent"),
                rs.getInt("PrivateKeyID")
        );

        rs.close();
        return result;
    }

    /**
     * Get a user's invitations to chats
     * @param RecipientID The UserID of the recipient
     * @return An array of chat invites
     * @throws SQLException Thrown by SQL errors
     */
    public ChatInvite[] GetChatInvitesByRecipientID(int RecipientID) throws SQLException {
        // Get the result set from the query
        ResultSet rs = this.Select("SELECT * FROM `ChatInvitations` WHERE `RecipientID` like " + RecipientID);

        // Check if there are no results
        rs.last();
        if (rs.getRow() == 0) {
            return new ChatInvite[0];
        }

        // There must be at least one result, so create an array of invites.
        ChatInvite[] results = new ChatInvite[rs.getRow()];

        rs.beforeFirst();

        while (rs.next()) {
            results[rs.getRow() - 1] = new ChatInvite(
                    rs.getInt("ChatInviteID"),
                    rs.getInt("ChatID"),
                    rs.getInt("SenderID"),
                    rs.getInt("RecipientID"),
                    rs.getLong("TimeSent"),
                    rs.getInt("PrivateKeyID")
            );
        }

        rs.close();
        return results;
    }

    /**
     * Get a user's invitations to chats
     * @param SenderID The UserID of the sender
     * @return An array of chat invites
     * @throws SQLException Thrown by SQL errors
     */
    public ChatInvite[] GetChatInvitesBySenderID(int SenderID) throws SQLException {
        // Get the result set from the query
        ResultSet rs = this.Select("SELECT * FROM `ChatInvitations` WHERE `SenderID` like " + SenderID);

        // Check if there are no results
        rs.last();
        if (rs.getRow() == 0) {
            return new ChatInvite[0];
        }

        // There must be at least one result, so create an array of invites.
        ChatInvite[] results = new ChatInvite[rs.getRow()];

        rs.beforeFirst();

        while (rs.next()) {
            results[rs.getRow() - 1] = new ChatInvite(
                    rs.getInt("ChatInviteID"),
                    rs.getInt("ChatID"),
                    rs.getInt("SenderID"),
                    rs.getInt("RecipientID"),
                    rs.getLong("TimeSent"),
                    rs.getInt("PrivateKeyID")
            );
        }

        rs.close();
        return results;
    }

    /**
     * Delete a chat invite from the database
     * @param ChatInviteID The ID of the chat invite
     * @throws SQLException Thrown by SQL errors
     */
    public void DeleteChatInvite(int ChatInviteID) throws SQLException {
        this.Update("DELETE FROM `ChatInvitations` WHERE `ChatInviteID` like " + ChatInviteID);
    }

    /**
     * Adds a user to the database
     * @param user The user to add
     * @throws SQLException Thrown by SQL errors
     */
    public void AddUser(User user) throws SQLException {
        this.Update("insert into `Users` (`Username`, `Password`, `DisplayName`, `DateCreated`, `ProfilePicturePath`) values (" +
                "\"" + user.Username + "\", " +
                "\"" + user.Password + "\", " +
                "\"" + user.DisplayName + "\", " +
                "\"" + user.DateCreated + "\", " +
                "\"" + user.ProfilePicturePath + "\");"
        );
    }

    /**
     * Adds a friendship to the database
     * @param friendship The friendship to add
     * @throws SQLException Thrown by SQL errors
     */
    public void AddFriendship(Friendship friendship) throws SQLException {
        this.Update("insert into `Friendships` (`UserID`, `FriendID`, `DateEstablished`) values (" +
                friendship.UserID + ", " +
                friendship.FriendID + ", " +
                "\"" + friendship.DateEstablished + "\");"
        );
    }

    /**
     * Adds a friend request to the database
     * @param friendRequest The friend request to add
     * @throws SQLException Thrown by SQL errors
     */
    public void AddFriendRequest(FriendRequest friendRequest) throws SQLException {
        this.Update("insert into `FriendRequests` (`SenderID`, `RecipientID`, `TimeSent`) values (" +
                friendRequest.SenderID + ", " +
                friendRequest.RecipientID + ", " +
                friendRequest.TimeSent + ");"
        );
    }

    /**
     * Adds a chat to the database
     * @param chat The chat to add
     * @throws SQLException Thrown by SQL errors
     */
    public void AddChat(Chat chat) throws SQLException {
        this.Update("insert into `Chats` (`Name`, `Description`, `PublicKeyID`) values (" +
                "\"" + chat.Name + "\", " +
                "\"" + chat.Description + "\", " +
                chat.PublicKeyID + ");"
        );
    }

    /**
     * Adds a chat invite to the database
     * @param chatInvite The chat invite to add
     * @throws SQLException Thrown by SQL errors
     */
    public void AddChatInvite(ChatInvite chatInvite) throws SQLException {
        this.Update("insert into `ChatInvitations` (`ChatID`, `SenderID`, `RecipientID`, `TimeSent`, `PrivateKeyID`) values (" +
                chatInvite.ChatID + ", " +
                chatInvite.SenderID + ", " +
                chatInvite.RecipientID + ", " +
                chatInvite.TimeSent + ", " +
                chatInvite.PrivateKeyID + ");"
        );
    }
}
