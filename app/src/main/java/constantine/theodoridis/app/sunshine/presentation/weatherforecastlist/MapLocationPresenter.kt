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

package constantine.theodoridis.app.sunshine.presentation.weatherforecastlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import constantine.theodoridis.app.sunshine.domain.usecase.GetGeoLocationUseCase
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class MapLocationPresenter(
  private val useCase: GetGeoLocationUseCase,
  private val scheduler: Scheduler
) : ViewModel() {
  private val compositeDisposable = CompositeDisposable()
  private val mapLocationObservable = MutableLiveData<String>()
  private val mapLocationErrorObservable = MutableLiveData<String>()
  
  fun onMapLocationOptionClick() {
    compositeDisposable.add(Single.fromCallable { useCase.getGeoLocation() }
      .subscribeOn(Schedulers.io())
      .observeOn(scheduler)
      .subscribeWith(object : DisposableSingleObserver<String>() {
        override fun onSuccess(geoLocation: String) {
          if (geoLocation == "") {
            mapLocationErrorObservable.postValue(geoLocation)
          } else {
            mapLocationObservable.postValue(geoLocation)
          }
        }
        
        override fun onError(e: Throwable) {
        }
      })
    )
  }
  
  fun mapLocation(): LiveData<String> {
    return mapLocationObservable
  }
  
  fun mapLocationError(): LiveData<String> {
    return mapLocationErrorObservable
  }
  
  override fun onCleared() {
    super.onCleared()
    compositeDisposable.clear()
  }
}
