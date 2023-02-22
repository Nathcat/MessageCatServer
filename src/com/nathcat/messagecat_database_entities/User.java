package com.nathcat.messagecat_database_entities;

import java.io.Serializable;
import java.lang.reflect.Field;

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
        return "User {\n" +
                "UserID=" + UserID +
                ",\nUsername='" + Username + '\'' +
                ",\nPassword='" + Password + '\'' +
                ",\nDisplayName='" + DisplayName + '\'' +
                ",\nDateCreated='" + DateCreated + '\'' +
                ",\nProfilePicturePath='" + ProfilePicturePath + '\'' +
                "\n}";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != User.class) {
            return false;
        }

        for (Field field : User.class.getFields()) {
            try {
                if (field.get(obj) == null) {
                    if (field.get(this) != null) {
                        return false;
                    }
                }
                else if (!field.get(obj).equals(field.get(this))) {
                    return false;
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return true;
    }
}
