package com.margin.camera.listeners;

/**
 * Created on Feb 09, 2016.
 *
 * @author Marta.Ginosyan
 */
public interface OnImageSavedListener {

    /**
     * The image was successfully saved at the specified 'path'.
     */
    void onImageSaved(String path);

    /**
     * The image failed to be saved.
     */
    void onImageSaveFailed(Exception e);
}
