package com.margin.camera.presenters;

import com.margin.camera.models.Note;

/**
 * Created on Feb 17, 2016.
 *
 * @author Marta.Ginosyan
 */
public interface IAnnotationPresenter {

    /**
     * @return noteId of the note that has been created
     */
    String onAddNoteButtonLongPressed();

    void onAddNoteButtonPressed();

    /**
     * Toggle the visibility of the Notes that are displayed on the image.
     * If they are visible, hide them. If they are hidden, make them visible.
     */
    void onVisibilityButtonPressed();

    void onCommentButtonPressed();

    void onHideViewPager();

    void onShowViewPager(int position);

    void onNoteViewPressed(Note note);
}
