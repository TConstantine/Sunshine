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
import constantine.theodoridis.app.sunshine.data.database.model.WeatherForecastDatabaseModelBuilder
import constantine.theodoridis.app.sunshine.data.datasource.ResourceDataSource
import constantine.theodoridis.app.sunshine.domain.repository.WeatherForecastRepository
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.*
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnit

@RunWith(JUnitParamsRunner::class)
class WeatherForecastDepositoryTest {
	companion object {
		private const val LOCATION = "London"
		private const val DATE_IN_MILLISECONDS = 946684800000L
		private const val FORECAST = "Forecast"
		private const val MAX_TEMPERATURE = 20.0F
		private const val MIN_TEMPERATURE = 10.0F
		private const val HUMIDITY = 5.0F
		private const val PRESSURE = 15.0F
		private const val WIND_SPEED = 25.0F
		private const val WIND_DEGREES = 200.0F
	}

	@Rule
	@JvmField
	val mockitoRule = MockitoJUnit.rule()!!

	@Mock
	private lateinit var mockDatabaseDataSource: DatabaseDataSource

	@Mock
	private lateinit var mockResourceDataSource: ResourceDataSource

	private lateinit var repository: WeatherForecastRepository

	@Before
	fun setUp() {
		repository = WeatherForecastDepository(
				mockDatabaseDataSource,
				mockResourceDataSource
		)
	}

	@Test
	fun shouldMapDate() {
		val databaseModel = WeatherForecastDatabaseModelBuilder
				.withDate(DATE_IN_MILLISECONDS)
				.build()
		`when`(mockDatabaseDataSource.getWeatherForecast(anyString(), anyLong())).thenReturn(databaseModel)
		`when`(mockResourceDataSource.getString(anyInt())).thenReturn(FORECAST)

		val weatherForecast = repository.getWeatherForecast(LOCATION, DATE_IN_MILLISECONDS)

		assertThat(weatherForecast.dateInMilliseconds, `is`(databaseModel.dateInMilliseconds))
	}

	@Test
	@Parameters
	fun shouldMapIconId(weatherId: Int, resourceId: Int) {
		val databaseModel = WeatherForecastDatabaseModelBuilder
				.withWeatherId(weatherId)
				.build()
		`when`(mockDatabaseDataSource.getWeatherForecast(anyString(), anyLong())).thenReturn(databaseModel)
		`when`(mockResourceDataSource.getString(anyInt())).thenReturn(FORECAST)

		val weatherForecast = repository.getWeatherForecast(LOCATION, DATE_IN_MILLISECONDS)

		assertThat(weatherForecast.iconId, `is`(resourceId))
	}

	private fun parametersForShouldMapIconId(): Any {
		return arrayOf<Any>(
				arrayOf<Any>(200, R.drawable.art_storm),
				arrayOf<Any>(210, R.drawable.art_storm),
				arrayOf<Any>(231, R.drawable.art_storm),
				arrayOf<Any>(300, R.drawable.art_light_rain),
				arrayOf<Any>(310, R.drawable.art_light_rain),
				arrayOf<Any>(320, R.drawable.art_light_rain),
				arrayOf<Any>(500, R.drawable.art_rain),
				arrayOf<Any>(502, R.drawable.art_rain),
				arrayOf<Any>(503, R.drawable.art_rain),
				arrayOf<Any>(511, R.drawable.art_snow),
				arrayOf<Any>(520, R.drawable.art_rain),
				arrayOf<Any>(525, R.drawable.art_rain),
				arrayOf<Any>(530, R.drawable.art_rain),
				arrayOf<Any>(600, R.drawable.art_snow),
				arrayOf<Any>(610, R.drawable.art_snow),
				arrayOf<Any>(621, R.drawable.art_snow),
				arrayOf<Any>(701, R.drawable.art_fog),
				arrayOf<Any>(730, R.drawable.art_fog),
				arrayOf<Any>(760, R.drawable.art_fog),
				arrayOf<Any>(761, R.drawable.art_storm),
				arrayOf<Any>(781, R.drawable.art_storm),
				arrayOf<Any>(800, R.drawable.art_clear),
				arrayOf<Any>(801, R.drawable.art_light_clouds),
				arrayOf<Any>(802, R.drawable.art_clouds),
				arrayOf<Any>(803, R.drawable.art_clouds),
				arrayOf<Any>(1000, -1)
		)
	}

	@Test
	@Parameters
	fun shouldTransformForecast(weatherId: Int, resourceId: Int, forecast: String) {
		val databaseModel = WeatherForecastDatabaseModelBuilder
				.withWeatherId(weatherId)
				.build()
		`when`(mockDatabaseDataSource.getWeatherForecast(anyString(), anyLong())).thenReturn(databaseModel)
		`when`(mockResourceDataSource.getString(anyInt())).thenReturn(forecast)

		val weatherForecast = repository.getWeatherForecast(LOCATION, DATE_IN_MILLISECONDS)

		verify(mockResourceDataSource).getString(resourceId)
		assertThat(weatherForecast.forecast, `is`(forecast))
	}

	private fun parametersForShouldTransformForecast(): Any {
		return arrayOf<Any>(
				arrayOf<Any>(200, R.string.condition_2xx_and_960, "Storm"),
				arrayOf<Any>(210, R.string.condition_2xx_and_960, "Storm"),
				arrayOf<Any>(232, R.string.condition_2xx_and_960, "Storm"),
				arrayOf<Any>(300, R.string.condition_3xx, "Drizzle"),
				arrayOf<Any>(310, R.string.condition_3xx, "Drizzle"),
				arrayOf<Any>(321, R.string.condition_3xx, "Drizzle"),
				arrayOf<Any>(500, R.string.condition_500, "Light Rain"),
				arrayOf<Any>(501, R.string.condition_501, "Moderate Rain"),
				arrayOf<Any>(502, R.string.condition_502, "Heavy Rain"),
				arrayOf<Any>(503, R.string.condition_503, "Intense Rain"),
				arrayOf<Any>(511, R.string.condition_511, "Freezing Rain"),
				arrayOf<Any>(520, R.string.condition_520, "Light Shower"),
				arrayOf<Any>(531, R.string.condition_531, "Ragged Shower"),
				arrayOf<Any>(600, R.string.condition_600, "Light Snow"),
				arrayOf<Any>(601, R.string.condition_601, "Snow"),
				arrayOf<Any>(602, R.string.condition_602, "Heavy Snow"),
				arrayOf<Any>(611, R.string.condition_611, "Sleet"),
				arrayOf<Any>(612, R.string.condition_612, "Shower Sleet"),
				arrayOf<Any>(615, R.string.condition_615_616, "Rain and Snow"),
				arrayOf<Any>(616, R.string.condition_615_616, "Rain and Snow"),
				arrayOf<Any>(620, R.string.condition_620_622, "Shower Snow"),
				arrayOf<Any>(621, R.string.condition_620_622, "Shower Snow"),
				arrayOf<Any>(622, R.string.condition_620_622, "Shower Snow"),
				arrayOf<Any>(701, R.string.condition_701, "Mist"),
				arrayOf<Any>(711, R.string.condition_711, "Smoke"),
				arrayOf<Any>(721, R.string.condition_721, "Haze"),
				arrayOf<Any>(731, R.string.condition_731, "Sand, Dust"),
				arrayOf<Any>(741, R.string.condition_741, "Fog"),
				arrayOf<Any>(751, R.string.condition_751, "Sand"),
				arrayOf<Any>(761, R.string.condition_761, "Dust"),
				arrayOf<Any>(762, R.string.condition_762, "Volcanic Ash"),
				arrayOf<Any>(771, R.string.condition_771, "Squalls"),
				arrayOf<Any>(781, R.string.condition_781_and_900, "Tornado"),
				arrayOf<Any>(800, R.string.condition_800, "Clear"),
				arrayOf<Any>(801, R.string.condition_801, "Mostly Clear"),
				arrayOf<Any>(802, R.string.condition_802, "Scattered Clouds"),
				arrayOf<Any>(803, R.string.condition_803, "Broken Clouds"),
				arrayOf<Any>(804, R.string.condition_804, "Overcast Clouds"),
				arrayOf<Any>(804, R.string.condition_804, "Overcast Clouds"),
				arrayOf<Any>(900, R.string.condition_781_and_900, "Tornado"),
				arrayOf<Any>(901, R.string.condition_901, "Tropical Storm"),
				arrayOf<Any>(902, R.string.condition_902_and_962, "Hurricane"),
				arrayOf<Any>(903, R.string.condition_903, "Cold"),
				arrayOf<Any>(904, R.string.condition_904, "Hot"),
				arrayOf<Any>(905, R.string.condition_905, "Windy"),
				arrayOf<Any>(906, R.string.condition_906, "Hail"),
				arrayOf<Any>(951, R.string.condition_951, "Calm"),
				arrayOf<Any>(952, R.string.condition_952, "Light Breeze"),
				arrayOf<Any>(953, R.string.condition_953, "Gentle Breeze"),
				arrayOf<Any>(954, R.string.condition_954, "Breeze"),
				arrayOf<Any>(955, R.string.condition_955, "Fresh Breeze"),
				arrayOf<Any>(956, R.string.condition_956, "Strong Breeze"),
				arrayOf<Any>(957, R.string.condition_957, "High Wind"),
				arrayOf<Any>(958, R.string.condition_958, "Gale"),
				arrayOf<Any>(959, R.string.condition_959, "Severe Gale"),
				arrayOf<Any>(959, R.string.condition_959, "Severe Gale"),
				arrayOf<Any>(960, R.string.condition_2xx_and_960, "Storm"),
				arrayOf<Any>(961, R.string.condition_961, "Violent Storm"),
				arrayOf<Any>(962, R.string.condition_902_and_962, "Hurricane"),
				arrayOf<Any>(1000, R.string.condition_unknown, "Unknown 1000")
		)
	}

	@Test
	fun shouldMapMaxTemperature() {
		val databaseModel = WeatherForecastDatabaseModelBuilder
				.withMaxTemperature(MAX_TEMPERATURE)
				.build()
		`when`(mockDatabaseDataSource.getWeatherForecast(anyString(), anyLong())).thenReturn(databaseModel)
		`when`(mockResourceDataSource.getString(anyInt())).thenReturn(FORECAST)

		val weatherForecast = repository.getWeatherForecast(LOCATION, DATE_IN_MILLISECONDS)

		assertThat(weatherForecast.maxTemperature, `is`(databaseModel.maxTemperature))
	}

	@Test
	fun shouldMapMinTemperature() {
		val databaseModel = WeatherForecastDatabaseModelBuilder
				.withMinTemperature(MIN_TEMPERATURE)
				.build()
		`when`(mockDatabaseDataSource.getWeatherForecast(anyString(), anyLong())).thenReturn(databaseModel)
		`when`(mockResourceDataSource.getString(anyInt())).thenReturn(FORECAST)

		val weatherForecast = repository.getWeatherForecast(LOCATION, DATE_IN_MILLISECONDS)

		assertThat(weatherForecast.minTemperature, `is`(databaseModel.minTemperature))
	}

	@Test
	fun shouldMapHumidity() {
		val databaseModel = WeatherForecastDatabaseModelBuilder
				.withHumidity(HUMIDITY)
				.build()
		`when`(mockDatabaseDataSource.getWeatherForecast(anyString(), anyLong())).thenReturn(databaseModel)
		`when`(mockResourceDataSource.getString(anyInt())).thenReturn(FORECAST)

		val weatherForecast = repository.getWeatherForecast(LOCATION, DATE_IN_MILLISECONDS)

		assertThat(weatherForecast.humidity, `is`(databaseModel.humidity))
	}

	@Test
	fun shouldMapPressure() {
		val databaseModel = WeatherForecastDatabaseModelBuilder
				.withPressure(PRESSURE)
				.build()
		`when`(mockDatabaseDataSource.getWeatherForecast(anyString(), anyLong())).thenReturn(databaseModel)
		`when`(mockResourceDataSource.getString(anyInt())).thenReturn(FORECAST)

		val weatherForecast = repository.getWeatherForecast(LOCATION, DATE_IN_MILLISECONDS)

		assertThat(weatherForecast.pressure, `is`(databaseModel.pressure))
	}

	@Test
	fun shouldMapWindSpeed() {
		val databaseModel = WeatherForecastDatabaseModelBuilder
				.withWindSpeed(WIND_SPEED)
				.build()
		`when`(mockDatabaseDataSource.getWeatherForecast(anyString(), anyLong())).thenReturn(databaseModel)
		`when`(mockResourceDataSource.getString(anyInt())).thenReturn(FORECAST)

		val weatherForecast = repository.getWeatherForecast(LOCATION, DATE_IN_MILLISECONDS)

		assertThat(weatherForecast.windSpeed, `is`(databaseModel.windSpeed))
	}

	@Test
	fun shouldMapWindDegrees() {
		val databaseModel = WeatherForecastDatabaseModelBuilder
				.withWindDegrees(WIND_DEGREES)
				.build()
		`when`(mockDatabaseDataSource.getWeatherForecast(anyString(), anyLong())).thenReturn(databaseModel)
		`when`(mockResourceDataSource.getString(anyInt())).thenReturn(FORECAST)

		val weatherForecast = repository.getWeatherForecast(LOCATION, DATE_IN_MILLISECONDS)

		assertThat(weatherForecast.windDegrees, `is`(databaseModel.windDegrees))
	}
}