package com.margin.camera.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.margin.camera.R;
import com.margin.camera.contracts.PhotoGalleryContract;
import com.margin.camera.contracts.PhotoPagerContract;
import com.margin.camera.fragments.PhotoPagerFragment;
import com.margin.camera.models.Photo;
import com.margin.camera.presenters.PhotoGalleryPresenter;
import com.margin.camera.utils.SystemUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created on Jul 11, 2016.
 *
 * @author Marta.Ginosyan
 */
public class PhotoGalleryActivity extends AppCompatActivity implements
        PhotoGalleryContract.View {

    protected List<Photo> mPhotos;
    protected int mCurrentPosition;
    private MenuItem mShareItem;
    private MenuItem mDeleteItem;

    private PhotoGalleryContract.Presenter mPresenter;

    /**
     * Launches PhotoGalleryActivity with inputted values
     *
     * @param context         {@link Context} that starts the activity
     * @param activityClass   {@link android.app.Activity} class that overrides PhotoGalleryActivity
     * @param photos          list of {@link Photo} objects, that will be shown in gallery
     * @param currentPosition index of default opened photo
     */
    public static void launch(Context context, Class<?> activityClass, Collection<Photo> photos,
                              int currentPosition) {
        launch(context, activityClass, photos, currentPosition, null);
    }

    /**
     * Launches PhotoGalleryActivity with inputted values
     *
     * @param context         {@link Context} that starts the activity
     * @param activityClass   {@link android.app.Activity} class that overrides PhotoGalleryActivity
     * @param photos          list of {@link Photo} objects, that will be shown in gallery
     * @param currentPosition index of default opened photo
     * @param extras          additional parameters that you want to pass in intent
     */
    public static void launch(Context context, Class<?> activityClass, Collection<Photo> photos,
                              int currentPosition, Bundle extras) {
        Intent intent = new Intent(context, activityClass);
        intent.putParcelableArrayListExtra(PhotoGalleryContract.PHOTOS, new ArrayList<>(photos));
        intent.putExtra(PhotoGalleryContract.POSITION, currentPosition);
        if (extras != null) intent.putExtras(extras);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_gallery);
        mPhotos = getIntent().getParcelableArrayListExtra(PhotoGalleryContract.PHOTOS);
        mCurrentPosition = getIntent().getIntExtra(PhotoGalleryContract.POSITION, 0);
        mPresenter = new PhotoGalleryPresenter(this);
        mPresenter.onCreate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.photo_gallery_menu, menu);
        mShareItem = menu.findItem(R.id.action_share);
        mDeleteItem = menu.findItem(R.id.action_delete);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_share) {
            mPresenter.onShareButtonPressed();
        } else if (itemId == R.id.action_delete) {
            mPresenter.onDeleteButtonPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setPadding(0, SystemUtils.getStatusBarHeight(this), 0, 0);
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);
                toolbar.setNavigationIcon(R.drawable.clear_icon_white);
                actionBar.setDisplayShowTitleEnabled(false);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPresenter.onClearButtonPressed();
                    }
                });
            }
        }
    }

    @Override
    public void showGalleryFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container,
                        PhotoPagerFragment.newInstance(mPhotos, mCurrentPosition))
                .commit();
    }

    @Override
    public void enableShareButton(boolean enable) {
        if (mShareItem != null) {
            if (enable) mShareItem.setIcon(R.drawable.share_icon_white);
            else mShareItem.setIcon(R.drawable.share_icon_grey);
            mShareItem.setEnabled(enable);
        }
    }

    @Override
    public void enableDeleteButton(boolean enable) {
        if (mDeleteItem != null) {
            if (enable) mDeleteItem.setIcon(R.drawable.delete_icon_white);
            else mDeleteItem.setIcon(R.drawable.delete_icon_grey);
            mDeleteItem.setEnabled(enable);
        }
    }

    @Override
    public void onDeletePressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment != null && fragment instanceof PhotoPagerContract.View) {
            ((PhotoPagerContract.View) fragment).onDeletePressed();
        }
    }

    @Override
    public void onSharePressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment != null && fragment instanceof PhotoPagerContract.View) {
            ((PhotoPagerContract.View) fragment).onSharePressed();
        }
    }

    @Override
    public void onSharePressed(Photo photo) {
        //TODO: implement
    }

    @Override
    public void onClosedPressed() {
        finish();
    }

    @Override
    public void onDeletePressed(Photo photo, int position) {
        mPhotos.remove(position);
    }

    @Override
    public void onUndoPressed(Photo photo, int position) {
        mPhotos.add(position, photo);
    }
}
