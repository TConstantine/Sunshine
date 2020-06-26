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

import android.content.ContentResolver
import constantine.theodoridis.app.sunshine.data.datasource.PreferenceDataSource
import constantine.theodoridis.app.sunshine.data.datasource.ResourceDataSource
import constantine.theodoridis.app.sunshine.domain.repository.LocationRepository
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnit

class LocationDepositoryTest {
	@Rule
	@JvmField
	val mockitoRule = MockitoJUnit.rule()!!

	@Mock
	private lateinit var mockPreferenceDataSource: PreferenceDataSource

	@Mock
	private lateinit var mockResourceDataSource: ResourceDataSource

	@Mock
	private lateinit var mockContentResolver: ContentResolver

	private lateinit var locationRepository: LocationRepository

	@Before
	fun setUp() {
		locationRepository = LocationDepository(mockPreferenceDataSource, mockResourceDataSource, mockContentResolver)
	}

	@Test
	fun givenResourceValuesFromResourceDataSource_whenGettingPreferredLocation_thenReturnPreferredLocationFromPreferenceDataSource() {
		val key = "key"
		`when`(mockResourceDataSource.getString(anyInt())).thenReturn(key)

		locationRepository.getPreferredLocation()

		verify(mockPreferenceDataSource).getString(key, key)
	}
}