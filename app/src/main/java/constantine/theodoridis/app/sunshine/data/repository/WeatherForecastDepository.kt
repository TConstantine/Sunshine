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

package constantine.theodoridis.app.sunshine.data.repository

import constantine.theodoridis.app.sunshine.R
import constantine.theodoridis.app.sunshine.data.database.DatabaseDataSource
import constantine.theodoridis.app.sunshine.data.datasource.ResourceDataSource
import constantine.theodoridis.app.sunshine.domain.entity.WeatherForecast
import constantine.theodoridis.app.sunshine.domain.repository.WeatherForecastRepository

class WeatherForecastDepository(
  private val databaseDataSource: DatabaseDataSource,
  private val resourceDataSource: ResourceDataSource
) : WeatherForecastRepository {
  companion object {
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
    private const val WEATHER_FORECAST_ID_760 = 760
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
  }
  
  override fun getWeatherForecast(location: String, date: Long): WeatherForecast {
    val databaseModel = databaseDataSource.getWeatherForecast(location, date)
    return WeatherForecast(
      dateInMilliseconds = databaseModel.dateInMilliseconds,
      iconId = getIconId(databaseModel.weatherId),
      forecast = resourceDataSource.getString(getResourceId(databaseModel.weatherId)),
      maxTemperature = databaseModel.maxTemperature,
      minTemperature = databaseModel.minTemperature,
      humidity = databaseModel.humidity,
      pressure = databaseModel.pressure,
      windSpeed = databaseModel.windSpeed,
      windDegrees = databaseModel.windDegrees
    )
  }
  
  private fun getIconId(weatherId: Int): Int {
    return if (weatherId in WEATHER_FORECAST_ID_200..WEATHER_FORECAST_ID_232 ||
      weatherId == WEATHER_FORECAST_ID_761 ||
      weatherId == WEATHER_FORECAST_ID_781) {
      R.drawable.art_storm
    } else if (weatherId in WEATHER_FORECAST_ID_300..WEATHER_FORECAST_ID_321) {
      R.drawable.art_light_rain
    } else if (weatherId in WEATHER_FORECAST_ID_500..WEATHER_FORECAST_ID_504 ||
      weatherId in WEATHER_FORECAST_ID_520..WEATHER_FORECAST_ID_531) {
      R.drawable.art_rain
    } else if (weatherId == WEATHER_FORECAST_ID_511 ||
      weatherId in WEATHER_FORECAST_ID_600..WEATHER_FORECAST_ID_622) {
      R.drawable.art_snow
    } else if (weatherId in WEATHER_FORECAST_ID_701..WEATHER_FORECAST_ID_760) {
      R.drawable.art_fog
    } else if (weatherId == WEATHER_FORECAST_ID_800) {
      R.drawable.art_clear
    } else if (weatherId == WEATHER_FORECAST_ID_801) {
      R.drawable.art_light_clouds
    } else if (weatherId in WEATHER_FORECAST_ID_802..WEATHER_FORECAST_ID_804) {
      R.drawable.art_clouds
    } else {
      -1
    }
  }
  
  private fun getResourceId(weatherId: Int): Int {
    return when (weatherId) {
      in WEATHER_FORECAST_ID_200..WEATHER_FORECAST_ID_232 -> R.string.condition_2xx_and_960
      in WEATHER_FORECAST_ID_300..WEATHER_FORECAST_ID_321 -> R.string.condition_3xx
      else -> when (weatherId) {
        WEATHER_FORECAST_ID_500 -> R.string.condition_500
        WEATHER_FORECAST_ID_501 -> R.string.condition_501
        WEATHER_FORECAST_ID_502 -> R.string.condition_502
        WEATHER_FORECAST_ID_503 -> R.string.condition_503
        WEATHER_FORECAST_ID_504 -> R.string.condition_504
        WEATHER_FORECAST_ID_511 -> R.string.condition_511
        WEATHER_FORECAST_ID_520 -> R.string.condition_520
        WEATHER_FORECAST_ID_531 -> R.string.condition_531
        WEATHER_FORECAST_ID_600 -> R.string.condition_600
        WEATHER_FORECAST_ID_601 -> R.string.condition_601
        WEATHER_FORECAST_ID_602 -> R.string.condition_602
        WEATHER_FORECAST_ID_611 -> R.string.condition_611
        WEATHER_FORECAST_ID_612 -> R.string.condition_612
        WEATHER_FORECAST_ID_615 -> R.string.condition_615_616
        WEATHER_FORECAST_ID_616 -> R.string.condition_615_616
        WEATHER_FORECAST_ID_620 -> R.string.condition_620_622
        WEATHER_FORECAST_ID_621 -> R.string.condition_620_622
        WEATHER_FORECAST_ID_622 -> R.string.condition_620_622
        WEATHER_FORECAST_ID_701 -> R.string.condition_701
        WEATHER_FORECAST_ID_711 -> R.string.condition_711
        WEATHER_FORECAST_ID_721 -> R.string.condition_721
        WEATHER_FORECAST_ID_731 -> R.string.condition_731
        WEATHER_FORECAST_ID_741 -> R.string.condition_741
        WEATHER_FORECAST_ID_751 -> R.string.condition_751
        WEATHER_FORECAST_ID_761 -> R.string.condition_761
        WEATHER_FORECAST_ID_762 -> R.string.condition_762
        WEATHER_FORECAST_ID_771 -> R.string.condition_771
        WEATHER_FORECAST_ID_781 -> R.string.condition_781_and_900
        WEATHER_FORECAST_ID_800 -> R.string.condition_800
        WEATHER_FORECAST_ID_801 -> R.string.condition_801
        WEATHER_FORECAST_ID_802 -> R.string.condition_802
        WEATHER_FORECAST_ID_803 -> R.string.condition_803
        WEATHER_FORECAST_ID_804 -> R.string.condition_804
        WEATHER_FORECAST_ID_900 -> R.string.condition_781_and_900
        WEATHER_FORECAST_ID_901 -> R.string.condition_901
        WEATHER_FORECAST_ID_902 -> R.string.condition_902_and_962
        WEATHER_FORECAST_ID_903 -> R.string.condition_903
        WEATHER_FORECAST_ID_904 -> R.string.condition_904
        WEATHER_FORECAST_ID_905 -> R.string.condition_905
        WEATHER_FORECAST_ID_906 -> R.string.condition_906
        WEATHER_FORECAST_ID_951 -> R.string.condition_951
        WEATHER_FORECAST_ID_952 -> R.string.condition_952
        WEATHER_FORECAST_ID_953 -> R.string.condition_953
        WEATHER_FORECAST_ID_954 -> R.string.condition_954
        WEATHER_FORECAST_ID_955 -> R.string.condition_955
        WEATHER_FORECAST_ID_956 -> R.string.condition_956
        WEATHER_FORECAST_ID_957 -> R.string.condition_957
        WEATHER_FORECAST_ID_958 -> R.string.condition_958
        WEATHER_FORECAST_ID_959 -> R.string.condition_959
        WEATHER_FORECAST_ID_960 -> R.string.condition_2xx_and_960
        WEATHER_FORECAST_ID_961 -> R.string.condition_961
        WEATHER_FORECAST_ID_962 -> R.string.condition_902_and_962
        else -> R.string.condition_unknown
      }
    }
  }
}
