package com.margin.camera.misc;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.margin.components.utils.CameraUtils;

import java.lang.ref.WeakReference;

/**
 * Created on May 31, 2016.
 *
 * @author Marta.Ginosyan
 */
public class LatestImageAsyncTask extends AsyncTask<Void, Void, Bitmap> {

    private static final String[] PROJECTION = new String[]{
            MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.ImageColumns.DATA,
            MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
            MediaStore.Images.ImageColumns.DATE_TAKEN,
            MediaStore.Images.ImageColumns.MIME_TYPE
    };
    private final int mImageWidth, mImageHeight;
    private WeakReference<Context> contextWeakReference;
    private WeakReference<ImageView> mImageViewWeakReference;
    private OnImageLoaded mOnImageLoaded;
    public LatestImageAsyncTask(ImageView imageView, int imageSize, OnImageLoaded imageLoaded) {
        this.mOnImageLoaded = imageLoaded;
        contextWeakReference = new WeakReference<>(imageView.getContext());
        mImageViewWeakReference = new WeakReference<>(imageView);
        mImageWidth = mImageHeight = imageSize;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {

        if (null != mImageViewWeakReference.get() && null != contextWeakReference.get()) {
            Cursor cursor = null;
            try {
                cursor = contextWeakReference.get()
                        .getContentResolver()
                        .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, PROJECTION, null,
                                null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");

                if (null != cursor && cursor.moveToFirst()) {
                    String imageLocation = cursor.getString(1);
                    return CameraUtils.decodeBitmapFromLocation(imageLocation,
                            mImageWidth, mImageHeight);
                }
            } finally {
                if (null != cursor) cursor.close();
            }

        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (null != bitmap && null != contextWeakReference.get() && null != mOnImageLoaded) {
            mOnImageLoaded.imageLoaded(bitmap);
        }
    }

    public interface OnImageLoaded {
        void imageLoaded(Bitmap bitmap);
    }

}
