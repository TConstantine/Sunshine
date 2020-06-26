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

package constantine.theodoridis.app.sunshine

import android.content.Context
import android.net.ConnectivityManager
import android.preference.PreferenceManager
import android.text.format.Time
import constantine.theodoridis.app.sunshine.sync.SunshineSyncAdapter
import java.text.SimpleDateFormat
import java.util.*

object Utility {
	private var DEFAULT_LAT_LONG = 0f

	fun isLocationLatLonAvailable(context: Context): Boolean {
		val prefs = PreferenceManager.getDefaultSharedPreferences(context)
		return prefs.contains(context.getString(R.string.pref_location_latitude)) && prefs.contains(context.getString(R.string.pref_location_longitude))
	}

	fun getLocationLatitude(context: Context): Float {
		val prefs = PreferenceManager.getDefaultSharedPreferences(context)
		return prefs.getFloat(context.getString(R.string.pref_location_latitude),
						DEFAULT_LAT_LONG)
	}

	fun getLocationLongitude(context: Context): Float {
		val prefs = PreferenceManager.getDefaultSharedPreferences(context)
		return prefs.getFloat(context.getString(R.string.pref_location_longitude),
				DEFAULT_LAT_LONG)
	}

	fun getPreferredLocation(context: Context): String {
		val prefs = PreferenceManager.getDefaultSharedPreferences(context)
		return prefs.getString(context.getString(R.string.preference_location_key),
				context.getString(R.string.preference_location_key_default))
	}

	private fun isMetric(context: Context): Boolean {
		val prefs = PreferenceManager.getDefaultSharedPreferences(context)
		return prefs.getString(context.getString(R.string.preference_temperature_unit_key),
				context.getString(R.string.pref_units_metric)) == context.getString(R.string.pref_units_metric)
	}

	fun formatTemperature(context: Context, temperature: Double): String {
		var newTemperature = temperature
		if (!isMetric(context)) {
			newTemperature = newTemperature * 1.8 + 32
		}
		return String.format(context.getString(R.string.format_temperature), newTemperature)
	}

	fun getFriendlyDayString(context: Context, dateInMillis: Long, displayLongToday: Boolean): String {
		val time = Time()
		time.setToNow()
		val currentTime = System.currentTimeMillis()
		val julianDay = Time.getJulianDay(dateInMillis, time.gmtoff)
		val currentJulianDay = Time.getJulianDay(currentTime, time.gmtoff)
		return if (displayLongToday && julianDay == currentJulianDay) {
			val today = context.getString(R.string.today)
			val formatId = R.string.format_full_friendly_date
			String.format(context.getString(
							formatId,
							today,
							getFormattedMonthDay(dateInMillis)))
		} else if (julianDay < currentJulianDay + 7) {
			getDayName(context, dateInMillis)
		} else {
			val shortenedDateFormat = SimpleDateFormat("EEE MMM dd", Locale.getDefault())
			shortenedDateFormat.format(dateInMillis)
		}
	}

	private fun getDayName(context: Context, dateInMillis: Long): String {
		val t = Time()
		t.setToNow()
		val julianDay = Time.getJulianDay(dateInMillis, t.gmtoff)
		val currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff)
		return when (julianDay) {
			currentJulianDay -> context.getString(R.string.today)
			currentJulianDay + 1 -> context.getString(R.string.tomorrow)
			else -> {
				val time = Time()
				time.setToNow()
				val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
				dayFormat.format(dateInMillis)
			}
		}
	}

	private fun getFormattedMonthDay(dateInMillis: Long): String {
		val time = Time()
		time.setToNow()
		val monthDayFormat = SimpleDateFormat("MMMM dd", Locale.getDefault())
		return monthDayFormat.format(dateInMillis)
	}

	fun getIconResourceForWeatherCondition(weatherId: Int): Int {
		if (weatherId in 200..232) {
			return R.drawable.ic_storm
		} else if (weatherId in 300..321) {
			return R.drawable.ic_light_rain
		} else if (weatherId in 500..504) {
			return R.drawable.ic_rain
		} else if (weatherId == 511) {
			return R.drawable.ic_snow
		} else if (weatherId in 520..531) {
			return R.drawable.ic_rain
		} else if (weatherId in 600..622) {
			return R.drawable.ic_snow
		} else if (weatherId in 701..761) {
			return R.drawable.ic_fog
		} else if (weatherId == 761 || weatherId == 781) {
			return R.drawable.ic_storm
		} else if (weatherId == 800) {
			return R.drawable.ic_clear
		} else if (weatherId == 801) {
			return R.drawable.ic_light_clouds
		} else if (weatherId in 802..804) {
			return R.drawable.ic_cloudy
		}
		return -1
	}

	fun usingLocalGraphics(context: Context): Boolean {
		val prefs = PreferenceManager.getDefaultSharedPreferences(context)
		val sunshineArtPack = context.getString(R.string.pref_art_pack_sunshine)
		return prefs.getString(context.getString(R.string.preference_icon_pack_key),
				sunshineArtPack) == sunshineArtPack
	}

	fun getArtUrlForWeatherCondition(context: Context, weatherId: Int): String? {
		val prefs = PreferenceManager.getDefaultSharedPreferences(context)
		val formatArtUrl = prefs.getString(context.getString(R.string.preference_icon_pack_key),
				context.getString(R.string.pref_art_pack_sunshine))
		if (weatherId in 200..232) {
			return String.format(Locale.US, formatArtUrl!!, "storm")
		} else if (weatherId in 300..321) {
			return String.format(Locale.US, formatArtUrl!!, "light_rain")
		} else if (weatherId in 500..504) {
			return String.format(Locale.US, formatArtUrl!!, "rain")
		} else if (weatherId == 511) {
			return String.format(Locale.US, formatArtUrl!!, "snow")
		} else if (weatherId in 520..531) {
			return String.format(Locale.US, formatArtUrl!!, "rain")
		} else if (weatherId in 600..622) {
			return String.format(Locale.US, formatArtUrl!!, "snow")
		} else if (weatherId in 701..761) {
			return String.format(Locale.US, formatArtUrl!!, "fog")
		} else if (weatherId == 761 || weatherId == 781) {
			return String.format(Locale.US, formatArtUrl!!, "storm")
		} else if (weatherId == 800) {
			return String.format(Locale.US, formatArtUrl!!, "clear")
		} else if (weatherId == 801) {
			return String.format(Locale.US, formatArtUrl!!, "light_clouds")
		} else if (weatherId in 802..804) {
			return String.format(Locale.US, formatArtUrl!!, "clouds")
		}
		return null
	}

	fun getArtResourceForWeatherCondition(weatherId: Int): Int {
		if (weatherId in 200..232) {
			return R.drawable.art_storm
		} else if (weatherId in 300..321) {
			return R.drawable.art_light_rain
		} else if (weatherId in 500..504) {
			return R.drawable.art_rain
		} else if (weatherId == 511) {
			return R.drawable.art_snow
		} else if (weatherId in 520..531) {
			return R.drawable.art_rain
		} else if (weatherId in 600..622) {
			return R.drawable.art_snow
		} else if (weatherId in 701..761) {
			return R.drawable.art_fog
		} else if (weatherId == 761 || weatherId == 781) {
			return R.drawable.art_storm
		} else if (weatherId == 800) {
			return R.drawable.art_clear
		} else if (weatherId == 801) {
			return R.drawable.art_light_clouds
		} else if (weatherId in 802..804) {
			return R.drawable.art_clouds
		}
		return -1
	}

	fun getStringForWeatherCondition(context: Context, weatherId: Int): String {
		val stringId: Int
		when (weatherId) {
			in 200..232 -> stringId = R.string.condition_2xx
			in 300..321 -> stringId = R.string.condition_3xx
			else -> when (weatherId) {
				500 -> stringId = R.string.condition_500
				501 -> stringId = R.string.condition_501
				502 -> stringId = R.string.condition_502
				503 -> stringId = R.string.condition_503
				504 -> stringId = R.string.condition_504
				511 -> stringId = R.string.condition_511
				520 -> stringId = R.string.condition_520
				531 -> stringId = R.string.condition_531
				600 -> stringId = R.string.condition_600
				601 -> stringId = R.string.condition_601
				602 -> stringId = R.string.condition_602
				611 -> stringId = R.string.condition_611
				612 -> stringId = R.string.condition_612
				615 -> stringId = R.string.condition_615
				616 -> stringId = R.string.condition_616
				620 -> stringId = R.string.condition_620
				621 -> stringId = R.string.condition_621
				622 -> stringId = R.string.condition_622
				701 -> stringId = R.string.condition_701
				711 -> stringId = R.string.condition_711
				721 -> stringId = R.string.condition_721
				731 -> stringId = R.string.condition_731
				741 -> stringId = R.string.condition_741
				751 -> stringId = R.string.condition_751
				761 -> stringId = R.string.condition_761
				762 -> stringId = R.string.condition_762
				771 -> stringId = R.string.condition_771
				781 -> stringId = R.string.condition_781
				800 -> stringId = R.string.condition_800
				801 -> stringId = R.string.condition_801
				802 -> stringId = R.string.condition_802
				803 -> stringId = R.string.condition_803
				804 -> stringId = R.string.condition_804
				900 -> stringId = R.string.condition_900
				901 -> stringId = R.string.condition_901
				902 -> stringId = R.string.condition_902
				903 -> stringId = R.string.condition_903
				904 -> stringId = R.string.condition_904
				905 -> stringId = R.string.condition_905
				906 -> stringId = R.string.condition_906
				951 -> stringId = R.string.condition_951
				952 -> stringId = R.string.condition_952
				953 -> stringId = R.string.condition_953
				954 -> stringId = R.string.condition_954
				955 -> stringId = R.string.condition_955
				956 -> stringId = R.string.condition_956
				957 -> stringId = R.string.condition_957
				958 -> stringId = R.string.condition_958
				959 -> stringId = R.string.condition_959
				960 -> stringId = R.string.condition_960
				961 -> stringId = R.string.condition_961
				962 -> stringId = R.string.condition_962
				else -> return context.getString(R.string.condition_unknown, weatherId)
			}
		}
		return context.getString(stringId)
	}

	fun getImageUrlForWeatherCondition(weatherId: Int): String? {
		if (weatherId in 200..232) {
			return "http://upload.wikimedia.org/wikipedia/commons/2/28/Thunderstorm_in_Annemasse,_France.jpg"
		} else if (weatherId in 300..321) {
			return "http://upload.wikimedia.org/wikipedia/commons/a/a0/Rain_on_leaf_504605006.jpg"
		} else if (weatherId in 500..504) {
			return "http://upload.wikimedia.org/wikipedia/commons/6/6c/Rain-on-Thassos.jpg"
		} else if (weatherId == 511) {
			return "http://upload.wikimedia.org/wikipedia/commons/b/b8/Fresh_snow.JPG"
		} else if (weatherId in 520..531) {
			return "http://upload.wikimedia.org/wikipedia/commons/6/6c/Rain-on-Thassos.jpg"
		} else if (weatherId in 600..622) {
			return "http://upload.wikimedia.org/wikipedia/commons/b/b8/Fresh_snow.JPG"
		} else if (weatherId in 701..761) {
			return "http://upload.wikimedia.org/wikipedia/commons/e/e6/Westminster_fog_-_London_-_UK.jpg"
		} else if (weatherId == 761 || weatherId == 781) {
			return "http://upload.wikimedia.org/wikipedia/commons/d/dc/Raised_dust_ahead_of_a_severe_thunderstorm_1.jpg"
		} else if (weatherId == 800) {
			return "http://upload.wikimedia.org/wikipedia/commons/7/7e/A_few_trees_and_the_sun_(6009964513).jpg"
		} else if (weatherId == 801) {
			return "http://upload.wikimedia.org/wikipedia/commons/e/e7/Cloudy_Blue_Sky_(5031259890).jpg"
		} else if (weatherId in 802..804) {
			return "http://upload.wikimedia.org/wikipedia/commons/5/54/Cloudy_hills_in_Elis,_Greece_2.jpg"
		}
		return null
	}

	fun isNetworkAvailable(c: Context): Boolean {
		val cm = c.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
		val activeNetwork = cm.activeNetworkInfo
		return activeNetwork != null && activeNetwork.isConnectedOrConnecting
	}

	@SunshineSyncAdapter.LocationStatus
	fun getLocationStatus(c: Context): Int {
		val sp = PreferenceManager.getDefaultSharedPreferences(c)
		return sp.getInt(c.getString(R.string.pref_location_status_key), SunshineSyncAdapter.LOCATION_STATUS_UNKNOWN)
	}

	fun resetLocationStatus(c: Context) {
		val sp = PreferenceManager.getDefaultSharedPreferences(c)
		val spe = sp.edit()
		spe.putInt(c.getString(R.string.pref_location_status_key), SunshineSyncAdapter.LOCATION_STATUS_UNKNOWN)
		spe.apply()
	}
}