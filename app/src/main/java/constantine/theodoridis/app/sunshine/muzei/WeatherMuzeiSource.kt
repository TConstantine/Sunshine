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

package constantine.theodoridis.app.sunshine.muzei

import android.content.Intent
import android.net.Uri
import com.google.android.apps.muzei.api.Artwork
import com.google.android.apps.muzei.api.MuzeiArtSource
import constantine.theodoridis.app.sunshine.Utility
import constantine.theodoridis.app.sunshine.data.WeatherContract
import constantine.theodoridis.app.sunshine.presentation.forecasts.ForecastsActivity
import constantine.theodoridis.app.sunshine.sync.SunshineSyncAdapter

class WeatherMuzeiSource : MuzeiArtSource("WeatherMuzeiSource") {
	companion object {
		private val FORECAST_COLUMNS = arrayOf(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, WeatherContract.WeatherEntry.COLUMN_SHORT_DESC)
		private const val INDEX_WEATHER_ID = 0
		private const val INDEX_SHORT_DESC = 1
	}

	override fun onHandleIntent(intent: Intent?) {
		super.onHandleIntent(intent)
		val dataUpdated = intent != null && SunshineSyncAdapter.ACTION_DATA_UPDATED == intent.action
		if (dataUpdated && isEnabled) {
			onUpdate(MuzeiArtSource.UPDATE_REASON_OTHER)
		}
	}

	override fun onUpdate(reason: Int) {
		val location = Utility.getPreferredLocation(this)
		val weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(location, System.currentTimeMillis())
		val cursor = contentResolver.query(weatherForLocationUri, FORECAST_COLUMNS, null, null, WeatherContract.WeatherEntry.COLUMN_DATE + " ASC")
		if (cursor!!.moveToFirst()) {
			val weatherId = cursor.getInt(INDEX_WEATHER_ID)
			val desc = cursor.getString(INDEX_SHORT_DESC)
			val imageUrl = Utility.getImageUrlForWeatherCondition(weatherId)
			if (imageUrl != null) {
				publishArtwork(Artwork.Builder()
						.imageUri(Uri.parse(imageUrl))
						.title(desc)
						.byline(location)
						.viewIntent(Intent(this, ForecastsActivity::class.java))
								.build())
			}
		}
		cursor.close()
	}
}
