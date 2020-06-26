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

package constantine.theodoridis.app.sunshine.domain.usecase

import constantine.theodoridis.app.sunshine.R
import constantine.theodoridis.app.sunshine.data.datasource.PreferenceDataSource
import constantine.theodoridis.app.sunshine.data.datasource.ResourceDataSource
import constantine.theodoridis.app.sunshine.data.model.Location
import constantine.theodoridis.app.sunshine.domain.repository.LocationRepository
import constantine.theodoridis.app.sunshine.domain.usecase.GetGeoLocation.Companion.COMMA_SEPARATOR
import constantine.theodoridis.app.sunshine.domain.usecase.GetGeoLocation.Companion.GEOLOCATION_PREFIX
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnit

class GetGeoLocationTest {
	@Rule
	@JvmField
	val mockitoRule = MockitoJUnit.rule()!!

	companion object {
		private const val LOCATION_SETTING = "location-setting"
	}

	@Mock
	private lateinit var mockResourceDataSource: ResourceDataSource

	@Mock
	private lateinit var mockPreferenceDataSource: PreferenceDataSource

	@Mock
	private lateinit var mockLocationRepository: LocationRepository

	private lateinit var getGeoLocationUseCase: GetGeoLocationUseCase

	@Before
	fun setUp() {
		getGeoLocationUseCase = GetGeoLocation(mockResourceDataSource, mockPreferenceDataSource, mockLocationRepository)
	}

	@Test
	fun givenLocationIsValid_whenExecutingUseCase_thenReturnLocationCoordinates() {
		givenLocationSetting()
		val latitude = 37.3894F
		val longitude = -122.083F
		val location = Location("", "", latitude, longitude)
		`when`(mockLocationRepository.hasLocation(LOCATION_SETTING)).thenReturn(true)
		`when`(mockLocationRepository.getLocation(LOCATION_SETTING)).thenReturn(location)

		val geoLocation = getGeoLocationUseCase.getGeoLocation()

		assertThat(geoLocation, `is`("$GEOLOCATION_PREFIX$latitude$COMMA_SEPARATOR$longitude"))
	}

	@Test
	fun givenLocationIsInvalid_whenExecutingUseCase_thenReturnNothing() {
		givenLocationSetting()
		`when`(mockLocationRepository.hasLocation(LOCATION_SETTING)).thenReturn(false)

		val geoLocation = getGeoLocationUseCase.getGeoLocation()

		assertThat(geoLocation, `is`(""))
	}

	private fun givenLocationSetting() {
		val key = "key"
		val defaultKey = "default-key"
		`when`(mockResourceDataSource.getString(R.string.preference_location_key)).thenReturn(key)
		`when`(mockResourceDataSource.getString(R.string.preference_location_key_default)).thenReturn(defaultKey)
		`when`(mockPreferenceDataSource.getString(key, defaultKey)).thenReturn(LOCATION_SETTING)
	}
}