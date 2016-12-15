package com.margin.camera.listeners;

/**
 * Created on Feb 17, 2016.
 *
 * @author Marta.Ginosyan
 */
public interface OnNoteDeletedListener {

    /**
     * The Note with the specified 'id' was successfully deleted.
     */
    void onNoteDeleted(String id);
}
