package com.margin.camera.listeners;

import com.margin.camera.models.Photo;

/**
 * Created on Jul 11, 2016.
 *
 * @author Marta.Ginosyan
 */
public interface OnDeletePressListener {

    /**
     * Performs an action when delete button was clicked
     */
    void onDeletePressed(Photo photo, int position);
}
