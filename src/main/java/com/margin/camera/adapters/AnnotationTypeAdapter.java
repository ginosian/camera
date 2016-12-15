package com.margin.camera.adapters;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.margin.camera.R;
import com.margin.camera.listeners.OnNoteChangedListener;
import com.margin.camera.models.AnnotationType;
import com.margin.camera.models.Note;

import java.util.List;

/**
 * Created on Feb 10, 2016.
 *
 * @author Marta.Ginosyan
 */
public class AnnotationTypeAdapter extends RecyclerView.Adapter<AnnotationTypeAdapter.TypeHolder> {

    private static final int MIN_SEVERITY = 1;

    private List<AnnotationType> mTypes;
    private OnNoteChangedListener mOnNoteChangeListener;
    private int mLastCheckedPos = -1;
    private Note mNote;

    public AnnotationTypeAdapter(List<AnnotationType> types, List<AnnotationType> reservedTypes,
                                 Note note, OnNoteChangedListener onNoteChangeListener) {
        mTypes = types;
        if (reservedTypes != null) {
            for (AnnotationType type : reservedTypes) {
                mTypes.remove(type);
            }
        }
        mOnNoteChangeListener = onNoteChangeListener;
        mNote = note;
    }

    @Override
    public TypeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.annotation_type_list_item, parent, false);
        return new TypeHolder(v);
    }

    @Override
    public void onBindViewHolder(TypeHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.title.setText(mTypes.get(position).getAnnotation());
        if (mLastCheckedPos == -1) {
            if (TextUtils.equals(mNote.type(), mTypes.get(position).getAnnotation())) {
                mLastCheckedPos = position;
            }
        }
        if (position == mLastCheckedPos) {
            holder.title.setChecked(true);
            holder.severity.setVisibility(View.VISIBLE);
            holder.severity.setProgress(mNote.severity() - 1);
        } else {
            holder.title.setChecked(false);
            holder.severity.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mTypes.size();
    }

    public class TypeHolder extends AnnotationTypeHolder {

        public TypeHolder(final View itemView) {
            super(itemView);
        }

        @Override
        public void onClick(View v) {
            if (!title.isChecked()) {
                mNote.setSeverity(MIN_SEVERITY);
                int oldCheckedPosition = mLastCheckedPos;
                mLastCheckedPos = TypeHolder.this.getAdapterPosition();
                notifyItemChanged(oldCheckedPosition);
                notifyItemChanged(mLastCheckedPos);
                if (mOnNoteChangeListener != null) {
                    mOnNoteChangeListener.onNoteTypeChanged(mNote.note_id(),
                            mTypes.get(mLastCheckedPos).getAnnotation());
                }
                if (mOnNoteChangeListener != null) {
                    mOnNoteChangeListener.onNoteSeverityChanged(mNote.note_id(), MIN_SEVERITY);
                }
            }
        }

        @Override
        protected void onSeekBarProgressChanged(int progress) {
            if (mOnNoteChangeListener != null) {
                mOnNoteChangeListener.onNoteSeverityChanged(mNote.note_id(), progress + 1);
            }
        }
    }
}

