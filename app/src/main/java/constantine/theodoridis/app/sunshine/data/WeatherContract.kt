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

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.provider.BaseColumns
import android.text.format.Time

object WeatherContract {
	const val CONTENT_AUTHORITY = "constantine.theodoridis.app.sunshine"
	val BASE_CONTENT_URI = Uri.parse("content://$CONTENT_AUTHORITY")!!
	const val PATH_WEATHER = "weather"
	const val PATH_LOCATION = "location"

	fun normalizeDate(startDate: Long): Long {
		val time = Time()
		time.set(startDate)
		val julianDay = Time.getJulianDay(startDate, time.gmtoff)
		return time.setJulianDay(julianDay)
	}

	class LocationEntry : BaseColumns {
		companion object {
			const val ID = BaseColumns._ID
			val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build()!!
			const val CONTENT_TYPE =
							ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION
			const val TABLE_NAME = "location"
			const val COLUMN_LOCATION_SETTING = "location_setting"
			const val COLUMN_CITY_NAME = "city_name"
			const val COLUMN_COORDINATE_LATITUDE = "coordinate_latitude"
			const val COLUMN_COORDINATE_LONGITUDE = "coordinate_longitude"

			fun buildLocationUri(id: Long): Uri {
				return ContentUris.withAppendedId(CONTENT_URI, id)
			}
		}
	}

	class WeatherEntry : BaseColumns {
		companion object {
			const val ID = BaseColumns._ID
			val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_WEATHER).build()!!
			const val CONTENT_TYPE =
							ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER
			const val CONTENT_ITEM_TYPE =
							ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER
			const val TABLE_NAME = "weather"
			const val COLUMN_LOC_KEY = "location_id"
			const val COLUMN_DATE = "date"
			const val COLUMN_WEATHER_ID = "weather_id"
			const val COLUMN_SHORT_DESC = "short_desc"
			const val COLUMN_MIN_TEMP = "min"
			const val COLUMN_MAX_TEMP = "max"
			const val COLUMN_HUMIDITY = "humidity"
			const val COLUMN_PRESSURE = "pressure"
			const val COLUMN_WIND_SPEED = "wind"
			const val COLUMN_DEGREES = "degrees"

			fun buildWeatherUri(id: Long): Uri {
				return ContentUris.withAppendedId(CONTENT_URI, id)
			}

			fun buildWeatherLocationWithStartDate(
							locationSetting: String, startDate: Long): Uri {
				val normalizedDate = normalizeDate(startDate)
				return CONTENT_URI.buildUpon().appendPath(locationSetting)
								.appendQueryParameter(COLUMN_DATE, java.lang.Long.toString(normalizedDate)).build()
			}

			fun buildWeatherLocationWithDate(locationSetting: String, date: Long): Uri {
				return CONTENT_URI.buildUpon().appendPath(locationSetting)
								.appendPath(java.lang.Long.toString(normalizeDate(date))).build()
			}

			fun getLocationSettingFromUri(uri: Uri): String {
				return uri.pathSegments[1]
			}

			fun getDateFromUri(uri: Uri): Long {
				return java.lang.Long.parseLong(uri.pathSegments[2])
			}

			fun getStartDateFromUri(uri: Uri): Long {
				val dateString = uri.getQueryParameter(COLUMN_DATE)
				return if (null != dateString && dateString.isNotEmpty())
					java.lang.Long.parseLong(dateString)
				else
					0
			}
		}
	}
}
