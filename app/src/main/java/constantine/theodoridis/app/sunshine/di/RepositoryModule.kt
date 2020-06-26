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

package constantine.theodoridis.app.sunshine.di

import android.content.ContentResolver
import constantine.theodoridis.app.sunshine.data.database.DatabaseDataSource
import constantine.theodoridis.app.sunshine.data.datasource.PreferenceDataSource
import constantine.theodoridis.app.sunshine.data.datasource.ResourceDataSource
import constantine.theodoridis.app.sunshine.data.repository.LocationDepository
import constantine.theodoridis.app.sunshine.data.repository.WeatherForecastDepository
import constantine.theodoridis.app.sunshine.domain.repository.LocationRepository
import constantine.theodoridis.app.sunshine.domain.repository.WeatherForecastRepository
import constantine.theodoridis.app.sunshine.weatherforecastdetails.data.repository.StringDepository
import constantine.theodoridis.app.sunshine.weatherforecastdetails.data.repository.TemperatureUnitDepository
import constantine.theodoridis.app.sunshine.weatherforecastdetails.domain.repository.StringRepository
import constantine.theodoridis.app.sunshine.weatherforecastdetails.domain.repository.TemperatureUnitRepository
import dagger.Module
import dagger.Provides

@Module
class RepositoryModule {
	@Provides
	fun provideLocationRepository(
			preferenceDataSource: PreferenceDataSource,
			resourceDataSource: ResourceDataSource,
			contentResolver: ContentResolver
	): LocationRepository {
		return LocationDepository(preferenceDataSource, resourceDataSource, contentResolver)
	}

	@Provides
	fun provideWeatherForecastRepository(
			databaseDataSource: DatabaseDataSource,
			resourceDataSource: ResourceDataSource
	): WeatherForecastRepository {
		return WeatherForecastDepository(databaseDataSource, resourceDataSource)
	}

	@Provides
	fun provideTemperatureUnitRepository(
			resourceDataSource: ResourceDataSource,
			preferenceDataSource: PreferenceDataSource
	): TemperatureUnitRepository {
		return TemperatureUnitDepository(resourceDataSource, preferenceDataSource)
	}

	@Provides
	fun provideStringRepository(
			resourceDataSource: ResourceDataSource
	): StringRepository {
		return StringDepository(resourceDataSource)
	}
}