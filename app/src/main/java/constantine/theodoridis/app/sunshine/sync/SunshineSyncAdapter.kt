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

package constantine.theodoridis.app.sunshine.sync

import android.accounts.Account
import android.accounts.AccountManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.annotation.IntDef
import android.support.v4.app.NotificationCompat
import android.support.v4.app.TaskStackBuilder
import android.text.format.Time
import com.bumptech.glide.Glide
import constantine.theodoridis.app.sunshine.BuildConfig
import constantine.theodoridis.app.sunshine.MainActivity
import constantine.theodoridis.app.sunshine.R
import constantine.theodoridis.app.sunshine.Utility
import constantine.theodoridis.app.sunshine.data.WeatherContract
import constantine.theodoridis.app.sunshine.muzei.WeatherMuzeiSource
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import java.util.concurrent.ExecutionException

class SunshineSyncAdapter(context: Context, autoInitialize: Boolean) : AbstractThreadedSyncAdapter(context, autoInitialize) {
	companion object {
		const val ACTION_DATA_UPDATED = "constantine.theodoridis.app.sunshine.ACTION_DATA_UPDATED"
		private const val SYNC_INTERVAL = 60 * 180
		private const val SYNC_FLEXTIME = SYNC_INTERVAL / 3
		private const val DAY_IN_MILLIS = (1000 * 60 * 60 * 24).toLong()
		private const val WEATHER_NOTIFICATION_ID = 3004
		private val NOTIFY_WEATHER_PROJECTION = arrayOf(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, WeatherContract.WeatherEntry.COLUMN_SHORT_DESC)
		private const val INDEX_WEATHER_ID = 0
		private const val INDEX_MAX_TEMP = 1
		private const val INDEX_MIN_TEMP = 2
		private const val INDEX_SHORT_DESC = 3
		const val LOCATION_STATUS_OK = 0
		const val LOCATION_STATUS_SERVER_DOWN = 1
		const val LOCATION_STATUS_SERVER_INVALID = 2
		const val LOCATION_STATUS_UNKNOWN = 3
		const val LOCATION_STATUS_INVALID = 4

		private fun configurePeriodicSync(context: Context, syncInterval: Int, flexTime: Int) {
			val account = getSyncAccount(context)
			val authority = context.getString(R.string.content_authority)
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				val request = SyncRequest.Builder().syncPeriodic(syncInterval.toLong(), flexTime.toLong()).setSyncAdapter(account, authority).setExtras(Bundle()).build()
				ContentResolver.requestSync(request)
			} else {
				ContentResolver.addPeriodicSync(account,
								authority, Bundle(), syncInterval.toLong())
			}
		}

		fun syncImmediately(context: Context) {
			val bundle = Bundle()
			bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true)
			bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true)
			ContentResolver.requestSync(getSyncAccount(context),
							context.getString(R.string.content_authority), bundle)
		}

		private fun getSyncAccount(context: Context): Account? {
			val accountManager = context.getSystemService(Context.ACCOUNT_SERVICE) as AccountManager
			val newAccount = Account(
							context.getString(R.string.app_name), context.getString(R.string.sync_account_type))
			if (null == accountManager.getPassword(newAccount)) {
				if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
					return null
				}
				onAccountCreated(newAccount, context)
			}
			return newAccount
		}

		private fun onAccountCreated(newAccount: Account, context: Context) {
			configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME)
			ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true)
			syncImmediately(context)
		}

		fun initializeSyncAdapter(context: Context) {
			getSyncAccount(context)
		}

		private fun setLocationStatus(c: Context, @LocationStatus locationStatus: Int) {
			val sp = PreferenceManager.getDefaultSharedPreferences(c)
			val spe = sp.edit()
			spe.putInt(c.getString(R.string.pref_location_status_key), locationStatus)
			spe.apply()
		}
	}

	@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
	@IntDef(LOCATION_STATUS_OK, LOCATION_STATUS_SERVER_DOWN, LOCATION_STATUS_SERVER_INVALID, LOCATION_STATUS_UNKNOWN, LOCATION_STATUS_INVALID)
	annotation class LocationStatus

	override fun onPerformSync(account: Account, extras: Bundle, authority: String, provider: ContentProviderClient, syncResult: SyncResult) {
		val context = context
		val locationQuery = Utility.getPreferredLocation(context)
		val locationLatitude = Utility.getLocationLatitude(context).toString()
		val locationLongitude = Utility.getLocationLongitude(context).toString()
		var urlConnection: HttpURLConnection? = null
		var reader: BufferedReader? = null
		var forecastJsonStr: String? = null
		val format = "json"
		val units = "metric"
		val numDays = 14
		try {
			val forecastBaseUrl = "http://api.openweathermap.org/data/2.5/forecast/daily?"
			val queryParam = "q"
			val latitudeParam = "lat"
			val longitudeParam = "lon"
			val formatParam = "mode"
			val unitsParam = "units"
			val daysParam = "cnt"
			val appIdParam = "APPID"
			val uriBuilder = Uri.parse(forecastBaseUrl).buildUpon()
			if (Utility.isLocationLatLonAvailable(context)) {
				uriBuilder.appendQueryParameter(latitudeParam, locationLatitude)
								.appendQueryParameter(longitudeParam, locationLongitude)
			} else {
				uriBuilder.appendQueryParameter(queryParam, locationQuery)
			}
			val builtUri = uriBuilder.appendQueryParameter(formatParam, format)
							.appendQueryParameter(unitsParam, units)
							.appendQueryParameter(daysParam, Integer.toString(numDays))
							.appendQueryParameter(appIdParam, BuildConfig.OPEN_WEATHER_MAP_API_KEY)
							.build()
			val url = URL(builtUri.toString())
			urlConnection = url.openConnection() as HttpURLConnection
			urlConnection.requestMethod = "GET"
			urlConnection.connect()
			val inputStream = urlConnection.inputStream
			val buffer = StringBuffer()
			if (inputStream == null) {
				return
			}
			reader = BufferedReader(InputStreamReader(inputStream))
			reader.lineSequence().forEach {
				buffer.append(it + "\n")
			}
			if (buffer.isEmpty()) {
				setLocationStatus(getContext(), LOCATION_STATUS_SERVER_DOWN)
				return
			}
			forecastJsonStr = buffer.toString()
			getWeatherDataFromJson(forecastJsonStr, locationQuery)
		} catch (e: IOException) {
			setLocationStatus(getContext(), LOCATION_STATUS_SERVER_DOWN)
		} catch (e: JSONException) {
			e.printStackTrace()
			setLocationStatus(getContext(), LOCATION_STATUS_SERVER_INVALID)
		} finally {
			urlConnection?.disconnect()
			if (reader != null) {
				try {
					reader.close()
				} catch (e: IOException) {
				}
			}
		}
		return
	}

	@Throws(JSONException::class)
	private fun getWeatherDataFromJson(forecastJsonStr: String,
																		 locationSetting: String) {
		val owmCity = "city"
		val owmCityName = "name"
		val owmCoordinate = "coord"
		val owmLatitude = "lat"
		val owmLongitude = "lon"
		val owmList = "list"
		val owmPressure = "pressure"
		val owmHumidity = "humidity"
		val owmWindSpeed = "speed"
		val owmWindDirection = "deg"
		val owmTemperature = "temp"
		val owmMax = "max"
		val owmMin = "min"
		val owmWeather = "weather"
		val owmDescription = "main"
		val owmWeatherId = "id"
		val owmMessageCode = "cod"
		try {
			val forecastJson = JSONObject(forecastJsonStr)
			if (forecastJson.has(owmMessageCode)) {
				val errorCode = forecastJson.getInt(owmMessageCode)
				when (errorCode) {
					HttpURLConnection.HTTP_OK -> {
					}
					HttpURLConnection.HTTP_NOT_FOUND -> {
						setLocationStatus(context, LOCATION_STATUS_INVALID)
						return
					}
					else -> {
						setLocationStatus(context, LOCATION_STATUS_SERVER_DOWN)
						return
					}
				}
			}
			val weatherArray = forecastJson.getJSONArray(owmList)
			val cityJson = forecastJson.getJSONObject(owmCity)
			val cityName = cityJson.getString(owmCityName)
			val cityCoordinate = cityJson.getJSONObject(owmCoordinate)
			val cityLatitude = cityCoordinate.getDouble(owmLatitude)
			val cityLongitude = cityCoordinate.getDouble(owmLongitude)
			val locationId = addLocation(locationSetting, cityName, cityLatitude, cityLongitude)
			val cVVector = Vector<ContentValues>(weatherArray.length())
			var dayTime = Time()
			dayTime.setToNow()
			val julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff)
			dayTime = Time()
			for (i in 0 until weatherArray.length()) {
				val dateTime: Long = dayTime.setJulianDay(julianStartDay + i)
				val pressure: Double
				val humidity: Int
				val windSpeed: Double
				val windDirection: Double
				val high: Double
				val low: Double
				val description: String
				val weatherId: Int
				val dayForecast = weatherArray.getJSONObject(i)
				pressure = dayForecast.getDouble(owmPressure)
				humidity = dayForecast.getInt(owmHumidity)
				windSpeed = dayForecast.getDouble(owmWindSpeed)
				windDirection = dayForecast.getDouble(owmWindDirection)
				val weatherObject = dayForecast.getJSONArray(owmWeather).getJSONObject(0)
				description = weatherObject.getString(owmDescription)
				weatherId = weatherObject.getInt(owmWeatherId)
				val temperatureObject = dayForecast.getJSONObject(owmTemperature)
				high = temperatureObject.getDouble(owmMax)
				low = temperatureObject.getDouble(owmMin)
				val weatherValues = ContentValues()
				weatherValues.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY, locationId)
				weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DATE, dateTime)
				weatherValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, humidity)
				weatherValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, pressure)
				weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, windSpeed)
				weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES, windDirection)
				weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, high)
				weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, low)
				weatherValues.put(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC, description)
				weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, weatherId)
				cVVector.add(weatherValues)
			}
			if (cVVector.size > 0) {
				val cvArray = cVVector.toTypedArray()
				context.contentResolver.bulkInsert(WeatherContract.WeatherEntry.CONTENT_URI, cvArray)
				context.contentResolver.delete(WeatherContract.WeatherEntry.CONTENT_URI,
								WeatherContract.WeatherEntry.COLUMN_DATE + " <= ?",
								arrayOf(java.lang.Long.toString(dayTime.setJulianDay(julianStartDay - 1))))
				updateWidgets()
				updateMuzei()
				notifyWeather()
			}
			setLocationStatus(context, LOCATION_STATUS_OK)
		} catch (e: JSONException) {
			e.printStackTrace()
			setLocationStatus(context, LOCATION_STATUS_SERVER_INVALID)
		}
	}

	private fun updateWidgets() {
		val context = context
		val dataUpdatedIntent = Intent(ACTION_DATA_UPDATED)
						.setPackage(context.packageName)
		context.sendBroadcast(dataUpdatedIntent)
	}

	private fun updateMuzei() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			val context = context
			context.startService(Intent(ACTION_DATA_UPDATED)
							.setClass(context, WeatherMuzeiSource::class.java))
		}
	}

	private fun notifyWeather() {
		val context = context
		val prefs = PreferenceManager.getDefaultSharedPreferences(context)
		val displayNotificationsKey = context.getString(R.string.pref_enable_notifications_key)
		val displayNotifications = prefs.getBoolean(displayNotificationsKey,
						java.lang.Boolean.parseBoolean(context.getString(R.string.pref_enable_notifications_default)))
		if (displayNotifications) {
			val lastNotificationKey = context.getString(R.string.pref_last_notification)
			val lastSync = prefs.getLong(lastNotificationKey, 0)
			if (System.currentTimeMillis() - lastSync >= DAY_IN_MILLIS) {
				val locationQuery = Utility.getPreferredLocation(context)
				val weatherUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(locationQuery, System.currentTimeMillis())
				val cursor = context.contentResolver.query(weatherUri, NOTIFY_WEATHER_PROJECTION, null, null, null)
				if (cursor!!.moveToFirst()) {
					val weatherId = cursor.getInt(INDEX_WEATHER_ID)
					val high = cursor.getDouble(INDEX_MAX_TEMP)
					val low = cursor.getDouble(INDEX_MIN_TEMP)
					val desc = cursor.getString(INDEX_SHORT_DESC)
					val iconId = Utility.getIconResourceForWeatherCondition(weatherId)
					val resources = context.resources
					val artResourceId = Utility.getArtResourceForWeatherCondition(weatherId)
					val artUrl = Utility.getArtUrlForWeatherCondition(context, weatherId)
					val largeIconWidth = resources.getDimensionPixelSize(android.R.dimen.notification_large_icon_width)
					val largeIconHeight = resources.getDimensionPixelSize(android.R.dimen.notification_large_icon_height)
					val largeIcon: Bitmap
					largeIcon = try {
						Glide.with(context)
										.load(artUrl)
										.asBitmap()
										.error(artResourceId)
										.fitCenter()
										.into(largeIconWidth, largeIconHeight).get()
					} catch (e: InterruptedException) {
						BitmapFactory.decodeResource(resources, artResourceId)
					} catch (e: ExecutionException) {
						BitmapFactory.decodeResource(resources, artResourceId)
					}
					val title = context.getString(R.string.app_name)
					val contentText = String.format(context.getString(R.string.format_notification),
									desc,
									Utility.formatTemperature(context, high),
									Utility.formatTemperature(context, low))
					val mBuilder = NotificationCompat.Builder(getContext())
									.setColor(resources.getColor(R.color.primary_light))
									.setSmallIcon(iconId)
									.setLargeIcon(largeIcon)
									.setContentTitle(title)
									.setContentText(contentText)
					val resultIntent = Intent(context, MainActivity::class.java)
					val stackBuilder = TaskStackBuilder.create(context)
					stackBuilder.addNextIntent(resultIntent)
					val resultPendingIntent = stackBuilder.getPendingIntent(
									0,
									PendingIntent.FLAG_UPDATE_CURRENT
					)
					mBuilder.setContentIntent(resultPendingIntent)
					val mNotificationManager = getContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
					mNotificationManager.notify(WEATHER_NOTIFICATION_ID, mBuilder.build())
					val editor = prefs.edit()
					editor.putLong(lastNotificationKey, System.currentTimeMillis())
					editor.apply()
				}
				cursor.close()
			}
		}
	}

	private fun addLocation(locationSetting: String, cityName: String, lat: Double, lon: Double): Long {
		val locationId: Long
		val locationCursor = context.contentResolver.query(
						WeatherContract.LocationEntry.CONTENT_URI,
						arrayOf(WeatherContract.LocationEntry.ID),
						WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ?",
						arrayOf(locationSetting), null)
		locationId = if (locationCursor!!.moveToFirst()) {
			val locationIdIndex = locationCursor.getColumnIndex(WeatherContract.LocationEntry.ID)
			locationCursor.getLong(locationIdIndex)
		} else {
			val locationValues = ContentValues()
			locationValues.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, cityName)
			locationValues.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING, locationSetting)
			locationValues.put(WeatherContract.LocationEntry.COLUMN_COORDINATE_LATITUDE, lat)
			locationValues.put(WeatherContract.LocationEntry.COLUMN_COORDINATE_LONGITUDE, lon)
			val insertedUri = context.contentResolver.insert(
							WeatherContract.LocationEntry.CONTENT_URI,
							locationValues
			)
			ContentUris.parseId(insertedUri)
		}
		locationCursor.close()
		return locationId
	}
}