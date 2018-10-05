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

import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import constantine.theodoridis.app.sunshine.data.WeatherContract

class ForecastAdapter(private val mContext: Context, private val mClickHandler: ForecastAdapterOnClickHandler, private val mEmptyView: View, choiceMode: Int) : RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder>() {
	companion object {
		private const val VIEW_TYPE_TODAY = 0
		private const val VIEW_TYPE_FUTURE_DAY = 1
	}

	private var mUseTodayLayout = true
	var cursor: Cursor? = null
		private set
	private val mICM: ItemChoiceManager = ItemChoiceManager(this)
	val selectedItemPosition: Int
		get() = mICM.selectedItemPosition

	inner class ForecastAdapterViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
		val mIconView: ImageView = view.findViewById<View>(R.id.list_item_icon) as ImageView
		val mDateView: TextView = view.findViewById<View>(R.id.listItemDateTextView) as TextView
		val mDescriptionView: TextView = view.findViewById<View>(R.id.listItemForecastTextView) as TextView
		val mHighTempView: TextView = view.findViewById<View>(R.id.listItemHighTextView) as TextView
		val mLowTempView: TextView = view.findViewById<View>(R.id.listItemLowTextView) as TextView

		init {
			view.setOnClickListener(this)
		}

		override fun onClick(v: View) {
			val adapterPosition = adapterPosition
			cursor!!.moveToPosition(adapterPosition)
			val dateColumnIndex = cursor!!.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATE)
			mClickHandler.onClick(cursor!!.getLong(dateColumnIndex), this)
			mICM.onClick(this)
		}
	}

	interface ForecastAdapterOnClickHandler {
		fun onClick(date: Long?, vh: ForecastAdapterViewHolder)
	}

	init {
		mICM.setChoiceMode(choiceMode)
	}

	override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ForecastAdapterViewHolder {
		if (viewGroup is RecyclerView) {
			var layoutId = -1
			when (viewType) {
				VIEW_TYPE_TODAY -> {
					layoutId = R.layout.list_item_forecast_today
				}
				VIEW_TYPE_FUTURE_DAY -> {
					layoutId = R.layout.list_item_forecast
				}
			}
			val view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, false)
			view.isFocusable = true
			return ForecastAdapterViewHolder(view)
		} else {
			throw RuntimeException("Not bound to RecyclerView")
		}
	}

	override fun onBindViewHolder(forecastAdapterViewHolder: ForecastAdapterViewHolder, position: Int) {
		cursor!!.moveToPosition(position)
		val weatherId = cursor!!.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID)
		val defaultImage: Int
		val useLongToday: Boolean
		when (getItemViewType(position)) {
			VIEW_TYPE_TODAY -> {
				defaultImage = Utility.getArtResourceForWeatherCondition(weatherId)
				useLongToday = true
			}
			else -> {
				defaultImage = Utility.getIconResourceForWeatherCondition(weatherId)
				useLongToday = false
			}
		}
		if (Utility.usingLocalGraphics(mContext)) {
			forecastAdapterViewHolder.mIconView.setImageResource(defaultImage)
		} else {
			Glide.with(mContext)
							.load(Utility.getArtUrlForWeatherCondition(mContext, weatherId))
							.error(defaultImage)
							.crossFade()
							.into(forecastAdapterViewHolder.mIconView)
		}
		ViewCompat.setTransitionName(forecastAdapterViewHolder.mIconView, "iconView$position")
		val dateInMillis = cursor!!.getLong(ForecastFragment.COL_WEATHER_DATE)
		forecastAdapterViewHolder.mDateView.text = Utility.getFriendlyDayString(mContext, dateInMillis, useLongToday)
		val description = Utility.getStringForWeatherCondition(mContext, weatherId)
		forecastAdapterViewHolder.mDescriptionView.text = description
		forecastAdapterViewHolder.mDescriptionView.contentDescription = mContext.getString(R.string.a11y_forecast, description)
		val high = cursor!!.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP)
		val highString = Utility.formatTemperature(mContext, high)
		forecastAdapterViewHolder.mHighTempView.text = highString
		forecastAdapterViewHolder.mHighTempView.contentDescription = mContext.getString(R.string.a11y_high_temp, highString)
		val low = cursor!!.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP)
		val lowString = Utility.formatTemperature(mContext, low)
		forecastAdapterViewHolder.mLowTempView.text = lowString
		forecastAdapterViewHolder.mLowTempView.contentDescription = mContext.getString(R.string.a11y_low_temp, lowString)
		mICM.onBindViewHolder(forecastAdapterViewHolder, position)
	}

	fun onRestoreInstanceState(savedInstanceState: Bundle) {
		mICM.onRestoreInstanceState(savedInstanceState)
	}

	fun onSaveInstanceState(outState: Bundle) {
		mICM.onSaveInstanceState(outState)
	}

	fun setUseTodayLayout(useTodayLayout: Boolean) {
		mUseTodayLayout = useTodayLayout
	}

	override fun getItemViewType(position: Int): Int {
		return if (position == 0 && mUseTodayLayout) VIEW_TYPE_TODAY else VIEW_TYPE_FUTURE_DAY
	}

	override fun getItemCount(): Int {
		return if (null == cursor) 0 else cursor!!.count
	}

	fun swapCursor(newCursor: Cursor) {
		cursor = newCursor
		notifyDataSetChanged()
		mEmptyView.visibility = if (itemCount == 0) View.VISIBLE else View.GONE
	}

	fun selectView(viewHolder: RecyclerView.ViewHolder) {
		if (viewHolder is ForecastAdapterViewHolder) {
			viewHolder.onClick(viewHolder.itemView)
		}
	}
}