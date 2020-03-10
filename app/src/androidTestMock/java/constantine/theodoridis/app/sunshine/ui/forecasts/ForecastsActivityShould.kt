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

import android.app.Activity.RESULT_OK
import android.app.Instrumentation.ActivityResult
import android.content.Intent.ACTION_VIEW
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import constantine.theodoridis.app.sunshine.MainActivity
import constantine.theodoridis.app.sunshine.R
import constantine.theodoridis.app.sunshine.data.repositories.FakeLocationDataSource
import constantine.theodoridis.app.sunshine.data.repositories.FakeLocationDataSource.Companion.DEFAULT_LATITUDE
import constantine.theodoridis.app.sunshine.data.repositories.FakeLocationDataSource.Companion.DEFAULT_LONGITUDE
import constantine.theodoridis.app.sunshine.domain.usecases.GetGeoLocation.Companion.COMMA_SEPARATOR
import constantine.theodoridis.app.sunshine.domain.usecases.GetGeoLocation.Companion.GEOLOCATION_PREFIX
import org.hamcrest.CoreMatchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ForecastsActivityShould {
	@Rule
	@JvmField
	val intentsTestRule = IntentsTestRule<MainActivity>(MainActivity::class.java)

	@Test
	fun display_location_in_google_maps_when_location_is_valid() {
		intending(hasAction(ACTION_VIEW)).respondWith(ActivityResult(RESULT_OK, null))
		openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)

		onView(withText(R.string.action_map)).perform(click())

		intended(allOf(
						hasAction(equalTo(ACTION_VIEW)),
						hasData("$GEOLOCATION_PREFIX$DEFAULT_LATITUDE$COMMA_SEPARATOR$DEFAULT_LONGITUDE")
		))
	}

	@Test
	fun display_error_when_location_is_invalid() {
		FakeLocationDataSource.isLocationValid = false
		openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)

		onView(withText(R.string.action_map)).perform(click())

		onView(withText(R.string.invalid_location_message))
						.inRoot(withDecorView(not(`is`(intentsTestRule.activity.window.decorView))))
						.check(matches(isDisplayed()))
	}
}