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

package constantine.theodoridis.app.sunshine.data.repositories

import constantine.theodoridis.app.sunshine.data.model.Location
import constantine.theodoridis.app.sunshine.domain.repositories.LocationRepository

class FakeLocationDataSource: LocationRepository {
	companion object {
		private const val DEFAULT_LOCATION_SETTING = "London"
		const val DEFAULT_LATITUDE = 51.5074F
		const val DEFAULT_LONGITUDE = -0.1278F
		var isLocationValid = true
	}
	override fun getLocation(locationSetting: String): Location {
		return Location(
						DEFAULT_LOCATION_SETTING,
						DEFAULT_LOCATION_SETTING,
						DEFAULT_LATITUDE,
						DEFAULT_LONGITUDE)
	}

	override fun hasLocation(locationSetting: String): Boolean {
		return isLocationValid
	}
}