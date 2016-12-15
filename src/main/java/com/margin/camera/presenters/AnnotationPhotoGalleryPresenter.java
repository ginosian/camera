package com.margin.camera.presenters;

import com.margin.camera.contracts.AnnotationPhotoGalleryContract;

/**
 * Created on Jul 12, 2016.
 *
 * @author Marta.Ginosyan
 */
public class AnnotationPhotoGalleryPresenter implements AnnotationPhotoGalleryContract.Presenter {

    private AnnotationPhotoGalleryContract.View mView;
    private boolean mIsVisibilityOn;

    public AnnotationPhotoGalleryPresenter(AnnotationPhotoGalleryContract.View view) {
        mView = view;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {
        mView = null;
    }

    @Override
    public void onVisibilityButtonPressed() {
        if (mView != null) {
            mIsVisibilityOn = !mIsVisibilityOn;
            mView.setVisibilityButtonOn(mIsVisibilityOn);
            mView.showAnnotations(!mIsVisibilityOn);
        }
    }
}
