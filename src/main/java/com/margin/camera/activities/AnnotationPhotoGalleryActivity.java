package com.margin.camera.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

import com.margin.camera.R;
import com.margin.camera.contracts.AnnotationPhotoGalleryContract;
import com.margin.camera.contracts.AnnotationPhotoPagerContract;
import com.margin.camera.fragments.AnnotationPhotoPagerFragment;
import com.margin.camera.presenters.AnnotationPhotoGalleryPresenter;

/**
 * Created on Jul 12, 2016.
 *
 * @author Marta.Ginosyan
 */
public class AnnotationPhotoGalleryActivity extends PhotoGalleryActivity implements
        AnnotationPhotoGalleryContract.View {

    private AnnotationPhotoGalleryContract.Presenter mPresenter;
    private MenuItem mVisibilityItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new AnnotationPhotoGalleryPresenter(this);
        mPresenter.onCreate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.annotation_photo_gallery_menu, menu);
        mVisibilityItem = menu.findItem(R.id.action_visibility);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_visibility) {
            mPresenter.onVisibilityButtonPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setVisibilityButtonOn(boolean isOn) {
        if (mVisibilityItem != null) {
            if (isOn) mVisibilityItem.setIcon(R.drawable.visibility_icon_white);
            else mVisibilityItem.setIcon(R.drawable.visibility_off_icon_white);
        }
    }

    @Override
    public void setVisibilityButtonVisible(boolean visible) {
        if (mVisibilityItem != null) mVisibilityItem.setVisible(visible);
    }

    @Override
    public void showAnnotations(boolean show) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment != null && fragment instanceof AnnotationPhotoPagerContract.View) {
            ((AnnotationPhotoPagerContract.View) fragment).showAnnotations(show);
        }
    }

    @Override
    public void showGalleryFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container,
                        AnnotationPhotoPagerFragment.create(mPhotos, mCurrentPosition))
                .commit();
    }
}
