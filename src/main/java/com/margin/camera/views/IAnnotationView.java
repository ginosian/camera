package com.margin.camera.views;

import android.support.annotation.FloatRange;
import android.view.View;

import com.margin.camera.models.Note;

/**
 * Created on Feb 11, 2016.
 *
 * @author Marta.Ginosyan
 */
public interface IAnnotationView {

    /**
     * Show or hide the 'add note' button.
     *
     * @param show True if the add note button should be visible, False otherwise.
     */
    void showAddNoteButton(boolean show);

    /**
     * Show or hide the 'note visibility' button.
     *
     * @param show True if the note visibility button should be visible, False otherwise.
     */
    void showVisibilityButton(boolean show);

    /**
     * Show or hide the 'comment' button.
     *
     * @param show True if the comment button should be visible, False otherwise.
     */
    void showCommentButton(boolean show);

    /**
     * Show or hide the 'delete' button.
     *
     * @param show True if the note visibility button should be visible, False otherwise.
     */
    void showDeleteButton(boolean show);

    /**
     * Draw a Note view on the Photo Capture Fragment's image preview.
     */
    void drawNote(Note note);

    /**
     * Remove the specified Note view from the Photo Capture Fragment (i.e., from its parent).
     */
    void removeNote(String noteId);

    /**
     * Show the Note Settings fragment.
     */
    void showNoteSettings(Note note);

    /**
     * Show the Note Adding fragment.
     */
    void showAddingFragment();

    /**
     * Show viewPager with ListFragments. Initialize the ViewPager if it's necessary.
     */
    void showViewPager(int defaultPosition);

    /**
     * Hide viewPager from the pager container.
     */
    void hideViewPager();

    /**
     * Hide the Note Settings fragment.
     *
     * @return True if the Note Settings were hidden, False otherwise (i.e., it is already hidden).
     */
    boolean hideNoteSettings();

    /**
     * Hide the Note Adding fragment.
     *
     * @return True if the Note Adding fragment was hidden, False otherwise (i.e., it is already
     * hidden).
     */
    boolean hideAddingFragment();

    /**
     * Set alpha to view
     */
    void setAlpha(View view, @FloatRange(from = 0.0, to = 1.0) float alpha);

    /**
     * Increase delete button size with animation
     */
    void increaseDeleteButtonSize();

    /**
     * Decrease delete button size with animation
     */
    void decreaseDeleteButtonSize();
}
