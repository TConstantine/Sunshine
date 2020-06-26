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

package constantine.theodoridis.app.sunshine.data.datasource

import android.content.SharedPreferences
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnit

class SharedPreferencesDataSourceTest {
	@Rule
	@JvmField
	val mockitoRule = MockitoJUnit.rule()!!

	@Mock
	private lateinit var mockSharedPreferences: SharedPreferences

	private lateinit var preferenceDataSource: PreferenceDataSource

	@Before
	fun setUp() {
		preferenceDataSource = SharedPreferencesDataSource(mockSharedPreferences)
	}

	@Test
	fun givenKeyAndDefaultKey_whenGettingString_thenGetStringFromSharedPreferences() {
		val key = "key"
		val defaultKey = "default-key"

		preferenceDataSource.getString(key, defaultKey)

		verify(mockSharedPreferences).getString(key, defaultKey)
	}
}