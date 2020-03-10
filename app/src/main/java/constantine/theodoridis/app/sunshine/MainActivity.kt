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

package constantine.theodoridis.app.sunshine

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import constantine.theodoridis.app.sunshine.data.WeatherContract
import constantine.theodoridis.app.sunshine.gcm.RegistrationIntentService
import constantine.theodoridis.app.sunshine.sync.SunshineSyncAdapter

class MainActivity : AppCompatActivity(), ForecastFragment.Callback {
	companion object {
		private const val DETAIL_FRAGMENT_TAG = "DETAIL_FRAGMENT_TAG"
		private const val PLAY_SERVICES_RESOLUTION_REQUEST = 9000
		const val SENT_TOKEN_TO_SERVER = "sentTokenToServer"
	}

	private var mTwoPane: Boolean = false
	private var mLocation: String? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mLocation = Utility.getPreferredLocation(this)
		val contentUri = if (intent != null) intent.data else null
		setContentView(R.layout.activity_main)
		val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
		setSupportActionBar(toolbar)
		supportActionBar!!.setDisplayShowTitleEnabled(false)
		if (findViewById<View>(R.id.weather_detail_container) != null) {
			mTwoPane = true
			if (savedInstanceState == null) {
				val fragment = DetailFragment()
				if (contentUri != null) {
					val args = Bundle()
					args.putParcelable(DetailFragment.DETAIL_URI, contentUri)
					fragment.arguments = args
				}
				supportFragmentManager.beginTransaction()
								.replace(R.id.weather_detail_container, fragment, DETAIL_FRAGMENT_TAG)
								.commit()
			}
		} else {
			mTwoPane = false
			supportActionBar!!.elevation = 0f
		}
		val forecastFragment = supportFragmentManager
						.findFragmentById(R.id.fragment_forecast) as ForecastFragment
		forecastFragment.setUseTodayLayout(!mTwoPane)
		if (contentUri != null) {
			forecastFragment.setInitialSelectedDate(
							WeatherContract.WeatherEntry.getDateFromUri(contentUri))
		}
		SunshineSyncAdapter.initializeSyncAdapter(this)
		if (checkPlayServices()) {
			val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
			val sentToken = sharedPreferences.getBoolean(SENT_TOKEN_TO_SERVER, false)
			if (!sentToken) {
				val intent = Intent(this, RegistrationIntentService::class.java)
				startService(intent)
			}
		}
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.main, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		val id = item.itemId
		if (id == R.id.action_settings) {
			startActivity(Intent(this, SettingsActivity::class.java))
			return true
		}
		return super.onOptionsItemSelected(item)
	}

	override fun onResume() {
		super.onResume()
		val location = Utility.getPreferredLocation(this)
		if (location != mLocation) {
			val ff = supportFragmentManager.findFragmentById(R.id.fragment_forecast) as? ForecastFragment
			ff?.onLocationChanged()
			val df = supportFragmentManager.findFragmentByTag(DETAIL_FRAGMENT_TAG) as? DetailFragment
			df?.onLocationChanged(location)
			mLocation = location
		}
	}

	override fun onItemSelected(contentUri: Uri, vh: ForecastAdapter.ForecastAdapterViewHolder) {
		if (mTwoPane) {
			val args = Bundle()
			args.putParcelable(DetailFragment.DETAIL_URI, contentUri)
			val fragment = DetailFragment()
			fragment.arguments = args
			supportFragmentManager.beginTransaction()
							.replace(R.id.weather_detail_container, fragment, DETAIL_FRAGMENT_TAG)
							.commit()
		} else {
			val intent = Intent(this, DetailActivity::class.java)
							.setData(contentUri)
			val activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
							Pair(vh.mIconView, getString(R.string.detail_icon_transition_name)))
			ActivityCompat.startActivity(this, intent, activityOptions.toBundle())
		}
	}

	private fun checkPlayServices(): Boolean {
		val apiAvailability = GoogleApiAvailability.getInstance()
		val resultCode = apiAvailability.isGooglePlayServicesAvailable(this)
		if (resultCode != ConnectionResult.SUCCESS) {
			if (apiAvailability.isUserResolvableError(resultCode)) {
				apiAvailability.getErrorDialog(this, resultCode,
								PLAY_SERVICES_RESOLUTION_REQUEST).show()
			} else {
				finish()
			}
			return false
		}
		return true
	}
}
