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

package constantine.theodoridis.app.sunshine.weatherforecastdetails

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import constantine.theodoridis.app.sunshine.R
import constantine.theodoridis.app.sunshine.data.database.DatabaseDataSource
import constantine.theodoridis.app.sunshine.data.database.model.WeatherForecastDatabaseModelBuilder
import constantine.theodoridis.app.sunshine.data.datasource.PreferenceDataSource
import constantine.theodoridis.app.sunshine.data.datasource.ResourceDataSource
import constantine.theodoridis.app.sunshine.data.repository.WeatherForecastDepository
import constantine.theodoridis.app.sunshine.weatherforecastdetails.data.repository.StringDepository
import constantine.theodoridis.app.sunshine.weatherforecastdetails.data.repository.TemperatureUnitDepository
import constantine.theodoridis.app.sunshine.weatherforecastdetails.domain.loadweatherforecastdetails.LoadWeatherForecastDetailsUseCase
import constantine.theodoridis.app.sunshine.weatherforecastdetails.presentation.WeatherForecastDetailsPresenter
import constantine.theodoridis.app.sunshine.weatherforecastdetails.presentation.WeatherForecastDetailsTransformer
import constantine.theodoridis.app.sunshine.weatherforecastdetails.presentation.WeatherForecastDetailsViewModel
import constantine.theodoridis.app.sunshine.weatherforecastdetails.presentation.WeatherForecastDetailsViewModelBuilder
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit

class WeatherForecastDisplayTest {
	companion object {
		private const val LOCATION = "London"
		private const val DATE_IN_MILLISECONDS = 946684800000L
		private const val WEATHER_ID = 800
		private const val FORECAST = "Clear"
		private const val FORECAST_LABEL = "Weather forecast:"
		private const val MAX_TEMPERATURE = 24.0F
		private const val MAX_TEMPERATURE_LABEL = "High:"
		private const val MIN_TEMPERATURE = 13.0F
		private const val MIN_TEMPERATURE_LABEL = "Low:"
		private const val HUMIDITY = 27.0F
		private const val PRESSURE = 1018.0F
		private const val PRESSURE_UNIT = "hPa"
		private const val WIND_SPEED = 5.0F
		private const val WIND_SPEED_UNIT = "km/h"
		private const val WIND_DEGREES = 292.5F
		private const val DIRECTION = "NW"
		private const val TEMPERATURE_UNIT_KEY = "temperature_unit_key"
		private const val DEFAULT_TEMPERATURE_UNIT = "metric"
		private const val TEMPERATURE_UNIT_METRIC = "metric"
		private const val ICON_LABEL = "Weather forecast icon:"
		private val WEATHER_FORECAST_DATABASE_MODEL = WeatherForecastDatabaseModelBuilder
				.withDate(DATE_IN_MILLISECONDS)
				.withWeatherId(WEATHER_ID)
				.withMaxTemperature(MAX_TEMPERATURE)
				.withMinTemperature(MIN_TEMPERATURE)
				.withHumidity(HUMIDITY)
				.withPressure(PRESSURE)
				.withWindSpeed(WIND_SPEED)
				.withWindDegrees(WIND_DEGREES)
				.build()
		private val WEATHER_FORECAST_VIEW_MODEL = WeatherForecastDetailsViewModelBuilder()
				.withDate("Saturday, January 01")
				.withIconId(R.drawable.art_clear)
				.withIconDescription("$ICON_LABEL $FORECAST")
				.withForecast(FORECAST)
				.withForecastDescription("$FORECAST_LABEL $FORECAST")
				.withMaxTemperature("24°")
				.withMaxTemperatureDescription("$MAX_TEMPERATURE_LABEL 24°")
				.withMinTemperature("13°")
				.withMinTemperatureDescription("$MIN_TEMPERATURE_LABEL 13°")
				.withHumidity("27 %")
				.withPressure("1018 $PRESSURE_UNIT")
				.withWind("5 $WIND_SPEED_UNIT $DIRECTION")
				.build()
	}

	@Rule
	@JvmField
	val mockitoRule = MockitoJUnit.rule()!!

	@Rule
	@JvmField
	val testExecutorRule = InstantTaskExecutorRule()

	@Mock
	private lateinit var mockObserver: Observer<WeatherForecastDetailsViewModel>

	@Mock
	private lateinit var mockDatabaseDataSource: DatabaseDataSource

	@Mock
	private lateinit var mockResourceDataSource: ResourceDataSource

	@Mock
	private lateinit var mockPreferenceDataSource: PreferenceDataSource

	private lateinit var presenter: WeatherForecastDetailsPresenter

	@Before
	fun setUp() {
		val weatherForecastRepository = WeatherForecastDepository(
				mockDatabaseDataSource,
				mockResourceDataSource
		)
		val temperatureUnitRepository = TemperatureUnitDepository(
				mockResourceDataSource,
				mockPreferenceDataSource
		)
		val stringRepository = StringDepository(mockResourceDataSource)
		val useCase = LoadWeatherForecastDetailsUseCase(
				weatherForecastRepository,
				temperatureUnitRepository,
				stringRepository
		)
		val transformer = WeatherForecastDetailsTransformer()
		presenter = WeatherForecastDetailsPresenter(
				useCase,
				transformer,
				Schedulers.trampoline()
		)
		RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
	}

	@After
	fun cleanUp() {
		RxJavaPlugins.reset()
	}

	@Test
	fun shouldEmitViewModel() {
		presenter.weatherForecastDetails().observeForever(mockObserver)
		`when`(mockResourceDataSource.getString(anyInt()))
				.thenReturn(FORECAST)
				.thenReturn(TEMPERATURE_UNIT_KEY)
				.thenReturn(DEFAULT_TEMPERATURE_UNIT)
				.thenReturn(ICON_LABEL)
				.thenReturn(FORECAST_LABEL)
				.thenReturn(MAX_TEMPERATURE_LABEL)
				.thenReturn(MIN_TEMPERATURE_LABEL)
				.thenReturn(PRESSURE_UNIT)
				.thenReturn(WIND_SPEED_UNIT)
		`when`(mockPreferenceDataSource.getString(anyString(), anyString())).thenReturn(TEMPERATURE_UNIT_METRIC)
		`when`(mockDatabaseDataSource.getWeatherForecast(anyString(), anyLong())).thenReturn(WEATHER_FORECAST_DATABASE_MODEL)

		presenter.loadWeatherForecastDetails(LOCATION, DATE_IN_MILLISECONDS)

		val inOrder = inOrder(mockResourceDataSource, mockPreferenceDataSource, mockDatabaseDataSource)
		inOrder.verify(mockDatabaseDataSource).getWeatherForecast(LOCATION, DATE_IN_MILLISECONDS)
		inOrder.verify(mockResourceDataSource).getString(R.string.condition_800)
		inOrder.verify(mockResourceDataSource).getString(R.string.preference_temperature_unit_key)
		inOrder.verify(mockResourceDataSource).getString(R.string.temperature_unit_metric)
		inOrder.verify(mockPreferenceDataSource).getString(TEMPERATURE_UNIT_KEY, DEFAULT_TEMPERATURE_UNIT)
		inOrder.verify(mockResourceDataSource).getString(R.string.icon_description_label)
		inOrder.verify(mockResourceDataSource).getString(R.string.forecast_description_label)
		inOrder.verify(mockResourceDataSource).getString(R.string.max_temperature_description_label)
		inOrder.verify(mockResourceDataSource).getString(R.string.min_temperature_description_label)
		inOrder.verify(mockResourceDataSource).getString(R.string.pressure_unit)
		inOrder.verify(mockResourceDataSource).getString(R.string.wind_speed_unit_metric)
		verify(mockObserver).onChanged(WEATHER_FORECAST_VIEW_MODEL)
	}
}