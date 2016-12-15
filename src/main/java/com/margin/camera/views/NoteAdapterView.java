package com.margin.camera.views;

import android.content.Context;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.margin.camera.R;
import com.margin.camera.adapters.NoteAdapter;
import com.margin.camera.listeners.OnNoteDragListener;
import com.margin.camera.managers.AnnotationManager;
import com.margin.camera.models.Note;
import com.margin.camera.utils.Constants;
import com.margin.components.utils.AnimationUtils;
import com.margin.components.utils.CoordinatesUtils;
import com.margin.components.utils.GATrackerUtils;

/**
 * Created on Feb 15, 2016.
 *
 * @author Marta.Ginosyan
 */
public class NoteAdapterView extends AdapterView<NoteAdapter> implements INoteAdapterView {

    private static final String TAG = NoteAdapterView.class.getSimpleName();

    private NoteAdapter mNoteAdapter;
    private int mSelectedPosition = -1;
    private int mNoteLeftOffset;
    private int mAnchorViewY;

    private NoteHorizontalView mNoteHorizontalView;

    private OnNoteDragListener mOnNoteDragListener;

    private View mNoteView; //currently touching noteView
    private OnTouchListener mNoteAdapterViewTouchListener = new OnTouchListener() {

        private static final int LONG_PRESS_DELAY = 500;
        private float startX, startY;
        private boolean isLongPressExecuted;
        private MotionEvent mEventActionDown;
        private final Runnable onLongPressAction = new Runnable() {

            public void run() {
                if (mEventActionDown != null) {
                    doLongPress(mEventActionDown, startX, startY);
                }
                isLongPressExecuted = true;
            }
        };
        private Handler longPressHandler = new Handler();

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (isLongPressExecuted) {
                if (mNoteView != null) {
                    mNoteView.dispatchTouchEvent(MotionEvent.obtain(event));
                }
            }
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    startX = event.getRawX();
                    startY = event.getRawY();
                    mEventActionDown = MotionEvent.obtain(event);
                    longPressHandler.postDelayed(onLongPressAction, LONG_PRESS_DELAY);
                    isLongPressExecuted = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (CoordinatesUtils.isMoved(startX, event.getRawX(), startY,
                            event.getRawY())) {
                        longPressHandler.removeCallbacks(onLongPressAction);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    longPressHandler.removeCallbacks(onLongPressAction);
                    if (!isLongPressExecuted) {
                        if (CoordinatesUtils.isClick(startX, event.getRawX(), startY,
                                event.getRawY())) {
                            //NoteView has not been dragged
                            //we can make standard click event here
                            doClickEvent((int) event.getRawX(), (int) event.getRawY());
                        }
                    }
                    isLongPressExecuted = false;
                    break;
                default:
                    return false;
            }
            return true;
        }
    };

    public NoteAdapterView(Context context) {
        super(context);
    }

    public NoteAdapterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public NoteAdapter getAdapter() {
        return mNoteAdapter;
    }

    @Override
    public void setAdapter(NoteAdapter adapter) {
        mNoteAdapter = adapter;
        removeAllViewsInLayout();
        requestLayout();
    }

    public void setOnNoteDragListener(OnNoteDragListener onNoteDragListener) {
        this.mOnNoteDragListener = onNoteDragListener;
    }

    @Override
    public View getSelectedView() {
        if (mNoteAdapter.getCount() > 0 && mSelectedPosition >= 0) {
            return getChildAt(mSelectedPosition);
        } else {
            return null;
        }
    }

    @Override
    public void setSelection(int position) {
        mSelectedPosition = position;
    }

    @Override
    public int getSelectedItemPosition() {
        return mSelectedPosition;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        // if we don't have an adapter, we don't need to do anything
        if (mNoteAdapter == null) {
            return;
        }

        if (getChildCount() == 0) {
            int position = 0;
            while (position < mNoteAdapter.getCount()) {
                addView(position);
                position++;
            }
        } else {
            positionItems();
        }
        if (mNoteHorizontalView != null) {
            mNoteHorizontalView.layout(0, mAnchorViewY - mNoteHorizontalView.getMeasuredHeight(),
                    mNoteHorizontalView.getMeasuredWidth(), mAnchorViewY);
        }
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mNoteHorizontalView != null) {
            measureChild(mNoteHorizontalView, widthMeasureSpec, heightMeasureSpec);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * Create and add {@link NoteHorizontalView} to the NoteAdapterView
     */
    private void addNoteHorizontalView() {
        mNoteHorizontalView = new NoteHorizontalView(getContext());
        mNoteHorizontalView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
        addViewInLayout(mNoteHorizontalView, -1, mNoteHorizontalView.getLayoutParams(), false);
    }

    /**
     * Create new view from adapter, add it to layout and position it in the right place, according
     * to {@link Note} object.
     */
    public void addView(int index) {
        //we should always add NoteHorizontalView at the last position
        if (mNoteHorizontalView == null) {
            addNoteHorizontalView();
            requestLayout();
        }
        Note note = mNoteAdapter.getItem(index);
        NoteView newChild = (NoteView) mNoteAdapter.getView(index, null, this);
        newChild.setNoteId(note.note_id());
        if (note.hasEmptyCoordinates()) {
            mNoteHorizontalView.addNoteView(newChild, note.type());
            newChild.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.green500));
            setBackgroundColor(newChild, note, note.severity());
            updateView(newChild);
        } else {
            newChild.setOnTouchListener(new NoteTouchListener(note));
            int childCount = getChildCount() - 1;
            addChild(newChild, childCount);
            positionItem(childCount);
            setSelection(childCount);
        }
        setNoteBackground(note.note_id(), note.severity());
    }

    /**
     * Remove view from layout with note ID and type
     */
    public void removeView(String noteId, String type) {
        View noteView = getViewById(noteId);
        if (noteView != null) {
            if (this == noteView.getParent()) {
                removeViewInLayout(noteView);
            } else {
                mNoteHorizontalView.removeNoteView(noteView, type);
            }
            setSelection(-1);
        }
    }

    /**
     * Remove all note views from NoteAdapterView
     */
    public void removeAllNoteViews() {
        removeAllViewsInLayout();
        addNoteHorizontalView();
        requestLayout();
    }

    /**
     * Update view in layout. It changes only measured size (width and height).
     */
    public void updateView(View view) {
        view.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        view.requestLayout();
    }

    /**
     * Adds a view as a child view and takes care of measuring it
     *
     * @param child The view to add
     * @param index Index to insert the view
     */
    private void addChild(View child, int index) {
        LayoutParams params = child.getLayoutParams();
        if (params == null) {
            params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        }
        addViewInLayout(child, index, params, false);

        child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
    }

    /**
     * Positions the children at the "correct" positions
     */
    private void positionItems() {

        for (int index = 0; index < getChildCount() - 1; index++) {
            positionItem(index);
        }
    }

    /**
     * Positions the child at the "correct" position
     */
    private void positionItem(int index) {

        View child = getChildAt(index);

        int width = child.getMeasuredWidth();
        int height = child.getMeasuredHeight();

        // the position (x,y) of any view is a left top corner of the view, but in our case
        // the POI is where the triangle is pointing to. So we should calculate actual
        // view position like:
        // leftViewPosition = noteX - triangleLeftOffset;
        // topViewPosition = noteY - noteViewHeight;
        if (mNoteLeftOffset == 0) {
            // offset is used in dp, so it should properly affect all screen densities.
            mNoteLeftOffset = (int) getResources().getDimension(R.dimen.note_triangle_left_offset);
        }
        // actual POI coordinates
        int[] coordinates = AnnotationManager.getInstance().getDefaultCoordinates(getContext());
        // place noteView in a correct position on layout
        child.layout(coordinates[0] - mNoteLeftOffset,
                coordinates[1] - height,
                coordinates[0] - mNoteLeftOffset + width,
                coordinates[1]);
    }

    @Override
    public void changeNoteType(String noteId, String oldType, String type) {
        View noteView = getViewById(noteId);
        if (noteView != null) {
            ((TextView) noteView.findViewById(R.id.note_title)).setText(type);
            updateView(noteView);
        }
        Note note = mNoteAdapter.getItemById(noteId);
        if (note.hasEmptyCoordinates()) {
            mNoteHorizontalView.changeType(oldType, type);
        }
    }

    /**
     * Choose and set right background color of Note View for current severity.
     */
    @Override
    public void setNoteBackground(String noteId, int severity) {
        View noteView = getViewById(noteId);
        Note note = mNoteAdapter.getItemById(noteId);
        if (noteView != null) {
            setBackgroundColor(noteView, note, severity);
            // TODO: This needs to be fixed
            // Due to the differences in the 9patch, the different bg colors change the size
            // So only use the size of the first position and don't update afterwards
            if (severity <= 1) {
                // Added this so that the first time you click on a type, the size will update
                // This is because onTypeChanged (which updates size) is called before onSeverityChanged
                updateView(noteView);
            }
        }
    }

    /**
     * Sets background color of the {@link NoteView}
     */
    private void setBackgroundColor(View noteView, Note note, int severity) {
        View background = noteView.findViewById(R.id.note_background);
        if (severity > 0 && severity < 34) {
            setBackgroundColorOrDrawable(note, background, R.color.green500,
                    R.drawable.note_green);
        } else if (severity >= 34 && severity < 67) {
            setBackgroundColorOrDrawable(note, background, R.color.orange500,
                    R.drawable.note_orange);
        } else if (severity >= 67) {
            setBackgroundColorOrDrawable(note, background, R.color.red900,
                    R.drawable.note_red);
        }
    }

    /**
     * Set background color or background drawable regardless note coordinates
     */
    private void setBackgroundColorOrDrawable(Note note, View background, int colorId,
                                              int drawableId) {
        if (note.hasEmptyCoordinates()) {
            background.setBackgroundColor(ContextCompat.getColor(getContext(), colorId));
        } else {
            background.setBackgroundResource(drawableId);
        }
    }

    /**
     * Animate NoteHorizontalView to selected position
     */
    public void animateNoteHorizontalView(int anchorViewY, int duration) {
        mAnchorViewY = anchorViewY;
        if (mNoteHorizontalView != null) {
            // AnimationUtils.moveAnimation(mNoteHorizontalView, duration,
            // mNoteHorizontalView.getLeft(), mAnchorViewY -
            // mNoteHorizontalView.getMeasuredHeight());
            // Move Animation does an unexpected bug with layout position
            // in some particular cases, so it's better to use fade animation here instead
            mNoteHorizontalView.setAlpha(0);
            mNoteHorizontalView.layout(0, mAnchorViewY - mNoteHorizontalView.getMeasuredHeight(),
                    mNoteHorizontalView.getMeasuredWidth(), mAnchorViewY);
            AnimationUtils.fadeInAnimation(mNoteHorizontalView, duration);
        }
    }

    /**
     * Get noteView by note Id
     */
    public NoteView getViewById(String noteId) {
        if (getChildCount() > 0) {
            for (int i = 0; i < getChildCount() - 1; i++) {
                NoteView noteView = (NoteView) getChildAt(i);
                if (TextUtils.equals(noteView.getNoteId(), noteId)) {
                    return noteView;
                }
            }
            if (mNoteHorizontalView != null) {
                return mNoteHorizontalView.getNoteViewByNoteId(noteId);
            }
        }
        return null;
    }

    @Override
    public void changeAlphaForNotes(float alpha, String noteId) {
        if (getChildCount() > 1) {
            for (int i = 0; i < getChildCount() - 1; i++) {
                NoteView noteView = (NoteView) getChildAt(i);
                if (!TextUtils.equals(noteView.getNoteId(), noteId)) {
                    noteView.setAlpha(alpha);
                }
            }
        }
        if (mNoteHorizontalView != null) {
            mNoteHorizontalView.changeAlphaForNotes(alpha, noteId);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //check if touch event is intersect with NoteHorizontalView first
        if (mNoteHorizontalView == null || !CoordinatesUtils.isViewContains(mNoteHorizontalView,
                (int) ev.getRawX(), (int) ev.getRawY())) {
            //we received touch event outside the NoteHorizontalView
            //let's check the NoteViews
            if (mNoteView == null) {
                mNoteView = getTouchedNoteView(ev);
                if (mNoteView == null) {
                    //neither NoteHorizontalView nor NoteViews were touched
                    //so we don't have to intercept touch events here
                    return false;
                }
            }
        } else {
            //we should pass touch event to NoteHorizontalView to let it be scrollable
            mNoteHorizontalView.dispatchTouchEvent(MotionEvent.obtain(ev));
        }
        //pass touch event in NoteAdapterView
        mNoteAdapterViewTouchListener.onTouch(this, ev);
        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
            //if we received event ACTION_DOWN we should check
            //if current touched noteView hasn't been assigned yet
            if (mNoteView == null) {
                mNoteView = getTouchedNoteView(ev);
            }
        }
        //pass touch event into the current touched noteView
        if (mNoteView != null) {
            mNoteView.dispatchTouchEvent(MotionEvent.obtain(ev));
        }
        return true;
    }

    /**
     * Return touched noteView if it was intersect by touchEvent, null otherwise
     */
    private View getTouchedNoteView(MotionEvent ev) {
        if (getChildCount() > 1) {
            for (int i = 0; i < getChildCount() - 1; i++) {
                View child = getChildAt(i);
                if (CoordinatesUtils.isViewContains(child,
                        (int) ev.getRawX(), (int) ev.getRawY())) {
                    return child;
                }
            }
        }
        return null;
    }

    /**
     * Do longPress action on NoteView
     */
    private void doLongPress(MotionEvent event, float startX, float startY) {
        NoteView noteView = mNoteHorizontalView.getNoteViewByCoordinates(
                (int) event.getRawX(), (int) event.getRawY());
        if (noteView != null) {
            Note note = mNoteAdapter.getItemById(noteView.getNoteId());
            noteView.setNoteId(null);
            mNoteHorizontalView.removeNoteView(noteView, note.type());
            requestLayout();
            AnnotationManager.getInstance().setDefaultCoordinates(getContext(), note);
            GATrackerUtils.trackEvent(getContext(),
                    Constants.EventCategory.ANNOTATION_CAMERA_WIDGET,
                    Constants.EventAction.ANNOTATION_LOCATION_ADDED, null, 0);
            addView(mNoteAdapter.getPosition(note));
            mNoteView = getViewById(note.note_id());
            if (mNoteView != null) {
                if (note.severity() < 1) {
                    mNoteView.setBackgroundResource(R.drawable.note_green);
                }
                updateView(mNoteView);
                AnimationUtils.moveAnimation(mNoteView, 0,
                        startX - (mNoteView.getMeasuredWidth() >> 1),
                        startY - (mNoteView.getMeasuredHeight() >> 1));
                mNoteView.dispatchTouchEvent(event);
            }
        }
    }

    /**
     * Do onClickEvent on NoteViews
     */
    private void doClickEvent(int x, int y) {
        if (getChildCount() > 0) {
            View noteView = mNoteHorizontalView.getNoteViewByCoordinates(x, y);
            if (noteView == null) {
                for (int i = 0; i < getChildCount() - 1; i++) {
                    View child = getChildAt(i);
                    if (CoordinatesUtils.isViewContains(child, x, y)) {
                        noteView = child;
                        break;
                    }
                }
            }
            if (noteView != null) {
                int notePosition = mNoteAdapter.getPosition(mNoteAdapter
                        .getItemById(((NoteView) noteView).getNoteId()));
                performItemClick(noteView, notePosition,
                        mNoteAdapter.getItemId(notePosition));
            }
        }
    }

    private final class NoteTouchListener implements OnTouchListener {

        private int mDraggingIndex;
        private float startX, startY;
        private float dX, dY; // offsets
        private Note note;

        public NoteTouchListener(Note note) {
            this.note = note;
        }

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    // start of touching noteView
                    startX = event.getRawX();
                    startY = event.getRawY();
                    // calculate initial offsets
                    dX = view.getX() - startX;
                    dY = view.getY() - startY;
                    // init index of dragging noteView
                    mDraggingIndex = getPositionForView(view);
                    setSelection(mDraggingIndex);
                    if (mOnNoteDragListener != null) {
                        mOnNoteDragListener.onNoteStartDragging(note.note_id());
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    // moving noteView with animation
                    AnimationUtils.moveAnimation(view, 0, event.getRawX() + dX,
                            event.getRawY() + dY);
                    if (mOnNoteDragListener != null) {
                        mOnNoteDragListener.onNoteDragging(note.note_id());
                    }
                    if (CoordinatesUtils.isViewsIntersect(view, mNoteHorizontalView)) {
                        if (!mNoteHorizontalView.isTypeReserved(note.type())) {
                            mNoteHorizontalView.setActiveState();
                        }
                    } else {
                        mNoteHorizontalView.setNormalState();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    // end of touching the noteView
                    // we can get the note from the adapter here and
                    // set x and y position according to view current position
                    // notice we should calculate actual Note position like:
                    // noteX = noteViewLeft + triangleLeftOffset;
                    // noteY = noteViewTop + noteViewHeight;
                    mNoteAdapter.onNotePositionChanged(note.note_id(), (int) (view.getX() +
                            mNoteLeftOffset), (int) (view.getY() + view.getMeasuredHeight()));
                    if (mOnNoteDragListener != null) {
                        mOnNoteDragListener.onNoteStopDragging(note.note_id());
                    }
                    int notePosition = mNoteAdapter.getPosition(note);
                    if (CoordinatesUtils.isViewsIntersect(view, mNoteHorizontalView)) {
                        //noteView were dropped into the NoteHorizontalView area
                        //so we will remove noteview from NoteAdapterView and then
                        //we will add it to NoteHorizontalView
                        if (!mNoteHorizontalView.isTypeReserved(note.type())) {
                            removeViewInLayout(view);
                            note.removeCoordinates();
                            GATrackerUtils.trackEvent(getContext(),
                                    Constants.EventCategory.ANNOTATION_CAMERA_WIDGET,
                                    Constants.EventAction.ANNOTATION_LOCATION_REMOVED, null, 0);
                            addView(mNoteAdapter.getPosition(note));
                        }
                    }
                    if (CoordinatesUtils.isClick(startX, event.getRawX(), startY,
                            event.getRawY())) {
                        if (notePosition != -1) {
                            performItemClick(view, notePosition,
                                    mNoteAdapter.getItemId(notePosition));
                        }
                    }
                    mNoteHorizontalView.setNormalState();
                    mNoteView = null;
                    break;
                default:
                    return false;
            }
            return true;
        }
    }
}
