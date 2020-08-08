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

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import constantine.theodoridis.app.sunshine.domain.usecase.GetGeoLocationUseCase
import io.reactivex.plugins.RxJavaPlugins.reset
import io.reactivex.plugins.RxJavaPlugins.setIoSchedulerHandler
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnit

class MapLocationPresenterTest {
  @get:Rule
  val executorRule = InstantTaskExecutorRule()
  
  @get:Rule
  val mockitoRule = MockitoJUnit.rule()!!
  
  @Mock
  private lateinit var mockObserver: Observer<String>
  
  @Mock
  private lateinit var mockGetGeoLocationUseCase: GetGeoLocationUseCase
  
  private lateinit var presenter: MapLocationPresenter
  
  @Before
  fun setUp() {
    presenter = MapLocationPresenter(mockGetGeoLocationUseCase, Schedulers.trampoline())
    setIoSchedulerHandler { Schedulers.trampoline() }
  }
  
  @After
  fun cleanUp() {
    reset()
  }
  
  @Test
  fun shouldDisplayMapLocation_WhenOnMapLocationOptionClickIsTriggeredAndLocationIsValid() {
    presenter.mapLocation().observeForever(mockObserver)
    val geoLocation = "geo-location"
    `when`(mockGetGeoLocationUseCase.getGeoLocation()).thenReturn(geoLocation)
    
    presenter.onMapLocationOptionClick()
    
    verify(mockObserver).onChanged(geoLocation)
  }
  
  @Test
  fun shouldDisplayInvalidLocationError_WhenOnMapLocationOptionClickIsTriggeredAndLocationIsInvalid() {
    presenter.mapLocationError().observeForever(mockObserver)
    val geoLocation = ""
    `when`(mockGetGeoLocationUseCase.getGeoLocation()).thenReturn(geoLocation)
    
    presenter.onMapLocationOptionClick()
    
    verify(mockObserver).onChanged(geoLocation)
  }
}
