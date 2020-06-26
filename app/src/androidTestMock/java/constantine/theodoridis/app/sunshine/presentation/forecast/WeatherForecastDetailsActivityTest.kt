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

package constantine.theodoridis.app.sunshine.presentation.forecast

import android.app.Activity
import android.app.Instrumentation.ActivityResult
import android.content.Intent
import android.net.Uri
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import constantine.theodoridis.app.sunshine.R
import constantine.theodoridis.app.sunshine.ui.weatherforecastdetails.WeatherForecastDetailsActivity
import constantine.theodoridis.app.sunshine.ui.weatherforecastdetails.WeatherForecastDetailsFragment
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WeatherForecastDetailsActivityTest {
	@Rule
	@JvmField
	val intentsTestRule = object : IntentsTestRule<WeatherForecastDetailsActivity>(WeatherForecastDetailsActivity::class.java) {
		override fun getActivityIntent(): Intent {
			return Intent(getInstrumentation().targetContext, WeatherForecastDetailsActivity::class.java).apply {
				putExtra(WeatherForecastDetailsFragment.DETAIL_URI, Uri.parse("content://constantine.theodoridis.app.sunshine/weather/94043/1584050400000"))
				putExtra(WeatherForecastDetailsFragment.DETAIL_TRANSITION_ANIMATION, true)
			}
		}
	}

	// TODO: Customize test to run on VERSION_CODE_LOLLIPOP and above to test flag
	// TODO: Remove mForecast null check in production code when Loader is replaced by ViewModel
	@Test
	fun givenOptionsMenuIsOpen_whenShareOptionIsClicked_thenShareForecast() {
		intending(hasAction(Intent.ACTION_SEND)).respondWith(ActivityResult(Activity.RESULT_OK, null))
		openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)

		onView(withText(R.string.action_share)).perform(click())

		intended(allOf(
				hasAction(equalTo(Intent.ACTION_SEND)),
				hasFlag(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET),
				hasType("text/plain"),
				hasExtra(Intent.EXTRA_TEXT, "#SunshineApp")
		))
	}
}