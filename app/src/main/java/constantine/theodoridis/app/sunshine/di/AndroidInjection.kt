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

import constantine.theodoridis.app.sunshine.presentation.forecasts.ForecastsActivity
import constantine.theodoridis.app.sunshine.presentation.forecasts.ForecastsContract
import constantine.theodoridis.app.sunshine.presentation.forecasts.ForecastsFragment
import constantine.theodoridis.app.sunshine.ui.weatherforecastdetails.WeatherForecastDetailsFragment

class AndroidInjection {
	companion object {
		fun inject(target: ForecastsActivity) {
			DaggerActivityComponent.builder()
					.applicationModule(ApplicationModule(target.applicationContext))
					.repositoryModule(RepositoryModule())
					.dataSourceModule(DataSourceModule())
					.build()
					.inject(target)
		}

		fun inject(target: ForecastsFragment, view: ForecastsContract.View) {
			DaggerFragmentComponent.builder()
					.applicationModule(ApplicationModule(target.context!!))
					.weatherForecastDetailsPresentationModule(WeatherForecastDetailsPresentationModule(view))
					.useCaseModule(UseCaseModule())
					.schedulerModule(SchedulerModule())
					.repositoryModule(RepositoryModule())
					.dataSourceModule(DataSourceModule())
					.build()
					.inject(target)
		}

		fun inject(target: WeatherForecastDetailsFragment) {
			DaggerFragmentComponent.builder()
					.applicationModule(ApplicationModule(target.context!!))
					.weatherForecastDetailsPresentationModule(WeatherForecastDetailsPresentationModule())
					.schedulerModule(SchedulerModule())
					.useCaseModule(UseCaseModule())
					.repositoryModule(RepositoryModule())
					.dataSourceModule(DataSourceModule())
					.build()
					.inject(target)
		}
	}
}