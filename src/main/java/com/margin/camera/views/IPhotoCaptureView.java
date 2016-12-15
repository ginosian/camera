package com.margin.camera.views;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.support.annotation.FloatRange;
import android.view.TextureView;

/**
 * Created on Feb 04, 2016.
 *
 * @author Marta.Ginosyan
 */
public interface IPhotoCaptureView {

    /**
     * Show or hide the 'photo capture' button.
     *
     * @param show True if the capture button should be visible, False otherwise.
     */
    void showCaptureButton(boolean show);

    /**
     * Show or hide the 'gallery' button.
     *
     * @param show True if the gallery button should be visible, False otherwise.
     */
    void showGalleryButton(boolean show);

    /**
     * Show or hide the 'close/discard' button.
     *
     * @param show True if the close button should be visible, False otherwise.
     */
    void showCloseButton(boolean show);

    /**
     * Show or hide the 'done' button.
     *
     * @param show True if the done button should be visible, False otherwise.
     */
    void showDoneButton(boolean show);

    /**
     * Show or hide the 'info' button.
     *
     * @param show True if the info button should be visible, False otherwise.
     */
    void showInfoButton(boolean show);

    /**
     * Show or hide the 'dark overlay'.
     *
     * @param show True if the dark overlay should be visible, False otherwise.
     */
    void showDarkOverlay(boolean show);

    /**
     * Show or hide the Camera view.
     *
     * @param show True if the camera view should be visible, False otherwise.
     */
    void showCameraView(boolean show);

    /**
     * Show or hide the 'photo preview'.
     *
     * @param show True if the photo preview view should be visible, False otherwise.
     */
    void showPhotoPreview(boolean show);

    /**
     * Set the Camera
     */
    void setCamera(Camera camera);

    /**
     * Use the Camera to take a picture.
     */
    void takePicture();

    /**
     * Release the Camera for other applications.
     **/
    void releaseCamera();

    /**
     * Set the Camera Preview.
     */
    void setPreview(TextureView tv);

    /**
     * Set the Camera Preview's surface texture when it is available.
     */
    void setCameraPreviewTexture(SurfaceTexture st);

    /**
     * Set the Close button alpha.
     */
    void setCloseButtonAlpha(@FloatRange(from = 0.0, to = 1.0) float alpha);

    /**
     * Set the Done button alpha.
     */
    void setDoneButtonAlpha(@FloatRange(from = 0.0, to = 1.0) float alpha);

    /**
     * Set the Close button image.
     */
    void setCloseButtonImage(int resourceDrawableId);
}
