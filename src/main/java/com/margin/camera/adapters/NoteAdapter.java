package com.margin.camera.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.margin.camera.R;
import com.margin.camera.models.Note;
import com.margin.camera.presenters.INoteAdapterPresenter;

import java.util.List;

/**
 * Created on Feb 15, 2016.
 *
 * @author Marta.Ginosyan
 */
public class NoteAdapter extends ArrayAdapter<Note> implements INoteAdapterPresenter {

    public NoteAdapter(Context context, int resource, List<Note> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Note note = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        NoteHolder noteHolder; // view lookup cache stored in tag
        if (convertView == null) {
            noteHolder = new NoteHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.note_list_item, parent, false);
            noteHolder.background = convertView.findViewById(R.id.note_background);
            noteHolder.title = (TextView) convertView.findViewById(R.id.note_title);
            convertView.setTag(noteHolder);
        } else {
            noteHolder = (NoteHolder) convertView.getTag();
        }
        // Populate the data into the template view using the data object
        noteHolder.title.setText(note.type());
        // Return the completed view to render on screen
        return convertView;
    }

    /**
     * Get note object by note ID
     */
    public Note getItemById(String noteId) {
        for (int i = 0; i < getCount(); i++) {
            Note note = getItem(i);
            if (TextUtils.equals(note.note_id(), noteId)) {
                return note;
            }
        }
        return null;
    }

    @Override
    public void onNoteTypeChanged(String noteId, String type) {
        Note note = getItemById(noteId);
        if (note != null) {
            note.setType(type);
        }
    }

    @Override
    public void onNoteSeverityChanged(String noteId, int severity) {
        Note note = getItemById(noteId);
        if (note != null) {
            note.setSeverity(severity);
        }
    }

    @Override
    public void onNoteCommentChanged(String noteId, String comment) {
        Note note = getItemById(noteId);
        if (note != null) {
            note.setComment(comment);
        }
    }

    @Override
    public void onNotePositionChanged(String noteId, int x, int y) {
        Note note = getItemById(noteId);
        if (note != null) {
            note.setX(x);
            note.setY(y);
        }
    }

    private static class NoteHolder {
        public View background;
        public TextView title;
    }
}
