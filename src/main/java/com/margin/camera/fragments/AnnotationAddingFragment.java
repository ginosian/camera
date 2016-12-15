package com.margin.camera.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.margin.camera.R;
import com.margin.camera.adapters.AnnotationAddingAdapter;
import com.margin.camera.adapters.AnnotationAddingAdapter.IAddingNoteListener;
import com.margin.camera.listeners.OnNoteChangedListener;
import com.margin.camera.listeners.OnNoteDeletedListener;
import com.margin.camera.managers.AnnotationManager;
import com.margin.camera.models.AnnotationType;
import com.margin.camera.models.Note;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created on Mar 11, 2016.
 *
 * @author Marta.Ginosyan
 */
public class AnnotationAddingFragment extends Fragment implements IAddingNoteListener,
        OnNoteDeletedListener {

    @SuppressWarnings("unused")
    private static final String TAG = AnnotationAddingFragment.class.getSimpleName();

    private static final String NOTES = "notes";
    private static final String LIST_OF_ANNOTATION_TYPES = "list_of_annotation_types";

    private List<AnnotationType> mAnnotationTypes;
    private List<Note> mNotes;

    private OnNoteChangedListener mOnNoteChangeListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g., upon screen orientation changes).
     */
    public AnnotationAddingFragment() {
    }

    /**
     * Create a new instance of AnnotationAddingFragment, initialized to
     * show the annotation types with 'notes' list and 'annotationTypes' types
     */
    public static AnnotationAddingFragment newInstance(Collection<Note> notes, Collection<AnnotationType>
            annotationTypes) {
        Bundle arguments = new Bundle();
        if (notes != null) {
            arguments.putParcelableArrayList(NOTES, new ArrayList<>(notes));
        }
        arguments.putParcelableArrayList(LIST_OF_ANNOTATION_TYPES, new ArrayList<>(annotationTypes));
        AnnotationAddingFragment fragment = new AnnotationAddingFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    /**
     * Create a new instance of AnnotationAddingFragment, initialized to
     * show the annotation types with 'annotationTypes' types
     */
    public static AnnotationAddingFragment newInstance(Collection<AnnotationType> annotationTypes) {
        return newInstance(null, annotationTypes);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Fragment parent = getParentFragment();
        if (parent != null) {
            try {
                mOnNoteChangeListener = (OnNoteChangedListener) parent;
            } catch (ClassCastException e) {
                throw new ClassCastException(parent.toString() +
                        " must implement OnNoteChangeListener!");
            }
        } else {
            throw new NullPointerException("parent fragment can't be null!");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().containsKey(NOTES)) {
                mNotes = getArguments().getParcelableArrayList(NOTES);
            } else {
                mNotes = new ArrayList<>();
            }
            mAnnotationTypes = getArguments().getParcelableArrayList(LIST_OF_ANNOTATION_TYPES);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_annotation_adding, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecyclerView(view);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnNoteChangeListener = null;
    }

    private void initRecyclerView(View root) {
        RecyclerView.Adapter adapter = new AnnotationAddingAdapter(mNotes, mAnnotationTypes, this);
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.annotation_types_list);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public Note onTypeChecked(int position, boolean isChecked) {
        String type = mAnnotationTypes.get(position).getAnnotation();
        if (!isChecked) {
            for (Iterator<Note> it = mNotes.iterator(); it.hasNext(); ) {
                Note note = it.next();
                if (note.hasEmptyCoordinates()) {
                    if (note.type().equals(type)) {
                        AnnotationManager.getInstance().deleteNote(note, this);
                        it.remove();
                        break;
                    }
                }
            }
        } else {
            Note note = AnnotationManager.getInstance().createNote(getContext(), false);
            note.setType(type);
            addNote(note);
            return note;
        }
        return null;
    }

    private void addNote(Note note) {
        mNotes.add(note);
        mOnNoteChangeListener.onNoteCreated(note);
    }

    @Override
    public void onSeverityChanged(int position, int progress) {
        String type = mAnnotationTypes.get(position).getAnnotation();
        for (Note note : mNotes) {
            if (note.hasEmptyCoordinates()) {
                if (note.type().equals(type)) {
                    note.setSeverity(progress);
                    mOnNoteChangeListener.onNoteSeverityChanged(note.note_id(), progress);
                }
            }
        }
    }

    @Override
    public void onNoteDeleted(String id) {
        mOnNoteChangeListener.onNoteDeleted(id);
    }
}
