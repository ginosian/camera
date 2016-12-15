package com.margin.camera.listeners;

/**
 * Created on Feb 09, 2016.
 *
 * @author Marta.Ginosyan
 */
public interface OnImageDeletedListener {

    /**
     * The image was successfully deleted.
     */
    void onImageDeleted();

    /**
     * The image failed to be deleted.
     */
    void onImageDeleteFailed(Exception e);
}
