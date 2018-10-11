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

package constantine.theodoridis.app.sunshine.domain.usecases

import constantine.theodoridis.app.sunshine.MockitoTest
import constantine.theodoridis.app.sunshine.R
import constantine.theodoridis.app.sunshine.data.model.Location
import constantine.theodoridis.app.sunshine.domain.helpers.ResourcesHelper
import constantine.theodoridis.app.sunshine.domain.helpers.SharedPreferencesHelper
import constantine.theodoridis.app.sunshine.domain.repositories.LocationRepository
import constantine.theodoridis.app.sunshine.domain.usecases.GetGeoLocation.Companion.COMMA_SEPARATOR
import constantine.theodoridis.app.sunshine.domain.usecases.GetGeoLocation.Companion.GEOLOCATION_PREFIX
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`

class GetGeoLocationShould: MockitoTest() {
	companion object {
		private const val LOCATION_SETTING = "location-setting"
	}

	@Mock
	private lateinit var resourcesHelper: ResourcesHelper

	@Mock
	private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

	@Mock
	private lateinit var locationRepository: LocationRepository

	private lateinit var getGeoLocationUseCase: GetGeoLocationUseCase

	@Before
	fun setUp() {
		getGeoLocationUseCase = GetGeoLocation(resourcesHelper, sharedPreferencesHelper, locationRepository)
	}

	@Test
	fun return_location_coordinates_when_location_is_valid() {
		givenLocationSetting()
		val latitude = 37.3894F
		val longitude = -122.083F
		val location = Location("", "", latitude, longitude)
		`when`(locationRepository.hasLocation(LOCATION_SETTING)).thenReturn(true)
		`when`(locationRepository.getLocation(LOCATION_SETTING)).thenReturn(location)

		val geoLocation = getGeoLocationUseCase.getGeoLocation()

		assertThat(geoLocation, `is`("$GEOLOCATION_PREFIX$latitude$COMMA_SEPARATOR$longitude"))
	}

	@Test
	fun return_nothing_when_location_is_invalid() {
		givenLocationSetting()
		`when`(locationRepository.hasLocation(LOCATION_SETTING)).thenReturn(false)

		val geoLocation = getGeoLocationUseCase.getGeoLocation()

		assertThat(geoLocation, `is`(""))
	}

	private fun givenLocationSetting() {
		val key = "key"
		val defaultKey = "default-key"
		`when`(resourcesHelper.getString(R.string.pref_location_key)).thenReturn(key)
		`when`(resourcesHelper.getString(R.string.pref_location_default)).thenReturn(defaultKey)
		`when`(sharedPreferencesHelper.getString(key, defaultKey)).thenReturn(LOCATION_SETTING)
	}
}