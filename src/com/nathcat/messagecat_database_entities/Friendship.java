package com.nathcat.messagecat_database_entities;

import java.io.Serializable;

/**
 * Represents a friendship from the database.
 */
public class Friendship implements Serializable {
    public final int FriendshipID;
    public final int UserID;
    public final int FriendID;
    public final String DateEstablished;

    public Friendship(int friendshipID, int userID, int friendID, String dateEstablished) {
        FriendshipID = friendshipID;
        UserID = userID;
        FriendID = friendID;
        DateEstablished = dateEstablished;
    }

    @Override
    public String toString() {
        return "Friendship{" +
                "FriendshipID=" + FriendshipID +
                ", UserID=" + UserID +
                ", FriendID=" + FriendID +
                ", DateEstablished='" + DateEstablished + '\'' +
                '}';
    }
}
