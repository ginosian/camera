package com.margin.camera.views;

import android.animation.LayoutTransition;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.margin.camera.R;
import com.margin.components.utils.CoordinatesUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Created on Mar 15, 2016.
 *
 * @author Marta.Ginosyan
 */
public class NoteHorizontalView extends HorizontalScrollView implements INoteHorizontalView {

    private static final int BACKGROUND_ALPHA = 26; // Hex value, 10% opacity
    private LinearLayout mChildContainer;
    private int mChildMargin;
    private Set<String> mReservedTypes = new HashSet<>();

    public NoteHorizontalView(Context context) {
        super(context);
        init();
    }

    public NoteHorizontalView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NoteHorizontalView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setNormalState();
        mChildContainer = new LinearLayout(getContext());
        mChildContainer.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        mChildContainer.setOrientation(LinearLayout.HORIZONTAL);
        mChildContainer.setLayoutTransition(new LayoutTransition());
        addView(mChildContainer);
        mChildMargin = (int) getResources().getDimension(R.dimen.spacing_small);
    }

    @Override
    public void addNoteView(final View noteView, String type) {
        mReservedTypes.add(type);
        ViewGroup.LayoutParams params = noteView.getLayoutParams();
        LinearLayout.LayoutParams marginParams = new LinearLayout.LayoutParams(params);
        marginParams.setMargins(mChildMargin, mChildMargin, mChildMargin, mChildMargin);
        noteView.setLayoutParams(marginParams);
        mChildContainer.post(new Runnable() {
            @Override
            public void run() {
                mChildContainer.addView(noteView, 0);
                NoteHorizontalView.this.fullScroll(HorizontalScrollView.FOCUS_LEFT);
            }
        });
    }

    @Override
    public void removeNoteView(final View noteView, String type) {
        mReservedTypes.remove(type);
        mChildContainer.post(new Runnable() {
            @Override
            public void run() {
                mChildContainer.removeView(noteView);
            }
        });
    }

    @Override
    public NoteView getNoteViewByNoteId(String noteId) {
        for (int i = 0; i < mChildContainer.getChildCount(); i++) {
            NoteView noteView = (NoteView) mChildContainer.getChildAt(i);
            if (TextUtils.equals(noteView.getNoteId(), noteId)) {
                return noteView;
            }
        }
        return null;
    }

    /**
     * Change type of the noteView, i.e. change reserved types container
     */
    public void changeType(String oldType, String newType) {
        mReservedTypes.remove(oldType);
        mReservedTypes.add(newType);
    }

    /**
     * Return noteView which contains this point, otherwise return null
     */
    public NoteView getNoteViewByCoordinates(int x, int y) {
        for (int i = 0; i < mChildContainer.getChildCount(); i++) {
            NoteView noteView = (NoteView) mChildContainer.getChildAt(i);
            if (CoordinatesUtils.isViewContains(noteView, x, y)) {
                return noteView;
            }
        }
        return null;
    }

    /**
     * Change opacity for all undragging notes
     */
    public void changeAlphaForNotes(float alpha, String noteId) {
        if (mChildContainer.getChildCount() > 0) {
            for (int i = 0; i < mChildContainer.getChildCount(); i++) {
                NoteView noteView = (NoteView) mChildContainer.getChildAt(i);
                if (!TextUtils.equals(noteView.getNoteId(), noteId)) {
                    noteView.setAlpha(alpha);
                }
            }
        }
    }

    /**
     * Check if noteView with such type is already added
     *
     * @return true if noteView with the same type is already added, false otherwise
     */
    public boolean isTypeReserved(String type) {
        return type == null || mReservedTypes.contains(type);
    }

    /**
     * Set NoteHorizontalView ready to add noteView,
     * i.e change background color and etc.
     */
    public void setActiveState() {
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.blue500));
        getBackground().setAlpha(BACKGROUND_ALPHA);
    }

    /**
     * Disable active state and revert all those changes
     */
    public void setNormalState() {
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.black));
        getBackground().setAlpha(BACKGROUND_ALPHA);
    }

    /**
     * Removes all note views from child container
     */
    public void clear() {
        mChildContainer.removeAllViewsInLayout();
    }
}
