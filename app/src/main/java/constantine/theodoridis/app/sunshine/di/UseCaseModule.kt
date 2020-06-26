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

import constantine.theodoridis.app.sunshine.data.datasource.PreferenceDataSource
import constantine.theodoridis.app.sunshine.data.datasource.ResourceDataSource
import constantine.theodoridis.app.sunshine.domain.repository.LocationRepository
import constantine.theodoridis.app.sunshine.domain.repository.WeatherForecastRepository
import constantine.theodoridis.app.sunshine.domain.usecase.GetGeoLocation
import constantine.theodoridis.app.sunshine.domain.usecase.GetGeoLocationUseCase
import constantine.theodoridis.app.sunshine.domain.usecase.UseCase
import constantine.theodoridis.app.sunshine.weatherforecastdetails.domain.loadweatherforecastdetails.LoadWeatherForecastDetailsRequest
import constantine.theodoridis.app.sunshine.weatherforecastdetails.domain.loadweatherforecastdetails.LoadWeatherForecastDetailsResponse
import constantine.theodoridis.app.sunshine.weatherforecastdetails.domain.loadweatherforecastdetails.LoadWeatherForecastDetailsUseCase
import constantine.theodoridis.app.sunshine.weatherforecastdetails.domain.repository.StringRepository
import constantine.theodoridis.app.sunshine.weatherforecastdetails.domain.repository.TemperatureUnitRepository
import dagger.Module
import dagger.Provides

@Module
class UseCaseModule {
	@Provides
	fun provideGetGeoLocationUseCase(
			resourceDataSource: ResourceDataSource,
			preferenceDataSource: PreferenceDataSource,
			locationRepository: LocationRepository
	): GetGeoLocationUseCase {
		return GetGeoLocation(resourceDataSource, preferenceDataSource, locationRepository)
	}

	@Provides
	fun provideLoadWeatherForecastUseCase(
			weatherForecastRepository: WeatherForecastRepository,
			temperatureUnitRepository: TemperatureUnitRepository,
			stringRepository: StringRepository
	): UseCase<LoadWeatherForecastDetailsRequest, LoadWeatherForecastDetailsResponse> {
		return LoadWeatherForecastDetailsUseCase(weatherForecastRepository, temperatureUnitRepository, stringRepository)
	}
}