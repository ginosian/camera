package com.margin.camera.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.margin.camera.R;
import com.margin.camera.models.AnnotationType;
import com.margin.camera.models.Note;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on Mar 11, 2016.
 *
 * @author Marta.Ginosyan
 */
public class AnnotationAddingAdapter extends
        RecyclerView.Adapter<AnnotationAddingAdapter.AddingTypeHolder> {

    private List<SimpleEntry<String, Note>> mNotesMap;

    private IAddingNoteListener mNoteListener;

    public AnnotationAddingAdapter(List<Note> notes, List<AnnotationType> types, IAddingNoteListener
            noteListener) {
        mNoteListener = noteListener;
        initNotesMap(notes, types);
    }

    private void initNotesMap(List<Note> notes, List<AnnotationType> types) {
        mNotesMap = new ArrayList<>(types.size());
        for (int i = 0; i < types.size(); i++) {
            mNotesMap.add(new SimpleEntry<String, Note>(types.get(i).getAnnotation(), null));
            for (Note note : notes) {
                if (note.hasEmptyCoordinates()) {
                    if (TextUtils.equals(note.type(), types.get(i).getAnnotation())) {
                        mNotesMap.get(i).setValue(note);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public AddingTypeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.annotation_adding_list_item, parent, false);
        return new AddingTypeHolder(v);
    }

    @Override
    public void onBindViewHolder(AddingTypeHolder holder, int position) {
        SimpleEntry<String, Note> noteEntry = mNotesMap.get(position);
        holder.title.setText(noteEntry.getKey());
        Note note = noteEntry.getValue();
        if (note != null) {
            holder.title.setChecked(true);
            holder.severity.setVisibility(View.VISIBLE);
            holder.severity.setProgress(note.severity() - 1);
        } else {
            holder.title.setChecked(false);
            holder.severity.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mNotesMap.size();
    }

    /**
     * Must be implement to listen to changes in notes list
     */
    public interface IAddingNoteListener {

        /**
         * Listen to type changes
         *
         * @return Note object if it was just created, null otherwise
         */
        Note onTypeChecked(int position, boolean isChecked);

        /**
         * Listen to severity changes
         */
        void onSeverityChanged(int position, int progress);
    }

    public class AddingTypeHolder extends AnnotationTypeHolder {

        public AddingTypeHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onClick(View v) {
            int position = AddingTypeHolder.this.getAdapterPosition();
            mNotesMap.get(position).setValue(mNoteListener.onTypeChecked(position,
                    !title.isChecked()));
            severity.setProgress(0);
            notifyItemChanged(position);
        }

        @Override
        protected void onSeekBarProgressChanged(int progress) {
            mNoteListener.onSeverityChanged(AddingTypeHolder.this.getAdapterPosition(),
                    progress + 1);
        }
    }
}