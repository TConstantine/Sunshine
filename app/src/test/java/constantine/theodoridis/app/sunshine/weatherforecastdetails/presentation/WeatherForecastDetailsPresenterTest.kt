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

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.any
import constantine.theodoridis.app.sunshine.domain.transform.Transformer
import constantine.theodoridis.app.sunshine.domain.usecase.UseCase
import constantine.theodoridis.app.sunshine.weatherforecastdetails.domain.loadweatherforecastdetails.LoadWeatherForecastDetailsRequest
import constantine.theodoridis.app.sunshine.weatherforecastdetails.domain.loadweatherforecastdetails.LoadWeatherForecastDetailsResponse
import constantine.theodoridis.app.sunshine.weatherforecastdetails.domain.loadweatherforecastdetails.LoadWeatherForecastDetailsResponseBuilder
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnit

class WeatherForecastDetailsPresenterTest {
	companion object {
		private const val LOCATION = "London"
		private const val DATE_IN_MILLISECONDS = 946684800000L
	}

	@Rule
	@JvmField
	val testMockitoRule = MockitoJUnit.rule()!!

	@Rule
	@JvmField
	val testExecutorRule = InstantTaskExecutorRule()

	@Mock
	private lateinit var mockObserver: Observer<WeatherForecastDetailsViewModel>

	@Mock
	private lateinit var mockUseCase: UseCase<LoadWeatherForecastDetailsRequest, LoadWeatherForecastDetailsResponse>

	@Mock
	private lateinit var mockTransformer: Transformer<LoadWeatherForecastDetailsResponse, WeatherForecastDetailsViewModel>

	private lateinit var presenter: WeatherForecastDetailsPresenter

	@Before
	fun setUp() {
		RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
		presenter = WeatherForecastDetailsPresenter(mockUseCase, mockTransformer, Schedulers.trampoline())
	}

	@After
	fun tearDown() {
		RxJavaPlugins.reset()
	}

	@Test
	fun shouldTransformResponseAndEmitViewModel() {
		presenter.weatherForecastDetails().observeForever(mockObserver)
		val viewModel = WeatherForecastDetailsViewModelBuilder().build()
		`when`(mockUseCase.execute(any())).thenReturn(LoadWeatherForecastDetailsResponseBuilder().build())
		`when`(mockTransformer.transform(any())).thenReturn(viewModel)

		presenter.loadWeatherForecastDetails(LOCATION, DATE_IN_MILLISECONDS)

		verify(mockObserver).onChanged(viewModel)
	}
}