package com.cargomatrix.camera.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.squareup.sqldelight.RowMapper;
import com.squareup.sqldelight.SqlDelightStatement;
import java.lang.Deprecated;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface NoteModel {
  String TABLE_NAME = "note";

  String _ID = "_id";

  String NOTE_ID = "note_id";

  String COMMENT = "comment";

  String TYPE = "type";

  String SEVERITY = "severity";

  String X = "x";

  String Y = "y";

  String PHOTO_ID = "photo_id";

  String CREATE_TABLE = ""
      + "CREATE TABLE note (\n"
      + "    _id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n"
      + "    note_id TEXT UNIQUE NOT NULL,\n"
      + "    comment TEXT,\n"
      + "    type TEXT,\n"
      + "    severity INTEGER NOT NULL,\n"
      + "    x REAL NOT NULL DEFAULT -1,\n"
      + "    y REAL NOT NULL DEFAULT -1,\n"
      + "    photo_id TEXT NOT NULL,\n"
      + "    FOREIGN KEY(photo_id) REFERENCES photo(photo_id) ON DELETE CASCADE ON UPDATE CASCADE\n"
      + ")";

  String CREATE_PHOTO_ID_INDEX = ""
      + "CREATE INDEX note_photo_id_index\n"
      + "ON note(photo_id)";

  String SELECT_BY_PHOTO_ID = ""
      + "SELECT *\n"
      + "FROM note\n"
      + "WHERE photo_id = ?";

  long _id();

  @NonNull
  String note_id();

  @Nullable
  String comment();

  @Nullable
  String type();

  int severity();

  float x();

  float y();

  @NonNull
  String photo_id();

  interface Creator<T extends NoteModel> {
    T create(long _id, @NonNull String note_id, @Nullable String comment, @Nullable String type, int severity, float x, float y, @NonNull String photo_id);
  }

  final class Mapper<T extends NoteModel> implements RowMapper<T> {
    private final Factory<T> noteModelFactory;

    public Mapper(Factory<T> noteModelFactory) {
      this.noteModelFactory = noteModelFactory;
    }

    @Override
    public T map(@NonNull Cursor cursor) {
      return noteModelFactory.creator.create(
          cursor.getLong(0),
          cursor.getString(1),
          cursor.isNull(2) ? null : cursor.getString(2),
          cursor.isNull(3) ? null : cursor.getString(3),
          cursor.getInt(4),
          cursor.getFloat(5),
          cursor.getFloat(6),
          cursor.getString(7)
      );
    }
  }

  final class Marshal {
    protected final ContentValues contentValues = new ContentValues();

    Marshal(@Nullable NoteModel copy) {
      if (copy != null) {
        this._id(copy._id());
        this.note_id(copy.note_id());
        this.comment(copy.comment());
        this.type(copy.type());
        this.severity(copy.severity());
        this.x(copy.x());
        this.y(copy.y());
        this.photo_id(copy.photo_id());
      }
    }

    public ContentValues asContentValues() {
      return contentValues;
    }

    public Marshal _id(long _id) {
      contentValues.put("_id", _id);
      return this;
    }

    public Marshal note_id(String note_id) {
      contentValues.put("note_id", note_id);
      return this;
    }

    public Marshal comment(String comment) {
      contentValues.put("comment", comment);
      return this;
    }

    public Marshal type(String type) {
      contentValues.put("type", type);
      return this;
    }

    public Marshal severity(int severity) {
      contentValues.put("severity", severity);
      return this;
    }

    public Marshal x(float x) {
      contentValues.put("x", x);
      return this;
    }

    public Marshal y(float y) {
      contentValues.put("y", y);
      return this;
    }

    public Marshal photo_id(String photo_id) {
      contentValues.put("photo_id", photo_id);
      return this;
    }
  }

  final class Factory<T extends NoteModel> {
    public final Creator<T> creator;

    public Factory(Creator<T> creator) {
      this.creator = creator;
    }

    /**
     * @deprecated Use compiled statements (https://github.com/square/sqldelight#compiled-statements)
     */
    @Deprecated
    public Marshal marshal() {
      return new Marshal(null);
    }

    /**
     * @deprecated Use compiled statements (https://github.com/square/sqldelight#compiled-statements)
     */
    @Deprecated
    public Marshal marshal(NoteModel copy) {
      return new Marshal(copy);
    }

    public SqlDelightStatement select_by_photo_id(@NonNull String photo_id) {
      List<String> args = new ArrayList<String>();
      int currentIndex = 1;
      StringBuilder query = new StringBuilder();
      query.append("SELECT *\n"
              + "FROM note\n"
              + "WHERE photo_id = ");
      query.append('?').append(currentIndex++);
      args.add(photo_id);
      return new SqlDelightStatement(query.toString(), args.toArray(new String[args.size()]), Collections.<String>singleton("note"));
    }

    public Mapper<T> select_by_photo_idMapper() {
      return new Mapper<T>(this);
    }
  }
}
