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

package constantine.theodoridis.app.sunshine.ui.forecasts

import constantine.theodoridis.app.sunshine.domain.usecases.GetGeoLocationUseCase

class ForecastsPresenter(private val view: ForecastsContract.View,
												 private val getGeoLocationUseCase: GetGeoLocationUseCase,
												 private val mainScheduler: Scheduler):
				ForecastsContract.Presenter {
	private val compositeDisposable = CompositeDisposable()

	override fun onDisplayMapLocation() {
		compositeDisposable.add(Single.fromCallable { getGeoLocationUseCase.getGeoLocation() }
						.subscribeOn(Schedulers.io())
						.observeOn(mainScheduler)
						.subscribeWith(object: DisposableSingleObserver<String>() {
							override fun onSuccess(geoLocation: String) {
								if (geoLocation == "") {
									view.displayInvalidLocationError()
								}
								else {
									view.displayMapLocation(geoLocation)
								}
							}

							override fun onError(e: Throwable) {
							}
						})
		)
	}

	override fun unSubscribe() {
		compositeDisposable.clear()
	}
}