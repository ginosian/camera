package com.margin.camera.adapters;

import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.margin.camera.R;
import com.margin.camera.listeners.OnPhotoClickListener;
import com.margin.camera.models.Note;
import com.margin.camera.models.Photo;
import com.margin.camera.views.NoteHorizontalView;
import com.margin.camera.views.NotePinView;
import com.margin.camera.views.NoteView;

import java.util.List;

/**
 * Created on Jul 13, 2016.
 *
 * @author Marta.Ginosyan
 */
public class AnnotationPhotoPagerAdapter extends PhotoPagerAdapter {

    private List<Photo> mPhotos;
    private boolean mIsShowAnnotations = true;

    public AnnotationPhotoPagerAdapter(List<Photo> photos, OnPhotoClickListener listener) {
        super(photos, listener);
        mPhotos = photos;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = (View) super.instantiateItem(container, position);
        setNoteViews(view, position);
        return view;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.annotation_photo_gallery_item;
    }

    public List<Photo> getPhotos() {
        return mPhotos;
    }

    /**
     * Shows or hides comment depending on the show value
     */
    public void showAnnotations(ViewPager pager, boolean show) {
        mIsShowAnnotations = show;
        for (int i = 0; i < getCount(); i++) {
            View pageView = pager.findViewWithTag(VIEW_TAG + i);
            if (pageView != null) setNoteViews(pageView, i);
        }
    }

    private void setNoteViews(View pageView, int position) {
        NotePinView notePinView = (NotePinView) pageView.findViewById(R.id.photo);
        NoteHorizontalView noteHorizontalView =
                (NoteHorizontalView) pageView.findViewById(R.id.note_horizontal_view);
        if (mIsShowAnnotations) {
            notePinView.showNotes(true);
            noteHorizontalView.setVisibility(View.VISIBLE);
            noteHorizontalView.setBackgroundColor(ContextCompat.getColor(notePinView.getContext(),
                    R.color.black50));
            noteHorizontalView.getBackground().setAlpha(255);
            Photo photo = mPhotos.get(position);
            if (photo != null && photo.getNotes() != null && !photo.getNotes().isEmpty()) {
                notePinView.addNotes(photo.getNotes());
                for (Note note : photo.getNotes()) {
                    if (note.hasEmptyCoordinates()) {
                        noteHorizontalView.addNoteView(
                                NoteView.createNoteView(notePinView.getContext(), note), note.type());
                    }
                }
            }
        } else {
            notePinView.showNotes(false);
            noteHorizontalView.clear();
            noteHorizontalView.setVisibility(View.GONE);
        }
    }
}
