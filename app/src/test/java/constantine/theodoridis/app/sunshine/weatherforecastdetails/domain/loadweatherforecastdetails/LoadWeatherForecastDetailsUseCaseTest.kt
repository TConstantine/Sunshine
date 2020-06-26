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

package constantine.theodoridis.app.sunshine.weatherforecastdetails.domain.loadweatherforecastdetails

import constantine.theodoridis.app.sunshine.domain.entity.TemperatureUnit
import constantine.theodoridis.app.sunshine.domain.repository.WeatherForecastRepository
import constantine.theodoridis.app.sunshine.domain.usecase.UseCase
import constantine.theodoridis.app.sunshine.weatherforecastdetails.domain.entity.WeatherForecastBuilder
import constantine.theodoridis.app.sunshine.weatherforecastdetails.domain.repository.StringRepository
import constantine.theodoridis.app.sunshine.weatherforecastdetails.domain.repository.TemperatureUnitRepository
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnit

@RunWith(JUnitParamsRunner::class)
class LoadWeatherForecastDetailsUseCaseTest {
	companion object {
		private const val LOCATION = "London"
		private const val DATE_IN_MILLISECONDS = 946684800000L
		private val TODAY_IN_MILLISECONDS = System.currentTimeMillis()
		private const val DAY_IN_MILLISECONDS = 86400000L
		private val TOMORROW_IN_MILLISECONDS = TODAY_IN_MILLISECONDS + DAY_IN_MILLISECONDS
		private const val TODAY = "Today"
		private const val TOMORROW = "Tomorrow"
		private const val DAY_OF_WEEK = "Saturday"
		private const val MONTH = "January"
		private const val DAY_OF_MONTH = 1
		private const val ICON_DESCRIPTION_LABEL = "Icon description label"
		private const val FORECAST_DESCRIPTION_LABEL = "Forecast description label"
		private const val TEMPERATURE = 10.0F
		private const val MAX_TEMPERATURE_DESCRIPTION_LABEL = "Max temperature description label"
		private const val MIN_TEMPERATURE_DESCRIPTION_LABEL = "Min temperature description label"
		private const val PRESSURE_UNIT = "hPa"
		private const val WIND_SPEED = 5.0F
		private const val TRANSFORMED_IMPERIAL_WIND_SPEED = 3.0F
		private val WEATHER_FORECAST_REQUEST = LoadWeatherForecastDetailsRequestBuilder
				.withLocation(LOCATION)
				.withDate(DATE_IN_MILLISECONDS)
				.build()
		private val WEATHER_FORECAST = WeatherForecastBuilder.build()
		private const val WIND_SPEED_UNIT_METRIC = "km/h"
		private const val WIND_SPEED_UNIT_IMPERIAL = "mph"
		private const val TEMPERATURE_UNIT_IMPERIAL = "Imperial"
	}

	@Rule
	@JvmField
	val mockitoRule = MockitoJUnit.rule()!!

	@Mock
	private lateinit var mockWeatherForecastRepository: WeatherForecastRepository

	@Mock
	private lateinit var mockTemperatureUnitRepository: TemperatureUnitRepository

	@Mock
	private lateinit var mockStringRepository: StringRepository

	private lateinit var useCase: UseCase<LoadWeatherForecastDetailsRequest, LoadWeatherForecastDetailsResponse>

	@Before
	fun setUp() {
		`when`(mockStringRepository.getIconDescriptionLabel()).thenReturn(ICON_DESCRIPTION_LABEL)
		`when`(mockStringRepository.getForecastDescriptionLabel()).thenReturn(FORECAST_DESCRIPTION_LABEL)
		`when`(mockStringRepository.getMaxTemperatureDescriptionLabel()).thenReturn(MAX_TEMPERATURE_DESCRIPTION_LABEL)
		`when`(mockStringRepository.getMinTemperatureDescriptionLabel()).thenReturn(MIN_TEMPERATURE_DESCRIPTION_LABEL)
		`when`(mockStringRepository.getPressureUnit()).thenReturn(PRESSURE_UNIT)

		useCase = LoadWeatherForecastDetailsUseCase(
				mockWeatherForecastRepository,
				mockTemperatureUnitRepository,
				mockStringRepository
		)
	}

	@Test
	fun shouldTransformDayOfWeek_WhenDayIsToday() {
		`when`(mockWeatherForecastRepository.getWeatherForecast(anyString(), anyLong()))
				.thenReturn(WeatherForecastBuilder.withDate(TODAY_IN_MILLISECONDS).build())
		`when`(mockTemperatureUnitRepository.getTemperatureUnit()).thenReturn(TemperatureUnit.METRIC)
		`when`(mockStringRepository.getToday()).thenReturn(TODAY)
		`when`(mockStringRepository.getWindSpeedUnitMetric()).thenReturn(WIND_SPEED_UNIT_METRIC)

		val response = useCase.execute(WEATHER_FORECAST_REQUEST)

		assertThat(response.dayOfWeek, `is`(TODAY))
	}

	@Test
	fun shouldTransformDayOfWeek_WhenDayIsTomorrow() {
		`when`(mockWeatherForecastRepository.getWeatherForecast(anyString(), anyLong()))
				.thenReturn(WeatherForecastBuilder.withDate(TOMORROW_IN_MILLISECONDS).build())
		`when`(mockTemperatureUnitRepository.getTemperatureUnit()).thenReturn(TemperatureUnit.METRIC)
		`when`(mockStringRepository.getTomorrow()).thenReturn(TOMORROW)
		`when`(mockStringRepository.getWindSpeedUnitMetric()).thenReturn(WIND_SPEED_UNIT_METRIC)

		val response = useCase.execute(WEATHER_FORECAST_REQUEST)

		assertThat(response.dayOfWeek, `is`(TOMORROW))
	}

	@Test
	fun shouldTransformDayOfWeek_WhenDayIsNotTodayOrTomorrow() {
		`when`(mockWeatherForecastRepository.getWeatherForecast(anyString(), anyLong())).thenReturn(WeatherForecastBuilder
				.withDate(DATE_IN_MILLISECONDS)
				.build())
		`when`(mockTemperatureUnitRepository.getTemperatureUnit()).thenReturn(TemperatureUnit.METRIC)
		`when`(mockStringRepository.getWindSpeedUnitMetric()).thenReturn(WIND_SPEED_UNIT_METRIC)

		val response = useCase.execute(WEATHER_FORECAST_REQUEST)

		assertThat(response.dayOfWeek, `is`(DAY_OF_WEEK))
	}

	@Test
	fun shouldTransformMonth() {
		`when`(mockWeatherForecastRepository.getWeatherForecast(anyString(), anyLong())).thenReturn(WeatherForecastBuilder
				.withDate(DATE_IN_MILLISECONDS)
				.build())
		`when`(mockTemperatureUnitRepository.getTemperatureUnit()).thenReturn(TemperatureUnit.METRIC)
		`when`(mockStringRepository.getWindSpeedUnitMetric()).thenReturn(WIND_SPEED_UNIT_METRIC)

		val response = useCase.execute(WEATHER_FORECAST_REQUEST)

		assertThat(response.month, `is`(MONTH))
	}

	@Test
	fun shouldTransformDayOfMonth() {
		`when`(mockWeatherForecastRepository.getWeatherForecast(anyString(), anyLong())).thenReturn(WeatherForecastBuilder
				.withDate(DATE_IN_MILLISECONDS)
				.build())
		`when`(mockTemperatureUnitRepository.getTemperatureUnit()).thenReturn(TemperatureUnit.METRIC)
		`when`(mockStringRepository.getWindSpeedUnitMetric()).thenReturn(WIND_SPEED_UNIT_METRIC)

		val response = useCase.execute(WEATHER_FORECAST_REQUEST)

		assertThat(response.dayOfMonth, `is`(DAY_OF_MONTH))
	}

	@Test
	fun shouldNotTransformIconId() {
		`when`(mockWeatherForecastRepository.getWeatherForecast(anyString(), anyLong())).thenReturn(WEATHER_FORECAST)
		`when`(mockTemperatureUnitRepository.getTemperatureUnit()).thenReturn(TemperatureUnit.METRIC)
		`when`(mockStringRepository.getWindSpeedUnitMetric()).thenReturn(WIND_SPEED_UNIT_METRIC)

		val response = useCase.execute(WEATHER_FORECAST_REQUEST)

		assertThat(response.iconId, `is`(WEATHER_FORECAST.iconId))
	}

	@Test
	fun shouldNotTransformIconDescriptionLabel() {
		`when`(mockWeatherForecastRepository.getWeatherForecast(anyString(), anyLong())).thenReturn(WEATHER_FORECAST)
		`when`(mockTemperatureUnitRepository.getTemperatureUnit()).thenReturn(TemperatureUnit.METRIC)
		`when`(mockStringRepository.getWindSpeedUnitMetric()).thenReturn(WIND_SPEED_UNIT_METRIC)

		val response = useCase.execute(WEATHER_FORECAST_REQUEST)

		assertThat(response.iconDescriptionLabel, `is`(ICON_DESCRIPTION_LABEL))
	}

	@Test
	fun shouldNotTransformForecast() {
		`when`(mockWeatherForecastRepository.getWeatherForecast(anyString(), anyLong())).thenReturn(WEATHER_FORECAST)
		`when`(mockTemperatureUnitRepository.getTemperatureUnit()).thenReturn(TemperatureUnit.METRIC)
		`when`(mockStringRepository.getWindSpeedUnitMetric()).thenReturn(WIND_SPEED_UNIT_METRIC)

		val response = useCase.execute(WEATHER_FORECAST_REQUEST)

		assertThat(response.forecast, `is`(WEATHER_FORECAST.forecast))
	}

	@Test
	fun shouldNotTransformForecastDescriptionLabel() {
		`when`(mockWeatherForecastRepository.getWeatherForecast(anyString(), anyLong())).thenReturn(WEATHER_FORECAST)
		`when`(mockTemperatureUnitRepository.getTemperatureUnit()).thenReturn(TemperatureUnit.METRIC)
		`when`(mockStringRepository.getWindSpeedUnitMetric()).thenReturn(WIND_SPEED_UNIT_METRIC)

		val response = useCase.execute(WEATHER_FORECAST_REQUEST)

		assertThat(response.forecastDescriptionLabel, `is`(FORECAST_DESCRIPTION_LABEL))
	}

	@Test
	fun shouldTransformTemperature_whenTemperatureUnitIsMetric() {
		`when`(mockWeatherForecastRepository.getWeatherForecast(anyString(), anyLong())).thenReturn(WeatherForecastBuilder
				.withDate(DATE_IN_MILLISECONDS)
				.withMaxTemperature(TEMPERATURE)
				.withMinTemperature(TEMPERATURE)
				.build())
		`when`(mockTemperatureUnitRepository.getTemperatureUnit()).thenReturn(TemperatureUnit.METRIC)
		`when`(mockStringRepository.getWindSpeedUnitMetric()).thenReturn(WIND_SPEED_UNIT_METRIC)

		val response = useCase.execute(WEATHER_FORECAST_REQUEST)

		assertThat(response.maxTemperature, `is`(TEMPERATURE))
		assertThat(response.minTemperature, `is`(TEMPERATURE))
	}

	fun shouldTransformTemperature_whenTemperatureUnitIsImperial() {
		`when`(mockWeatherForecastRepository.getWeatherForecast(anyString(), anyLong())).thenReturn(WeatherForecastBuilder
				.withDate(DATE_IN_MILLISECONDS)
				.withMaxTemperature(TEMPERATURE)
				.withMinTemperature(TEMPERATURE)
				.build())
		`when`(mockTemperatureUnitRepository.getTemperatureUnit()).thenReturn(TemperatureUnit.IMPERIAL)
		`when`(mockStringRepository.getWindSpeedUnitMetric()).thenReturn(WIND_SPEED_UNIT_IMPERIAL)

		val response = useCase.execute(WEATHER_FORECAST_REQUEST)

		assertThat(response.maxTemperature, `is`(50.0F))
		assertThat(response.minTemperature, `is`(50.0F))
	}

	@Test
	fun shouldNotTransformMaxTemperatureDescriptionLabel() {
		`when`(mockWeatherForecastRepository.getWeatherForecast(anyString(), anyLong())).thenReturn(WEATHER_FORECAST)
		`when`(mockTemperatureUnitRepository.getTemperatureUnit()).thenReturn(TemperatureUnit.METRIC)
		`when`(mockStringRepository.getWindSpeedUnitMetric()).thenReturn(WIND_SPEED_UNIT_METRIC)

		val response = useCase.execute(WEATHER_FORECAST_REQUEST)

		assertThat(response.maxTemperatureDescriptionLabel, `is`(MAX_TEMPERATURE_DESCRIPTION_LABEL))
	}

	@Test
	fun shouldNotTransformMinTemperatureDescriptionLabel() {
		`when`(mockWeatherForecastRepository.getWeatherForecast(anyString(), anyLong())).thenReturn(WEATHER_FORECAST)
		`when`(mockTemperatureUnitRepository.getTemperatureUnit()).thenReturn(TemperatureUnit.METRIC)
		`when`(mockStringRepository.getWindSpeedUnitMetric()).thenReturn(WIND_SPEED_UNIT_METRIC)

		val response = useCase.execute(WEATHER_FORECAST_REQUEST)

		assertThat(response.minTemperatureDescriptionLabel, `is`(MIN_TEMPERATURE_DESCRIPTION_LABEL))
	}

	@Test
	fun shouldNotTransformHumidity() {
		`when`(mockWeatherForecastRepository.getWeatherForecast(anyString(), anyLong())).thenReturn(WEATHER_FORECAST)
		`when`(mockTemperatureUnitRepository.getTemperatureUnit()).thenReturn(TemperatureUnit.METRIC)
		`when`(mockStringRepository.getWindSpeedUnitMetric()).thenReturn(WIND_SPEED_UNIT_METRIC)

		val response = useCase.execute(WEATHER_FORECAST_REQUEST)

		assertThat(response.humidity, `is`(WEATHER_FORECAST.humidity))
	}

	@Test
	fun shouldNotTransformPressure() {
		`when`(mockWeatherForecastRepository.getWeatherForecast(anyString(), anyLong())).thenReturn(WEATHER_FORECAST)
		`when`(mockTemperatureUnitRepository.getTemperatureUnit()).thenReturn(TemperatureUnit.METRIC)
		`when`(mockStringRepository.getWindSpeedUnitMetric()).thenReturn(WIND_SPEED_UNIT_METRIC)

		val response = useCase.execute(WEATHER_FORECAST_REQUEST)

		assertThat(response.pressure, `is`(WEATHER_FORECAST.pressure))
	}

	@Test
	fun shouldNotTransformPressureUnit() {
		`when`(mockWeatherForecastRepository.getWeatherForecast(anyString(), anyLong())).thenReturn(WEATHER_FORECAST)
		`when`(mockTemperatureUnitRepository.getTemperatureUnit()).thenReturn(TemperatureUnit.METRIC)
		`when`(mockStringRepository.getWindSpeedUnitMetric()).thenReturn(WIND_SPEED_UNIT_METRIC)

		val response = useCase.execute(WEATHER_FORECAST_REQUEST)

		assertThat(response.pressureUnit, `is`(PRESSURE_UNIT))
	}

	@Test
	fun shouldMapWindSpeedUnit_whenTemperatureUnitIsMetric() {
		`when`(mockWeatherForecastRepository.getWeatherForecast(anyString(), anyLong())).thenReturn(WEATHER_FORECAST)
		`when`(mockTemperatureUnitRepository.getTemperatureUnit()).thenReturn(TemperatureUnit.METRIC)
		`when`(mockStringRepository.getWindSpeedUnitMetric()).thenReturn(WIND_SPEED_UNIT_METRIC)

		val response = useCase.execute(WEATHER_FORECAST_REQUEST)

		assertThat(response.windSpeedUnit, `is`(WIND_SPEED_UNIT_METRIC))
	}

	@Test
	fun shouldMapWindSpeedUnit_whenTemperatureUnitIsImperial() {
		`when`(mockWeatherForecastRepository.getWeatherForecast(anyString(), anyLong())).thenReturn(WeatherForecastBuilder
				.withDate(DATE_IN_MILLISECONDS)
				.build())
		`when`(mockTemperatureUnitRepository.getTemperatureUnit()).thenReturn(TemperatureUnit.IMPERIAL)
		`when`(mockStringRepository.getWindSpeedUnitImperial()).thenReturn(WIND_SPEED_UNIT_IMPERIAL)

		val response = useCase.execute(WEATHER_FORECAST_REQUEST)

		assertThat(response.windSpeedUnit, `is`(WIND_SPEED_UNIT_IMPERIAL))
	}

	@Test
	fun shouldNotTransformWindSpeed_whenTemperatureUnitIsMetric() {
		`when`(mockWeatherForecastRepository.getWeatherForecast(anyString(), anyLong())).thenReturn(WeatherForecastBuilder
				.withDate(DATE_IN_MILLISECONDS)
				.withWindSpeed(WIND_SPEED)
				.build())
		`when`(mockTemperatureUnitRepository.getTemperatureUnit()).thenReturn(TemperatureUnit.METRIC)
		`when`(mockStringRepository.getWindSpeedUnitMetric()).thenReturn(WIND_SPEED_UNIT_METRIC)

		val response = useCase.execute(WEATHER_FORECAST_REQUEST)

		assertThat(response.windSpeed, `is`(WIND_SPEED))
	}

	fun shouldTransformWindSpeed_whenTemperatureUnitIsImperial() {
		`when`(mockWeatherForecastRepository.getWeatherForecast(anyString(), anyLong())).thenReturn(WeatherForecastBuilder
				.withDate(DATE_IN_MILLISECONDS)
				.withWindSpeed(WIND_SPEED)
				.build())
		`when`(mockTemperatureUnitRepository.getTemperatureUnit()).thenReturn(TemperatureUnit.IMPERIAL)
		`when`(mockStringRepository.getWindSpeedUnitImperial()).thenReturn(TEMPERATURE_UNIT_IMPERIAL)

		val response = useCase.execute(WEATHER_FORECAST_REQUEST)

		assertThat(response.windSpeed, `is`(TRANSFORMED_IMPERIAL_WIND_SPEED))
	}

	@Test
	@Parameters(value = [
		"337.5, N",
		"338.0, N",
		"22.0, N",
		"22.5, NE",
		"67.0, NE",
		"67.5, E",
		"112.0, E",
		"112.5, SE",
		"157.0, SE",
		"157.5, S",
		"202.0, S",
		"202.5, SW",
		"247.0, SW",
		"247.5, W",
		"292.0, W",
		"292.5, NW",
		"337.0, NW",
		"337.0, NW"
	])
	fun shouldTransformWindDirection(windDegrees: Float, transformedWindDirection: String) {
		`when`(mockWeatherForecastRepository.getWeatherForecast(anyString(), anyLong())).thenReturn(WeatherForecastBuilder
				.withDate(DATE_IN_MILLISECONDS)
				.withWindDegrees(windDegrees)
				.build())
		`when`(mockTemperatureUnitRepository.getTemperatureUnit()).thenReturn(TemperatureUnit.METRIC)
		`when`(mockStringRepository.getWindSpeedUnitMetric()).thenReturn(WIND_SPEED_UNIT_METRIC)

		val response = useCase.execute(WEATHER_FORECAST_REQUEST)

		assertThat(response.windDirection, `is`(transformedWindDirection))
	}
}