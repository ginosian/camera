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

public interface GeoLocationModel {
  String TABLE_NAME = "geo_location";

  String _ID = "_id";

  String LONGITUDE = "longitude";

  String LATITUDE = "latitude";

  String PHOTO_ID = "photo_id";

  String CREATE_TABLE = ""
      + "CREATE TABLE geo_location (\n"
      + "    _id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n"
      + "    longitude REAL NOT NULL,\n"
      + "    latitude REAL NOT NULL,\n"
      + "    photo_id TEXT NOT NULL,\n"
      + "    FOREIGN KEY(photo_id) REFERENCES photo(photo_id) ON DELETE CASCADE ON UPDATE CASCADE\n"
      + ")";

  String CREATE_PHOTO_ID_INDEX = ""
      + "CREATE INDEX geo_location_photo_id_index\n"
      + "ON geo_location(photo_id)";

  String SELECT_BY_PHOTO_ID = ""
      + "SELECT *\n"
      + "FROM geo_location\n"
      + "WHERE photo_id = ?\n"
      + "LIMIT 1";

  long _id();

  double longitude();

  double latitude();

  @NonNull
  String photo_id();

  interface Creator<T extends GeoLocationModel> {
    T create(long _id, double longitude, double latitude, @NonNull String photo_id);
  }

  final class Mapper<T extends GeoLocationModel> implements RowMapper<T> {
    private final Factory<T> geoLocationModelFactory;

    public Mapper(Factory<T> geoLocationModelFactory) {
      this.geoLocationModelFactory = geoLocationModelFactory;
    }

    @Override
    public T map(@NonNull Cursor cursor) {
      return geoLocationModelFactory.creator.create(
          cursor.getLong(0),
          cursor.getDouble(1),
          cursor.getDouble(2),
          cursor.getString(3)
      );
    }
  }

  final class Marshal {
    protected final ContentValues contentValues = new ContentValues();

    Marshal(@Nullable GeoLocationModel copy) {
      if (copy != null) {
        this._id(copy._id());
        this.longitude(copy.longitude());
        this.latitude(copy.latitude());
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

    public Marshal longitude(double longitude) {
      contentValues.put("longitude", longitude);
      return this;
    }

    public Marshal latitude(double latitude) {
      contentValues.put("latitude", latitude);
      return this;
    }

    public Marshal photo_id(String photo_id) {
      contentValues.put("photo_id", photo_id);
      return this;
    }
  }

  final class Factory<T extends GeoLocationModel> {
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
    public Marshal marshal(GeoLocationModel copy) {
      return new Marshal(copy);
    }

    public SqlDelightStatement select_by_photo_id(@NonNull String photo_id) {
      List<String> args = new ArrayList<String>();
      int currentIndex = 1;
      StringBuilder query = new StringBuilder();
      query.append("SELECT *\n"
              + "FROM geo_location\n"
              + "WHERE photo_id = ");
      query.append('?').append(currentIndex++);
      args.add(photo_id);
      query.append("\n"
              + "LIMIT 1");
      return new SqlDelightStatement(query.toString(), args.toArray(new String[args.size()]), Collections.<String>singleton("geo_location"));
    }

    public Mapper<T> select_by_photo_idMapper() {
      return new Mapper<T>(this);
    }
  }
}
