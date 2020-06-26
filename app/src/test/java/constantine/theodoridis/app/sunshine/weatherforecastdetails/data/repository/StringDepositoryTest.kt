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

import constantine.theodoridis.app.sunshine.data.datasource.ResourceDataSource
import constantine.theodoridis.app.sunshine.weatherforecastdetails.domain.repository.StringRepository
import org.junit.Before
import org.junit.Rule
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit

class StringDepositoryTest {
	companion object {
		private const val RESOURCE_ID = 0
		private const val STRING = "String"
	}

	@Rule
	@JvmField
	val mockitoRule = MockitoJUnit.rule()!!

	@Mock
	private lateinit var mockResourceDataSource: ResourceDataSource

	private lateinit var repository: StringRepository

	@Before
	fun setUp() {
		repository = StringDepository(mockResourceDataSource)
	}

//	@Test
//	fun shouldReturnStringRetrievedFromResourceDataSource() {
//		`when`(mockResourceDataSource.getString(anyInt())).thenReturn(STRING)
//
//		val string = repository.getString(RESOURCE_ID)
//
//		verify(mockResourceDataSource).getString(RESOURCE_ID)
//		assertThat(string, `is`(STRING))
//	}
}