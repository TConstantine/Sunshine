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

package constantine.theodoridis.app.sunshine.presentation.settings

import android.app.Activity.RESULT_OK
import android.app.Instrumentation.ActivityResult
import android.content.res.Resources
import android.preference.Preference
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.PreferenceMatchers.withKey
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import constantine.theodoridis.app.sunshine.R
import constantine.theodoridis.app.sunshine.SettingsActivity
import constantine.theodoridis.app.sunshine.SettingsActivity.Companion.GOOGLE_ATTRIBUTION
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.hamcrest.CoreMatchers.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(JUnitParamsRunner::class)
class SettingsActivityTest {
	@Rule
	@JvmField
	val intentsTestRule = IntentsTestRule(SettingsActivity::class.java)

	private lateinit var resources: Resources

	@Before
	fun setUp() {
		resources = intentsTestRule.activity.resources
	}

	@Test
	fun givenLocationPreferenceIsSelected_whenLocationIsEntered_thenDisplayEnteredLocation() {
		val locationPreferenceKey = resources.getString(R.string.preference_location_key)
		val locationPreference = onData(allOf(`is`(instanceOf(Preference::class.java)), withKey(locationPreferenceKey)))
		locationPreference.perform(click())

		val location = "Athens"
		onView(withHint(resources.getString(R.string.location_edit_text_hint)))
				.inRoot(isDialog()).perform(replaceText(location))
		onView(withId(android.R.id.button1)).perform(click())

		Thread.sleep(5000) // TODO: Remove this line when FakeDataSources are implemented
		locationPreference.onChildView((withText(location))).check(matches(isDisplayed()))
	}

	@Test
	fun givenLocationPreferenceIsSelected_whenCancelButtonIsPressed_thenDisplayedLocationRemainsUnchanged() {
		val locationPreferenceKey = resources.getString(R.string.preference_location_key)
		val locationPreference = onData(allOf(`is`(instanceOf(Preference::class.java)), withKey(locationPreferenceKey)))
		locationPreference.perform(click())

		val currentLocation = "London"
		onView(withHint(resources.getString(R.string.location_edit_text_hint)))
				.inRoot(isDialog()).perform(replaceText(currentLocation))
		onView(withId(android.R.id.button2)).perform(click())

		val previousLocation = "Athens"
		locationPreference.onChildView((withText(previousLocation))).check(matches(isDisplayed()))
	}

	@Test
	@Parameters(
			"${R.string.temperature_unit_metric}",
			"${R.string.temperature_unit_imperial}"
	)
	fun givenTemperatureUnitPreferenceIsSelected_whenTemperatureUnitIsSelected_thenDisplaySelectedTemperatureUnit(resourceId: Int) {
		val temperatureUnitPreferenceKey = resources.getString(R.string.preference_temperature_unit_key)
		val temperatureUnitPreference = onData(allOf(`is`(instanceOf(Preference::class.java)), withKey(temperatureUnitPreferenceKey)))
		temperatureUnitPreference.perform(click())

		onView(withText(resourceId)).perform(click())

		temperatureUnitPreference.onChildView((withText(resourceId))).check(matches(isDisplayed()))
	}

	@Test
	@Parameters(
			"${R.string.preference_icon_pack_sunshine}",
			"${R.string.preference_icon_pack_cute_dogs}"
	)
	fun givenIconPackPreferenceIsSelected_whenIconPackIsSelected_thenDisplaySelectedIconPack(resourceId: Int) {
		val iconPackPreferenceKey = resources.getString(R.string.preference_icon_pack_key)
		val iconPackPreference = onData(allOf(`is`(instanceOf(Preference::class.java)), withKey(iconPackPreferenceKey)))
		iconPackPreference.perform(click())

		onView(withText(resourceId)).perform(click())

		iconPackPreference.onChildView((withText(resourceId))).check(matches(isDisplayed()))
	}

	@Test
	fun givenPlacePickerReturnsWithoutData_whenLocationIconIsClicked_thenNotDisplayAttributions() {
		intending(hasComponent(SettingsActivity::class.java.name))
				.respondWith(ActivityResult(RESULT_OK, null))

		onView(withId(R.id.current_location)).perform(click())

		onView(withTagValue(`is`(GOOGLE_ATTRIBUTION))).check(matches(not(isDisplayed())))
	}
}