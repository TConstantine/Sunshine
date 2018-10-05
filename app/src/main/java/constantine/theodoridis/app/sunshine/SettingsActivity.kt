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

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.preference.ListPreference
import android.preference.Preference
import android.preference.PreferenceActivity
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import com.google.android.gms.location.places.ui.PlacePicker
import constantine.theodoridis.app.sunshine.data.WeatherContract
import constantine.theodoridis.app.sunshine.sync.SunshineSyncAdapter

class SettingsActivity : PreferenceActivity(), Preference.OnPreferenceChangeListener, SharedPreferences.OnSharedPreferenceChangeListener {
	companion object {
		const val PLACE_PICKER_REQUEST = 9090
	}

	private var mAttribution: ImageView? = null

	public override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		addPreferencesFromResource(R.xml.pref_general)
		bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_location_key)))
		bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_units_key)))
		bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_art_pack_key)))
		mAttribution = ImageView(this)
		mAttribution!!.setImageResource(R.drawable.powered_by_google_light)
		if (!Utility.isLocationLatLonAvailable(this)) {
			mAttribution!!.visibility = View.GONE
		}
		setListFooter(mAttribution)
	}

	override fun onResume() {
		val sp = PreferenceManager.getDefaultSharedPreferences(this)
		sp.registerOnSharedPreferenceChangeListener(this)
		super.onResume()
	}

	override fun onPause() {
		val sp = PreferenceManager.getDefaultSharedPreferences(this)
		sp.unregisterOnSharedPreferenceChangeListener(this)
		super.onPause()
	}

	private fun bindPreferenceSummaryToValue(preference: Preference) {
		preference.onPreferenceChangeListener = this
		setPreferenceSummary(preference, PreferenceManager
										.getDefaultSharedPreferences(preference.context)
										.getString(preference.key, "")!!)
	}

	private fun setPreferenceSummary(preference: Preference, value: Any) {
		val stringValue = value.toString()
		val key = preference.key
		if (preference is ListPreference) {
			val prefIndex = preference.findIndexOfValue(stringValue)
			if (prefIndex >= 0) {
				preference.setSummary(preference.entries[prefIndex])
			}
		} else if (key == getString(R.string.pref_location_key)) {
			@SunshineSyncAdapter.LocationStatus val status = Utility.getLocationStatus(this)
			when (status) {
				SunshineSyncAdapter.LOCATION_STATUS_OK -> preference.summary = stringValue
				SunshineSyncAdapter.LOCATION_STATUS_UNKNOWN -> preference.summary = getString(R.string.pref_location_unknown_description, value.toString())
				SunshineSyncAdapter.LOCATION_STATUS_INVALID -> preference.summary = getString(R.string.pref_location_error_description, value.toString())
				else -> preference.summary = stringValue
			}
		} else {
			preference.summary = stringValue
		}
	}

	override fun onPreferenceChange(preference: Preference, value: Any): Boolean {
		setPreferenceSummary(preference, value)
		return true
	}

	override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
		when (key) {
			getString(R.string.pref_location_key) -> {
				val editor = sharedPreferences.edit()
				editor.remove(getString(R.string.pref_location_latitude))
				editor.remove(getString(R.string.pref_location_longitude))
				editor.apply()
				if (mAttribution != null) {
					mAttribution!!.visibility = View.GONE
				}
				Utility.resetLocationStatus(this)
				SunshineSyncAdapter.syncImmediately(this)
			}
			getString(R.string.pref_units_key) -> contentResolver.notifyChange(WeatherContract.WeatherEntry.CONTENT_URI, null)
			getString(R.string.pref_location_status_key) -> {
				val locationPreference = findPreference(getString(R.string.pref_location_key))
				bindPreferenceSummaryToValue(locationPreference)
			}
			getString(R.string.pref_art_pack_key) -> contentResolver.notifyChange(WeatherContract.WeatherEntry.CONTENT_URI, null)
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	override fun getParentActivityIntent(): Intent? {
		return super.getParentActivityIntent()!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
		if (requestCode == PLACE_PICKER_REQUEST) {
			if (resultCode == Activity.RESULT_OK) {
				val place = PlacePicker.getPlace(data, this)
				var address = place.address!!.toString()
				val latLong = place.latLng
				if (TextUtils.isEmpty(address)) {
					address = String.format("(%.2f, %.2f)", latLong.latitude, latLong.longitude)
				}
				val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
				val editor = sharedPreferences.edit()
				editor.putString(getString(R.string.pref_location_key), address)
				editor.putFloat(getString(R.string.pref_location_latitude),
								latLong.latitude.toFloat())
				editor.putFloat(getString(R.string.pref_location_longitude),
								latLong.longitude.toFloat())
				editor.apply()
				val locationPreference = findPreference(getString(R.string.pref_location_key))
				setPreferenceSummary(locationPreference, address)
				if (mAttribution != null) {
					mAttribution!!.visibility = View.VISIBLE
				} else {
					val rootView = findViewById<View>(android.R.id.content)
					Snackbar.make(rootView, getString(R.string.attribution_text),
									Snackbar.LENGTH_LONG).show()
				}
				Utility.resetLocationStatus(this)
				SunshineSyncAdapter.syncImmediately(this)
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data)
		}
	}
}
