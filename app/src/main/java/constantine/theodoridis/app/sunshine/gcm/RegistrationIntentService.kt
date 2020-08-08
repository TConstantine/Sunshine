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

package constantine.theodoridis.app.sunshine.gcm

import android.app.IntentService
import android.content.Intent
import android.preference.PreferenceManager
import constantine.theodoridis.app.sunshine.presentation.weatherforecastlist.ForecastsActivity

class RegistrationIntentService : IntentService(TAG) {
	companion object {
		private const val TAG = "RegIntentService"
	}

	override fun onHandleIntent(intent: Intent?) {
		val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
		try {
			synchronized(TAG) {
				sharedPreferences.edit().putBoolean(ForecastsActivity.SENT_TOKEN_TO_SERVER, true).apply()
			}
		} catch (e: Exception) {
			sharedPreferences.edit().putBoolean(ForecastsActivity.SENT_TOKEN_TO_SERVER, false).apply()
		}
	}
}
