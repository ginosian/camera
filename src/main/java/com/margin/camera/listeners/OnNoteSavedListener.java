package com.margin.camera.listeners;

import com.margin.camera.models.Note;

/**
 * Created on Feb 17, 2016.
 *
 * @author Marta.Ginosyan
 */
public interface OnNoteSavedListener {

    /**
     * The Note was successfully saved.
     */
    void onNoteSaved(Note note);
}
