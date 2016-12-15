package com.margin.camera.contracts;

import com.margin.camera.fragments.PhotoPagerFragment;
import com.margin.camera.listeners.OnClosePressListener;
import com.margin.camera.listeners.OnDeletePressListener;
import com.margin.camera.listeners.OnSharePressListener;
import com.margin.camera.listeners.OnUndoPressListener;

/**
 * Created on Jul 11, 2016.
 *
 * @author Marta.Ginosyan
 */
public interface PhotoGalleryContract {

    String PHOTOS = "photos";
    String POSITION = "position";

    interface View extends OnClosePressListener, OnSharePressListener, OnDeletePressListener,
            OnUndoPressListener {

        /**
         * Sets up the {@link android.support.v7.widget.Toolbar}
         */
        void initToolbar();

        /**
         * Shows the {@link PhotoPagerFragment}
         */
        void showGalleryFragment();

        /**
         * Disables or enables share menu button
         */
        void enableShareButton(boolean enable);

        /**
         * Disables or enables delete menu button
         */
        void enableDeleteButton(boolean enable);

        /**
         * Passes delete action to fragment
         */
        void onDeletePressed();

        /**
         * Passes share action to fragment
         */
        void onSharePressed();
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
         * Performs an action when clear button pressed
         */
        void onClearButtonPressed();

        /**
         * Performs an action when share button pressed
         */
        void onShareButtonPressed();

        /**
         * Performs an action when delete button pressed
         */
        void onDeleteButtonPressed();
    }

    interface Model {

    }
}
