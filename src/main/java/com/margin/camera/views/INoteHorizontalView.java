package com.margin.camera.views;

import android.view.View;

/**
 * Created on Mar 15, 2016.
 *
 * @author Marta.Ginosyan
 */
public interface INoteHorizontalView {

    /**
     * Add noteView to childContainer
     */
    void addNoteView(View noteView, String type);

    /**
     * Remove noteView from childContainer
     */
    void removeNoteView(View noteView, String type);

    /**
     * @return noteView associated with this noteId
     */
    NoteView getNoteViewByNoteId(String noteId);
}
