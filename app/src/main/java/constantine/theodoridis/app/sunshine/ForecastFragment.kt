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
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.AppBarLayout
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v4.view.ViewCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.*
import android.widget.AbsListView
import android.widget.TextView
import android.widget.Toast
import constantine.theodoridis.app.sunshine.data.WeatherContract
import constantine.theodoridis.app.sunshine.di.AndroidInjection
import constantine.theodoridis.app.sunshine.sync.SunshineSyncAdapter
import constantine.theodoridis.app.sunshine.ui.forecasts.ForecastsContract
import javax.inject.Inject

class ForecastFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor>,
				SharedPreferences.OnSharedPreferenceChangeListener, ForecastsContract.View {
	companion object {
		private const val FORECAST_LOADER = 0
		private val FORECAST_COLUMNS = arrayOf(WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry.ID, WeatherContract.WeatherEntry.COLUMN_DATE, WeatherContract.WeatherEntry.COLUMN_SHORT_DESC, WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING, WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, WeatherContract.LocationEntry.COLUMN_COORDINATE_LATITUDE, WeatherContract.LocationEntry.COLUMN_COORDINATE_LONGITUDE)
		internal const val COL_WEATHER_DATE = 1
		internal const val COL_WEATHER_MAX_TEMP = 3
		internal const val COL_WEATHER_MIN_TEMP = 4
		internal const val COL_WEATHER_CONDITION_ID = 6
	}

	private var mForecastAdapter: ForecastAdapter? = null
	private var mRecyclerView: RecyclerView? = null
	private var mUseTodayLayout: Boolean = false
	private var mAutoSelectView: Boolean = false
	private var mChoiceMode: Int = 0
	private var mHoldForTransition: Boolean = false
	private var mInitialSelectedDate: Long = -1

	@Inject
	lateinit var presenter: ForecastsContract.Presenter

	interface Callback {
		fun onItemSelected(dateUri: Uri, vh: ForecastAdapter.ForecastAdapterViewHolder)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setHasOptionsMenu(true)
	}

	override fun onResume() {
		val sp = PreferenceManager.getDefaultSharedPreferences(activity)
		sp.registerOnSharedPreferenceChangeListener(this)
		super.onResume()
	}

	override fun onPause() {
		val sp = PreferenceManager.getDefaultSharedPreferences(activity)
		sp.unregisterOnSharedPreferenceChangeListener(this)
		super.onPause()
	}

	override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
		inflater!!.inflate(R.menu.forecastfragment, menu)
	}

	override fun onOptionsItemSelected(item: MenuItem?): Boolean {
		val id = item!!.itemId
		if (id == R.id.action_map) {
			presenter.onDisplayMapLocation()
			return true
		}
		return super.onOptionsItemSelected(item)
	}

	override fun onInflate(context: Context?, attrs: AttributeSet?, savedInstanceState: Bundle?) {
		super.onInflate(context, attrs, savedInstanceState)
		val a = activity!!.obtainStyledAttributes(attrs, R.styleable.ForecastFragment,
						0, 0)
		mChoiceMode = a.getInt(R.styleable.ForecastFragment_android_choiceMode, AbsListView.CHOICE_MODE_NONE)
		mAutoSelectView = a.getBoolean(R.styleable.ForecastFragment_autoSelectView, false)
		mHoldForTransition = a.getBoolean(R.styleable.ForecastFragment_sharedElementTransitions, false)
		a.recycle()
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
														savedInstanceState: Bundle?): View? {
		val rootView = inflater.inflate(R.layout.fragment_main, container, false)
		mRecyclerView = rootView.findViewById<View>(R.id.recyclerViewForecast) as RecyclerView
		mRecyclerView!!.layoutManager = LinearLayoutManager(activity)
		val emptyView = rootView.findViewById<View>(R.id.recyclerViewForecastEmpty)
		mRecyclerView!!.setHasFixedSize(true)
		mForecastAdapter = ForecastAdapter(activity!!, object : ForecastAdapter.ForecastAdapterOnClickHandler {
			override fun onClick(date: Long?, vh: ForecastAdapter.ForecastAdapterViewHolder) {
				val locationSetting = Utility.getPreferredLocation(context!!)
				(activity as Callback)
								.onItemSelected(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
												locationSetting, date!!),
												vh
								)
			}
		}, emptyView, mChoiceMode)
		mRecyclerView!!.adapter = mForecastAdapter
		val parallaxView = rootView.findViewById<View>(R.id.parallax_bar)
		if (null != parallaxView) {
			mRecyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
				@TargetApi(Build.VERSION_CODES.HONEYCOMB)
				override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
					super.onScrolled(recyclerView, dx, dy)
					val max = parallaxView.height
					if (dy > 0) {
						parallaxView.translationY = Math.max((-max).toFloat(), parallaxView.translationY - dy / 2)
					} else {
						parallaxView.translationY = Math.min(0f, parallaxView.translationY - dy / 2)
					}
				}
			})
		}
		val appbarView = rootView.findViewById<View>(R.id.appbar) as? AppBarLayout
		if (appbarView != null) {
			ViewCompat.setElevation(appbarView, 0f)
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			mRecyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
				@TargetApi(Build.VERSION_CODES.LOLLIPOP)
				override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
					if (0 == mRecyclerView!!.computeVerticalScrollOffset()) {
						appbarView?.elevation = 0f
					} else {
						appbarView?.elevation = appbarView?.targetElevation!!
					}
				}
			})
		}
		if (savedInstanceState != null) {
			mForecastAdapter!!.onRestoreInstanceState(savedInstanceState)
		}
		mForecastAdapter!!.setUseTodayLayout(mUseTodayLayout)
		return rootView
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		AndroidInjection.inject(this, this)
		if (mHoldForTransition) {
			activity!!.supportPostponeEnterTransition()
		}
		loaderManager.initLoader(FORECAST_LOADER, null, this)
		super.onActivityCreated(savedInstanceState)
	}

	override fun onStop() {
		super.onStop()
		presenter.unSubscribe()
	}

	internal fun onLocationChanged() {
		loaderManager.restartLoader(FORECAST_LOADER, null, this)
	}

	override fun onDestroy() {
		super.onDestroy()
		if (null != mRecyclerView) {
			mRecyclerView!!.clearOnScrollListeners()
		}
	}

	override fun onSaveInstanceState(outState: Bundle) {
		mForecastAdapter!!.onSaveInstanceState(outState)
		super.onSaveInstanceState(outState)
	}

	override fun onCreateLoader(i: Int, bundle: Bundle?): Loader<Cursor> {
		val sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC"
		val locationSetting = Utility.getPreferredLocation(context!!)
		val weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
						locationSetting, System.currentTimeMillis())
		return CursorLoader(activity!!,
						weatherForLocationUri,
						FORECAST_COLUMNS, null, null,
						sortOrder)
	}

	override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {
		mForecastAdapter!!.swapCursor(data)
		updateEmptyView()
		if (data.count == 0) {
			activity!!.supportStartPostponedEnterTransition()
		} else {
			mRecyclerView!!.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
				override fun onPreDraw(): Boolean {
					if (mRecyclerView!!.childCount > 0) {
						mRecyclerView!!.viewTreeObserver.removeOnPreDrawListener(this)
						var position = mForecastAdapter!!.selectedItemPosition
						if (position == RecyclerView.NO_POSITION && -1L != mInitialSelectedDate) {
							val newData = mForecastAdapter!!.cursor
							val count = newData!!.count
							val dateColumn = newData.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATE)
							for (i in 0 until count) {
								newData.moveToPosition(i)
								if (newData.getLong(dateColumn) == mInitialSelectedDate) {
									position = i
									break
								}
							}
						}
						if (position == RecyclerView.NO_POSITION) position = 0
						mRecyclerView!!.smoothScrollToPosition(position)
						val vh = mRecyclerView!!.findViewHolderForAdapterPosition(position)
						if (null != vh && mAutoSelectView) {
							mForecastAdapter!!.selectView(vh)
						}
						if (mHoldForTransition) {
							activity!!.supportStartPostponedEnterTransition()
						}
						return true
					}
					return false
				}
			})
		}
	}

	override fun onLoaderReset(loader: Loader<Cursor>) {
		mForecastAdapter!!.swapCursor(null!!)
	}

	override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
		if (key == getString(R.string.pref_location_status_key)) {
			updateEmptyView()
		}
	}

	override fun displayMapLocation(geoLocation: String) {
		val intent = Intent(ACTION_VIEW, Uri.parse(geoLocation))
		if (intent.resolveActivity(activity!!.packageManager) != null) {
			startActivity(intent)
		}
	}

	override fun displayInvalidLocationError() {
		Toast.makeText(context, R.string.invalid_location_message, Toast.LENGTH_SHORT).show()
	}

	fun setUseTodayLayout(useTodayLayout: Boolean) {
		mUseTodayLayout = useTodayLayout
		if (mForecastAdapter != null) {
			mForecastAdapter!!.setUseTodayLayout(mUseTodayLayout)
		}
	}

	fun setInitialSelectedDate(initialSelectedDate: Long) {
		mInitialSelectedDate = initialSelectedDate
	}

	private fun updateEmptyView() {
		if (mForecastAdapter!!.itemCount == 0) {
			val tv = view!!.findViewById<View>(R.id.recyclerViewForecastEmpty) as TextView
			var message = R.string.empty_forecast_list
			@SunshineSyncAdapter.LocationStatus val location = Utility.getLocationStatus(context!!)
			when (location) {
				SunshineSyncAdapter.LOCATION_STATUS_SERVER_DOWN -> message = R.string.empty_forecast_list_server_down
				SunshineSyncAdapter.LOCATION_STATUS_SERVER_INVALID -> message = R.string.empty_forecast_list_server_error
				SunshineSyncAdapter.LOCATION_STATUS_INVALID -> message = R.string.empty_forecast_list_invalid_location
				else -> if (!Utility.isNetworkAvailable(activity!!)) {
					message = R.string.empty_forecast_list_no_network
				}
			}
			tv.setText(message)
		}
	}
}