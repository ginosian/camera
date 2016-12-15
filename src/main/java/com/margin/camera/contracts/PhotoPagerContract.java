package com.margin.camera.contracts;

import com.margin.camera.listeners.OnDeletePressListener;
import com.margin.camera.listeners.OnPhotoClickListener;
import com.margin.camera.listeners.OnSharePressListener;
import com.margin.camera.listeners.OnUndoPressListener;
import com.margin.camera.models.Photo;

/**
 * Created on Jul 11, 2016.
 *
 * @author Marta.Ginosyan
 */
public interface PhotoPagerContract {

    interface View extends OnDeletePressListener, OnUndoPressListener, OnSharePressListener {

        /**
         * Sets up {@link android.support.v4.view.ViewPager} with photos
         */
        void initPhotoPager();

        /**
         * Shows and hides action bar depending on show value
         */
        void showActionBar(boolean show);

        /**
         * Shows and hides comments on photo pages depending on show value
         */
        void showComments(boolean show);

        /**
         * Shows and hides system UI (i.e. status bar and etc.) depending on show value
         */
        void showSystemUI(boolean show);

        /**
         * Shows {@link android.support.design.widget.Snackbar} with undo action
         */
        void showUndoSnackbar();

        /**
         * Hides {@link android.support.design.widget.Snackbar} with undo action
         */
        void hideUndoSnackbar();

        /**
         * Refreshes the {@link android.support.v4.view.ViewPager}
         */
        void refreshPhotoPager();

        /**
         * Goes to photo page with inputted index
         */
        void setCurrentPhotoPage(int index);

        /**
         * Disables or enables share menu button
         */
        void enableShareButton(boolean enable);

        /**
         * Disables or enables delete menu button
         */
        void enableDeleteButton(boolean enable);

        /**
         * Delete button was pressed
         */
        void onDeletePressed();

        /**
         * Share button was pressed
         */
        void onSharePressed();
    }

    interface Presenter extends OnPhotoClickListener {

        /**
         * Performs an action when view was created
         */
        void onCreate();

        /**
         * Performs an action when view was destroyed
         */
        void onDestroy();

        /**
         * Performs an action when delete button pressed
         *
         * @param currentPhotoIndex index of current photo page
         */
        void onDeleteButtonPressed(int currentPhotoIndex);

        /**
         * Performs an action when undo button pressed
         */
        void onUndoButtonPressed();

        /**
         * Performs an action when share button pressed
         *
         * @param currentPhotoIndex index of current photo page
         */
        void onShareButtonPressed(int currentPhotoIndex);
    }

    interface Model {

        /**
         * Adds photo to photos list
         */
        void addPhoto(Photo photo, int index);

        /**
         * Removes photo from photos list
         */
        void deletePhoto(int index);

        /**
         * Restores just deleted photo back
         */
        void restoreDeletedPhoto();

        /**
         * Returns index of just deleted photo
         */
        int getDeletedPhotoIndex();

        /**
         * Returns current photo count
         */
        int getPhotoCount();

        /**
         * Returns photo with selected index
         */
        Photo getPhoto(int index);
    }
}
