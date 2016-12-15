package com.margin.camera.models;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.margin.components.models.IListItem;

/**
 * Created on Feb 29, 2016.
 *
 * @author Marta.Ginosyan
 */
public class Property extends DatabaseObject implements IListItem, com.margin.camera.models.PropertyModel {

    public static final Mapper<Property> MAPPER = new Mapper<>(new Mapper.Creator<Property>() {
        @Override
        public Property create(long _id, String title, String subtext, String photo_id) {
            Property property = new Property(title, subtext, photo_id);
            property.setId(_id);
            return property;
        }
    });
    // parcel keys
    private static final String TITLE = "title";
    private static final String SUBTEXT = "subtext";
    /**
     * Creator required for class implementing the parcelable interface.
     */
    public static final Parcelable.Creator<Property> CREATOR = new Creator<Property>() {

        @Override
        public Property createFromParcel(Parcel source) {
            // read the bundle containing key value pairs from the parcel
            Bundle bundle = source.readBundle(getClass().getClassLoader());

            // instantiate a property using values from the bundle
            return new Property(bundle.getString(TITLE), bundle.getString(SUBTEXT));
        }

        @Override
        public Property[] newArray(int size) {
            return new Property[size];
        }

    };
    @SerializedName("title")
    private String mTitle;
    @SerializedName("subtext")
    private String mSubtext;
    private transient String mPhotoId;

    public Property(String title, String subtext, String photoId) {
        this(title, subtext);
        mPhotoId = photoId;
    }

    public Property(String title, String subtext) {
        mTitle = title;
        mSubtext = subtext;
    }

    @Override
    public String getTitle() {
        return title();
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    @Override
    public String getSubtext() {
        return subtext();
    }

    public void setSubtext(String subtext) {
        mSubtext = subtext;
    }

    public void setPhotoId(String photoId) {
        mPhotoId = photoId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // create a bundle for the key value pairs
        Bundle bundle = new Bundle();

        // insert the key value pairs to the bundle
        bundle.putString(TITLE, mTitle);
        bundle.putString(SUBTEXT, mSubtext);

        // write the key value pairs to the parcel
        dest.writeBundle(bundle);
    }

    @Nullable
    @Override
    public String title() {
        return mTitle;
    }

    @Nullable
    @Override
    public String subtext() {
        return mSubtext;
    }

    @NonNull
    @Override
    public String photo_id() {
        return mPhotoId;
    }

    public static final class Marshal extends PropertyMarshal<Marshal> {

        public Marshal(Property model) {
            super(model);
        }

        @Override
        public Marshal _id(long /*ignored*/_id) {
            return this;
        }
    }
}
