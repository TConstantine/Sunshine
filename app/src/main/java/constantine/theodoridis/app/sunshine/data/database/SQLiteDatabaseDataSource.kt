/*
 *  Copyright (C) 2020 Constantine Theodoridis
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

package constantine.theodoridis.app.sunshine.data.database

import android.content.ContentResolver
import constantine.theodoridis.app.sunshine.data.WeatherContract
import constantine.theodoridis.app.sunshine.data.WeatherContract.WeatherEntry.Companion.COLUMN_DATE
import constantine.theodoridis.app.sunshine.data.WeatherContract.WeatherEntry.Companion.COLUMN_DEGREES
import constantine.theodoridis.app.sunshine.data.WeatherContract.WeatherEntry.Companion.COLUMN_HUMIDITY
import constantine.theodoridis.app.sunshine.data.WeatherContract.WeatherEntry.Companion.COLUMN_MAX_TEMP
import constantine.theodoridis.app.sunshine.data.WeatherContract.WeatherEntry.Companion.COLUMN_MIN_TEMP
import constantine.theodoridis.app.sunshine.data.WeatherContract.WeatherEntry.Companion.COLUMN_PRESSURE
import constantine.theodoridis.app.sunshine.data.WeatherContract.WeatherEntry.Companion.COLUMN_WEATHER_ID
import constantine.theodoridis.app.sunshine.data.WeatherContract.WeatherEntry.Companion.COLUMN_WIND_SPEED
import constantine.theodoridis.app.sunshine.data.database.model.WeatherForecastDatabaseModel

class SQLiteDatabaseDataSource(private val contentResolver: ContentResolver) : DatabaseDataSource {
	override fun getWeatherForecast(location: String, date: Long): WeatherForecastDatabaseModel {
		val locationCursor = contentResolver.query(
				WeatherContract.LocationEntry.CONTENT_URI,
				arrayOf(WeatherContract.LocationEntry.ID),
				"${WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING} = ?",
				arrayOf(location),
				null
		)
		if (locationCursor != null) {
			locationCursor.moveToFirst()
			val locationId = locationCursor.getInt(locationCursor.getColumnIndex(WeatherContract.LocationEntry.ID))
			locationCursor.close()
			val weatherForecastCursor = contentResolver.query(
					WeatherContract.WeatherEntry.CONTENT_URI,
					arrayOf(
							COLUMN_DATE,
							COLUMN_WEATHER_ID,
							COLUMN_MAX_TEMP,
							COLUMN_MIN_TEMP,
							COLUMN_HUMIDITY,
							COLUMN_PRESSURE,
							COLUMN_WIND_SPEED,
							COLUMN_DEGREES
					),
					"${WeatherContract.WeatherEntry.COLUMN_LOC_KEY} = ? AND $COLUMN_DATE = ?",
					arrayOf(locationId.toString(), date.toString()),
					null)
			if (weatherForecastCursor != null) {
				weatherForecastCursor.moveToFirst()
				val weatherForecast = WeatherForecastDatabaseModel(
						locationSettingId = locationId,
						dateInMilliseconds = weatherForecastCursor.getLong(weatherForecastCursor.getColumnIndex(COLUMN_DATE)),
						weatherId = weatherForecastCursor.getInt(weatherForecastCursor.getColumnIndex(COLUMN_WEATHER_ID)),
						maxTemperature = weatherForecastCursor.getFloat(weatherForecastCursor.getColumnIndex(COLUMN_MAX_TEMP)),
						minTemperature = weatherForecastCursor.getFloat(weatherForecastCursor.getColumnIndex(COLUMN_MIN_TEMP)),
						humidity = weatherForecastCursor.getFloat(weatherForecastCursor.getColumnIndex(COLUMN_HUMIDITY)),
						pressure = weatherForecastCursor.getFloat(weatherForecastCursor.getColumnIndex(COLUMN_PRESSURE)),
						windSpeed = weatherForecastCursor.getFloat(weatherForecastCursor.getColumnIndex(COLUMN_WIND_SPEED)),
						windDegrees = weatherForecastCursor.getFloat(weatherForecastCursor.getColumnIndex(COLUMN_DEGREES))
				)
				weatherForecastCursor.close()
				return weatherForecast
			}
			throw NullPointerException("Could not get weather forecast. Cursor is null")
		}
		throw NullPointerException("Could not get location id. Cursor is null")
	}
}