package com.nathcat.messagecat_database_entities;

import java.io.Serializable;

/**
 * Represents a friend request from the database.
 */
public class FriendRequest implements Serializable {
    public final int FriendRequestID;
    public final int SenderID;
    public final int RecipientID;
    public final long TimeSent;

    public FriendRequest(int friendRequestID, int senderID, int recipientID, long timeSent) {
        FriendRequestID = friendRequestID;
        SenderID = senderID;
        RecipientID = recipientID;
        TimeSent = timeSent;
    }

    @Override
    public String toString() {
        return "FriendRequest{" +
                "FriendRequestID=" + FriendRequestID +
                ", SenderID=" + SenderID +
                ", RecipientID=" + RecipientID +
                ", TimeSent=" + TimeSent +
                '}';
    }
}
