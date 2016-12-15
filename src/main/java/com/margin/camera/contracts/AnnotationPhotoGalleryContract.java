package com.margin.camera.contracts;

/**
 * Created on Jul 12, 2016.
 *
 * @author Marta.Ginosyan
 */
public interface AnnotationPhotoGalleryContract {

    interface View {

        /**
         * Turns visibility button on or off depending on isOn param
         */
        void setVisibilityButtonOn(boolean isOn);

        /**
         * Turns visibility of visibility button on or off depending on visible param
         */
        void setVisibilityButtonVisible(boolean visible);

        /**
         * Shows or hides annotations from the photo page
         */
        void showAnnotations(boolean show);
    }

    interface Presenter {

        /**
         * Performs an action when view was created
         */
        void onCreate();

        /**
         * Performs an action when view was destroyed
         */
        void onDestroy();

        /**
         * Performs an action when visibility button was pressed
         */
        void onVisibilityButtonPressed();
    }

    interface Model {

    }
}
