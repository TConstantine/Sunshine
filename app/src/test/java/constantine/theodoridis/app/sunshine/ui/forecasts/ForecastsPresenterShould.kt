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

package constantine.theodoridis.app.sunshine.ui.forecasts

import constantine.theodoridis.app.sunshine.MockitoTest
import constantine.theodoridis.app.sunshine.domain.usecases.GetGeoLocationUseCase
import io.reactivex.plugins.RxJavaPlugins.reset
import io.reactivex.plugins.RxJavaPlugins.setIoSchedulerHandler
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify

class ForecastsPresenterShould: MockitoTest() {
	@Mock
	private lateinit var view: ForecastsContract.View

	@Mock
	private lateinit var getGeoLocationUseCase: GetGeoLocationUseCase

	private lateinit var presenter: ForecastsContract.Presenter

	@Before
	fun setUp() {
		presenter = ForecastsPresenter(view, getGeoLocationUseCase, Schedulers.trampoline())
		setIoSchedulerHandler { Schedulers.trampoline() }
	}

	@After
	fun cleanUp() {
		reset()
	}

	@Test
	fun display_location_in_map_when_location_is_valid() {
		val geoLocation = "geo-location"
		`when`(getGeoLocationUseCase.getGeoLocation()).thenReturn(geoLocation)

		presenter.onDisplayMapLocation()

		verify(view).displayMapLocation(geoLocation)
	}

	@Test
	fun display_error_when_location_is_invalid() {
		val geoLocation = ""
		`when`(getGeoLocationUseCase.getGeoLocation()).thenReturn(geoLocation)

		presenter.onDisplayMapLocation()

		verify(view).displayInvalidLocationError()
	}
}