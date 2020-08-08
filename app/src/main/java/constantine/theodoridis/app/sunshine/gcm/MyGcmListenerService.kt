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

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.android.gms.gcm.GcmListenerService
import constantine.theodoridis.app.sunshine.R
import constantine.theodoridis.app.sunshine.presentation.weatherforecastlist.ForecastsActivity
import org.json.JSONException
import org.json.JSONObject

class MyGcmListenerService : GcmListenerService() {
  companion object {
    private const val TAG = "MyGcmListenerService"
    private const val EXTRA_DATA = "data"
    private const val EXTRA_WEATHER = "weather"
    private const val EXTRA_LOCATION = "location"
    const val NOTIFICATION_ID = 1
  }
  
  override fun onMessageReceived(from: String?, data: Bundle?) {
    if (!data!!.isEmpty) {
      val senderId = getString(R.string.gcm_defaultSenderId)
      if (senderId.isEmpty()) {
        Toast.makeText(this, "SenderID string needs to be set", Toast.LENGTH_LONG).show()
      }
      if (senderId == from) {
        try {
          val jsonObject = JSONObject(data.getString(EXTRA_DATA))
          val weather = jsonObject.getString(EXTRA_WEATHER)
          val location = jsonObject.getString(EXTRA_LOCATION)
          val alert = String.format(getString(R.string.gcm_weather_alert), weather, location)
          sendNotification(alert)
        } catch (e: JSONException) {
        }
      }
    }
  }
  
  private fun sendNotification(message: String) {
    val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val contentIntent = PendingIntent.getActivity(this, 0, Intent(this, ForecastsActivity::class.java), 0)
    val largeIcon = BitmapFactory.decodeResource(this.resources, R.drawable.art_storm)
    val mBuilder = NotificationCompat.Builder(this)
      .setSmallIcon(R.drawable.art_clear)
      .setLargeIcon(largeIcon)
      .setContentTitle("Weather Alert!")
      .setStyle(NotificationCompat.BigTextStyle().bigText(message))
      .setContentText(message)
      .setPriority(NotificationCompat.PRIORITY_HIGH)
    mBuilder.setContentIntent(contentIntent)
    mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build())
  }
}
