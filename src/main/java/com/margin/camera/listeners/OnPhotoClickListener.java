package com.margin.camera.listeners;

import com.margin.camera.models.Photo;

/**
 * Created on Jul 13, 2016.
 *
 * @author Marta.Ginosyan
 */
public interface OnPhotoClickListener {

    /**
     * Performs an action when photo in gallery was clicked
     */
    void onPhotoClicked(Photo photo, int position);
}
