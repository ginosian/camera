package com.margin.camera.managers;

import android.content.Context;

import com.margin.camera.listeners.OnImageDeletedListener;
import com.margin.camera.listeners.OnImageSavedListener;

/**
 * Created on Feb 07, 2016.
 *
 * @author Marta.Ginosyan
 */
public interface IPhotoCaptureManager {

    /**
     * Save the imageData to the specified path.
     */
    void saveTakenImage(Context context, String path, byte[] imageData, int entityId,
                        boolean rotate, OnImageSavedListener onImageSavedListener);

    /**
     * Delete the image at the specified path.
     */
    void deleteTakenImage(String path, OnImageDeletedListener onImageDeletedListener);
}
