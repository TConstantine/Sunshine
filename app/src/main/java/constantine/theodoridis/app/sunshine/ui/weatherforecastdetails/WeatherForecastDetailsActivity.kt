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

package constantine.theodoridis.app.sunshine.ui.weatherforecastdetails

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import constantine.theodoridis.app.sunshine.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeatherForecastDetailsActivity : AppCompatActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_weather_forecast_details)
		if (savedInstanceState == null) {
			val arguments = Bundle()
			arguments.putParcelable(WeatherForecastDetailsFragment.DETAIL_URI, intent.data)
			arguments.putBoolean(WeatherForecastDetailsFragment.DETAIL_TRANSITION_ANIMATION, true)
			val fragment = WeatherForecastDetailsFragment()
			fragment.arguments = arguments
			supportFragmentManager.beginTransaction()
					.add(R.id.weather_detail_container, fragment)
					.commit()
			supportPostponeEnterTransition()
		}
	}
}