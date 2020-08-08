/*
 *  Copyright (C) 2018 Constantine Theodoridis
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package constantine.theodoridis.app.sunshine.data

import android.annotation.TargetApi
import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.os.Build

class WeatherProvider : ContentProvider() {
  companion object {
    private val sUriMatcher = buildUriMatcher()
    internal const val WEATHER = 100
    internal const val WEATHER_WITH_LOCATION = 101
    internal const val WEATHER_WITH_LOCATION_AND_DATE = 102
    internal const val LOCATION = 300
    private val sWeatherByLocationSettingQueryBuilder: SQLiteQueryBuilder = SQLiteQueryBuilder()
    private const val sLocationSettingSelection = WeatherContract.LocationEntry.TABLE_NAME +
      "." + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? "
    private const val sLocationSettingWithStartDateSelection = WeatherContract.LocationEntry.TABLE_NAME +
      "." + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
      WeatherContract.WeatherEntry.COLUMN_DATE + " >= ? "
    private const val sLocationSettingAndDaySelection = WeatherContract.LocationEntry.TABLE_NAME +
      "." + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
      WeatherContract.WeatherEntry.COLUMN_DATE + " = ? "
    
    init {
      sWeatherByLocationSettingQueryBuilder.tables = WeatherContract.WeatherEntry.TABLE_NAME + " INNER JOIN " +
        WeatherContract.LocationEntry.TABLE_NAME +
        " ON " + WeatherContract.WeatherEntry.TABLE_NAME +
        "." + WeatherContract.WeatherEntry.COLUMN_LOC_KEY +
        " = " + WeatherContract.LocationEntry.TABLE_NAME +
        "." + WeatherContract.LocationEntry.ID
    }
    
    private fun buildUriMatcher(): UriMatcher {
      val matcher = UriMatcher(UriMatcher.NO_MATCH)
      val authority = WeatherContract.CONTENT_AUTHORITY
      matcher.addURI(authority, WeatherContract.PATH_WEATHER, WEATHER)
      matcher.addURI(authority, WeatherContract.PATH_WEATHER + "/*", WEATHER_WITH_LOCATION)
      matcher.addURI(authority, WeatherContract.PATH_WEATHER + "/*/#", WEATHER_WITH_LOCATION_AND_DATE)
      matcher.addURI(authority, WeatherContract.PATH_LOCATION, LOCATION)
      return matcher
    }
  }
  
  private var mOpenHelper: WeatherDbHelper? = null
  
  private fun getWeatherByLocationSetting(uri: Uri, projection: Array<String>?, sortOrder: String?): Cursor {
    val locationSetting = WeatherContract.WeatherEntry.getLocationSettingFromUri(uri)
    val startDate = WeatherContract.WeatherEntry.getStartDateFromUri(uri)
    val selectionArgs: Array<String>
    val selection: String
    if (startDate == 0L) {
      selection = sLocationSettingSelection
      selectionArgs = arrayOf(locationSetting)
    } else {
      selectionArgs = arrayOf(locationSetting, java.lang.Long.toString(startDate))
      selection = sLocationSettingWithStartDateSelection
    }
    return sWeatherByLocationSettingQueryBuilder.query(mOpenHelper!!.readableDatabase,
      projection,
      selection,
      selectionArgs, null, null,
      sortOrder
    )
  }
  
  private fun getWeatherByLocationSettingAndDate(
    uri: Uri, projection: Array<String>?, sortOrder: String?): Cursor {
    val locationSetting = WeatherContract.WeatherEntry.getLocationSettingFromUri(uri)
    val date = WeatherContract.WeatherEntry.getDateFromUri(uri)
    return sWeatherByLocationSettingQueryBuilder.query(mOpenHelper!!.readableDatabase,
      projection,
      sLocationSettingAndDaySelection,
      arrayOf(locationSetting, java.lang.Long.toString(date)), null, null,
      sortOrder
    )
  }
  
  override fun onCreate(): Boolean {
    mOpenHelper = WeatherDbHelper(context)
    return true
  }
  
  override fun getType(uri: Uri): String? {
    val match = sUriMatcher.match(uri)
    return when (match) {
      WEATHER_WITH_LOCATION_AND_DATE -> WeatherContract.WeatherEntry.CONTENT_ITEM_TYPE
      WEATHER_WITH_LOCATION -> WeatherContract.WeatherEntry.CONTENT_TYPE
      WEATHER -> WeatherContract.WeatherEntry.CONTENT_TYPE
      LOCATION -> WeatherContract.LocationEntry.CONTENT_TYPE
      else -> throw UnsupportedOperationException("Unknown uri: $uri")
    }
  }
  
  override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?,
                     sortOrder: String?): Cursor? {
    val retCursor: Cursor
    when (sUriMatcher.match(uri)) {
      WEATHER_WITH_LOCATION_AND_DATE -> {
        retCursor = getWeatherByLocationSettingAndDate(uri, projection, sortOrder)
      }
      WEATHER_WITH_LOCATION -> {
        retCursor = getWeatherByLocationSetting(uri, projection, sortOrder)
      }
      WEATHER -> {
        retCursor = mOpenHelper!!.readableDatabase.query(
          WeatherContract.WeatherEntry.TABLE_NAME,
          projection,
          selection,
          selectionArgs, null, null,
          sortOrder
        )
      }
      LOCATION -> {
        retCursor = mOpenHelper!!.readableDatabase.query(
          WeatherContract.LocationEntry.TABLE_NAME,
          projection,
          selection,
          selectionArgs, null, null,
          sortOrder
        )
      }
      else -> throw UnsupportedOperationException("Unknown uri: $uri")
    }
    retCursor.setNotificationUri(context!!.contentResolver, uri)
    return retCursor
  }
  
  override fun insert(uri: Uri, values: ContentValues?): Uri? {
    val db = mOpenHelper!!.writableDatabase
    val match = sUriMatcher.match(uri)
    val returnUri: Uri
    when (match) {
      WEATHER -> {
        normalizeDate(values!!)
        val id = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, values)
        if (id > 0)
          returnUri = WeatherContract.WeatherEntry.buildWeatherUri(id)
        else
          throw android.database.SQLException("Failed to insert row into $uri")
      }
      LOCATION -> {
        val id = db.insert(WeatherContract.LocationEntry.TABLE_NAME, null, values)
        if (id > 0)
          returnUri = WeatherContract.LocationEntry.buildLocationUri(id)
        else
          throw android.database.SQLException("Failed to insert row into $uri")
      }
      else -> throw UnsupportedOperationException("Unknown uri: $uri")
    }
    context!!.contentResolver.notifyChange(uri, null)
    return returnUri
  }
  
  override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
    var selection = selection
    val db = mOpenHelper!!.writableDatabase
    val match = sUriMatcher.match(uri)
    val rowsDeleted: Int
    if (null == selection) selection = "1"
    rowsDeleted = when (match) {
      WEATHER -> db.delete(
        WeatherContract.WeatherEntry.TABLE_NAME, selection, selectionArgs)
      LOCATION -> db.delete(
        WeatherContract.LocationEntry.TABLE_NAME, selection, selectionArgs)
      else -> throw UnsupportedOperationException("Unknown uri: $uri")
    }
    if (rowsDeleted != 0) {
      context!!.contentResolver.notifyChange(uri, null)
    }
    return rowsDeleted
  }
  
  private fun normalizeDate(values: ContentValues) {
    if (values.containsKey(WeatherContract.WeatherEntry.COLUMN_DATE)) {
      val dateValue = values.getAsLong(WeatherContract.WeatherEntry.COLUMN_DATE)!!
      values.put(WeatherContract.WeatherEntry.COLUMN_DATE, WeatherContract.normalizeDate(dateValue))
    }
  }
  
  override fun update(
    uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
    val db = mOpenHelper!!.writableDatabase
    val match = sUriMatcher.match(uri)
    val rowsUpdated: Int
    rowsUpdated = when (match) {
      WEATHER -> {
        normalizeDate(values!!)
        db.update(WeatherContract.WeatherEntry.TABLE_NAME, values, selection,
          selectionArgs)
      }
      LOCATION -> db.update(WeatherContract.LocationEntry.TABLE_NAME, values, selection,
        selectionArgs)
      else -> throw UnsupportedOperationException("Unknown uri: $uri")
    }
    if (rowsUpdated != 0) {
      context!!.contentResolver.notifyChange(uri, null)
    }
    return rowsUpdated
  }
  
  override fun bulkInsert(uri: Uri, values: Array<ContentValues>): Int {
    val db = mOpenHelper!!.writableDatabase
    val match = sUriMatcher.match(uri)
    when (match) {
      WEATHER -> {
        db.beginTransaction()
        var returnCount = 0
        try {
          for (value in values) {
            normalizeDate(value)
            val id = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, value)
            if (id != -1L) {
              returnCount++
            }
          }
          db.setTransactionSuccessful()
        } finally {
          db.endTransaction()
        }
        context!!.contentResolver.notifyChange(uri, null)
        return returnCount
      }
      else -> return super.bulkInsert(uri, values)
    }
  }
  
  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  override fun shutdown() {
    mOpenHelper!!.close()
    super.shutdown()
  }
}
