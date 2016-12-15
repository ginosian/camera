package com.margin.camera.presenters;

/**
 * Created on Feb 07, 2016.
 *
 * @author Marta.Ginosyan
 */
public interface IPhotoCapturePresenter {

    /**
     * The capture button was pressed.
     */
    void onCaptureButtonPressed();

    /**
     * The image has been captured.
     * Save the image to the fragment's given file path.
     */
    void onPictureCaptured(byte[] imageData);

    /**
     * The image has been selected from the photo gallery.
     * Save the image to the fragment's given file path.
     */
    void onPictureCapturedFromGallery(byte[] imageData);

    /**
     * The close button was pressed.
     * If the image preview is shown, delete the image and show the camera preview.
     * If the camera preview is shown, send the callback to the activity.
     */
    void onCloseButtonPressed();

    /**
     * The done button was pressed.
     * Send the callback to the activity.
     */
    void onDoneButtonPressed();

    /**
     * The photo preview was pressed.
     */
    void onPhotoPreviewPressed();

    /**
     * The info button was pressed.
     */
    void onInfoButtonPressed();
}
