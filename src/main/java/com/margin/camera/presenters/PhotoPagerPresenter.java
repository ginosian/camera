package com.margin.camera.presenters;

import com.margin.camera.contracts.PhotoPagerContract;
import com.margin.camera.managers.PhotoPagerManager;
import com.margin.camera.models.Photo;

import java.util.List;

/**
 * Created on Jul 11, 2016.
 *
 * @author Marta.Ginosyan
 */
public class PhotoPagerPresenter implements PhotoPagerContract.Presenter {

    private PhotoPagerContract.View mView;
    private PhotoPagerContract.Model mModel;
    private boolean mIsActionBarShown = true;

    public PhotoPagerPresenter(PhotoPagerContract.View view, List<Photo> photos) {
        mView = view;
        mModel = new PhotoPagerManager(photos);
    }

    @Override
    public void onCreate() {
        if (mView != null) {
            mView.initPhotoPager();
        }
    }

    @Override
    public void onDestroy() {
        mView = null;
    }

    @Override
    public void onDeleteButtonPressed(int currentPhotoIndex) {
        if (mView != null) {
            mView.onDeletePressed(mModel.getPhoto(currentPhotoIndex), currentPhotoIndex);
            mModel.deletePhoto(currentPhotoIndex);
            mView.refreshPhotoPager();
            mView.showUndoSnackbar();
            if (mModel.getPhotoCount() == 0) {
                mView.enableShareButton(false);
                mView.enableDeleteButton(false);
            }
        }
    }

    @Override
    public void onUndoButtonPressed() {
        if (mView != null) {
            if (mModel.getPhotoCount() == 0) {
                mView.enableShareButton(true);
                mView.enableDeleteButton(true);
            }
            mModel.restoreDeletedPhoto();
            mView.hideUndoSnackbar();
            mView.refreshPhotoPager();
            mView.setCurrentPhotoPage(mModel.getDeletedPhotoIndex());
            mView.onUndoPressed(mModel.getPhoto(mModel.getDeletedPhotoIndex()),
                    mModel.getDeletedPhotoIndex());
        }
    }

    @Override
    public void onShareButtonPressed(int currentPhotoIndex) {
        if (mView != null) {
            mView.onSharePressed(mModel.getPhoto(currentPhotoIndex));
        }
    }

    @Override
    public void onPhotoClicked(Photo photo, int position) {
        if (mView != null) {
            mIsActionBarShown = !mIsActionBarShown;
            mView.showActionBar(mIsActionBarShown);
            mView.showComments(mIsActionBarShown);
            mView.showSystemUI(mIsActionBarShown);
        }
    }

    protected PhotoPagerContract.Model getModel() {
        return mModel;
    }
}
