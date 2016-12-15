package com.margin.camera.presenters;

import com.margin.camera.contracts.PhotoGalleryContract;

/**
 * Created on Jul 11, 2016.
 *
 * @author Marta.Ginosyan
 */
public class PhotoGalleryPresenter implements PhotoGalleryContract.Presenter {

    private PhotoGalleryContract.View mView;

    public PhotoGalleryPresenter(PhotoGalleryContract.View view) {
        mView = view;
    }

    @Override
    public void onCreate() {
        if (mView != null) {
            mView.initToolbar();
            mView.showGalleryFragment();
        }
    }

    @Override
    public void onDestroy() {
        mView = null;
    }

    @Override
    public void onClearButtonPressed() {
        if (mView != null) mView.onClosedPressed();
    }

    @Override
    public void onShareButtonPressed() {
        if (mView != null) mView.onSharePressed();
    }

    @Override
    public void onDeleteButtonPressed() {
        if (mView != null) mView.onDeletePressed();
    }
}
