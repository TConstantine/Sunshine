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

import constantine.theodoridis.app.sunshine.domain.helpers.ResourcesHelper
import constantine.theodoridis.app.sunshine.domain.helpers.SharedPreferencesHelper
import constantine.theodoridis.app.sunshine.domain.repositories.LocationRepository
import constantine.theodoridis.app.sunshine.domain.usecases.GetGeoLocation
import constantine.theodoridis.app.sunshine.domain.usecases.GetGeoLocationUseCase
import dagger.Module
import dagger.Provides

@Module
class UseCaseModule {
	@Provides
	fun provideGetGeoLocationUseCase(resourcesHelper: ResourcesHelper,
																	 sharedPreferencesHelper: SharedPreferencesHelper,
																	 locationRepository: LocationRepository):
					GetGeoLocationUseCase {
		return GetGeoLocation(resourcesHelper, sharedPreferencesHelper, locationRepository)
	}
}