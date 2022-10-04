package com.nathcat.messagecat_server;

import java.lang.reflect.Field;

/**
 * A listening rule to be supplied to a client when they want to listen for actions performed
 * on the server by other clients.
 *
 * @author Nathan "Nathcat" Baines
 */

public class ListenRule {
    private int id = -1;                // Unique identifier for this listening rule
    public ConnectionHandler handler;   // Handler which is handling the client this listen rule was created by
    private RequestType listenForType;  // The request type which this listen rule is listening for

    // The listen rule will only be triggered if the data in fieldToMatch matches the data in objectToMatch
    private Field fieldToMatch;
    private Object objectToMatch;

    public ListenRule(ConnectionHandler handler, RequestType listenForType, Field fieldToMatch, Object objectToMatch) {
        this.handler = handler;
        this.listenForType = listenForType;
        this.fieldToMatch = fieldToMatch;
        this.objectToMatch = objectToMatch;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) throws IDAlreadySetException {
        if (this.id != -1) {
            throw new IDAlreadySetException();
        }

        this.id = id;
    }

    /**
     * Checks if a request matches the listen rule's criteria, and send the request to the client if it does
     * @param type The type of request
     * @param data The data object to compare to
     * @return True if the listen rule criteria is met, False if not.
     */
    public boolean CheckRequest(RequestType type, Object data) throws NoSuchFieldException, IllegalAccessException {
        return type == listenForType && data.getClass()  // Compare request type to the type we are listening for and get the class object of data
                .getField(fieldToMatch.getName())        // Get the field we are comparing
                .get(data) == objectToMatch;             // Get the data from the field in the instance of data and compare to objectToMatch
    }
}


class IDAlreadySetException extends Exception { }