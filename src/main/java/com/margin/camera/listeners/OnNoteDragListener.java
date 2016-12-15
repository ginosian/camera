package com.margin.camera.listeners;

/**
 * Created on Mar 05, 2016.
 *
 * @author Marta.Ginosyan
 */
public interface OnNoteDragListener {

    /**
     * The Note with the specified 'id' was touched to start dragging.
     */
    void onNoteStartDragging(String noteId);

    /**
     * The Note with the specified 'id' is being dragging.
     */
    void onNoteDragging(String noteId);

    /**
     * The Note with the specified 'id' was released.
     */
    void onNoteStopDragging(String noteId);
}
