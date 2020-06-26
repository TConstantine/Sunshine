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

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import constantine.theodoridis.app.sunshine.R
import constantine.theodoridis.app.sunshine.data.WeatherContract
import constantine.theodoridis.app.sunshine.di.AndroidInjection
import constantine.theodoridis.app.sunshine.weatherforecastdetails.presentation.WeatherForecastDetailsPresenter
import constantine.theodoridis.app.sunshine.weatherforecastdetails.presentation.WeatherForecastDetailsViewModel
import javax.inject.Inject

class WeatherForecastDetailsFragment : Fragment() {
	companion object {
		internal const val DETAIL_URI = "URI"
		internal const val DETAIL_TRANSITION_ANIMATION = "DTA"
		private const val FORECAST_SHARE_HASHTAG = "#SunshineApp"
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

	@Inject
	lateinit var presenter: WeatherForecastDetailsPresenter

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

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		if (activity is WeatherForecastDetailsActivity) {
			inflater.inflate(R.menu.forecast_menu, menu)
			finishCreatingMenu(menu)
		}
	}

	private fun createShareForecastIntent(): Intent {
		val shareIntent = Intent(Intent.ACTION_SEND)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
		} else {
			shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
		}
		shareIntent.type = "text/plain"
		if (mForecast == null) {
			shareIntent.putExtra(Intent.EXTRA_TEXT, FORECAST_SHARE_HASHTAG)
		} else {
			shareIntent.putExtra(Intent.EXTRA_TEXT, "$mForecast $FORECAST_SHARE_HASHTAG")
		}
		return shareIntent
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		AndroidInjection.inject(this)
		val vp = view!!.parent
		if (vp is CardView) {
			(vp as View).visibility = View.INVISIBLE
		}
		val location = mUri!!.pathSegments[mUri!!.pathSegments.size - 2]
		val date = mUri!!.lastPathSegment!!.toLong()
		presenter.weatherForecastDetails()
				.observe(viewLifecycleOwner, Observer { displayWeatherForecastDetails(it) })
		presenter.loadWeatherForecastDetails(location, date)
		super.onActivityCreated(savedInstanceState)
	}

	internal fun onLocationChanged(newLocation: String) {
		val uri = mUri
		if (null != uri) {
			val date = WeatherContract.WeatherEntry.getDateFromUri(uri)
			val updatedUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation, date)
			mUri = updatedUri
		}
	}

	private fun displayWeatherForecastDetails(viewModel: WeatherForecastDetailsViewModel) {
		val vp = view!!.parent
		if (vp is CardView) {
			(vp as View).visibility = View.VISIBLE
		}
		mDateView!!.text = viewModel.date
		mIconView!!.setImageResource(viewModel.iconId)
		mIconView!!.contentDescription = viewModel.iconDescription
//		if (Utility.usingLocalGraphics(context!!)) {
//			mIconView!!.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId))
//		} else {
//			Glide.with(this)
//					.load(Utility.getArtUrlForWeatherCondition(context!!, weatherId))
//					.error(Utility.getArtResourceForWeatherCondition(weatherId))
//					.transition(withCrossFade())
//					.into(mIconView!!)
//		}
		mDescriptionView!!.text = viewModel.forecast
		mDescriptionView!!.contentDescription = viewModel.forecastDescription
		mHighTempView!!.text = viewModel.maxTemperature
		mHighTempView!!.contentDescription = viewModel.maxTemperatureDescription
		mLowTempView!!.text = viewModel.minTemperature
		mLowTempView!!.contentDescription = viewModel.minTemperatureDescription
		mHumidityView!!.text = viewModel.humidity
		mPressureView!!.text = viewModel.pressure
		mWindView!!.text = viewModel.wind
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
			toolbarView.inflateMenu(R.menu.forecast_menu)
			finishCreatingMenu(toolbarView.menu)
		}
	}
}