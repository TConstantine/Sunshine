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

package constantine.theodoridis.app.sunshine.widget

import android.annotation.TargetApi
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.TaskStackBuilder
import constantine.theodoridis.app.sunshine.R
import constantine.theodoridis.app.sunshine.presentation.forecasts.ForecastsActivity
import constantine.theodoridis.app.sunshine.sync.SunshineSyncAdapter
import constantine.theodoridis.app.sunshine.ui.weatherforecastdetails.WeatherForecastDetailsActivity

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
class DetailWidgetProvider : AppWidgetProvider() {
	override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
		for (appWidgetId in appWidgetIds) {
			val views = RemoteViews(context.packageName, R.layout.widget_detail)
			val intent = Intent(context, ForecastsActivity::class.java)
			val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
			views.setOnClickPendingIntent(R.id.widget, pendingIntent)
			setRemoteAdapter(context, views)
			val useDetailActivity = context.resources.getBoolean(R.bool.use_detail_activity)
			val clickIntentTemplate = if (useDetailActivity)
				Intent(context, WeatherForecastDetailsActivity::class.java)
			else
				Intent(context, ForecastsActivity::class.java)
			val clickPendingIntentTemplate = TaskStackBuilder.create(context)
					.addNextIntentWithParentStack(clickIntentTemplate)
							.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
			views.setPendingIntentTemplate(R.id.widget_list, clickPendingIntentTemplate)
			views.setEmptyView(R.id.widget_list, R.id.widget_empty)
			appWidgetManager.updateAppWidget(appWidgetId, views)
		}
	}

	override fun onReceive(context: Context, intent: Intent) {
		super.onReceive(context, intent)
		if (SunshineSyncAdapter.ACTION_DATA_UPDATED == intent.action) {
			val appWidgetManager = AppWidgetManager.getInstance(context)
			val appWidgetIds = appWidgetManager.getAppWidgetIds(
							ComponentName(context, javaClass))
			appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list)
		}
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private fun setRemoteAdapter(context: Context, views: RemoteViews) {
		views.setRemoteAdapter(R.id.widget_list,
						Intent(context, DetailWidgetRemoteViewsService::class.java))
	}
}
