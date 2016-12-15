package com.margin.camera.listeners;

import com.margin.camera.models.Photo;

/**
 * Created on Jul 11, 2016.
 *
 * @author Marta.Ginosyan
 */
public interface OnSharePressListener {

    /**
     * Performs an action when share button was pressed
     */
    void onSharePressed(Photo photo);
}
