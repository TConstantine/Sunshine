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
		return if (weatherId in 200..232 || weatherId == 761 || weatherId == 781) {
			R.drawable.art_storm
		} else if (weatherId in 300..321) {
			R.drawable.art_light_rain
		} else if (weatherId in 500..504 || weatherId in 520..531) {
			R.drawable.art_rain
		} else if (weatherId == 511 || weatherId in 600..622) {
			R.drawable.art_snow
		} else if (weatherId in 701..760) {
			R.drawable.art_fog
		} else if (weatherId == 800) {
			R.drawable.art_clear
		} else if (weatherId == 801) {
			R.drawable.art_light_clouds
		} else if (weatherId in 802..804) {
			R.drawable.art_clouds
		} else {
			-1
		}
	}

	private fun getResourceId(weatherId: Int): Int {
		return when (weatherId) {
			in 200..232 -> R.string.condition_2xx_and_960
			in 300..321 -> R.string.condition_3xx
			else -> when (weatherId) {
				500 -> R.string.condition_500
				501 -> R.string.condition_501
				502 -> R.string.condition_502
				503 -> R.string.condition_503
				504 -> R.string.condition_504
				511 -> R.string.condition_511
				520 -> R.string.condition_520
				531 -> R.string.condition_531
				600 -> R.string.condition_600
				601 -> R.string.condition_601
				602 -> R.string.condition_602
				611 -> R.string.condition_611
				612 -> R.string.condition_612
				615 -> R.string.condition_615_616
				616 -> R.string.condition_615_616
				620 -> R.string.condition_620_622
				621 -> R.string.condition_620_622
				622 -> R.string.condition_620_622
				701 -> R.string.condition_701
				711 -> R.string.condition_711
				721 -> R.string.condition_721
				731 -> R.string.condition_731
				741 -> R.string.condition_741
				751 -> R.string.condition_751
				761 -> R.string.condition_761
				762 -> R.string.condition_762
				771 -> R.string.condition_771
				781 -> R.string.condition_781_and_900
				800 -> R.string.condition_800
				801 -> R.string.condition_801
				802 -> R.string.condition_802
				803 -> R.string.condition_803
				804 -> R.string.condition_804
				900 -> R.string.condition_781_and_900
				901 -> R.string.condition_901
				902 -> R.string.condition_902_and_962
				903 -> R.string.condition_903
				904 -> R.string.condition_904
				905 -> R.string.condition_905
				906 -> R.string.condition_906
				951 -> R.string.condition_951
				952 -> R.string.condition_952
				953 -> R.string.condition_953
				954 -> R.string.condition_954
				955 -> R.string.condition_955
				956 -> R.string.condition_956
				957 -> R.string.condition_957
				958 -> R.string.condition_958
				959 -> R.string.condition_959
				960 -> R.string.condition_2xx_and_960
				961 -> R.string.condition_961
				962 -> R.string.condition_902_and_962
				else -> R.string.condition_unknown
			}
		}
	}
}