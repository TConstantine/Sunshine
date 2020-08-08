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
import android.app.IntentService
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import constantine.theodoridis.app.sunshine.R
import constantine.theodoridis.app.sunshine.Utility
import constantine.theodoridis.app.sunshine.data.WeatherContract
import constantine.theodoridis.app.sunshine.presentation.weatherforecastlist.ForecastsActivity

class TodayWidgetIntentService : IntentService("TodayWidgetIntentService") {
  companion object {
    private val FORECAST_COLUMNS = arrayOf(
      WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
      WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
      WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
      WeatherContract.WeatherEntry.COLUMN_MIN_TEMP
    )
    private const val INDEX_WEATHER_ID = 0
    private const val INDEX_SHORT_DESC = 1
    private const val INDEX_MAX_TEMP = 2
    private const val INDEX_MIN_TEMP = 3
  }
  
  override fun onHandleIntent(intent: Intent?) {
    val appWidgetManager = AppWidgetManager.getInstance(this)
    val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(this,
      TodayWidgetProvider::class.java))
    val location = Utility.getPreferredLocation(this)
    val weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
      location, System.currentTimeMillis())
    val data = contentResolver.query(
      weatherForLocationUri,
      FORECAST_COLUMNS,
      null,
      null,
      WeatherContract.WeatherEntry.COLUMN_DATE + " ASC"
    )
      ?: return
    if (!data.moveToFirst()) {
      data.close()
      return
    }
    val weatherId = data.getInt(INDEX_WEATHER_ID)
    val weatherArtResourceId = Utility.getArtResourceForWeatherCondition(weatherId)
    val description = data.getString(INDEX_SHORT_DESC)
    val maxTemp = data.getDouble(INDEX_MAX_TEMP)
    val minTemp = data.getDouble(INDEX_MIN_TEMP)
    val formattedMaxTemperature = Utility.formatTemperature(this, maxTemp)
    val formattedMinTemperature = Utility.formatTemperature(this, minTemp)
    data.close()
    for (appWidgetId in appWidgetIds) {
      val widgetWidth = getWidgetWidth()
      val defaultWidth = resources.getDimensionPixelSize(R.dimen.widget_today_default_width)
      val largeWidth = resources.getDimensionPixelSize(R.dimen.widget_today_large_width)
      val layoutId: Int
      layoutId = when {
        widgetWidth >= largeWidth -> R.layout.widget_today_large
        widgetWidth >= defaultWidth -> R.layout.widget_today
        else -> R.layout.widget_today_small
      }
      val views = RemoteViews(packageName, layoutId)
      views.setImageViewResource(R.id.widget_icon, weatherArtResourceId)
      setRemoteContentDescription(views, description)
      views.setTextViewText(R.id.widget_description, description)
      views.setTextViewText(R.id.widget_high_temperature, formattedMaxTemperature)
      views.setTextViewText(R.id.widget_low_temperature, formattedMinTemperature)
      val launchIntent = Intent(this, ForecastsActivity::class.java)
      val pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0)
      views.setOnClickPendingIntent(R.id.widget, pendingIntent)
      appWidgetManager.updateAppWidget(appWidgetId, views)
    }
  }
  
  private fun getWidgetWidth(): Int {
    return resources.getDimensionPixelSize(R.dimen.widget_today_default_width)
  }
  
  @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
  private fun setRemoteContentDescription(views: RemoteViews, description: String) {
    views.setContentDescription(R.id.widget_icon, description)
  }
}
