package com.margin.camera.contracts;

/**
 * Created on Jul 12, 2016.
 *
 * @author Marta.Ginosyan
 */
public interface AnnotationPhotoPagerContract {

    interface View extends PhotoPagerContract.View {

        /**
         * Shows or hides annotations from the photo page
         */
        void showAnnotations(boolean show);

        /**
         * Turns visibility of visibility button on or off depending on visible param
         */
        void setVisibilityButtonVisible(boolean visible);
    }

    interface Presenter extends PhotoPagerContract.Presenter {

    }

    interface Model {

    }
}
