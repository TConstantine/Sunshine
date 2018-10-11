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

package constantine.theodoridis.app.sunshine.data.helpers

import android.content.res.Resources
import constantine.theodoridis.app.sunshine.MockitoTest
import constantine.theodoridis.app.sunshine.domain.helpers.ResourcesHelper
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`

class AndroidResourcesShould: MockitoTest() {
	@Mock
	private lateinit var resources: Resources

	private lateinit var resourcesHelper: ResourcesHelper

	@Before
	fun setUp() {
		resourcesHelper = AndroidResources(resources)
	}

	@Test
	fun return_the_string_value_of_a_resource_id() {
		val resourceId = 12345
		val resourceIdToString = "12345"
		`when`(resources.getString(resourceId)).thenReturn(resourceIdToString)

		val resourceIdStringValue = resourcesHelper.getString(resourceId)

		assertThat(resourceIdStringValue, `is`(resourceIdToString))
	}
}