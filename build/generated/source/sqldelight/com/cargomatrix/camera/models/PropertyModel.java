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

public interface PropertyModel {
  String TABLE_NAME = "property";

  String _ID = "_id";

  String TITLE = "title";

  String SUBTEXT = "subtext";

  String PHOTO_ID = "photo_id";

  String CREATE_TABLE = ""
      + "CREATE TABLE property (\n"
      + "    _id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n"
      + "    title TEXT,\n"
      + "    subtext TEXT,\n"
      + "    photo_id TEXT NOT NULL,\n"
      + "    FOREIGN KEY(photo_id) REFERENCES photo(photo_id) ON DELETE CASCADE ON UPDATE CASCADE\n"
      + ")";

  String CREATE_PHOTO_ID_INDEX = ""
      + "CREATE INDEX property_photo_id_index\n"
      + "ON property(photo_id)";

  String SELECT_BY_PHOTO_ID = ""
      + "SELECT *\n"
      + "FROM property\n"
      + "WHERE photo_id = ?";

  long _id();

  @Nullable
  String title();

  @Nullable
  String subtext();

  @NonNull
  String photo_id();

  interface Creator<T extends PropertyModel> {
    T create(long _id, @Nullable String title, @Nullable String subtext, @NonNull String photo_id);
  }

  final class Mapper<T extends PropertyModel> implements RowMapper<T> {
    private final Factory<T> propertyModelFactory;

    public Mapper(Factory<T> propertyModelFactory) {
      this.propertyModelFactory = propertyModelFactory;
    }

    @Override
    public T map(@NonNull Cursor cursor) {
      return propertyModelFactory.creator.create(
          cursor.getLong(0),
          cursor.isNull(1) ? null : cursor.getString(1),
          cursor.isNull(2) ? null : cursor.getString(2),
          cursor.getString(3)
      );
    }
  }

  final class Marshal {
    protected final ContentValues contentValues = new ContentValues();

    Marshal(@Nullable PropertyModel copy) {
      if (copy != null) {
        this._id(copy._id());
        this.title(copy.title());
        this.subtext(copy.subtext());
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

    public Marshal title(String title) {
      contentValues.put("title", title);
      return this;
    }

    public Marshal subtext(String subtext) {
      contentValues.put("subtext", subtext);
      return this;
    }

    public Marshal photo_id(String photo_id) {
      contentValues.put("photo_id", photo_id);
      return this;
    }
  }

  final class Factory<T extends PropertyModel> {
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
    public Marshal marshal(PropertyModel copy) {
      return new Marshal(copy);
    }

    public SqlDelightStatement select_by_photo_id(@NonNull String photo_id) {
      List<String> args = new ArrayList<String>();
      int currentIndex = 1;
      StringBuilder query = new StringBuilder();
      query.append("SELECT *\n"
              + "FROM property\n"
              + "WHERE photo_id = ");
      query.append('?').append(currentIndex++);
      args.add(photo_id);
      return new SqlDelightStatement(query.toString(), args.toArray(new String[args.size()]), Collections.<String>singleton("property"));
    }

    public Mapper<T> select_by_photo_idMapper() {
      return new Mapper<T>(this);
    }
  }
}
