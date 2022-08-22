package com.nathcat.messagecat_database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * Manages the expiry of things such as friend requests, or chat invitations.
 */
public class ExpirationManager extends Thread {
    private final Database db;
    private final long maxTimeElapsed = 2592000000L;

    public ExpirationManager(Database db) {
        this.db = db;
    }

    @Override
    public void run() {
        // Get all friend requests from the database
        ResultSet rs;

        // Check friend requests
        try {
            rs = this.db.mySQLHandler.Select("select * from `friendrequests`");

            while (rs.next()) {
                long timeSent = rs.getLong("TimeSent");
                long currentTime = new Date().getTime();
                if (currentTime >= (timeSent + this.maxTimeElapsed)) {
                    this.db.mySQLHandler.Update("delete from `friendrequests` where `FriendRequestID` like " + rs.getInt("FriendRequestID"));
                }
            }

        } catch (SQLException e) {
            this.DebugLog(e.getMessage() + " when getting friend requests.");
        }

        // Check chat invitations
        try {
            rs = this.db.mySQLHandler.Select("select * from `chatinvitations`");

            while (rs.next()) {
                long timeSent = rs.getLong("TimeSent");
                long currentTime = new Date().getTime();
                if (currentTime >= (timeSent + this.maxTimeElapsed)) {
                    this.db.keyStore.RemoveKeyPair(rs.getInt("PrivateKeyID"));
                    this.db.mySQLHandler.Update("delete from `chatinvitations` where `ChatInviteID` like " + rs.getInt("ChatInviteID"));
                }
            }

        } catch (SQLException e) {
            this.DebugLog(e.getMessage() + " when getting chat invites");
        }
    }

    private void DebugLog(String message) {
        System.out.println("Database (ExpirationManager): " + message);
    }
}
