package com.margin.camera.presenters;

/**
 * Created on Feb 18, 2016.
 *
 * @author Marta.Ginosyan
 */
public interface INoteAdapterPresenter {

    /**
     * The Note at the specified 'position' changed its 'type'.
     */
    void onNoteTypeChanged(String noteId, String type);

    /**
     * The Note at the specified 'position' changed its 'severity'.
     */
    void onNoteSeverityChanged(String noteId, int severity);

    /**
     * The Note at the specified 'position' changed its 'comment'.
     */
    void onNoteCommentChanged(String noteId, String comment);

    /**
     * The Note at the specified 'position' changed its 'x' and 'y' coordinates.
     */
    void onNotePositionChanged(String noteId, int x, int y);

}
