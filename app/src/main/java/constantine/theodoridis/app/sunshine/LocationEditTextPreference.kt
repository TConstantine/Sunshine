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

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.preference.EditTextPreference
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.ui.PlacePicker

class LocationEditTextPreference(context: Context, attrs: AttributeSet) : EditTextPreference(context, attrs) {
	companion object {
		private const val DEFAULT_MINIMUM_LOCATION_LENGTH = 2
	}

	private var mMinLength: Int = 0

	init {
		val a = context.theme.obtainStyledAttributes(attrs, R.styleable.LocationEditTextPreference, 0, 0)
		try {
			mMinLength = a.getInteger(R.styleable.LocationEditTextPreference_minLength, DEFAULT_MINIMUM_LOCATION_LENGTH)
		} finally {
			a.recycle()
		}
		val apiAvailability = GoogleApiAvailability.getInstance()
		val resultCode = apiAvailability.isGooglePlayServicesAvailable(getContext())
		if (resultCode == ConnectionResult.SUCCESS) {
			widgetLayoutResource = R.layout.pref_current_location
		}
	}

	override fun onCreateView(parent: ViewGroup): View {
		val view = super.onCreateView(parent)
		val currentLocation = view.findViewById<View>(R.id.current_location)
		currentLocation.setOnClickListener {
			val context = context
			val builder = PlacePicker.IntentBuilder()
			val settingsActivity = context as SettingsActivity
			try {
				settingsActivity.startActivityForResult(builder.build(settingsActivity), SettingsActivity.PLACE_PICKER_REQUEST)
			} catch (e: GooglePlayServicesNotAvailableException) {
			} catch (e: GooglePlayServicesRepairableException) {
			}
		}
		return view
	}

	override fun showDialog(state: Bundle?) {
		super.showDialog(state)
		val et = editText
		et.hint = context.resources.getString(R.string.location_edit_text_hint)
		et.addTextChangedListener(object : TextWatcher {
			override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
			}

			override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
			}

			override fun afterTextChanged(s: Editable) {
				val d = dialog
				if (d is AlertDialog) {
					val positiveButton = d.getButton(AlertDialog.BUTTON_POSITIVE)
					positiveButton.isEnabled = s.length >= mMinLength
				}
			}
		})
	}
}
