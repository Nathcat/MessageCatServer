package com.nathcat.messagecat_database_entities;

import java.io.Serializable;

/**
 * Represents a chat from the database.
 */
public class Chat implements Serializable {
    public final int ChatID;
    public final String Name;
    public final String Description;
    public final int PublicKeyID;

    public Chat(int chatID, String name, String description, int publicKeyID) {
        ChatID = chatID;
        Name = name;
        Description = description;
        PublicKeyID = publicKeyID;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "ChatID=" + ChatID +
                ", Name='" + Name + '\'' +
                ", Description='" + Description + '\'' +
                ", PublicKeyID=" + PublicKeyID +
                '}';
    }
}
