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

import constantine.theodoridis.app.sunshine.R
import constantine.theodoridis.app.sunshine.domain.helpers.ResourcesHelper
import constantine.theodoridis.app.sunshine.domain.helpers.SharedPreferencesHelper
import constantine.theodoridis.app.sunshine.domain.repositories.LocationRepository

class GetGeoLocation(private val resourcesHelper: ResourcesHelper,
										 private val sharedPreferencesHelper: SharedPreferencesHelper,
										 private val locationRepository: LocationRepository):
				GetGeoLocationUseCase {
	companion object {
		const val GEOLOCATION_PREFIX = "geo:"
		const val COMMA_SEPARATOR = ","
	}

	override fun getGeoLocation(): String {
		val locationSetting = sharedPreferencesHelper.getString(
						resourcesHelper.getString(R.string.pref_location_key),
						resourcesHelper.getString(R.string.pref_location_default))
		if (locationRepository.hasLocation(locationSetting)) {
			val location = locationRepository.getLocation(locationSetting)
			return "$GEOLOCATION_PREFIX${location.latitude}$COMMA_SEPARATOR${location.longitude}"
		}
		return ""
	}
}