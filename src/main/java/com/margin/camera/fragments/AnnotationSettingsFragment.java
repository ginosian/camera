package com.margin.camera.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.margin.camera.R;
import com.margin.camera.adapters.AnnotationTypeAdapter;
import com.margin.camera.listeners.OnNoteChangedListener;
import com.margin.camera.listeners.OnNoteDeletedListener;
import com.margin.camera.managers.AnnotationManager;
import com.margin.camera.models.AnnotationType;
import com.margin.camera.models.Note;
import com.margin.camera.presenters.IAnnotationSettingsPresenter;
import com.margin.camera.utils.Constants;
import com.margin.camera.views.IAnnotationSettingsView;
import com.margin.components.utils.GATrackerUtils;
import com.margin.components.utils.ImeUtils;
import com.margin.components.utils.MenuUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created on Feb 10, 2016.
 *
 * @author Marta.Ginosyan
 */
public class AnnotationSettingsFragment extends Fragment implements IAnnotationSettingsPresenter,
        IAnnotationSettingsView, PopupMenu.OnMenuItemClickListener, OnNoteDeletedListener {

    @SuppressWarnings("unused")
    private static final String TAG = AnnotationSettingsFragment.class.getSimpleName();

    private static final String NOTE = "note";
    private static final String LIST_OF_ANNOTATION_TYPES = "list_of_annotation_types";
    private static final String LIST_OF_RESERVED_ANNOTATION_TYPES =
            "list_of_reserved_annotation_types";

    private static final int OVERFLOW_BUTTON_FADE_DURATION = 350;

    private OnNoteChangedListener mOnNoteChangeListener;

    private View mOverflowButton;

    private Note mNote;
    private List<AnnotationType> mAnnotationTypes;
    private List<AnnotationType> mReservedAnnotationTypes;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g., upon screen orientation changes).
     */
    public AnnotationSettingsFragment() {
    }

    /**
     * Create a new instance of AnnotationSettingsFragment, initialized to
     * show the annotation with predefined type, severity and comment
     */
    public static AnnotationSettingsFragment newInstance(Note note, Collection<Note> allNotes,
                                                         Collection<AnnotationType> annotationTypes) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(NOTE, note);
        arguments.putParcelableArrayList(LIST_OF_ANNOTATION_TYPES, new ArrayList<>(annotationTypes));
        if (note.hasEmptyCoordinates()) {
            ArrayList<AnnotationType> reservedTypes = new ArrayList<>(annotationTypes.size());
            for (Note otherNote : allNotes) {
                if (!otherNote.equals(note) && otherNote.hasEmptyCoordinates()) {
                    for (AnnotationType type : annotationTypes) {
                        if (type.getAnnotation().equals(otherNote.type())) {
                            reservedTypes.add(type);
                        }
                    }
                }
            }
            arguments.putParcelableArrayList(LIST_OF_RESERVED_ANNOTATION_TYPES, reservedTypes);
        }
        AnnotationSettingsFragment fragment = new AnnotationSettingsFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onNoteDeleted(String id) {
        mOnNoteChangeListener.onNoteDeleted(id);
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
            mNote = getArguments().getParcelable(NOTE);
            mAnnotationTypes = getArguments().getParcelableArrayList(LIST_OF_ANNOTATION_TYPES);
            if (getArguments().containsKey(LIST_OF_RESERVED_ANNOTATION_TYPES)) {
                mReservedAnnotationTypes = getArguments().getParcelableArrayList(
                        LIST_OF_RESERVED_ANNOTATION_TYPES);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_annotation_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecyclerView(view);
        initCommentEditText(view);
        mOverflowButton = view.findViewById(R.id.overflow_button);
        mOverflowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOverflowButtonPressed();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnNoteChangeListener = null;
    }

    @Override
    public Animation onCreateAnimation(int transit, final boolean enter, int nextAnim) {
        Animation anim;
        if (enter) {
            anim = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        } else {
            anim = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in);
        }

        anim.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                if (enter) {
                    com.margin.components.utils.AnimationUtils.fadeInAnimation(mOverflowButton,
                            OVERFLOW_BUTTON_FADE_DURATION);
                }
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
            }
        });

        if (!enter) {
            showOverflowButton(false);
        }
        return anim;
    }

    @Override
    public void showOverflowButton(boolean show) {
        mOverflowButton.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onOverflowButtonPressed() {
        MenuUtils.showDeviceMenu(mOverflowButton, this,
                R.menu.annotation_photo_capture_fragment_menu);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            AnnotationManager.getInstance().deleteNote(mNote, this);
            GATrackerUtils.trackEvent(getContext(),
                    Constants.EventCategory.ANNOTATION_CAMERA_WIDGET,
                    Constants.EventAction.ANNOTATION_MANUAL_DELETE, null, 0);
            return true;
        }
        return false;
    }

    private void initRecyclerView(View root) {
        RecyclerView.Adapter adapter = new AnnotationTypeAdapter(mAnnotationTypes,
                mReservedAnnotationTypes, mNote, mOnNoteChangeListener);
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.annotation_types_list);
        recyclerView.setAdapter(adapter);
    }

    private void initCommentEditText(View root) {
        TextView commentEditText = (TextView) root.findViewById(R.id.comment);
        commentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mOnNoteChangeListener.onNoteCommentChanged(mNote.note_id(), s.toString());
            }
        });
        commentEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (v.getId() == R.id.comment && !hasFocus) ImeUtils.hideIme(v);
                else ImeUtils.showIme(v);
            }
        });
        if (!TextUtils.isEmpty(mNote.comment())) {
            commentEditText.setText(mNote.comment());
        }
    }
}
