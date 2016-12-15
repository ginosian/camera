package com.margin.camera.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.margin.camera.R;
import com.margin.camera.adapters.NoteAdapter;
import com.margin.camera.listeners.OnNoteChangedListener;
import com.margin.camera.listeners.OnNoteDeletedListener;
import com.margin.camera.listeners.OnNoteDragListener;
import com.margin.camera.managers.AnnotationManager;
import com.margin.camera.models.AnnotationType;
import com.margin.camera.models.Note;
import com.margin.camera.models.Property;
import com.margin.camera.presenters.IAnnotationPresenter;
import com.margin.camera.utils.Constants;
import com.margin.camera.utils.DialogUtils;
import com.margin.camera.views.IAnnotationView;
import com.margin.camera.views.NoteAdapterView;
import com.margin.components.fragments.ListFragment;
import com.margin.components.utils.AnimationUtils;
import com.margin.components.utils.CoordinatesUtils;
import com.margin.components.utils.GATrackerUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * Created on Feb 18, 2016.
 *
 * @author Marta.Ginosyan
 */
public class AnnotationPhotoCaptureFragment extends PhotoCaptureFragment implements
        IAnnotationView, IAnnotationPresenter, OnNoteChangedListener, OnNoteDragListener,
        CommentFragment.OnCommentChangedListener, OnNoteDeletedListener {

    public static final String TAG = AnnotationPhotoCaptureFragment.class.getSimpleName();

    public static final String LIST_OF_ANNOTATION_TYPES = "list_of_annotation_types";

    private static final String FRAGMENT_SETTINGS_TAG = AnnotationSettingsFragment.class.getSimpleName();
    private static final String FRAGMENT_ADDING_TAG = AnnotationAddingFragment.class.getSimpleName();

    private static final int FADE_DURATION = 300;
    private static final int FADE_ANNOTATION_ADDING_BAR_DURATION = 750;
    private static final int RESIZE_DURATION = 250;
    private static final float NORMAL_SIZE = 1.0f;
    private static final float INCREASED_SIZE = 1.35f;

    private static final int PROPERTIES_PAGE = 0;
    private static final int NOTES_PAGE = 1;

    private int mAddingFragmentContentHeight;
    private int mFragmentSlideAnimationDuration;

    private ImageView mVisibilityButton;
    private View mAddButton;
    private View mDeleteButton;
    private View mCommentButton;
    private NoteAdapterView mNoteAdapterView;
    private NoteAdapter mNoteAdapter;
    private ViewPager mFragmentViewPager;
    private ViewGroup mPagerContainer;

    private boolean mIsNoteViewVisible = true;
    private boolean mIsDeleteButtonAnimating = false;

    private ArrayList<AnnotationType> mAnnotationTypes;

    private int mCommentEditingCounter;

    /**
     * Create a new instance of AnnotationPhotoCaptureFragment, the same as
     * PhotoCaptureFragment.
     * <p>
     * See {@link PhotoCaptureFragment#newInstance(int, String, Collection)}
     */
    public static AnnotationPhotoCaptureFragment newInstance(int entityId, String imageDirPath,
                                                             Collection<Property> properties,
                                                             Collection<AnnotationType> annotationTypes) {
        AnnotationPhotoCaptureFragment fragment = new AnnotationPhotoCaptureFragment();
        Bundle arguments = createArguments(entityId, imageDirPath, properties);
        arguments.putParcelableArrayList(LIST_OF_ANNOTATION_TYPES, new ArrayList<>(annotationTypes));
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAnnotationTypes = getArguments().getParcelableArrayList(LIST_OF_ANNOTATION_TYPES);
        }
        mAddingFragmentContentHeight = (int) getResources().getDimension(
                R.dimen.annotation_adding_content_height);
        mFragmentSlideAnimationDuration = getResources().getInteger(R.integer
                .animation_slide_duration);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_annotation_photo_capturing, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAddButton = view.findViewById(R.id.add_button);
        mAddButton.setOnTouchListener(new AddButtonTouchListener());
        mDeleteButton = view.findViewById(R.id.delete_button);
        mNoteAdapterView = new NoteAdapterView(getContext());
        mNoteAdapterView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mCameraLayout.addView(mNoteAdapterView);
        mNoteAdapter = new NoteAdapter(getContext(), R.layout.note_list_item, mPhoto.getNotes());
        mNoteAdapterView.setAdapter(mNoteAdapter);
        mNoteAdapterView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < parent.getAdapter().getCount()) {
                    onNoteViewPressed((Note) parent.getAdapter().getItem(position));
                }
            }
        });
        mNoteAdapterView.setOnNoteDragListener(this);
        mVisibilityButton = (ImageView) view.findViewById(R.id.visibility_button);
        mVisibilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onVisibilityButtonPressed();
            }
        });
        mCommentButton = getLayoutInflater(savedInstanceState).inflate(
                R.layout.comment_button, mButtonsLayout, false);
        mButtonsLayout.addView(mCommentButton);
        mCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCommentButtonPressed();
            }
        });
        showCommentButton(false);
        mPagerContainer = (ViewGroup) view.findViewById(R.id.properties_container);
    }

    @Override
    public void showAddNoteButton(boolean show) {
        setVisibility(mAddButton, show);
    }

    @Override
    public void showVisibilityButton(boolean show) {
        setVisibility(mVisibilityButton, show);
    }

    @Override
    public void showCommentButton(boolean show) {
        setVisibility(mCommentButton, show);
    }

    @Override
    public void showDeleteButton(boolean show) {
        setVisibility(mDeleteButton, show);
    }

    @Override
    public void showNoteSettings(Note note) {
        showFragment(AnnotationSettingsFragment.newInstance(note, mPhoto.getNotes(),
                mAnnotationTypes), FRAGMENT_SETTINGS_TAG, false);
        mNoteAdapterView.changeAlphaForNotes(TRANSPARENT_40, note.note_id());
        if (note.hasEmptyCoordinates()) {
            mNoteAdapterView.animateNoteHorizontalView(mNoteAdapterView.getMeasuredHeight() -
                    mAddingFragmentContentHeight, FADE_ANNOTATION_ADDING_BAR_DURATION);
        }
    }

    @Override
    public void showAddingFragment() {
        showFragment(AnnotationAddingFragment.newInstance(
                mPhoto.getNotes(), mAnnotationTypes), FRAGMENT_ADDING_TAG, true);
        mNoteAdapterView.animateNoteHorizontalView(mNoteAdapterView.getMeasuredHeight() -
                mAddingFragmentContentHeight, FADE_ANNOTATION_ADDING_BAR_DURATION);
    }

    @Override
    public boolean hideNoteSettings() {
        return hideFragmentByTag(FRAGMENT_SETTINGS_TAG);
    }

    @Override
    public boolean hideAddingFragment() {
        return hideFragmentByTag(FRAGMENT_ADDING_TAG);
    }

    /**
     * Show fragment using fragment object and tag
     */
    private void showFragment(Fragment fragment, String tag, boolean withCustomAnimation) {
        hideFragmentByTag(tag);
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        if (withCustomAnimation) {
            ft.setCustomAnimations(R.anim.slide_up, R.anim.slide_in, R.anim.slide_up,
                    R.anim.slide_in);
        }
        ft.replace(R.id.annotation_fragment_container, fragment, tag);
        ft.addToBackStack(tag);
        ft.commit();
        showAddNoteButton(false);
        showVisibilityButton(false);
        setCloseButtonImage(R.drawable.back_button_icon);
    }

    /**
     * Hide fragment using it's previously added tag
     *
     * @return true if fragment was hidden, false otherwise
     */
    private boolean hideFragmentByTag(String tag) {
        if (isFragmentVisible(tag)) {
            getChildFragmentManager().popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            showAddNoteButton(true);
            showVisibilityButton(mNoteAdapter.getCount() > 0);
            setCloseButtonImage(R.drawable.close_button_icon);
            mNoteAdapterView.changeAlphaForNotes(FULL_ALPHA, null);
            mNoteAdapterView.animateNoteHorizontalView(mButtonsLayout.getTop(),
                    FADE_ANNOTATION_ADDING_BAR_DURATION);
            return true;
        }
        return false;
    }

    @Override
    public void setAlpha(View view, @FloatRange(from = 0.0, to = 1.0) float alpha) {
        view.setAlpha(alpha);
    }

    /**
     * @param tag fragment tag to find fragment in fragment manager
     * @return True if the Fragment are visible, False otherwise.
     */
    private boolean isFragmentVisible(String tag) {
        return getChildFragmentManager().findFragmentByTag(tag) != null;
    }

    @Override
    public void drawNote(Note note) {
        mNoteAdapter.add(note);
        mNoteAdapter.notifyDataSetChanged();
        mNoteAdapterView.addView(mNoteAdapter.getCount() - 1);
    }

    @Override
    public void removeNote(String noteId) {
        Note note = mNoteAdapter.getItemById(noteId);
        String type = note.type();
        mNoteAdapter.remove(note);
        mNoteAdapter.notifyDataSetChanged();
        mNoteAdapterView.removeView(noteId, type);
        showVisibilityButton(mNoteAdapter.getCount() > 0 &&
                !isFragmentVisible(FRAGMENT_SETTINGS_TAG) &&
                !isFragmentVisible(FRAGMENT_ADDING_TAG));
    }

    @Override
    public void onVisibilityButtonPressed() {
        if (mNoteAdapterView.getVisibility() == View.INVISIBLE) {
            mIsNoteViewVisible = true;
            AnimationUtils.fadeInAnimation(mNoteAdapterView, FADE_DURATION);
            mVisibilityButton.setImageResource(R.drawable.visibility_off_button_icon);
            GATrackerUtils.trackEvent(getContext(),
                    Constants.EventCategory.ANNOTATION_CAMERA_WIDGET,
                    Constants.EventAction.VISIBILITY_TOGGLED,
                    null, Constants.EventValue.ANNOTATION_HIDDEN);
        } else {
            mIsNoteViewVisible = false;
            AnimationUtils.fadeAnimation(mNoteAdapterView, FADE_DURATION, 0f, View.INVISIBLE);
            mVisibilityButton.setImageResource(R.drawable.visibility_on_button_icon);
            GATrackerUtils.trackEvent(getContext(),
                    Constants.EventCategory.ANNOTATION_CAMERA_WIDGET,
                    Constants.EventAction.VISIBILITY_TOGGLED,
                    null, Constants.EventValue.ANNOTATION_SHOWN);
        }
    }

    @Override
    public void onImageDeleted() {
        showAddNoteButton(false);
        showCommentButton(false);
        removeAllNotes();
        super.onImageDeleted();
        mPhoto.setComment(null);
    }

    @Override
    public void onImageSaved(String path) {
        showAddNoteButton(true);
        showCommentButton(true);
        super.onImageSaved(path);
        if (!mPhoto.getNotes().isEmpty()) {
            GATrackerUtils.trackEvent(getContext(),
                    Constants.EventCategory.ANNOTATION_CAMERA_WIDGET,
                    Constants.EventAction.ANNOTATION_SAVED, null,
                    mPhoto.getNotes().size());
        }
    }

    @Override
    public String onAddNoteButtonLongPressed() {
        Note note = AnnotationManager.getInstance().createNote(getContext(), true);
        onNoteCreated(note);
        GATrackerUtils.trackEvent(getContext(),
                Constants.EventCategory.ANNOTATION_CAMERA_WIDGET,
                Constants.EventAction.ANNOTATION_ADDING_SHORTCUT, null, 0);
        return note.note_id();
    }

    @Override
    public void onAddNoteButtonPressed() {
        if (!isListPagerIsShowing()) {
            showAddingFragment();
        }
    }

    @Override
    public void onHideViewPager() {
        setAlpha(mInfoButton, FULL_ALPHA);
        setAlpha(mCommentButton, FULL_ALPHA);
        showVisibilityButton(mNoteAdapter.getCount() > 0);
        showAddNoteButton(true);
        setAlpha(mAddButton, FULL_ALPHA);
    }

    @Override
    public void onShowViewPager(int position) {
        switch (position) {
            case PROPERTIES_PAGE:
                setAlpha(mInfoButton, FULL_ALPHA);
                setAlpha(mCommentButton, TRANSPARENT_40);
                GATrackerUtils.trackEvent(getContext(),
                        Constants.EventCategory.ANNOTATION_CAMERA_WIDGET,
                        Constants.EventAction.IMAGE_PROPERTIES, null, 0);
                break;
            case NOTES_PAGE:
                setAlpha(mInfoButton, TRANSPARENT_40);
                setAlpha(mCommentButton, FULL_ALPHA);
                break;
        }
    }

    @Override
    public void onNoteViewPressed(Note note) {
        showNoteSettings(note);
    }

    @Override
    public void onNoteTypeChanged(String id, String type) {
        Note note = mNoteAdapter.getItemById(id);
        String oldType = note.type();
        mNoteAdapter.onNoteTypeChanged(id, type);
        mNoteAdapterView.changeNoteType(id, oldType, type);
    }

    @Override
    public void onNoteSeverityChanged(String id, int severity) {
        mNoteAdapter.onNoteSeverityChanged(id, severity);
        mNoteAdapterView.setNoteBackground(id, severity);
    }

    @Override
    public void onNoteCommentChanged(String id, String comment) {
        mNoteAdapter.onNoteCommentChanged(id, comment);
    }

    @Override
    public void onNoteCreated(Note note) {
        drawNote(note);
    }

    @Override
    public void onNoteDeleted(String id) {
        removeNote(id);
        hideNoteSettings();
    }

    /**
     * Clear all notes from the Note Adapter
     */
    private void removeAllNotes() {
        mNoteAdapter.clear();
        mNoteAdapter.notifyDataSetChanged();
        mNoteAdapterView.removeAllNoteViews();
        showVisibilityButton(false);
    }

    public void confirmImageDelete() {
        DialogUtils.showImageWarningDialog(getContext(), new Runnable() {
            @Override
            public void run() {
                AnnotationPhotoCaptureFragment.super.onCloseButtonPressed();
            }
        });
    }

    @Override
    public void onCloseButtonPressed() {
        if (isListPagerIsShowing()) {
            switch (mFragmentViewPager.getCurrentItem()) {
                case PROPERTIES_PAGE:
                    onInfoButtonPressed();
                    break;
                case NOTES_PAGE:
                    onCommentButtonPressed();
                    break;
            }
        } else {
            if (!hideNoteSettings() && !hideAddingFragment()) {
                if (mNoteAdapter.getCount() > 0) {
                    confirmImageDelete();
                } else {
                    super.onCloseButtonPressed();
                }
            }
        }
    }

    @Override
    public void onDoneButtonPressed() {
        trackCommentEditedEvent();
        super.onDoneButtonPressed();
    }

    @Override
    public void onPhotoPreviewPressed() {
        if (!hideNoteSettings()) {
            hideAddingFragment();
        } else if (!hideAddingFragment()) {
            super.onPhotoPreviewPressed();
        }
    }

    @Override
    public void onCommentButtonPressed() {
        if (isListPagerIsShowing() && mFragmentViewPager.getCurrentItem() != NOTES_PAGE) {
            mFragmentViewPager.setCurrentItem(NOTES_PAGE);
            return;
        }
        super.onInfoButtonPressed();

        if (!isListPagerIsShowing()) {
            showViewPager(NOTES_PAGE);
            showVisibilityButton(false);
            showAddNoteButton(false);
            setAlpha(mAddButton, TRANSPARENT_40);
            setAlpha(mInfoButton, TRANSPARENT_40);
        } else {
            hideViewPager();
        }
    }

    @Override
    protected void showPropertiesFragment() {
        // Do nothing
    }

    @Override
    protected void trackImageDiscarding(String category) {
        super.trackImageDiscarding(Constants.EventCategory.ANNOTATION_CAMERA_WIDGET);
        trackCommentEditedEvent();
    }

    /**
     * Send number of times the comment was edited
     */
    private void trackCommentEditedEvent() {
        if (!TextUtils.isEmpty(mPhoto.comment())) {
            GATrackerUtils.trackEvent(getContext(),
                    Constants.EventCategory.ANNOTATION_CAMERA_WIDGET,
                    Constants.EventAction.COMMENT_EDITED,
                    mPhoto.comment(), mCommentEditingCounter);
            mCommentEditingCounter = 0;
        }
    }

    @Override
    public void onInfoButtonPressed() {
        if (isListPagerIsShowing() && mFragmentViewPager.getCurrentItem() != PROPERTIES_PAGE) {
            mFragmentViewPager.setCurrentItem(PROPERTIES_PAGE);
            return;
        }
        super.onInfoButtonPressed();

        if (!isListPagerIsShowing()) {
            GATrackerUtils.trackEvent(getContext(),
                    Constants.EventCategory.ANNOTATION_CAMERA_WIDGET,
                    Constants.EventAction.IMAGE_PROPERTIES, null, 0);
            showViewPager(PROPERTIES_PAGE);
            showAddNoteButton(false);
            showVisibilityButton(false);
            setAlpha(mCommentButton, TRANSPARENT_40);
        } else {
            hideViewPager();
        }
    }

    @Override
    public void showViewPager(int defaultPosition) {
        if (mFragmentViewPager == null) {
            mFragmentViewPager = new ViewPager(getContext());
            mFragmentViewPager.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                    .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mFragmentViewPager.setId(new Random().nextInt(1000));
            PagerAdapter pagerAdapter = new FragmentPagerAdapter(getChildFragmentManager());
            mFragmentViewPager.setAdapter(pagerAdapter);
            mFragmentViewPager.addOnPageChangeListener(new PageChangeListener());
        }
        mFragmentViewPager.setCurrentItem(defaultPosition);
        mFragmentViewPager.getAdapter().notifyDataSetChanged();
        mPagerContainer.addView(mFragmentViewPager);
    }

    @Override
    public void hideViewPager() {
        mPagerContainer.removeView(mFragmentViewPager);
        onHideViewPager();
    }

    /**
     * @return is listPager shown or not
     */
    private boolean isListPagerIsShowing() {
        return mFragmentViewPager != null && mFragmentViewPager.getParent() != null;
    }

    @Override
    public void onNoteStartDragging(String noteId) {
        hideNoteSettings();
        hideAddingFragment();
        setCloseButtonAlpha(TRANSPARENT_40);
        setAlpha(mInfoButton, TRANSPARENT_40);
        setAlpha(mCommentButton, TRANSPARENT_40);
        setDoneButtonAlpha(TRANSPARENT_40);
        showVisibilityButton(false);
        setAlpha(mAddButton, FULL_TRANSPARENT);
        showDeleteButton(true);
        setVisibility(mNoteAdapterView, true);
        mNoteAdapterView.changeAlphaForNotes(TRANSPARENT_40, noteId);
    }

    @Override
    public void onNoteDragging(String noteId) {
        View noteView = mNoteAdapterView.getViewById(noteId);
        if (CoordinatesUtils.isViewsIntersect(noteView, mDeleteButton)) {
            setAlpha(noteView, TRANSPARENT_40);
            increaseDeleteButtonSize();
        } else {
            setAlpha(noteView, FULL_ALPHA);
            decreaseDeleteButtonSize();
        }
    }

    @Override
    public void onNoteStopDragging(String noteId) {
        setCloseButtonAlpha(FULL_ALPHA);
        setAlpha(mInfoButton, FULL_ALPHA);
        setAlpha(mCommentButton, FULL_ALPHA);
        setDoneButtonAlpha(FULL_ALPHA);
        setAlpha(mVisibilityButton, FULL_ALPHA);
        showVisibilityButton(true);
        setAlpha(mAddButton, FULL_ALPHA);
        mNoteAdapterView.changeAlphaForNotes(FULL_ALPHA, noteId);
        if (CoordinatesUtils.isViewsIntersect(mNoteAdapterView.getViewById(noteId),
                mDeleteButton)) {
            AnnotationManager.getInstance().deleteNote(mNoteAdapter.getItemById(noteId), this);
            GATrackerUtils.trackEvent(getContext(),
                    Constants.EventCategory.ANNOTATION_CAMERA_WIDGET,
                    Constants.EventAction.ANNOTATION_DRAG_DELETE, null, 0);
        }
        if (!mIsNoteViewVisible) {
            setVisibility(mNoteAdapterView, false);
        }
        decreaseDeleteButtonSize();
        showDeleteButton(false);
    }

    @Override
    public void increaseDeleteButtonSize() {
        if (!mIsDeleteButtonAnimating) {
            mIsDeleteButtonAnimating = true;
            AnimationUtils.resizeAnimation(mDeleteButton, RESIZE_DURATION, INCREASED_SIZE);
        }
    }

    @Override
    public void decreaseDeleteButtonSize() {
        if (mIsDeleteButtonAnimating) {
            mIsDeleteButtonAnimating = false;
            AnimationUtils.resizeAnimation(mDeleteButton, RESIZE_DURATION, NORMAL_SIZE);
        }
    }

    @Override
    public void onCommentChanged(String comment) {
        mPhoto.setComment(comment);
    }

    @Override
    public void onCommentEdited() {
        mCommentEditingCounter++;
    }

    /**
     * A list pager adapter that represents ListFragments.
     */
    private class FragmentPagerAdapter extends FragmentStatePagerAdapter {

        private static final int NUM_PAGES = 2;

        public FragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case PROPERTIES_PAGE:
                    return PropertiesFragment.create(mPhoto.getProperties());
                case NOTES_PAGE:
                    return CommentFragment.newInstance(mPhoto.comment());
            }
            return null;
        }

        @Override
        public int getItemPosition(Object object) {
            if (object != null) {
                if (object instanceof CommentFragment) {
                    // Update the Comment fragment
                    CommentFragment fragment = (CommentFragment) object;
                    fragment.update(mPhoto.comment());
                } else if (object instanceof ListFragment) {
                    // Update the List fragment
                    ListFragment fragment = (ListFragment) object;
                    fragment.updateList();
                }
            }
            return super.getItemPosition(object);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    /**
     * Callback class for responding to changing state of the selected page.
     */
    private class PageChangeListener extends ViewPager.SimpleOnPageChangeListener {

        @Override
        public void onPageSelected(int position) {
            onShowViewPager(position);
        }
    }

    /**
     * Callback class for listening to touching event of the add button
     */
    private class AddButtonTouchListener implements View.OnTouchListener {

        private static final int LONG_PRESS_DELAY = 300;
        private float startX, startY;
        private boolean isLongPressExecuted;
        private MotionEvent mEventActionDown;
        private Handler longPressHandler = new Handler();
        private String mNoteId;
        private final Runnable onLongPressAction = new Runnable() {

            public void run() {
                mNoteId = onAddNoteButtonLongPressed();
                if (mEventActionDown != null) {
                    View noteView = mNoteAdapterView.getViewById(mNoteId);
                    if (noteView != null) {
                        AnimationUtils.moveAnimation(noteView, 0,
                                startX - (noteView.getMeasuredWidth() >> 1),
                                startY - (noteView.getMeasuredHeight() >> 1));
                        noteView.dispatchTouchEvent(mEventActionDown);
                    }
                }
                isLongPressExecuted = true;
            }
        };

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if (isLongPressExecuted) {
                View noteView = mNoteAdapterView.getSelectedView();
                if (noteView != null) {
                    noteView.dispatchTouchEvent(MotionEvent.obtain(event));
                }
            }
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    mEventActionDown = MotionEvent.obtain(event);
                    startX = event.getRawX();
                    startY = event.getRawY();
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
                            onAddNoteButtonPressed();
                        }
                    } else {
                        Note note = mNoteAdapter.getItemById(mNoteId);
                        if (note != null) {
                            showNoteSettings(note);
                        }
                    }
                    break;
                default:
                    return false;
            }
            return true;
        }
    }
}
