package com.margin.camera.views;

import android.support.annotation.FloatRange;

/**
 * Created on Feb 18, 2016.
 *
 * @author Marta.Ginosyan
 */
public interface INoteAdapterView {

    /**
     * Change the text of the Note view to the specified 'type'.
     */
    void changeNoteType(String noteId, String oldType, String type);

    /**
     * Change the color of the Note view depending on the 'severity' threshold.
     * <p/>
     * Severity threshold:
     * Green: 0 <= severity <= 33
     * Orange: 34 <= severity <= 66
     * Red: 67 <= severity <= 100
     */
    void setNoteBackground(String noteId, int severity);

    /**
     * Change opacity for all undragging notes
     *
     * @param alpha  - target alpha that will be applied on all note views,
     *               should be from 0.0 to 1.0
     * @param noteId - noteId of dragging note view, this view will be untouched
     */
    void changeAlphaForNotes(@FloatRange(from = 0.0, to = 1.0) float alpha, String noteId);
}
