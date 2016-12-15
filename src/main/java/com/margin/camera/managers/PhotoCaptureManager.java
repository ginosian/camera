package com.margin.camera.managers;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.margin.camera.listeners.OnImageDeletedListener;
import com.margin.camera.listeners.OnImageSavedListener;
import com.margin.components.utils.CameraUtils;
import com.margin.components.utils.GATrackerUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created on Feb 08, 2016.
 *
 * @author Marta.Ginosyan
 */
public class PhotoCaptureManager implements IPhotoCaptureManager {

    private static final String TAG = PhotoCaptureManager.class.getSimpleName();
    private static final String DATE_FORMAT_STRING = "yyyyMMdd_HHmmss";
    private static final SimpleDateFormat sDateFormatter =
            new SimpleDateFormat(DATE_FORMAT_STRING, Locale.US);
    private static final int JPEG_QUALITY = 90;

    @Override
    public void saveTakenImage(Context context, String path, byte[] imageData, int entityId,
                               boolean rotate, OnImageSavedListener onImageSavedListener) {
        if (imageData != null && imageData.length > 0) {
            File pictureFile = getOutputMediaFile(path, entityId);
            if (pictureFile == null) {
                onImageSavedListener.onImageSaveFailed(
                        new IOException("Error creating media file: check storage permissions")
                );
                return;
            }
            Bitmap original = CameraUtils.byteArrayToBitmap(imageData);
            if (rotate) original = CameraUtils.rotateImageIfNeeded(context, original);
            Bitmap bitmap = CameraUtils.cropAndScaleImageIfNeeded(context, original);
            if (bitmap != original) {
                original.recycle();
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, fos);
                fos.flush();
                fos.close();
                onImageSavedListener.onImageSaved(pictureFile.getAbsolutePath());
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
                onImageSavedListener.onImageSaveFailed(e);
                GATrackerUtils.trackException(context, e);
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
                onImageSavedListener.onImageSaveFailed(e);
                GATrackerUtils.trackException(context, e);
            }
        } else {
            onImageSavedListener.onImageSaveFailed(new Exception("Empty image data!"));
        }
    }

    @Override
    public void deleteTakenImage(String path, OnImageDeletedListener onImageDeletedListener) {
        if (TextUtils.isEmpty(path)) {
            onImageDeletedListener.onImageDeleteFailed(
                    new IllegalArgumentException("Error deleting image: image has an empty path")
            );
            return;
        }
        File fdelete = new File(path);
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                Log.d(TAG, "File Deleted :" + path);
                onImageDeletedListener.onImageDeleted();
            } else {
                Log.d(TAG, "File not Deleted :" + path);
                onImageDeletedListener.onImageDeleteFailed(
                        new IOException("File has not been deleted!"));
            }
        }
    }

    /**
     * Return a new File at the specified 'path', with a file name of the form:
     * IMG_'entityId'_'yyyyMMdd_HHmmss'.jpg
     */
    private File getOutputMediaFile(String path, int entityId) {
        File mediaStorageDir = new File(path);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Failed to create directory");
                return null;
            }
        }
        // Create a media file name
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + entityId + "_" + sDateFormatter.format(new Date()) + ".jpg");
        return mediaFile;
    }
}
