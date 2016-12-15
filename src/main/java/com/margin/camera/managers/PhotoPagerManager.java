package com.margin.camera.managers;

import com.margin.camera.contracts.PhotoPagerContract;
import com.margin.camera.models.Photo;

import java.util.List;

/**
 * Created on Jul 13, 2016.
 *
 * @author Marta.Ginosyan
 */
public class PhotoPagerManager implements PhotoPagerContract.Model {

    private List<Photo> mPhotos;
    private Photo mDeletedPhoto;
    private int mDeletedPhotoIndex;

    public PhotoPagerManager(List<Photo> photos) {
        mPhotos = photos;
    }

    @Override
    public void addPhoto(Photo photo, int index) {
        if (mPhotos != null && index >= 0 && index <= mPhotos.size()) {
            mPhotos.add(index, photo);
        }
    }

    @Override
    public void deletePhoto(int index) {
        if (mPhotos != null && index >= 0 && index < mPhotos.size()) {
            mDeletedPhoto = mPhotos.remove(index);
            mDeletedPhotoIndex = index;
        }
    }

    @Override
    public void restoreDeletedPhoto() {
        if (mDeletedPhoto != null) {
            addPhoto(mDeletedPhoto, mDeletedPhotoIndex);
        }
    }

    @Override
    public int getDeletedPhotoIndex() {
        return mDeletedPhotoIndex;
    }

    @Override
    public int getPhotoCount() {
        if (mPhotos != null) return mPhotos.size();
        return 0;
    }

    @Override
    public Photo getPhoto(int index) {
        if (mPhotos != null && index >= 0 && index < mPhotos.size()) {
            return mPhotos.get(index);
        }
        return null;
    }
}
