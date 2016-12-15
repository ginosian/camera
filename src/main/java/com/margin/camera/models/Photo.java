package com.margin.camera.models;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on Feb 04, 2016.
 *
 * @author Marta.Ginosyan
 */
public class Photo extends DatabaseObject implements Parcelable, PhotoModel {

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };
    public static final Mapper<Photo> MAPPER = new Mapper<>(new Mapper.Creator<Photo>() {

        @Override
        public Photo create(long _id, String photo_id, String image_path, String comment,
                            String create_date, String photo_data, String url, String location_code,
                            String record_id, String reason, String username, String reference,
                            String task_id, boolean is_sent) {
            Photo photo = new Photo(photo_id, image_path, comment, create_date, photo_data, url,
                    location_code, record_id, reason, username, reference, task_id, is_sent);
            photo.setId(_id);
            return photo;
        }
    });
    @SerializedName("path")
    private String mPhotoId;
    @SerializedName("device_path")
    private String mImagePath;
    @SerializedName("location")
    private GeoLocation mLocation = new GeoLocation();
    @SerializedName("comment")
    private String mComment;
    @SerializedName("notes")
    private List<Note> mNotes = new ArrayList<>();
    @SerializedName("properties")
    private List<Property> mProperties = new ArrayList<>();
    @SerializedName("created")
    private String mCreateDate;
    @SerializedName("Image")
    private String mPhotoData;
    @SerializedName("url")
    private String mUrl;
    @SerializedName("locationCode")
    private String mLocationCode;
    @SerializedName("user")
    private String mUsername;
    @SerializedName("reason")
    private String mReason;
    @SerializedName("recordId")
    private String mRecordId;
    private transient String mTaskId;
    private transient boolean mIsSend;
    private transient String mReference;

    public Photo() {
    }

    public Photo(String photo_id, String image_path, String comment, String create_date,
                 String photo_data, String url, String location_code, String record_id,
                 String reason, String username, String reference, String task_id,
                 boolean is_sent) {
        mPhotoId = photo_id;
        mImagePath = image_path;
        mComment = comment;
        mCreateDate = create_date;
        mPhotoData = photo_data;
        mUrl = url;
        mLocationCode = location_code;
        mRecordId = record_id;
        mReason = reason;
        mUsername = username;
        mTaskId = task_id;
        mReference = reference;
        mIsSend = is_sent;
    }

    protected Photo(Parcel in) {
        mPhotoId = in.readString();
        mImagePath = in.readString();
        mLocation = in.readParcelable(GeoLocation.class.getClassLoader());
        mComment = in.readString();
        mNotes = in.createTypedArrayList(Note.CREATOR);
        mProperties = in.createTypedArrayList(Property.CREATOR);
        mCreateDate = in.readString();
        mPhotoData = in.readString();
        mUrl = in.readString();
        mLocationCode = in.readString();
        mUsername = in.readString();
        mReason = in.readString();
        mRecordId = in.readString();
        mTaskId = in.readString();
        mReference = in.readString();
        mIsSend = in.readInt() == 1;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPhotoId);
        dest.writeString(mImagePath);
        dest.writeParcelable(mLocation, flags);
        dest.writeString(mComment);
        dest.writeTypedList(mNotes);
        dest.writeTypedList(mProperties);
        dest.writeString(mCreateDate);
        dest.writeString(mPhotoData);
        dest.writeString(mUrl);
        dest.writeString(mLocationCode);
        dest.writeString(mUsername);
        dest.writeString(mReason);
        dest.writeString(mRecordId);
        dest.writeString(mTaskId);
        dest.writeString(mReference);
        dest.writeInt(mIsSend ? 1 : 0);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void setReference(String reference) {
        mReference = reference;
    }

    public void setTaskId(String taskId) {
        mTaskId = taskId;
    }

    public void setIsSend(boolean isSend) {
        mIsSend = isSend;
    }

    public void setPhotoId(String photoId) {
        mPhotoId = photoId;
    }

    public void setImagePath(String imagePath) {
        mImagePath = imagePath;
    }

    public GeoLocation getLocation() {
        return mLocation;
    }

    public void setLocation(GeoLocation location) {
        if (location != null) {
            mLocation = location;
        }
    }

    public void setLocation(Location location) {
        if (location != null) {
            mLocation = new GeoLocation(location.getLongitude(), location.getLatitude());
        }
    }

    public void setComment(String comment) {
        mComment = comment;
    }

    public List<Note> getNotes() {
        return mNotes;
    }

    public void setNotes(List<Note> notes) {
        mNotes = notes;
    }

    public List<Property> getProperties() {
        return mProperties;
    }

    public void setProperties(List<Property> properties) {
        mProperties = properties;
    }

    public void addProperty(Property property) {
        if (mProperties != null) {
            mProperties.add(property);
        }
    }

    public void setCreateDate(String mCreateDate) {
        this.mCreateDate = mCreateDate;
    }

    public void setPhotoData(String mPhotoData) {
        this.mPhotoData = mPhotoData;
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public void setLocationCode(String mLocationCode) {
        this.mLocationCode = mLocationCode;
    }

    public void setUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Photo photo = (Photo) o;

        if (mPhotoId != photo.mPhotoId) return false;
        if (mImagePath != null ? !mImagePath.equals(photo.mImagePath) : photo.mImagePath != null)
            return false;
        if (mLocation != null ? !mLocation.equals(photo.mLocation) : photo.mLocation != null)
            return false;
        if (mComment != null ? !mComment.equals(photo.mComment) : photo.mComment != null)
            return false;
        if (mNotes != null ? !mNotes.equals(photo.mNotes) : photo.mNotes != null) return false;
        if (mProperties != null ? !mProperties.equals(photo.mProperties) : photo.mProperties != null)
            return false;
        if (mCreateDate != null ? !mCreateDate.equals(photo.mCreateDate) : photo.mCreateDate != null)
            return false;
        if (mPhotoData != null ? !mPhotoData.equals(photo.mPhotoData) : photo.mPhotoData != null)
            return false;
        if (mUrl != null ? !mUrl.equals(photo.mUrl) : photo.mUrl != null) return false;
        if (mLocationCode != null ? !mLocationCode.equals(photo.mLocationCode) : photo.mLocationCode != null)
            return false;
        return mUsername != null ? mUsername.equals(photo.mUsername) : photo.mUsername == null;

    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + (mPhotoId != null ? mPhotoId.hashCode() : 0);
        result = 31 * result + (mImagePath != null ? mImagePath.hashCode() : 0);
        result = 31 * result + (mLocation != null ? mLocation.hashCode() : 0);
        result = 31 * result + (mComment != null ? mComment.hashCode() : 0);
        result = 31 * result + (mNotes != null ? mNotes.hashCode() : 0);
        result = 31 * result + (mProperties != null ? mProperties.hashCode() : 0);
        result = 31 * result + (mCreateDate != null ? mCreateDate.hashCode() : 0);
        result = 31 * result + (mPhotoData != null ? mPhotoData.hashCode() : 0);
        result = 31 * result + (mUrl != null ? mUrl.hashCode() : 0);
        result = 31 * result + (mLocationCode != null ? mLocationCode.hashCode() : 0);
        result = 31 * result + (mUsername != null ? mUsername.hashCode() : 0);
        return result;
    }

    @NonNull
    @Override
    public String photo_id() {
        return mPhotoId;
    }

    @Nullable
    @Override
    public String image_path() {
        return mImagePath;
    }

    @Nullable
    @Override
    public String comment() {
        return mComment;
    }

    @Nullable
    @Override
    public String create_date() {
        return mCreateDate;
    }

    @Nullable
    @Override
    public String photo_data() {
        return mPhotoData;
    }

    @Nullable
    @Override
    public String url() {
        return mUrl;
    }

    @Nullable
    @Override
    public String location_code() {
        return mLocationCode;
    }

    @NonNull
    @Override
    public String record_id() {
        return mRecordId;
    }

    @Nullable
    @Override
    public String reason() {
        return mReason;
    }

    @Nullable
    @Override
    public String username() {
        return mUsername;
    }

    @NonNull
    @Override
    public String reference() {
        return mReference;
    }

    @NonNull
    @Override
    public String task_id() {
        return mTaskId;
    }

    @Override
    public boolean is_sent() {
        return mIsSend;
    }

    public static final class Marshal extends PhotoMarshal<Marshal> {

        public Marshal(PhotoModel model) {
            super(model);
        }

        @Override
        public Marshal _id(long /*ignored*/_id) {
            return this;
        }
    }
}
