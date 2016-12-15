package com.margin.camera.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.margin.camera.R;


public class CommentFragment extends Fragment {
    private static final String ARG_COMMENT = "comment";

    private String mComment;
    private EditText mCommentEditText;
    private boolean mIsChanged;

    private OnCommentChangedListener mCommentChangedListener;

    public CommentFragment() {
        // Required empty public constructor
    }

    public static CommentFragment newInstance(String comment) {
        CommentFragment fragment = new CommentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_COMMENT, comment);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().containsKey(ARG_COMMENT)) {
                mComment = getArguments().getString(ARG_COMMENT);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comment, container, false);
        view.setOnClickListener(new LayoutClickListener());

        // Initialize the comment edit text box
        mCommentEditText = (EditText) view.findViewById(R.id.comment);
        mCommentEditText.addTextChangedListener(new CommentTextWatcher());
        updateCommentText();

        mCommentEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (v.getId() == R.id.comment && !hasFocus) {
                    if (mIsChanged) {
                        mIsChanged = false;
                        if (mCommentChangedListener != null) {
                            mCommentChangedListener.onCommentEdited();
                        }
                    }
                }
            }
        });
        mIsChanged = false;
        return view;
    }

    private void updateCommentText() {
        mCommentEditText.setText(mComment);
    }

    public void onCommentChanged(String comment) {
        if (mCommentChangedListener != null) {
            mCommentChangedListener.onCommentChanged(comment);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Fragment parent = getParentFragment();
        if (parent != null) {
            try {
                mCommentChangedListener = (OnCommentChangedListener) parent;
            } catch (ClassCastException e) {
                throw new ClassCastException(parent.toString() +
                        " must implement OnNoteChangeListener!");
            }
        } else {
            throw new NullPointerException("Parent fragment cannot be null!");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCommentChangedListener = null;
    }

    public void update(String comment) {
        mComment = comment;
        updateCommentText();
    }


    public interface OnCommentChangedListener {
        /**
         * The comment was changed.
         */
        void onCommentChanged(String comment);

        /**
         * The comment was edited. User did changes on comment and finished editing
         * (i.e. commentEditText has lost focus)
         */
        void onCommentEdited();
    }

    /**
     * Send a callback via OnCommentChangedListener if text was changed.
     */
    class CommentTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            onCommentChanged(s.toString());
            mIsChanged = true;
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    /**
     * Handle touch events that happen on the main layout.
     */
    class LayoutClickListener implements View.OnClickListener {

        /**
         * Touch event when clicking on the main layout.
         * <p/>
         * If the focus is on the {@link EditText} comment box:
         * Close the keyboard and remove the focus from the {@link EditText} comment box.
         * <p/>
         * If the focus is not on the {@link EditText} comment box (i.e., main layout):
         * Close the fragment via {@link Activity#onBackPressed()}.
         *
         * @param v The main layout
         */
        @Override
        public void onClick(View v) {
            // Get the view that is currently in focus
            View view = getActivity().getCurrentFocus();

            if (view != null && view.getId() == R.id.comment) {
                // EditText Comment box is in focus:

                // Close the keyboard
                InputMethodManager imm = (InputMethodManager) getActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                // Remove focus of the EditText Comment box.
                // The clearFocus() View method will clear the focus and set it to the first
                // available view (which is still the EditText box).
                // So, in order to get around this, we set the v (the main layout) to be
                // focusableInTouchMode(true), then clear the focus of the EditText.
                v.setFocusableInTouchMode(true);
                view.clearFocus();

                // We reset the focusableInTouchMode to make sure that the main layout cannot
                // get focus when clicking on it (i.e., this if statement will never be true).
                v.setFocusableInTouchMode(false);
            } else {
                // Main layout has focus:
                // Close the fragment via onBackPressed()
                getActivity().onBackPressed();
            }
        }
    }
}
