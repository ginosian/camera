package com.margin.camera.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.margin.camera.R;
import com.margin.camera.adapters.AnnotationPhotoPagerAdapter;
import com.margin.camera.adapters.PhotoPagerAdapter;
import com.margin.camera.contracts.AnnotationPhotoGalleryContract;
import com.margin.camera.contracts.AnnotationPhotoPagerContract;
import com.margin.camera.contracts.PhotoPagerContract;
import com.margin.camera.listeners.OnPhotoClickListener;
import com.margin.camera.models.Photo;
import com.margin.camera.presenters.AnnotationPhotoPagerPresenter;

import java.util.Collection;
import java.util.List;

/**
 * Created on Jul 12, 2016.
 *
 * @author Marta.Ginosyan
 */
public class AnnotationPhotoPagerFragment extends PhotoPagerFragment implements
        AnnotationPhotoPagerContract.View {

    private ViewPager mViewPager;
    private AnnotationPhotoPagerAdapter mPagerAdapter;

    /**
     * Create a new instance of AnnotationPhotoPagerFragment, initialized to
     * show the photos with {@param photos} list and {@param currentPosition} position
     */
    public static AnnotationPhotoPagerFragment create(Collection<Photo> photos,
                                                      int currentPosition) {
        AnnotationPhotoPagerFragment fragment = new AnnotationPhotoPagerFragment();
        fragment.setArguments(createArguments(photos, currentPosition));
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view != null) {
            mViewPager = (ViewPager) view.findViewById(R.id.pager);
        }
        return view;
    }

    @Override
    protected PhotoPagerAdapter getPhotoPagerAdapter(List<Photo> photos,
                                                     OnPhotoClickListener listener) {
        mPagerAdapter = new AnnotationPhotoPagerAdapter(photos, listener);
        return mPagerAdapter;
    }

    @Override
    protected PhotoPagerContract.Presenter getPhotoPagerPresenter(List<Photo> photos) {
        return new AnnotationPhotoPagerPresenter(this, photos);
    }

    @Override
    public void showAnnotations(boolean show) {
        if (mPagerAdapter != null && mViewPager != null) {
            mPagerAdapter.showAnnotations(mViewPager, show);
        }
    }

    @Override
    public void setVisibilityButtonVisible(boolean visible) {
        if (getActivity() instanceof AnnotationPhotoGalleryContract.View) {
            ((AnnotationPhotoGalleryContract.View) getActivity()).setVisibilityButtonVisible(visible);
        }
    }
}
