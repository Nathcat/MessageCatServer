package com.nathcat.messagecat_database_entities;

import java.io.Serializable;

/**
 * Represents a User from the database
 */
public class User implements Serializable {
    public final int UserID;
    public final String Username;
    public final String Password;
    public final String DisplayName;
    public final String DateCreated;
    public final String ProfilePicturePath;

    public User(int userID, String username, String password, String displayName, String dateCreated, String profilePicturePath) {
        UserID = userID;
        Username = username;
        Password = password;
        DisplayName = displayName;
        DateCreated = dateCreated;
        ProfilePicturePath = profilePicturePath;
    }

    @Override
    public String toString() {
        return "User{" +
                "UserID=" + UserID +
                ", Username='" + Username + '\'' +
                ", Password='" + Password + '\'' +
                ", DisplayName='" + DisplayName + '\'' +
                ", DateCreated='" + DateCreated + '\'' +
                ", ProfilePicturePath='" + ProfilePicturePath + '\'' +
                '}';
    }
}
