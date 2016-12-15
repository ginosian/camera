package com.margin.camera.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.margin.camera.R;
import com.margin.camera.adapters.PhotoPagerAdapter;
import com.margin.camera.contracts.PhotoGalleryContract;
import com.margin.camera.contracts.PhotoPagerContract;
import com.margin.camera.listeners.OnDeletePressListener;
import com.margin.camera.listeners.OnPhotoClickListener;
import com.margin.camera.listeners.OnSharePressListener;
import com.margin.camera.listeners.OnUndoPressListener;
import com.margin.camera.models.Photo;
import com.margin.camera.presenters.PhotoPagerPresenter;
import com.margin.camera.utils.SystemUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created on Jul 11, 2016.
 *
 * @author Marta.Ginosyan
 */
public class PhotoPagerFragment extends Fragment implements PhotoPagerContract.View {

    private ViewPager mViewPager;
    private List<Photo> mPhotos;
    private int mCurrentPosition;
    private PhotoPagerContract.Presenter mPresenter;
    private PhotoPagerAdapter mPagerAdapter;
    private Snackbar mSnackbar;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g., upon screen orientation changes).
     */
    public PhotoPagerFragment() {
    }

    /**
     * Create a new instance of PhotoPagerFragment, initialized to
     * show the photos with {@param photos} list and {@param currentPosition} position
     */
    public static PhotoPagerFragment newInstance(Collection<Photo> photos, int currentPosition) {
        PhotoPagerFragment fragment = new PhotoPagerFragment();
        fragment.setArguments(createArguments(photos, currentPosition));
        return fragment;
    }

    /**
     * Create common arguments for all photo fragments. There are thee arguments:
     * 'entityId', 'imageDirPath' and 'properties'.
     */
    protected static Bundle createArguments(Collection<Photo> photos, int currentPosition) {
        Bundle arguments = new Bundle();
        arguments.putParcelableArrayList(PhotoGalleryContract.PHOTOS, new ArrayList<>(photos));
        arguments.putInt(PhotoGalleryContract.POSITION, currentPosition);
        return arguments;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPhotos = getArguments().getParcelableArrayList(PhotoGalleryContract.PHOTOS);
        mCurrentPosition = getArguments().getInt(PhotoGalleryContract.POSITION);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery_component, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        mPresenter = getPhotoPagerPresenter(mPhotos);
        mPresenter.onCreate();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.onDestroy();
    }

    @Override
    public void initPhotoPager() {
        mPagerAdapter = getPhotoPagerAdapter(mPhotos, mPresenter);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(mCurrentPosition);
    }

    @Override
    public void showActionBar(boolean show) {
        if (getActivity() instanceof AppCompatActivity) {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                if (show) actionBar.show();
                else actionBar.hide();
            }
        }
    }

    @Override
    public void showComments(boolean show) {
        if (mViewPager != null && mPagerAdapter != null) {
            if (show) mPagerAdapter.showComment(mViewPager, true);
            else mPagerAdapter.showComment(mViewPager, false);
        }
    }

    @Override
    public void showSystemUI(boolean show) {
        if (show) SystemUtils.showSystemUI(getActivity());
        else SystemUtils.hideSystemUI(getActivity());
    }

    @Override
    public void showUndoSnackbar() {
        mSnackbar = Snackbar.make(getActivity().findViewById(android.R.id.content),
                R.string.message_item_moved_to_trash, Snackbar.LENGTH_LONG);
        mSnackbar.setAction(R.string.menu_action_undo, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onUndoButtonPressed();
            }
        });
        mSnackbar.show();
    }

    @Override
    public void hideUndoSnackbar() {
        if (mSnackbar != null) mSnackbar.dismiss();
    }

    @Override
    public void refreshPhotoPager() {
        if (mPagerAdapter != null) {
            mPagerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setCurrentPhotoPage(int index) {
        if (mViewPager != null && index >= 0 && index < mViewPager.getChildCount()) {
            mViewPager.setCurrentItem(index);
        }
    }

    @Override
    public void enableShareButton(boolean enable) {
        if (getActivity() instanceof PhotoGalleryContract.View) {
            ((PhotoGalleryContract.View) getActivity()).enableShareButton(enable);
        }
    }

    @Override
    public void enableDeleteButton(boolean enable) {
        if (getActivity() instanceof PhotoGalleryContract.View) {
            ((PhotoGalleryContract.View) getActivity()).enableDeleteButton(enable);
        }
    }

    @Override
    public void onDeletePressed() {
        if (mViewPager != null) {
            mPresenter.onDeleteButtonPressed(mViewPager.getCurrentItem());
        }
    }

    @Override
    public void onSharePressed() {
        if (mViewPager != null) {
            mPresenter.onShareButtonPressed(mViewPager.getCurrentItem());
        }
    }

    @Override
    public void onSharePressed(Photo photo) {
        if (getActivity() instanceof OnSharePressListener) {
            ((OnSharePressListener) getActivity()).onSharePressed(photo);
        }
    }

    @Override
    public void onDeletePressed(Photo photo, int position) {
        if (getActivity() instanceof OnDeletePressListener) {
            ((OnDeletePressListener) getActivity()).onDeletePressed(photo, position);
        }
    }

    @Override
    public void onUndoPressed(Photo photo, int position) {
        if (getActivity() instanceof OnUndoPressListener) {
            ((OnUndoPressListener) getActivity()).onUndoPressed(photo, position);
        }
    }

    /**
     * Creates new {@link PhotoPagerAdapter} for {@link ViewPager}
     */
    protected PhotoPagerAdapter getPhotoPagerAdapter(List<Photo> photos,
                                                     OnPhotoClickListener listener) {
        return new PhotoPagerAdapter(photos, listener);
    }

    /**
     * Creates new {@link com.margin.camera.contracts.PhotoPagerContract.Presenter}
     */
    protected PhotoPagerContract.Presenter getPhotoPagerPresenter(List<Photo> photos) {
        return new PhotoPagerPresenter(this, photos);
    }
}
