package com.nathcat.messagecat_database_entities;

/**
 * Represents a chat invite from the database.
 */
public class ChatInvite {
    public final int ChatInviteID;
    public final int ChatID;
    public final int SenderID;
    public final int RecipientID;
    public final long TimeSent;
    public final int PrivateKeyID;

    public ChatInvite(int chatInviteID, int chatID, int senderID, int recipientID, long timeSent, int privateKeyID) {
        ChatInviteID = chatInviteID;
        ChatID = chatID;
        SenderID = senderID;
        RecipientID = recipientID;
        TimeSent = timeSent;
        PrivateKeyID = privateKeyID;
    }

    @Override
    public String toString() {
        return "ChatInvite{" +
                "ChatInviteID=" + ChatInviteID +
                ", ChatID=" + ChatID +
                ", SenderID=" + SenderID +
                ", RecipientID=" + RecipientID +
                ", TimeSent=" + TimeSent +
                ", PrivateKeyID=" + PrivateKeyID +
                '}';
    }
}
