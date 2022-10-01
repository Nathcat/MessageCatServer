package com.nathcat.messagecat_database_entities;

import org.json.simple.JSONObject;

import java.io.Serializable;

/**
 * Represents a message from the database.
 */
public class Message implements Serializable {
    public final int SenderID;
    public final int ChatID;
    public final long TimeSent;
    public final Object Content;

    public Message(int senderID, int chatID, long timeSent, Object content) {
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

    public JSONObject GetJSONObject() {
        JSONObject json = new JSONObject();
        json.put("SenderID", this.SenderID);
        json.put("ChatID", this.ChatID);
        json.put("TimeSent", this.TimeSent);
        json.put("Content", this.Content);

        return json;
    }
}
