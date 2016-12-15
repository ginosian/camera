package com.margin.camera.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created on Feb 04, 2016.
 *
 * @author Marta.Ginosyan
 */
public class AnnotationType implements Parcelable {

    public static final Creator<AnnotationType> CREATOR = new Creator<AnnotationType>() {
        @Override
        public AnnotationType createFromParcel(Parcel in) {
            return new AnnotationType(in);
        }

        @Override
        public AnnotationType[] newArray(int size) {
            return new AnnotationType[size];
        }
    };
    @SerializedName("annotation")
    private String mAnnotation;

    public AnnotationType() {
    }

    public AnnotationType(String annotation) {
        this.mAnnotation = annotation;
    }

    protected AnnotationType(Parcel in) {
        mAnnotation = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mAnnotation);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getAnnotation() {
        return mAnnotation;
    }

    public void setAnnotation(String mAnnotation) {
        this.mAnnotation = mAnnotation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AnnotationType that = (AnnotationType) o;

        return mAnnotation != null ? mAnnotation.equals(that.mAnnotation) : that.mAnnotation == null;

    }

    @Override
    public int hashCode() {
        return mAnnotation != null ? mAnnotation.hashCode() : 0;
    }
}
