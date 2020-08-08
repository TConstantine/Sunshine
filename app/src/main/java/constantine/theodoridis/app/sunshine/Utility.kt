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
  private const val CELSIUS_TO_FAHRENHEIT_MULTIPLIER = 1.8F
  private const val CELSIUS_TO_FAHRENHEIT_DIFFERENCE = 32
  private const val DAYS_OF_WEEK = 7
  private const val WEATHER_FORECAST_ID_200 = 200
  private const val WEATHER_FORECAST_ID_232 = 232
  private const val WEATHER_FORECAST_ID_300 = 300
  private const val WEATHER_FORECAST_ID_321 = 321
  private const val WEATHER_FORECAST_ID_500 = 500
  private const val WEATHER_FORECAST_ID_501 = 501
  private const val WEATHER_FORECAST_ID_502 = 502
  private const val WEATHER_FORECAST_ID_503 = 503
  private const val WEATHER_FORECAST_ID_504 = 504
  private const val WEATHER_FORECAST_ID_511 = 511
  private const val WEATHER_FORECAST_ID_520 = 520
  private const val WEATHER_FORECAST_ID_531 = 531
  private const val WEATHER_FORECAST_ID_600 = 600
  private const val WEATHER_FORECAST_ID_601 = 601
  private const val WEATHER_FORECAST_ID_602 = 602
  private const val WEATHER_FORECAST_ID_611 = 611
  private const val WEATHER_FORECAST_ID_612 = 612
  private const val WEATHER_FORECAST_ID_615 = 615
  private const val WEATHER_FORECAST_ID_616 = 616
  private const val WEATHER_FORECAST_ID_620 = 620
  private const val WEATHER_FORECAST_ID_621 = 621
  private const val WEATHER_FORECAST_ID_622 = 622
  private const val WEATHER_FORECAST_ID_701 = 701
  private const val WEATHER_FORECAST_ID_711 = 711
  private const val WEATHER_FORECAST_ID_721 = 721
  private const val WEATHER_FORECAST_ID_731 = 731
  private const val WEATHER_FORECAST_ID_741 = 741
  private const val WEATHER_FORECAST_ID_751 = 751
  private const val WEATHER_FORECAST_ID_761 = 761
  private const val WEATHER_FORECAST_ID_762 = 762
  private const val WEATHER_FORECAST_ID_771 = 771
  private const val WEATHER_FORECAST_ID_781 = 781
  private const val WEATHER_FORECAST_ID_800 = 800
  private const val WEATHER_FORECAST_ID_801 = 801
  private const val WEATHER_FORECAST_ID_802 = 802
  private const val WEATHER_FORECAST_ID_803 = 803
  private const val WEATHER_FORECAST_ID_804 = 804
  private const val WEATHER_FORECAST_ID_900 = 900
  private const val WEATHER_FORECAST_ID_901 = 901
  private const val WEATHER_FORECAST_ID_902 = 902
  private const val WEATHER_FORECAST_ID_903 = 903
  private const val WEATHER_FORECAST_ID_904 = 904
  private const val WEATHER_FORECAST_ID_905 = 905
  private const val WEATHER_FORECAST_ID_906 = 906
  private const val WEATHER_FORECAST_ID_951 = 951
  private const val WEATHER_FORECAST_ID_952 = 952
  private const val WEATHER_FORECAST_ID_953 = 953
  private const val WEATHER_FORECAST_ID_954 = 954
  private const val WEATHER_FORECAST_ID_955 = 955
  private const val WEATHER_FORECAST_ID_956 = 956
  private const val WEATHER_FORECAST_ID_957 = 957
  private const val WEATHER_FORECAST_ID_958 = 958
  private const val WEATHER_FORECAST_ID_959 = 959
  private const val WEATHER_FORECAST_ID_960 = 960
  private const val WEATHER_FORECAST_ID_961 = 961
  private const val WEATHER_FORECAST_ID_962 = 962
  
  fun isLocationLatLonAvailable(context: Context): Boolean {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    return prefs.contains(context.getString(R.string.pref_location_latitude)) &&
      prefs.contains(context.getString(R.string.pref_location_longitude))
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
      newTemperature = newTemperature * CELSIUS_TO_FAHRENHEIT_MULTIPLIER + CELSIUS_TO_FAHRENHEIT_DIFFERENCE
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
    } else if (julianDay < currentJulianDay + DAYS_OF_WEEK) {
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
    if (weatherId in WEATHER_FORECAST_ID_200..WEATHER_FORECAST_ID_232) {
      return R.drawable.ic_storm
    } else if (weatherId in WEATHER_FORECAST_ID_300..WEATHER_FORECAST_ID_321) {
      return R.drawable.ic_light_rain
    } else if (weatherId in WEATHER_FORECAST_ID_500..WEATHER_FORECAST_ID_504) {
      return R.drawable.ic_rain
    } else if (weatherId == WEATHER_FORECAST_ID_511) {
      return R.drawable.ic_snow
    } else if (weatherId in WEATHER_FORECAST_ID_520..WEATHER_FORECAST_ID_531) {
      return R.drawable.ic_rain
    } else if (weatherId in WEATHER_FORECAST_ID_600..WEATHER_FORECAST_ID_622) {
      return R.drawable.ic_snow
    } else if (weatherId in WEATHER_FORECAST_ID_701..WEATHER_FORECAST_ID_761) {
      return R.drawable.ic_fog
    } else if (weatherId == WEATHER_FORECAST_ID_761 || weatherId == WEATHER_FORECAST_ID_781) {
      return R.drawable.ic_storm
    } else if (weatherId == WEATHER_FORECAST_ID_800) {
      return R.drawable.ic_clear
    } else if (weatherId == WEATHER_FORECAST_ID_801) {
      return R.drawable.ic_light_clouds
    } else if (weatherId in WEATHER_FORECAST_ID_802..WEATHER_FORECAST_ID_804) {
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
    if (weatherId in WEATHER_FORECAST_ID_200..WEATHER_FORECAST_ID_232) {
      return String.format(Locale.US, formatArtUrl!!, "storm")
    } else if (weatherId in WEATHER_FORECAST_ID_300..WEATHER_FORECAST_ID_321) {
      return String.format(Locale.US, formatArtUrl!!, "light_rain")
    } else if (weatherId in WEATHER_FORECAST_ID_500..WEATHER_FORECAST_ID_504) {
      return String.format(Locale.US, formatArtUrl!!, "rain")
    } else if (weatherId == WEATHER_FORECAST_ID_511) {
      return String.format(Locale.US, formatArtUrl!!, "snow")
    } else if (weatherId in WEATHER_FORECAST_ID_520..WEATHER_FORECAST_ID_531) {
      return String.format(Locale.US, formatArtUrl!!, "rain")
    } else if (weatherId in WEATHER_FORECAST_ID_600..WEATHER_FORECAST_ID_622) {
      return String.format(Locale.US, formatArtUrl!!, "snow")
    } else if (weatherId in WEATHER_FORECAST_ID_701..WEATHER_FORECAST_ID_761) {
      return String.format(Locale.US, formatArtUrl!!, "fog")
    } else if (weatherId == WEATHER_FORECAST_ID_761 || weatherId == WEATHER_FORECAST_ID_781) {
      return String.format(Locale.US, formatArtUrl!!, "storm")
    } else if (weatherId == WEATHER_FORECAST_ID_800) {
      return String.format(Locale.US, formatArtUrl!!, "clear")
    } else if (weatherId == WEATHER_FORECAST_ID_801) {
      return String.format(Locale.US, formatArtUrl!!, "light_clouds")
    } else if (weatherId in WEATHER_FORECAST_ID_802..WEATHER_FORECAST_ID_804) {
      return String.format(Locale.US, formatArtUrl!!, "clouds")
    }
    return null
  }
  
  fun getArtResourceForWeatherCondition(weatherId: Int): Int {
    if (weatherId in WEATHER_FORECAST_ID_200..WEATHER_FORECAST_ID_232) {
      return R.drawable.art_storm
    } else if (weatherId in WEATHER_FORECAST_ID_300..WEATHER_FORECAST_ID_321) {
      return R.drawable.art_light_rain
    } else if (weatherId in WEATHER_FORECAST_ID_500..WEATHER_FORECAST_ID_504) {
      return R.drawable.art_rain
    } else if (weatherId == WEATHER_FORECAST_ID_511) {
      return R.drawable.art_snow
    } else if (weatherId in WEATHER_FORECAST_ID_520..WEATHER_FORECAST_ID_531) {
      return R.drawable.art_rain
    } else if (weatherId in WEATHER_FORECAST_ID_600..WEATHER_FORECAST_ID_622) {
      return R.drawable.art_snow
    } else if (weatherId in WEATHER_FORECAST_ID_701..WEATHER_FORECAST_ID_761) {
      return R.drawable.art_fog
    } else if (weatherId == WEATHER_FORECAST_ID_761 || weatherId == WEATHER_FORECAST_ID_781) {
      return R.drawable.art_storm
    } else if (weatherId == WEATHER_FORECAST_ID_800) {
      return R.drawable.art_clear
    } else if (weatherId == WEATHER_FORECAST_ID_801) {
      return R.drawable.art_light_clouds
    } else if (weatherId in WEATHER_FORECAST_ID_802..WEATHER_FORECAST_ID_804) {
      return R.drawable.art_clouds
    }
    return -1
  }
  
  fun getStringForWeatherCondition(context: Context, weatherId: Int): String {
    val stringId: Int
    when (weatherId) {
      in WEATHER_FORECAST_ID_200..WEATHER_FORECAST_ID_232 -> stringId = R.string.condition_2xx
      in WEATHER_FORECAST_ID_300..WEATHER_FORECAST_ID_321 -> stringId = R.string.condition_3xx
      else -> when (weatherId) {
        WEATHER_FORECAST_ID_500 -> stringId = R.string.condition_500
        WEATHER_FORECAST_ID_501 -> stringId = R.string.condition_501
        WEATHER_FORECAST_ID_502 -> stringId = R.string.condition_502
        WEATHER_FORECAST_ID_503 -> stringId = R.string.condition_503
        WEATHER_FORECAST_ID_504 -> stringId = R.string.condition_504
        WEATHER_FORECAST_ID_511 -> stringId = R.string.condition_511
        WEATHER_FORECAST_ID_520 -> stringId = R.string.condition_520
        WEATHER_FORECAST_ID_531 -> stringId = R.string.condition_531
        WEATHER_FORECAST_ID_600 -> stringId = R.string.condition_600
        WEATHER_FORECAST_ID_601 -> stringId = R.string.condition_601
        WEATHER_FORECAST_ID_602 -> stringId = R.string.condition_602
        WEATHER_FORECAST_ID_611 -> stringId = R.string.condition_611
        WEATHER_FORECAST_ID_612 -> stringId = R.string.condition_612
        WEATHER_FORECAST_ID_615 -> stringId = R.string.condition_615
        WEATHER_FORECAST_ID_616 -> stringId = R.string.condition_616
        WEATHER_FORECAST_ID_620 -> stringId = R.string.condition_620
        WEATHER_FORECAST_ID_621 -> stringId = R.string.condition_621
        WEATHER_FORECAST_ID_622 -> stringId = R.string.condition_622
        WEATHER_FORECAST_ID_701 -> stringId = R.string.condition_701
        WEATHER_FORECAST_ID_711 -> stringId = R.string.condition_711
        WEATHER_FORECAST_ID_721 -> stringId = R.string.condition_721
        WEATHER_FORECAST_ID_731 -> stringId = R.string.condition_731
        WEATHER_FORECAST_ID_741 -> stringId = R.string.condition_741
        WEATHER_FORECAST_ID_751 -> stringId = R.string.condition_751
        WEATHER_FORECAST_ID_761 -> stringId = R.string.condition_761
        WEATHER_FORECAST_ID_762 -> stringId = R.string.condition_762
        WEATHER_FORECAST_ID_771 -> stringId = R.string.condition_771
        WEATHER_FORECAST_ID_781 -> stringId = R.string.condition_781
        WEATHER_FORECAST_ID_800 -> stringId = R.string.condition_800
        WEATHER_FORECAST_ID_801 -> stringId = R.string.condition_801
        WEATHER_FORECAST_ID_802 -> stringId = R.string.condition_802
        WEATHER_FORECAST_ID_803 -> stringId = R.string.condition_803
        WEATHER_FORECAST_ID_804 -> stringId = R.string.condition_804
        WEATHER_FORECAST_ID_900 -> stringId = R.string.condition_900
        WEATHER_FORECAST_ID_901 -> stringId = R.string.condition_901
        WEATHER_FORECAST_ID_902 -> stringId = R.string.condition_902
        WEATHER_FORECAST_ID_903 -> stringId = R.string.condition_903
        WEATHER_FORECAST_ID_904 -> stringId = R.string.condition_904
        WEATHER_FORECAST_ID_905 -> stringId = R.string.condition_905
        WEATHER_FORECAST_ID_906 -> stringId = R.string.condition_906
        WEATHER_FORECAST_ID_951 -> stringId = R.string.condition_951
        WEATHER_FORECAST_ID_952 -> stringId = R.string.condition_952
        WEATHER_FORECAST_ID_953 -> stringId = R.string.condition_953
        WEATHER_FORECAST_ID_954 -> stringId = R.string.condition_954
        WEATHER_FORECAST_ID_955 -> stringId = R.string.condition_955
        WEATHER_FORECAST_ID_956 -> stringId = R.string.condition_956
        WEATHER_FORECAST_ID_957 -> stringId = R.string.condition_957
        WEATHER_FORECAST_ID_958 -> stringId = R.string.condition_958
        WEATHER_FORECAST_ID_959 -> stringId = R.string.condition_959
        WEATHER_FORECAST_ID_960 -> stringId = R.string.condition_960
        WEATHER_FORECAST_ID_961 -> stringId = R.string.condition_961
        WEATHER_FORECAST_ID_962 -> stringId = R.string.condition_962
        else -> return context.getString(R.string.condition_unknown, weatherId)
      }
    }
    return context.getString(stringId)
  }
  
  fun getImageUrlForWeatherCondition(weatherId: Int): String? {
    if (weatherId in WEATHER_FORECAST_ID_200..WEATHER_FORECAST_ID_232) {
      return "http://upload.wikimedia.org/wikipedia/commons/2/28/Thunderstorm_in_Annemasse,_France.jpg"
    } else if (weatherId in WEATHER_FORECAST_ID_300..WEATHER_FORECAST_ID_321) {
      return "http://upload.wikimedia.org/wikipedia/commons/a/a0/Rain_on_leaf_504605006.jpg"
    } else if (weatherId in WEATHER_FORECAST_ID_500..WEATHER_FORECAST_ID_504) {
      return "http://upload.wikimedia.org/wikipedia/commons/6/6c/Rain-on-Thassos.jpg"
    } else if (weatherId == WEATHER_FORECAST_ID_511) {
      return "http://upload.wikimedia.org/wikipedia/commons/b/b8/Fresh_snow.JPG"
    } else if (weatherId in WEATHER_FORECAST_ID_520..WEATHER_FORECAST_ID_531) {
      return "http://upload.wikimedia.org/wikipedia/commons/6/6c/Rain-on-Thassos.jpg"
    } else if (weatherId in WEATHER_FORECAST_ID_600..WEATHER_FORECAST_ID_622) {
      return "http://upload.wikimedia.org/wikipedia/commons/b/b8/Fresh_snow.JPG"
    } else if (weatherId in WEATHER_FORECAST_ID_701..WEATHER_FORECAST_ID_761) {
      return "http://upload.wikimedia.org/wikipedia/commons/e/e6/Westminster_fog_-_London_-_UK.jpg"
    } else if (weatherId == WEATHER_FORECAST_ID_761 || weatherId == WEATHER_FORECAST_ID_781) {
      return "http://upload.wikimedia.org/wikipedia/commons/d/dc/Raised_dust_ahead_of_a_severe_thunderstorm_1.jpg"
    } else if (weatherId == WEATHER_FORECAST_ID_800) {
      return "http://upload.wikimedia.org/wikipedia/commons/7/7e/A_few_trees_and_the_sun_(6009964513).jpg"
    } else if (weatherId == WEATHER_FORECAST_ID_801) {
      return "http://upload.wikimedia.org/wikipedia/commons/e/e7/Cloudy_Blue_Sky_(5031259890).jpg"
    } else if (weatherId in WEATHER_FORECAST_ID_802..WEATHER_FORECAST_ID_804) {
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
