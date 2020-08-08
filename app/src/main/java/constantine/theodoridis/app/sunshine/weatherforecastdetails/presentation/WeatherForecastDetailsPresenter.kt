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

package constantine.theodoridis.app.sunshine.weatherforecastdetails.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import constantine.theodoridis.app.sunshine.domain.transform.Transformer
import constantine.theodoridis.app.sunshine.domain.usecase.UseCase
import constantine.theodoridis.app.sunshine.weatherforecastdetails.domain.loadweatherforecastdetails.LoadWeatherForecastDetailsRequest
import constantine.theodoridis.app.sunshine.weatherforecastdetails.domain.loadweatherforecastdetails.LoadWeatherForecastDetailsResponse
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class WeatherForecastDetailsPresenter(
  private val useCase: UseCase<LoadWeatherForecastDetailsRequest, LoadWeatherForecastDetailsResponse>,
  private val transformer: Transformer<LoadWeatherForecastDetailsResponse, WeatherForecastDetailsViewModel>,
  private val scheduler: Scheduler
) : ViewModel() {
  private val compositeDisposable = CompositeDisposable()
  private val weatherForecastDetailsObservable = MutableLiveData<WeatherForecastDetailsViewModel>()
  
  fun loadWeatherForecastDetails(location: String, date: Long) {
    compositeDisposable.add(Single.fromCallable { useCase.execute(LoadWeatherForecastDetailsRequest(location, date)) }
      .subscribeOn(Schedulers.io())
      .observeOn(scheduler)
      .subscribeWith(object : DisposableSingleObserver<LoadWeatherForecastDetailsResponse>() {
        override fun onSuccess(response: LoadWeatherForecastDetailsResponse) {
          weatherForecastDetailsObservable.postValue(transformer.transform(response))
        }
        
        override fun onError(e: Throwable) {
        }
      })
    )
  }
  
  fun weatherForecastDetails(): LiveData<WeatherForecastDetailsViewModel> {
    return weatherForecastDetailsObservable
  }
  
  override fun onCleared() {
    compositeDisposable.clear()
  }
}
