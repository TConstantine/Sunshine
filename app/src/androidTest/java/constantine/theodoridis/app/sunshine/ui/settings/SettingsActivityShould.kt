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

package constantine.theodoridis.app.sunshine.ui.settings

import android.app.Activity.RESULT_OK
import android.app.Instrumentation.ActivityResult
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.intent.Intents.intending
import android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent
import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.runner.AndroidJUnit4
import constantine.theodoridis.app.sunshine.R
import constantine.theodoridis.app.sunshine.SettingsActivity
import constantine.theodoridis.app.sunshine.SettingsActivity.Companion.GOOGLE_ATTRIBUTION
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsActivityShould {
	@Rule
	@JvmField
	val intentsTestRule = IntentsTestRule<SettingsActivity>(SettingsActivity::class.java)

	@Test
	fun display_location_button() {
		onView(withId(R.id.current_location)).check(matches(isDisplayed()))
	}

	@Test
	fun not_display_attributions_when_place_picker_returns_without_data() {
		intending(hasComponent(SettingsActivity::class.java.name))
						.respondWith(ActivityResult(RESULT_OK, null))

		onView(withId(R.id.current_location)).perform(click())

		onView(withTagValue(`is`(GOOGLE_ATTRIBUTION))).check(matches(not(isDisplayed())))
	}
}