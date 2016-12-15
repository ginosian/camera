package com.margin.camera.models;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * Created on Feb 04, 2016.
 *
 * @author Marta.Ginosyan
 */
public class GeoLocation extends DatabaseObject implements Parcelable, GeoLocationModel {

    public static final Mapper<GeoLocation> MAPPER = new Mapper<>(new Mapper.Creator<GeoLocation>() {
        @Override
        public GeoLocation create(long _id, double longitude, double latitude, String photo_id) {
            GeoLocation location = new GeoLocation(longitude, latitude, photo_id);
            location.setId(_id);
            return location;
        }
    });
    // parcel keys
    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";
    /**
     * Creator required for class implementing the parcelable interface.
     */
    public static final Parcelable.Creator<GeoLocation> CREATOR = new Creator<GeoLocation>() {

        @Override
        public GeoLocation createFromParcel(Parcel source) {
            // read the bundle containing key value pairs from the parcel
            Bundle bundle = source.readBundle(getClass().getClassLoader());

            // instantiate a note using values from the bundle
            GeoLocation geoLocation = new GeoLocation();
            geoLocation.setLatitude(bundle.getDouble(LATITUDE));
            geoLocation.setLongitude(bundle.getDouble(LONGITUDE));
            return geoLocation;
        }

        @Override
        public GeoLocation[] newArray(int size) {
            return new GeoLocation[size];
        }

    };
    @SerializedName("longitude")
    private double mLongitude;
    @SerializedName("latitude")
    private double mLatitude;
    private transient String mPhotoId;

    public GeoLocation() {
    }

    public GeoLocation(double longitude, double latitude, String photoId) {
        this(longitude, latitude);
        mPhotoId = photoId;
    }

    public GeoLocation(double longitude, double latitude) {
        this.mLongitude = longitude;
        this.mLatitude = latitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
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
        bundle.putDouble(LATITUDE, mLatitude);
        bundle.putDouble(LONGITUDE, mLongitude);

        // write the key value pairs to the parcel
        dest.writeBundle(bundle);
    }

    @Override
    public double longitude() {
        return mLongitude;
    }

    @Override
    public double latitude() {
        return mLatitude;
    }

    @NonNull
    @Override
    public String photo_id() {
        return mPhotoId;
    }

    public static final class Marshal extends GeoLocationMarshal<Marshal> {

        public Marshal(GeoLocationModel model) {
            super(model);
        }

        @Override
        public Marshal _id(long /*ignored*/_id) {
            return this;
        }
    }
}
