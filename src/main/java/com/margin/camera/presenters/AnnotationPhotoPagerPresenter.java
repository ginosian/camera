package com.margin.camera.presenters;

import com.margin.camera.contracts.AnnotationPhotoPagerContract;
import com.margin.camera.models.Photo;

import java.util.List;

/**
 * Created on Jul 15, 2016.
 *
 * @author Marta.Ginosyan
 */
public class AnnotationPhotoPagerPresenter extends PhotoPagerPresenter
        implements AnnotationPhotoPagerContract.Presenter {

    private AnnotationPhotoPagerContract.View mView;

    public AnnotationPhotoPagerPresenter(AnnotationPhotoPagerContract.View view,
                                         List<Photo> photos) {
        super(view, photos);
        mView = view;
    }

    @Override
    public void onDeleteButtonPressed(int currentPhotoIndex) {
        super.onDeleteButtonPressed(currentPhotoIndex);
        if (getModel().getPhotoCount() == 0) {
            mView.setVisibilityButtonVisible(false);
        }
    }

    @Override
    public void onUndoButtonPressed() {
        if (getModel().getPhotoCount() == 0) {
            mView.setVisibilityButtonVisible(true);
        }
        super.onUndoButtonPressed();
    }
}
