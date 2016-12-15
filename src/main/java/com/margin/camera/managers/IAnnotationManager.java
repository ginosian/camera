package com.margin.camera.managers;

import android.content.Context;

import com.margin.camera.listeners.OnNoteDeletedListener;
import com.margin.camera.listeners.OnNoteSavedListener;
import com.margin.camera.models.Note;

/**
 * Created on Feb 17, 2016.
 *
 * @author Marta.Ginosyan
 */
public interface IAnnotationManager {

    Note createNote(Context context, boolean withCoordinates);

    void createNote(Context context, OnNoteSavedListener onNoteSavedListener,
                    boolean withCoordinates);

    void setDefaultCoordinates(Context context, Note note);

    void deleteNote(Note note, OnNoteDeletedListener onNoteDeletedListener);
}
