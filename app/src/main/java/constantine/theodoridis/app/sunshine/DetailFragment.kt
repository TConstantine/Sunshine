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
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.support.v7.widget.Toolbar
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import constantine.theodoridis.app.sunshine.data.WeatherContract

class DetailFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor> {
	companion object {
		internal const val DETAIL_URI = "URI"
		internal const val DETAIL_TRANSITION_ANIMATION = "DTA"
		private const val FORECAST_SHARE_HASHTAG = " #SunshineApp"
		private const val DETAIL_LOADER = 0
		private val DETAIL_COLUMNS = arrayOf(WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry.ID, WeatherContract.WeatherEntry.COLUMN_DATE, WeatherContract.WeatherEntry.COLUMN_SHORT_DESC, WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, WeatherContract.WeatherEntry.COLUMN_HUMIDITY, WeatherContract.WeatherEntry.COLUMN_PRESSURE, WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, WeatherContract.WeatherEntry.COLUMN_DEGREES, WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING)
		const val COL_WEATHER_DATE = 1
		const val COL_WEATHER_MAX_TEMP = 3
		const val COL_WEATHER_MIN_TEMP = 4
		const val COL_WEATHER_HUMIDITY = 5
		const val COL_WEATHER_PRESSURE = 6
		const val COL_WEATHER_WIND_SPEED = 7
		const val COL_WEATHER_DEGREES = 8
		const val COL_WEATHER_CONDITION_ID = 9
	}

	private var mForecast: String? = null
	private var mUri: Uri? = null
	private var mTransitionAnimation: Boolean = false
	private var mIconView: ImageView? = null
	private var mDateView: TextView? = null
	private var mDescriptionView: TextView? = null
	private var mHighTempView: TextView? = null
	private var mLowTempView: TextView? = null
	private var mHumidityView: TextView? = null
	private var mHumidityLabelView: TextView? = null
	private var mWindView: TextView? = null
	private var mWindLabelView: TextView? = null
	private var mPressureView: TextView? = null
	private var mPressureLabelView: TextView? = null

	init {
		setHasOptionsMenu(true)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
														savedInstanceState: Bundle?): View? {
		val arguments = arguments
		if (arguments != null) {
			mUri = arguments.getParcelable(DETAIL_URI)
			mTransitionAnimation = arguments.getBoolean(DETAIL_TRANSITION_ANIMATION, false)
		}
		val rootView = inflater.inflate(R.layout.fragment_detail_start, container, false)
		mIconView = rootView.findViewById<View>(R.id.detail_icon) as ImageView
		mDateView = rootView.findViewById<View>(R.id.detailDateTextView) as TextView
		mDescriptionView = rootView.findViewById<View>(R.id.detailForecastTextView) as TextView
		mHighTempView = rootView.findViewById<View>(R.id.detailHighTextView) as TextView
		mLowTempView = rootView.findViewById<View>(R.id.detailLowTextView) as TextView
		mHumidityView = rootView.findViewById<View>(R.id.detailHumidityTextView) as TextView
		mHumidityLabelView = rootView.findViewById<View>(R.id.detailHumidityLabelTextView) as TextView
		mWindView = rootView.findViewById<View>(R.id.detailWindTextView) as TextView
		mWindLabelView = rootView.findViewById<View>(R.id.detailWindLabelTextView) as TextView
		mPressureView = rootView.findViewById<View>(R.id.detailPressureTextView) as TextView
		mPressureLabelView = rootView.findViewById<View>(R.id.detailPressureLabelTextView) as TextView
		return rootView
	}

	private fun finishCreatingMenu(menu: Menu) {
		val menuItem = menu.findItem(R.id.action_share)
		menuItem.intent = createShareForecastIntent()
	}

	override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
		if (activity is DetailActivity) {
			inflater!!.inflate(R.menu.detailfragment, menu)
			finishCreatingMenu(menu!!)
		}
	}

	private fun createShareForecastIntent(): Intent {
		val shareIntent = Intent(Intent.ACTION_SEND)
		shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
		shareIntent.type = "text/plain"
		shareIntent.putExtra(Intent.EXTRA_TEXT, mForecast!! + FORECAST_SHARE_HASHTAG)
		return shareIntent
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		loaderManager.initLoader(DETAIL_LOADER, null, this)
		super.onActivityCreated(savedInstanceState)
	}

	internal fun onLocationChanged(newLocation: String) {
		val uri = mUri
		if (null != uri) {
			val date = WeatherContract.WeatherEntry.getDateFromUri(uri)
			val updatedUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation, date)
			mUri = updatedUri
			loaderManager.restartLoader(DETAIL_LOADER, null, this)
		}
	}

	override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
		if (null != mUri) {
			return CursorLoader(
							activity!!,
							mUri!!,
							DETAIL_COLUMNS, null, null, null
			)
		}
		val vp = view!!.parent
		if (vp is CardView) {
			(vp as View).visibility = View.INVISIBLE
		}
		return Loader(context!!)
	}

	override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
		if (data != null && data.moveToFirst()) {
			val vp = view!!.parent
			if (vp is CardView) {
				(vp as View).visibility = View.VISIBLE
			}
			val weatherId = data.getInt(COL_WEATHER_CONDITION_ID)
			if (Utility.usingLocalGraphics(context!!)) {
				mIconView!!.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId))
			} else {
				Glide.with(this)
								.load(Utility.getArtUrlForWeatherCondition(context!!, weatherId))
								.error(Utility.getArtResourceForWeatherCondition(weatherId))
								.crossFade()
								.into(mIconView!!)
			}
			val date = data.getLong(COL_WEATHER_DATE)
			val dateText = Utility.getFullFriendlyDayString(context!!, date)
			mDateView!!.text = dateText
			val description = Utility.getStringForWeatherCondition(context!!, weatherId)
			mDescriptionView!!.text = description
			mDescriptionView!!.contentDescription = getString(R.string.a11y_forecast, description)
			mIconView!!.contentDescription = getString(R.string.a11y_forecast_icon, description)
			Utility.isMetric(context!!)
			val high = data.getDouble(COL_WEATHER_MAX_TEMP)
			val highString = Utility.formatTemperature(context!!, high)
			mHighTempView!!.text = highString
			mHighTempView!!.contentDescription = getString(R.string.a11y_high_temp, highString)
			val low = data.getDouble(COL_WEATHER_MIN_TEMP)
			val lowString = Utility.formatTemperature(context!!, low)
			mLowTempView!!.text = lowString
			mLowTempView!!.contentDescription = getString(R.string.a11y_low_temp, lowString)
			val humidity = data.getFloat(COL_WEATHER_HUMIDITY)
			mHumidityView!!.text = activity!!.getString(R.string.format_humidity, humidity)
			val windSpeedStr = data.getFloat(COL_WEATHER_WIND_SPEED)
			val windDirStr = data.getFloat(COL_WEATHER_DEGREES)
			mWindView!!.text = Utility.getFormattedWind(context!!, windSpeedStr, windDirStr)
			val pressure = data.getFloat(COL_WEATHER_PRESSURE)
			mPressureView!!.text = getString(R.string.format_pressure, pressure)
			mForecast = String.format("%s - %s - %s/%s", dateText, description, high, low)

		}
		val activity = activity as AppCompatActivity?
		val toolbarView = view!!.findViewById<View>(R.id.toolbar) as Toolbar
		if (mTransitionAnimation) {
			activity!!.supportStartPostponedEnterTransition()
			activity.setSupportActionBar(toolbarView)
			activity.supportActionBar!!.setDisplayShowTitleEnabled(false)
			activity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
		} else {
			val menu = toolbarView.menu
			menu?.clear()
			toolbarView.inflateMenu(R.menu.detailfragment)
			finishCreatingMenu(toolbarView.menu)
		}
	}

	override fun onLoaderReset(loader: Loader<Cursor>) {}
}