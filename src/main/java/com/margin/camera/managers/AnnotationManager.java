package com.margin.camera.managers;

import android.content.Context;

import com.margin.camera.listeners.OnNoteDeletedListener;
import com.margin.camera.listeners.OnNoteSavedListener;
import com.margin.camera.models.Note;

import java.util.UUID;

/**
 * Created on Feb 17, 2016.
 *
 * @author Marta.Ginosyan
 */
public class AnnotationManager implements IAnnotationManager {

    private static AnnotationManager sAnnotationManager;
    private static int[] sDefaultCoordinates;

    private AnnotationManager() {
    }

    public static AnnotationManager getInstance() {
        if (sAnnotationManager == null) {
            sAnnotationManager = new AnnotationManager();
        }
        return sAnnotationManager;
    }

    @Override
    public Note createNote(Context context, boolean withCoordinates) {
        Note note = new Note();
        note.setNoteId(getUniqueId());
        if (withCoordinates) {
            setDefaultCoordinates(context, note);
        }
        return note;
    }

    @Override
    public void createNote(Context context, OnNoteSavedListener onNoteSavedListener,
                           boolean withCoordinates) {
        Note note = createNote(context, withCoordinates);
        onNoteSavedListener.onNoteSaved(note);
    }

    @Override
    public void setDefaultCoordinates(Context context, Note note) {
        int[] coordinates = getDefaultCoordinates(context);
        note.setX(coordinates[0]);
        note.setY(coordinates[1]);
    }

    public int[] getDefaultCoordinates(Context context) {
        if (sDefaultCoordinates == null) {
            int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
            int screenHeight = context.getResources().getDisplayMetrics().heightPixels;
            sDefaultCoordinates = new int[]{screenWidth / 2, screenHeight / 3};
        }
        return sDefaultCoordinates;
    }

    @Override
    public void deleteNote(Note note, OnNoteDeletedListener onNoteDeletedListener) {
        // TODO: Does nothing for now
        String noteId = note.note_id();
        onNoteDeletedListener.onNoteDeleted(noteId);
    }

    /**
     * Create unique string ID based on the {@link UUID}.
     */
    public String getUniqueId() {
        return UUID.randomUUID().toString();
    }
}
