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

package constantine.theodoridis.app.sunshine.widget

import android.annotation.TargetApi
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.os.Binder
import android.os.Build
import android.widget.AdapterView
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import constantine.theodoridis.app.sunshine.R
import constantine.theodoridis.app.sunshine.Utility
import constantine.theodoridis.app.sunshine.data.WeatherContract
import java.util.concurrent.ExecutionException

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
class DetailWidgetRemoteViewsService : RemoteViewsService() {
	companion object {
		private val FORECAST_COLUMNS = arrayOf(WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry.ID, WeatherContract.WeatherEntry.COLUMN_DATE, WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, WeatherContract.WeatherEntry.COLUMN_SHORT_DESC, WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, WeatherContract.WeatherEntry.COLUMN_MIN_TEMP)
		internal const val INDEX_WEATHER_ID = 0
		internal const val INDEX_WEATHER_DATE = 1
		internal const val INDEX_WEATHER_CONDITION_ID = 2
		internal const val INDEX_WEATHER_DESC = 3
		internal const val INDEX_WEATHER_MAX_TEMP = 4
		internal const val INDEX_WEATHER_MIN_TEMP = 5
	}

	override fun onGetViewFactory(intent: Intent): RemoteViewsService.RemoteViewsFactory {
		return object : RemoteViewsService.RemoteViewsFactory {
			private var data: Cursor? = null

			override fun onCreate() {}

			override fun onDataSetChanged() {
				if (data != null) {
					data!!.close()
				}

				val identityToken = Binder.clearCallingIdentity()
				val location = Utility.getPreferredLocation(this@DetailWidgetRemoteViewsService)
				val weatherForLocationUri = WeatherContract.WeatherEntry
								.buildWeatherLocationWithStartDate(location, System.currentTimeMillis())
				data = contentResolver.query(weatherForLocationUri,
								FORECAST_COLUMNS, null, null,
								WeatherContract.WeatherEntry.COLUMN_DATE + " ASC")
				Binder.restoreCallingIdentity(identityToken)
			}

			override fun onDestroy() {
				if (data != null) {
					data!!.close()
					data = null
				}
			}

			override fun getCount(): Int {
				return if (data == null) 0 else data!!.count
			}

			override fun getViewAt(position: Int): RemoteViews? {
				if (position == AdapterView.INVALID_POSITION ||
					data == null || !data!!.moveToPosition(position)) {
					return null
				}
				val views = RemoteViews(packageName,
								R.layout.widget_detail_list_item)
				val weatherId = data!!.getInt(INDEX_WEATHER_CONDITION_ID)
				val weatherArtResourceId = Utility.getIconResourceForWeatherCondition(weatherId)
				var weatherArtImage: Bitmap? = null
				if (!Utility.usingLocalGraphics(this@DetailWidgetRemoteViewsService)) {
					val weatherArtResourceUrl = Utility.getArtUrlForWeatherCondition(
									this@DetailWidgetRemoteViewsService, weatherId)
					try {
						weatherArtImage = Glide.with(this@DetailWidgetRemoteViewsService)
							.asBitmap()
							.load(weatherArtResourceUrl)
							.error(weatherArtResourceId)
							.into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get()
					} catch (e: InterruptedException) {
					} catch (e: ExecutionException) {
					}
				}
				val description = data!!.getString(INDEX_WEATHER_DESC)
				val dateInMillis = data!!.getLong(INDEX_WEATHER_DATE)
				val formattedDate = Utility.getFriendlyDayString(
								this@DetailWidgetRemoteViewsService, dateInMillis, false)
				val maxTemp = data!!.getDouble(INDEX_WEATHER_MAX_TEMP)
				val minTemp = data!!.getDouble(INDEX_WEATHER_MIN_TEMP)
				val formattedMaxTemperature = Utility.formatTemperature(this@DetailWidgetRemoteViewsService, maxTemp)
				val formattedMinTemperature = Utility.formatTemperature(this@DetailWidgetRemoteViewsService, minTemp)
				if (weatherArtImage != null) {
					views.setImageViewBitmap(R.id.widget_icon, weatherArtImage)
				} else {
					views.setImageViewResource(R.id.widget_icon, weatherArtResourceId)
				}
				setRemoteContentDescription(views, description)
				views.setTextViewText(R.id.widget_date, formattedDate)
				views.setTextViewText(R.id.widget_description, description)
				views.setTextViewText(R.id.widget_high_temperature, formattedMaxTemperature)
				views.setTextViewText(R.id.widget_low_temperature, formattedMinTemperature)
				val fillInIntent = Intent()
				val locationSetting = Utility.getPreferredLocation(this@DetailWidgetRemoteViewsService)
				val weatherUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
								locationSetting,
								dateInMillis)
				fillInIntent.data = weatherUri
				views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent)
				return views
			}

			@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
			private fun setRemoteContentDescription(views: RemoteViews, description: String) {
				views.setContentDescription(R.id.widget_icon, description)
			}

			override fun getLoadingView(): RemoteViews {
				return RemoteViews(packageName, R.layout.widget_detail_list_item)
			}

			override fun getViewTypeCount(): Int {
				return 1
			}

			override fun getItemId(position: Int): Long {
				return if (data!!.moveToPosition(position)) data!!.getLong(INDEX_WEATHER_ID) else position.toLong()
			}

			override fun hasStableIds(): Boolean {
				return true
			}
		}
	}
}
