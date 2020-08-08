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

package constantine.theodoridis.app.sunshine.ui.weatherforecastlist

import android.app.Activity.RESULT_OK
import android.app.Instrumentation
import android.content.Intent.ACTION_VIEW
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import constantine.theodoridis.app.sunshine.R
import constantine.theodoridis.app.sunshine.SettingsActivity
import constantine.theodoridis.app.sunshine.data.database.DatabaseDataSource
import constantine.theodoridis.app.sunshine.data.datasource.PreferenceDataSource
import constantine.theodoridis.app.sunshine.data.datasource.ResourceDataSource
import constantine.theodoridis.app.sunshine.data.model.Location
import constantine.theodoridis.app.sunshine.data.repository.WeatherForecastDepository
import constantine.theodoridis.app.sunshine.di.RepositoryModule
import constantine.theodoridis.app.sunshine.domain.repository.LocationRepository
import constantine.theodoridis.app.sunshine.domain.repository.WeatherForecastRepository
import constantine.theodoridis.app.sunshine.presentation.weatherforecastlist.ForecastsActivity
import constantine.theodoridis.app.sunshine.ui.weatherforecastlist.WeatherForecastListActivityTest.FakeLocationDepository.Companion.LATITUDE
import constantine.theodoridis.app.sunshine.ui.weatherforecastlist.WeatherForecastListActivityTest.FakeLocationDepository.Companion.LONGITUDE
import constantine.theodoridis.app.sunshine.weatherforecastdetails.data.repository.StringDepository
import constantine.theodoridis.app.sunshine.weatherforecastdetails.data.repository.TemperatureUnitDepository
import constantine.theodoridis.app.sunshine.weatherforecastdetails.domain.repository.StringRepository
import constantine.theodoridis.app.sunshine.weatherforecastdetails.domain.repository.TemperatureUnitRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.hamcrest.CoreMatchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain

@HiltAndroidTest
@UninstallModules(RepositoryModule::class)
class WeatherForecastListActivityTest {
  private val hiltRule = HiltAndroidRule(this)
  private val intentsRule = IntentsTestRule(ForecastsActivity::class.java)
  
  @get:Rule
  val rules = RuleChain.outerRule(hiltRule).around(intentsRule)!!
  
  @Test
  fun shouldDisplayLocationInGoogleMaps_WhenMapOptionIsClickedAndLocationIsValid() {
    FakeLocationDepository.isLocationValid = true
    intending(hasAction(ACTION_VIEW)).respondWith(Instrumentation.ActivityResult(RESULT_OK, null))
    openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
    
    onView(withText(R.string.action_map)).perform(click())
    
    intended(allOf(
      hasAction(equalTo(ACTION_VIEW)),
      hasData("geo:$LATITUDE,$LONGITUDE")
    ))
  }
  
  @Test
  fun shouldDisplayErrorMessage_WhenMapOptionIsClickedAndLocationIsInvalid() {
    FakeLocationDepository.isLocationValid = false
    openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
    
    onView(withText(R.string.action_map)).perform(click())
    
    onView(withText(R.string.invalid_location_message))
      .inRoot(withDecorView(not(`is`(intentsRule.activity.window.decorView))))
      .check(matches(isDisplayed()))
  }
  
  @Test
  fun shouldDisplaySettingsScreen_WhenSettingsOptionIsClicked() {
    openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
    
    onView(withText(R.string.action_settings)).perform(click())
    
    intended(hasComponent(SettingsActivity::class.java.name))
  }
  
  @Module
  @InstallIn(ActivityComponent::class)
  class TestRepositoryModule {
    @Provides
    fun provideLocationRepository(): LocationRepository {
      return FakeLocationDepository()
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
  
  class FakeLocationDepository : LocationRepository {
    companion object {
      var isLocationValid = true
      private const val CITY_NAME = "London"
      private const val LOCATION_SETTING = "London"
      const val LATITUDE = 51.5074F
      const val LONGITUDE = 0.1278F
    }
    
    override fun getPreferredLocation(): String? {
      return LOCATION_SETTING
    }
    
    override fun getLocation(locationSetting: String): Location {
      return Location(
        cityName = CITY_NAME,
        locationSetting = LOCATION_SETTING,
        latitude = LATITUDE,
        longitude = LONGITUDE
      )
    }
    
    override fun hasLocation(locationSetting: String): Boolean {
      return isLocationValid
    }
  }
}
