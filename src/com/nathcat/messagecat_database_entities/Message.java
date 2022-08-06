package com.nathcat.messagecat_database_entities;

/**
 * Represents a message from the database.
 */
public class Message {
    public final int SenderID;
    public final int ChatID;
    public final long TimeSent;
    public final String Content;

    public Message(int senderID, int chatID, long timeSent, String content) {
        SenderID = senderID;
        ChatID = chatID;
        TimeSent = timeSent;
        Content = content;
    }

    @Override
    public String toString() {
        return "Message{" +
                "SenderID=" + SenderID +
                ", ChatID=" + ChatID +
                ", TimeSent=" + TimeSent +
                ", Content='" + Content + '\'' +
                '}';
    }
}
