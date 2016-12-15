package com.cargomatrix.camera.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.squareup.sqldelight.RowMapper;
import com.squareup.sqldelight.SqlDelightCompiledStatement;
import com.squareup.sqldelight.SqlDelightStatement;
import java.lang.Deprecated;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface PhotoModel {
  String TABLE_NAME = "photo";

  String _ID = "_id";

  String PHOTO_ID = "photo_id";

  String IMAGE_PATH = "image_path";

  String COMMENT = "comment";

  String CREATE_DATE = "create_date";

  String PHOTO_DATA = "photo_data";

  String URL = "url";

  String LOCATION_CODE = "location_code";

  String RECORD_ID = "record_id";

  String REASON = "reason";

  String USERNAME = "username";

  String REFERENCE = "reference";

  String TASK_ID = "task_id";

  String IS_SENT = "is_sent";

  String CREATE_TABLE = ""
      + "CREATE TABLE photo (\n"
      + "    _id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n"
      + "    photo_id TEXT UNIQUE NOT NULL,\n"
      + "    image_path TEXT,\n"
      + "    comment TEXT,\n"
      + "    create_date TEXT,\n"
      + "    photo_data TEXT,\n"
      + "    url TEXT,\n"
      + "    location_code TEXT,\n"
      + "    record_id TEXT,\n"
      + "    reason TEXT,\n"
      + "    username TEXT,\n"
      + "    reference TEXT NOT NULL,\n"
      + "    task_id TEXT NOT NULL,\n"
      + "    is_sent INTEGER NOT NULL DEFAULT 0\n"
      + ")";

  String CREATE_TASK_ID_INDEX = ""
      + "CREATE INDEX photo_task_id_index ON photo(task_id)";

  String CREATE_PHOTO_ID_INDEX = ""
      + "CREATE INDEX photo_photo_id_index ON photo(photo_id)";

  String CREATE_REFERENCE_INDEX = ""
      + "CREATE INDEX photo_reference_index ON photo(reference)";

  String SELECT_BY_TASK_ID = ""
      + "SELECT *\n"
      + "FROM photo\n"
      + "WHERE task_id = ?";

  String SELECT_BY_REFERENCE = ""
      + "SELECT *\n"
      + "FROM photo\n"
      + "WHERE reference = ?";

  long _id();

  @NonNull
  String photo_id();

  @Nullable
  String image_path();

  @Nullable
  String comment();

  @Nullable
  String create_date();

  @Nullable
  String photo_data();

  @Nullable
  String url();

  @Nullable
  String location_code();

  @Nullable
  String record_id();

  @Nullable
  String reason();

  @Nullable
  String username();

  @NonNull
  String reference();

  @NonNull
  String task_id();

  boolean is_sent();

  interface Creator<T extends PhotoModel> {
    T create(long _id, @NonNull String photo_id, @Nullable String image_path, @Nullable String comment, @Nullable String create_date, @Nullable String photo_data, @Nullable String url, @Nullable String location_code, @Nullable String record_id, @Nullable String reason, @Nullable String username, @NonNull String reference, @NonNull String task_id, boolean is_sent);
  }

  final class Mapper<T extends PhotoModel> implements RowMapper<T> {
    private final Factory<T> photoModelFactory;

    public Mapper(Factory<T> photoModelFactory) {
      this.photoModelFactory = photoModelFactory;
    }

    @Override
    public T map(@NonNull Cursor cursor) {
      return photoModelFactory.creator.create(
          cursor.getLong(0),
          cursor.getString(1),
          cursor.isNull(2) ? null : cursor.getString(2),
          cursor.isNull(3) ? null : cursor.getString(3),
          cursor.isNull(4) ? null : cursor.getString(4),
          cursor.isNull(5) ? null : cursor.getString(5),
          cursor.isNull(6) ? null : cursor.getString(6),
          cursor.isNull(7) ? null : cursor.getString(7),
          cursor.isNull(8) ? null : cursor.getString(8),
          cursor.isNull(9) ? null : cursor.getString(9),
          cursor.isNull(10) ? null : cursor.getString(10),
          cursor.getString(11),
          cursor.getString(12),
          cursor.getInt(13) == 1
      );
    }
  }

  final class Marshal {
    protected final ContentValues contentValues = new ContentValues();

    Marshal(@Nullable PhotoModel copy) {
      if (copy != null) {
        this._id(copy._id());
        this.photo_id(copy.photo_id());
        this.image_path(copy.image_path());
        this.comment(copy.comment());
        this.create_date(copy.create_date());
        this.photo_data(copy.photo_data());
        this.url(copy.url());
        this.location_code(copy.location_code());
        this.record_id(copy.record_id());
        this.reason(copy.reason());
        this.username(copy.username());
        this.reference(copy.reference());
        this.task_id(copy.task_id());
        this.is_sent(copy.is_sent());
      }
    }

    public ContentValues asContentValues() {
      return contentValues;
    }

    public Marshal _id(long _id) {
      contentValues.put("_id", _id);
      return this;
    }

    public Marshal photo_id(String photo_id) {
      contentValues.put("photo_id", photo_id);
      return this;
    }

    public Marshal image_path(String image_path) {
      contentValues.put("image_path", image_path);
      return this;
    }

    public Marshal comment(String comment) {
      contentValues.put("comment", comment);
      return this;
    }

    public Marshal create_date(String create_date) {
      contentValues.put("create_date", create_date);
      return this;
    }

    public Marshal photo_data(String photo_data) {
      contentValues.put("photo_data", photo_data);
      return this;
    }

    public Marshal url(String url) {
      contentValues.put("url", url);
      return this;
    }

    public Marshal location_code(String location_code) {
      contentValues.put("location_code", location_code);
      return this;
    }

    public Marshal record_id(String record_id) {
      contentValues.put("record_id", record_id);
      return this;
    }

    public Marshal reason(String reason) {
      contentValues.put("reason", reason);
      return this;
    }

    public Marshal username(String username) {
      contentValues.put("username", username);
      return this;
    }

    public Marshal reference(String reference) {
      contentValues.put("reference", reference);
      return this;
    }

    public Marshal task_id(String task_id) {
      contentValues.put("task_id", task_id);
      return this;
    }

    public Marshal is_sent(boolean is_sent) {
      contentValues.put("is_sent", is_sent ? 1 : 0);
      return this;
    }
  }

  final class Factory<T extends PhotoModel> {
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
    public Marshal marshal(PhotoModel copy) {
      return new Marshal(copy);
    }

    public SqlDelightStatement select_by_task_id(@NonNull String task_id) {
      List<String> args = new ArrayList<String>();
      int currentIndex = 1;
      StringBuilder query = new StringBuilder();
      query.append("SELECT *\n"
              + "FROM photo\n"
              + "WHERE task_id = ");
      query.append('?').append(currentIndex++);
      args.add(task_id);
      return new SqlDelightStatement(query.toString(), args.toArray(new String[args.size()]), Collections.<String>singleton("photo"));
    }

    public SqlDelightStatement select_by_reference(@NonNull String reference) {
      List<String> args = new ArrayList<String>();
      int currentIndex = 1;
      StringBuilder query = new StringBuilder();
      query.append("SELECT *\n"
              + "FROM photo\n"
              + "WHERE reference = ");
      query.append('?').append(currentIndex++);
      args.add(reference);
      return new SqlDelightStatement(query.toString(), args.toArray(new String[args.size()]), Collections.<String>singleton("photo"));
    }

    /**
     * @deprecated Use {@link Update_is_send_status}
     */
    @Deprecated
    public SqlDelightStatement update_is_send_status(boolean is_sent, @NonNull String photo_id) {
      List<String> args = new ArrayList<String>();
      int currentIndex = 1;
      StringBuilder query = new StringBuilder();
      query.append("UPDATE photo\n"
              + "SET is_sent = ");
      query.append(is_sent ? 1 : 0);
      query.append("\n"
              + "WHERE photo_id = ");
      query.append('?').append(currentIndex++);
      args.add(photo_id);
      return new SqlDelightStatement(query.toString(), args.toArray(new String[args.size()]), Collections.<String>singleton("photo"));
    }

    public Mapper<T> select_by_task_idMapper() {
      return new Mapper<T>(this);
    }

    public Mapper<T> select_by_referenceMapper() {
      return new Mapper<T>(this);
    }
  }

  final class Update_is_send_status extends SqlDelightCompiledStatement.Update {
    public Update_is_send_status(SQLiteDatabase database) {
      super("photo", database.compileStatement(""
              + "UPDATE photo\n"
              + "SET is_sent = ?\n"
              + "WHERE photo_id = ?"));
    }

    public void bind(boolean is_sent, @NonNull String photo_id) {
      program.bindLong(1, is_sent ? 1 : 0);
      program.bindString(2, photo_id);
    }
  }
}
