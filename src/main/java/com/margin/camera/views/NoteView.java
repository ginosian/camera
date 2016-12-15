package com.margin.camera.views;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.margin.camera.R;
import com.margin.camera.models.Note;

/**
 * Created on Mar 15, 2016.
 *
 * @author Marta.Ginosyan
 */
public class NoteView extends RelativeLayout {

    private String mNoteId;

    public NoteView(Context context) {
        super(context);
    }

    public NoteView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoteView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public static View createNoteView(Context context, Note note) {
        View noteView = LayoutInflater.from(context).inflate(R.layout.note_list_item, null);
        noteView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        TextView title = (TextView) noteView.findViewById(R.id.note_title);
        title.setText(note.type());
        setBackgroundColor(context, noteView, note, note.severity());
        return noteView;
    }

    /**
     * Sets background color of the {@link NoteView}
     */
    private static void setBackgroundColor(Context context, View noteView, Note note, int severity) {
        View background = noteView.findViewById(R.id.note_background);
        if (severity >= 0 && severity < 34) {
            setBackgroundColorOrDrawable(context, note, background, R.color.green500,
                    R.drawable.note_green);
        } else if (severity >= 34 && severity < 67) {
            setBackgroundColorOrDrawable(context, note, background, R.color.orange500,
                    R.drawable.note_orange);
        } else if (severity >= 67) {
            setBackgroundColorOrDrawable(context, note, background, R.color.red900,
                    R.drawable.note_red);
        }
    }

    /**
     * Set background color or background drawable regardless note coordinates
     */
    private static void setBackgroundColorOrDrawable(Context context, Note note, View background,
                                                     int colorId, int drawableId) {
        if (note.hasEmptyCoordinates()) {
            background.setBackgroundColor(ContextCompat.getColor(context, colorId));
        } else {
            background.setBackgroundResource(drawableId);
        }
    }

    public String getNoteId() {
        return mNoteId;
    }

    public void setNoteId(String noteId) {
        mNoteId = noteId;
    }
}
