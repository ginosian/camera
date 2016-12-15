package com.margin.camera.listeners;

import com.margin.camera.models.Photo;

/**
 * Created on Jul 19, 2016.
 *
 * @author Marta.Ginosyan
 */
public interface OnUndoPressListener {

    /**
     * Performs an action when undo button was clicked
     */
    void onUndoPressed(Photo photo, int position);
}
