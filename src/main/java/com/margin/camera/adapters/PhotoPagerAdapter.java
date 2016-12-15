package com.margin.camera.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.LayoutRes;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.margin.camera.R;
import com.margin.camera.listeners.OnPhotoClickListener;
import com.margin.camera.models.Photo;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.io.File;
import java.util.List;

/**
 * Created on Jul 13, 2016.
 *
 * @author Marta.Ginosyan
 */
public class PhotoPagerAdapter extends PagerAdapter {

    public static final String VIEW_TAG = "gallery_item_view_tag";

    private List<Photo> mPhotos;
    private OnPhotoClickListener mListener;
    private boolean mIsShowComment = true;

    public PhotoPagerAdapter(List<Photo> photos, OnPhotoClickListener listener) {
        mPhotos = photos;
        mListener = listener;
    }

    @Override
    public int getCount() {
        return mPhotos.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        LayoutInflater inflater = (LayoutInflater) container.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(getLayoutResourceId(), container, false);
        final Photo photo = mPhotos.get(position);
        if (photo != null) {
            setPhotoImage(itemView, photo, position);
            setComment(itemView, photo);
        }
        container.addView(itemView);
        itemView.setTag(VIEW_TAG + position);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    /**
     * Shows or hides comment depending on the show value
     */
    public void showComment(ViewPager pager, boolean show) {
        mIsShowComment = show;
        for (int i = 0; i < getCount(); i++) {
            setComment(pager.findViewWithTag(VIEW_TAG + i), mPhotos.get(i));
        }
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE; //TODO: it causes view instantiating every time
    }

    @LayoutRes
    protected int getLayoutResourceId() {
        return R.layout.photo_gallery_item;
    }

    /**
     * Sets the comment text into the photo page view
     */
    private void setComment(View photoPage, Photo photo) {
        if (photoPage != null) {
            TextView comment = (TextView) photoPage.findViewById(R.id.comment);
            if (mIsShowComment) {
                if (photo != null && !TextUtils.isEmpty(photo.comment())) {
                    comment.setVisibility(View.VISIBLE);
                    comment.setText(photo.comment());
                } else comment.setVisibility(View.GONE);
            } else comment.setVisibility(View.GONE);
        }
    }

    /**
     * Sets image resource to photo page
     */
    private void setPhotoImage(View photoPage, final Photo photo, final int position) {
        if (photoPage != null) {
            final SubsamplingScaleImageView imageView =
                    (SubsamplingScaleImageView) photoPage.findViewById(R.id.photo);
            if (!TextUtils.isEmpty(photo.url())) loadImageFromNetwork(imageView, photo.url());
            else loadImageFromFile(imageView, photo.image_path());
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) mListener.onPhotoClicked(photo, position);
                }
            });
        }
    }

    /**
     * Loads image from network
     */
    private void loadImageFromNetwork(final SubsamplingScaleImageView imageView, String url) {
        // Load the image from the specified URL
        Glide.with(imageView.getContext()).load(url).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource,
                                        GlideAnimation<? super Bitmap> glideAnimation) {
                imageView.setImage(ImageSource.bitmap(resource));
            }
        });
    }

    /**
     * Loads image from file
     */
    private void loadImageFromFile(final SubsamplingScaleImageView imageView, String imagePath) {
        @SuppressWarnings("ConstantConditions")
        File file = new File(imagePath);
        if (file.exists()) {
            // Load the image on the device if the path exists
            Glide.with(imageView.getContext()).load(file).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource,
                                            GlideAnimation<? super Bitmap> glideAnimation) {
                    imageView.setImage(ImageSource.bitmap(resource));
                }
            });
        }
    }
}
