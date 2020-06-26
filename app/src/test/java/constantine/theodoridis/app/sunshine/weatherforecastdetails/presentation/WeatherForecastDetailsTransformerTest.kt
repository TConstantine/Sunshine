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

package constantine.theodoridis.app.sunshine.weatherforecastdetails.presentation

import constantine.theodoridis.app.sunshine.domain.transform.Transformer
import constantine.theodoridis.app.sunshine.weatherforecastdetails.domain.loadweatherforecastdetails.LoadWeatherForecastDetailsResponse
import constantine.theodoridis.app.sunshine.weatherforecastdetails.domain.loadweatherforecastdetails.LoadWeatherForecastDetailsResponseBuilder
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

class WeatherForecastDetailsTransformerTest {
	companion object {
		private const val DAY_OF_WEEK = "Monday"
		private const val MONTH = "January"
		private const val DATE_OF_MONTH = 1
		private const val ICON_ID = 1
		private const val ICON_DESCRIPTION_LABEL = "Weather forecast icon:"
		private const val FORECAST = "Clear"
		private const val FORECAST_DESCRIPTION_LABEL = "Weather forecast:"
		private const val TEMPERATURE = 10.0F
		private const val TRANSFORMED_TEMPERATURE = "10°"
		private const val TEMPERATURE_DESCRIPTION_LABEL = "Temperature:"
		private const val HUMIDITY = 15.0F
		private const val TRANSFORMED_HUMIDITY = "15 %"
		private const val PRESSURE = 100.0F
		private const val PRESSURE_UNIT = "hPa"
		private const val TRANSFORMED_PRESSURE = "100 $PRESSURE_UNIT"
		private const val WIND_SPEED = 5.0F
		private const val WIND_SPEED_UNIT = "km/h"
		private const val WIND_DIRECTION = "N"
		private const val TRANSFORMED_WIND = "5 $WIND_SPEED_UNIT $WIND_DIRECTION"
	}

	private lateinit var responseBuilder: LoadWeatherForecastDetailsResponseBuilder

	private lateinit var transformer: Transformer<LoadWeatherForecastDetailsResponse, WeatherForecastDetailsViewModel>

	@Before
	fun setUp() {
		responseBuilder = LoadWeatherForecastDetailsResponseBuilder()
		transformer = WeatherForecastDetailsTransformer()
	}

	@Test
	fun shouldTransformDate() {
		val viewModel = transformer.transform(responseBuilder
				.withDayOfWeek(DAY_OF_WEEK)
				.withMonth(MONTH)
				.withDateOfMonth(DATE_OF_MONTH)
				.build())

		assertThat(viewModel.date, `is`("$DAY_OF_WEEK, $MONTH 0$DATE_OF_MONTH"))
	}

	@Test
	fun shouldNotTransformIconId() {
		val viewModel = transformer.transform(responseBuilder
				.withIconId(ICON_ID)
				.build())

		assertThat(viewModel.iconId, `is`(ICON_ID))
	}

	@Test
	fun shouldTransformIconDescription() {
		val viewModel = transformer.transform(responseBuilder
				.withIconDescriptionLabel(ICON_DESCRIPTION_LABEL)
				.withForecast(FORECAST)
				.build())

		assertThat(viewModel.iconDescription, `is`("$ICON_DESCRIPTION_LABEL $FORECAST"))
	}

	@Test
	fun shouldNotTransformForecast() {
		val viewModel = transformer.transform(responseBuilder
				.withForecast(FORECAST)
				.build())

		assertThat(viewModel.forecast, `is`(FORECAST))
	}

	@Test
	fun shouldTransformForecastDescription() {
		val viewModel = transformer.transform(responseBuilder
				.withForecast(FORECAST)
				.withForecastDescriptionLabel(FORECAST_DESCRIPTION_LABEL)
				.build())

		assertThat(viewModel.forecastDescription, `is`("$FORECAST_DESCRIPTION_LABEL $FORECAST"))
	}

	@Test
	fun shouldTransformTemperature() {
		val viewModel = transformer.transform(responseBuilder
				.withTemperature(TEMPERATURE)
				.build())

		assertThat(viewModel.minTemperature, `is`(TRANSFORMED_TEMPERATURE))
		assertThat(viewModel.maxTemperature, `is`(TRANSFORMED_TEMPERATURE))
	}

	@Test
	fun shouldTransformTemperatureDescriptions() {
		val viewModel = transformer.transform(responseBuilder
				.withTemperature(TEMPERATURE)
				.withTemperatureDescriptionLabel(TEMPERATURE_DESCRIPTION_LABEL)
				.build())

		assertThat(viewModel.minTemperatureDescription, `is`("$TEMPERATURE_DESCRIPTION_LABEL $TRANSFORMED_TEMPERATURE"))
		assertThat(viewModel.maxTemperatureDescription, `is`("$TEMPERATURE_DESCRIPTION_LABEL $TRANSFORMED_TEMPERATURE"))
	}

	@Test
	fun shouldTransformHumidity() {
		val viewModel = transformer.transform(responseBuilder
				.withHumidity(HUMIDITY)
				.build())

		assertThat(viewModel.humidity, `is`(TRANSFORMED_HUMIDITY))
	}

	@Test
	fun shouldTransformPressure() {
		val viewModel = transformer.transform(responseBuilder
				.withPressure(PRESSURE)
				.withPressureUnit(PRESSURE_UNIT)
				.build())

		assertThat(viewModel.pressure, `is`(TRANSFORMED_PRESSURE))
	}

	@Test
	fun shouldTransformWind() {
		val viewModel = transformer.transform(responseBuilder
				.withWindSpeed(WIND_SPEED)
				.withWindSpeedUnit(WIND_SPEED_UNIT)
				.withWindDirection(WIND_DIRECTION)
				.build())

		assertThat(viewModel.wind, `is`(TRANSFORMED_WIND))
	}
}