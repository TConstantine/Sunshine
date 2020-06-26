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

class TemperatureUnitDepository(
		private val resourceDataSource: ResourceDataSource,
		private val preferenceDataSource: PreferenceDataSource
) : TemperatureUnitRepository {
	companion object {
		private const val IMPERIAL = "imperial"
	}

	override fun getTemperatureUnit(): TemperatureUnit {
		return transformTemperatureUnit(
				preferenceDataSource.getString(
						resourceDataSource.getString(R.string.preference_temperature_unit_key),
						resourceDataSource.getString(R.string.temperature_unit_metric)
				)
		)
	}

	private fun transformTemperatureUnit(temperatureUnit: String?): TemperatureUnit {
		return when (temperatureUnit) {
			IMPERIAL -> TemperatureUnit.IMPERIAL
			else -> TemperatureUnit.METRIC
		}
	}
}