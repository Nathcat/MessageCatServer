package com.nathcat.messagecat_server;

/**
 * Generic cloneable object
 *
 * @author Nathan "Nathcat" Baines
 */
public class CloneableObject implements Cloneable {
    public final Object object;

    public CloneableObject(Object object) {
        this.object = object;
    }

    @Override
    public CloneableObject clone() {
        try {
            return (CloneableObject) super.clone();

        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return null;
    }
}
