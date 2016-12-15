package com.margin.camera.listeners;

import com.margin.camera.models.Note;

/**
 * Created on Mar 14, 2016.
 *
 * @author Marta.Ginosyan
 */
public interface OnNoteChangedListener {
    void onNoteTypeChanged(String id, String type);

    void onNoteSeverityChanged(String id, int severity);

    void onNoteCommentChanged(String id, String comment);

    void onNoteCreated(Note note);

    void onNoteDeleted(String id);
}