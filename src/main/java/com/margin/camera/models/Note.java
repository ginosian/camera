package com.margin.camera.models;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.margin.components.models.IListItem;
import com.google.gson.annotations.SerializedName;

/**
 * Created on Feb 04, 2016.
 *
 * @author Marta.Ginosyan
 */
public class Note extends DatabaseObject implements IListItem, NoteModel {

    public static final Mapper<Note> MAPPER = new Mapper<>(new Mapper.Creator<Note>() {
        @Override
        public Note create(long _id, String note_id, String comment, String type, int severity,
                           float x, float y, String photo_id) {
            Note note = new Note(note_id, comment, type, severity, x, y, photo_id);
            note.setId(_id);
            return note;
        }
    });
    // parcel keys
    private static final String NOTE_ID = "note_id";
    private static final String TYPE = "type";
    private static final String SEVERITY = "severity";
    private static final String COMMENT = "comment";
    private static final String X = "x";
    private static final String Y = "y";
    /**
     * Creator required for class implementing the parcelable interface.
     */
    public static final Parcelable.Creator<Note> CREATOR = new Creator<Note>() {

        @Override
        public Note createFromParcel(Parcel source) {
            // read the bundle containing key value pairs from the parcel
            Bundle bundle = source.readBundle(getClass().getClassLoader());

            // instantiate a note using values from the bundle
            Note note = new Note();
            note.setNoteId(bundle.getString(NOTE_ID));
            note.setType(bundle.getString(TYPE));
            note.setSeverity(bundle.getInt(SEVERITY));
            note.setComment(bundle.getString(COMMENT));
            note.setX(bundle.getFloat(X));
            note.setY(bundle.getFloat(Y));
            return note;
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }

    };
    @SerializedName("note_id")
    private String mNoteId;
    @SerializedName("type")
    private String mType;
    @SerializedName("severity")
    private int mSeverity;
    @SerializedName("comment")
    private String mComment;
    @SerializedName("x")
    private float mX = -1;
    @SerializedName("y")
    private float mY = -1;
    private transient String mPhotoId;

    public Note() {
        /*Required empty bean constructor*/
    }

    public Note(String note_id, String comment, String type, int severity,
                float x, float y, String photo_id) {
        mNoteId = note_id;
        mComment = comment;
        mType = type;
        mSeverity = severity;
        mX = x;
        mY = y;
        mPhotoId = photo_id;
    }

    public void setNoteId(String noteId) {
        mNoteId = noteId;
    }

    public void setType(String type) {
        mType = type;
    }

    public void setSeverity(int severity) {
        mSeverity = severity;
    }

    public void setComment(String comment) {
        mComment = comment;
    }

    public void setX(float x) {
        mX = x;
    }

    public void setY(float y) {
        mY = y;
    }

    public void setPhotoId(String photoId) {
        mPhotoId = photoId;
    }

    /**
     * Check if the Note has empty coordinates.
     *
     * @return True if the Note has empty corrdinates, False otherwise.
     */
    public boolean hasEmptyCoordinates() {
        return mX <= 0 && mY <= 0;
    }

    /**
     * Clear note coordinates, i.e reset it to defaults
     */
    public void removeCoordinates() {
        mX = -1;
        mY = -1;
    }

    @Override
    public boolean equals(Object aThat) {
        //check for self-comparison
        if (this == aThat) return true;
        //use instanceof instead of getClass here for two reasons
        //1. if need be, it can match any supertype, and not just one class;
        //2. it renders an explict check for "that == null" redundant, since
        //it does the check for null already - "null instanceof [type]" always
        //returns false. (See Effective Java by Joshua Bloch.)
        if (!(aThat instanceof Note)) return false;
        //Alternative to the above line :
        //if ( aThat == null || aThat.getClass() != this.getClass() ) return false;
        //cast to native object is now safe
        Note that = (Note) aThat;
        //now a proper field-by-field evaluation can be made
        return TextUtils.equals(this.note_id(), that.note_id());
    }

    @Override
    public String getTitle() {
        return type();
    }

    @Override
    public String getSubtext() {
        return comment();
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
        bundle.putString(NOTE_ID, mNoteId);
        bundle.putString(TYPE, mType);
        bundle.putInt(SEVERITY, mSeverity);
        bundle.putString(COMMENT, mComment);
        bundle.putFloat(X, mX);
        bundle.putFloat(Y, mY);

        // write the key value pairs to the parcel
        dest.writeBundle(bundle);
    }

    @NonNull
    @Override
    public String note_id() {
        return mNoteId;
    }

    @Nullable
    @Override
    public String comment() {
        return mComment;
    }

    @Nullable
    @Override
    public String type() {
        return mType;
    }

    @Override
    public int severity() {
        return mSeverity;
    }

    @Override
    public float x() {
        return mX;
    }

    @Override
    public float y() {
        return mY;
    }

    @NonNull
    @Override
    public String photo_id() {
        return mPhotoId;
    }

    public static final class Marshal extends NoteMarshal<Marshal> {

        public Marshal(NoteModel model) {
            super(model);
        }

        @Override
        public Marshal _id(long /*ignored*/_id) {
            return this;
        }
    }
}
