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

package constantine.theodoridis.app.sunshine.weatherforecastdetails.data.repository

import constantine.theodoridis.app.sunshine.R
import constantine.theodoridis.app.sunshine.data.datasource.PreferenceDataSource
import constantine.theodoridis.app.sunshine.data.datasource.ResourceDataSource
import constantine.theodoridis.app.sunshine.domain.entity.TemperatureUnit
import constantine.theodoridis.app.sunshine.weatherforecastdetails.domain.repository.TemperatureUnitRepository
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit

@RunWith(JUnitParamsRunner::class)
class TemperatureUnitDepositoryTest {
	companion object {
		private const val KEY = "key"
		private const val DEFAULT_TEMPERATURE_UNIT = "metric"
	}

	@Rule
	@JvmField
	val mockitoRule = MockitoJUnit.rule()!!

	@Mock
	private lateinit var mockPreferenceDataSource: PreferenceDataSource

	@Mock
	private lateinit var mockResourceDataSource: ResourceDataSource

	private lateinit var repository: TemperatureUnitRepository

	@Before
	fun setUp() {
		repository = TemperatureUnitDepository(mockResourceDataSource, mockPreferenceDataSource)
	}

	@Test
	@Parameters(value = [
		"metric, METRIC",
		"imperial, IMPERIAL",
		"null, METRIC"
	])
	fun shouldReturnTemperatureUnitRetrievedFromPreferenceDataSource(temperatureUnit: String, expected: TemperatureUnit) {
		`when`(mockResourceDataSource.getString(anyInt())).thenReturn(KEY).thenReturn(DEFAULT_TEMPERATURE_UNIT)
		`when`(mockPreferenceDataSource.getString(anyString(), anyString())).thenReturn(temperatureUnit)

		val actual = repository.getTemperatureUnit()

		verify(mockResourceDataSource).getString(R.string.preference_temperature_unit_key)
		verify(mockResourceDataSource).getString(R.string.temperature_unit_metric)
		verify(mockPreferenceDataSource).getString(KEY, DEFAULT_TEMPERATURE_UNIT)
		assertThat(actual, `is`(expected))
	}
}