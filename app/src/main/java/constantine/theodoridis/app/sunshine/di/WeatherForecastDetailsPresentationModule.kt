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

import constantine.theodoridis.app.sunshine.domain.transform.Transformer
import constantine.theodoridis.app.sunshine.domain.usecase.UseCase
import constantine.theodoridis.app.sunshine.weatherforecastdetails.domain.loadweatherforecastdetails.LoadWeatherForecastDetailsRequest
import constantine.theodoridis.app.sunshine.weatherforecastdetails.domain.loadweatherforecastdetails.LoadWeatherForecastDetailsResponse
import constantine.theodoridis.app.sunshine.weatherforecastdetails.presentation.WeatherForecastDetailsPresenter
import constantine.theodoridis.app.sunshine.weatherforecastdetails.presentation.WeatherForecastDetailsTransformer
import constantine.theodoridis.app.sunshine.weatherforecastdetails.presentation.WeatherForecastDetailsViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import io.reactivex.Scheduler

// TODO: Remove constructor with view, when ForecastsPresenter is transformed into ViewModel
@Module
@InstallIn(FragmentComponent::class)
class WeatherForecastDetailsPresentationModule {
//	@Provides
//	fun provideForecastsContractPresenter(
//			view: ForecastsContract.View,
//			getGeoLocationUseCase: GetGeoLocationUseCase,
//			mainScheduler: Scheduler
//	): ForecastsContract.Presenter {
//		return ForecastsPresenter(view, getGeoLocationUseCase, mainScheduler)
//	}

	@Provides
	fun provideWeatherForecastDetailsPresenter(
			useCase: UseCase<LoadWeatherForecastDetailsRequest, LoadWeatherForecastDetailsResponse>,
			transformer: Transformer<LoadWeatherForecastDetailsResponse, WeatherForecastDetailsViewModel>,
			scheduler: Scheduler
	): WeatherForecastDetailsPresenter {
		return WeatherForecastDetailsPresenter(useCase, transformer, scheduler)
	}

	@Provides
	fun provideWeatherForecastDetailsTransformer(
	): Transformer<LoadWeatherForecastDetailsResponse, WeatherForecastDetailsViewModel> {
		return WeatherForecastDetailsTransformer()
	}
}
