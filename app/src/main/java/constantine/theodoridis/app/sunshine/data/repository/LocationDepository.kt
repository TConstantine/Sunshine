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

package constantine.theodoridis.app.sunshine.data.repository

import android.content.ContentResolver
import constantine.theodoridis.app.sunshine.R
import constantine.theodoridis.app.sunshine.data.WeatherContract.LocationEntry.Companion.COLUMN_CITY_NAME
import constantine.theodoridis.app.sunshine.data.WeatherContract.LocationEntry.Companion.COLUMN_COORDINATE_LATITUDE
import constantine.theodoridis.app.sunshine.data.WeatherContract.LocationEntry.Companion.COLUMN_COORDINATE_LONGITUDE
import constantine.theodoridis.app.sunshine.data.WeatherContract.LocationEntry.Companion.COLUMN_LOCATION_SETTING
import constantine.theodoridis.app.sunshine.data.WeatherContract.LocationEntry.Companion.CONTENT_URI
import constantine.theodoridis.app.sunshine.data.WeatherContract.LocationEntry.Companion.ID
import constantine.theodoridis.app.sunshine.data.datasource.PreferenceDataSource
import constantine.theodoridis.app.sunshine.data.datasource.ResourceDataSource
import constantine.theodoridis.app.sunshine.data.model.Location
import constantine.theodoridis.app.sunshine.domain.repository.LocationRepository

class LocationDepository(
  private val preferenceDataSource: PreferenceDataSource,
  private val resourceDataSource: ResourceDataSource,
  private val contentResolver: ContentResolver
) : LocationRepository {
  override fun getPreferredLocation(): String? {
    return preferenceDataSource.getString(
      resourceDataSource.getString(R.string.preference_location_key),
      resourceDataSource.getString(R.string.preference_location_key_default)
    )
  }
  
  override fun getLocation(locationSetting: String): Location {
    val locationCursor = contentResolver.query(
      CONTENT_URI,
      arrayOf(COLUMN_CITY_NAME, COLUMN_COORDINATE_LATITUDE, COLUMN_COORDINATE_LONGITUDE),
      "$COLUMN_LOCATION_SETTING = ?",
      arrayOf(locationSetting),
      null)
    locationCursor.moveToFirst()
    val location = Location(
      locationCursor.getString(locationCursor.getColumnIndex(COLUMN_CITY_NAME)),
      locationSetting,
      locationCursor.getFloat(locationCursor.getColumnIndex(COLUMN_COORDINATE_LATITUDE)),
      locationCursor.getFloat(locationCursor.getColumnIndex(COLUMN_COORDINATE_LONGITUDE)))
    locationCursor.close()
    return location
  }
  
  override fun hasLocation(locationSetting: String): Boolean {
    val locationCursor = contentResolver.query(
      CONTENT_URI,
      arrayOf(ID),
      "$COLUMN_LOCATION_SETTING = ?",
      arrayOf(locationSetting),
      null
    )
    val hasLocation = locationCursor.count != 0
    locationCursor.close()
    return hasLocation
  }
}
