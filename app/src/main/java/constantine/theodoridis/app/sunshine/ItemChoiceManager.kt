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

import android.os.Bundle
import android.os.Parcel
import android.support.v4.util.LongSparseArray
import android.support.v4.view.ViewCompat
import android.support.v7.widget.RecyclerView
import android.util.SparseBooleanArray
import android.widget.AbsListView
import android.widget.Checkable

class ItemChoiceManager(private val mAdapter: RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder>) {
	companion object {
		private const val CHECK_POSITION_SEARCH_DISTANCE = 20
	}

	private val selectedItemsKey = "SIK"
	private var mChoiceMode: Int = 0
	private val mAdapterDataObserver = object : RecyclerView.AdapterDataObserver() {
		override fun onChanged() {
			super.onChanged()
			if (mAdapter.hasStableIds())
				confirmCheckedPositionsById(mAdapter.itemCount)
		}
	}
	private var mCheckStates = SparseBooleanArray()
	private var mCheckedIdStates = LongSparseArray<Int>()
	val selectedItemPosition: Int
		get() = if (mCheckStates.size() == 0) {
			RecyclerView.NO_POSITION
		} else {
			mCheckStates.keyAt(0)
		}

	fun onClick(vh: ForecastAdapter.ForecastAdapterViewHolder) {
		if (mChoiceMode == AbsListView.CHOICE_MODE_NONE)
			return
		val checkedItemCount = mCheckStates.size()
		val position = vh.adapterPosition
		if (position == RecyclerView.NO_POSITION) {
			return
		}
		when (mChoiceMode) {
			AbsListView.CHOICE_MODE_NONE -> {
			}
			AbsListView.CHOICE_MODE_SINGLE -> {
				val checked = mCheckStates.get(position, false)
				if (!checked) {
					for (i in 0 until checkedItemCount) {
						mAdapter.notifyItemChanged(mCheckStates.keyAt(i))
					}
					mCheckStates.clear()
					mCheckStates.put(position, true)
					mCheckedIdStates.clear()
					mCheckedIdStates.put(mAdapter.getItemId(position), position)
				}
				mAdapter.onBindViewHolder(vh, position)
			}
			AbsListView.CHOICE_MODE_MULTIPLE -> {
				val checked = mCheckStates.get(position, false)
				mCheckStates.put(position, !checked)
				mAdapter.onBindViewHolder(vh, position)
			}
			AbsListView.CHOICE_MODE_MULTIPLE_MODAL -> {
				throw RuntimeException("Multiple Modal not implemented in ItemChoiceManager.")
			}
		}
	}

	fun setChoiceMode(choiceMode: Int) {
		if (mChoiceMode != choiceMode) {
			mChoiceMode = choiceMode
			clearSelections()
		}
	}

	private fun isItemChecked(position: Int): Boolean {
		return mCheckStates.get(position)
	}

	private fun clearSelections() {
		mCheckStates.clear()
		mCheckedIdStates.clear()
	}

	internal fun confirmCheckedPositionsById(oldItemCount: Int) {
		mCheckStates.clear()
		var checkedIndex = 0
		while (checkedIndex < mCheckedIdStates.size()) {
			val id = mCheckedIdStates.keyAt(checkedIndex)
			val lastPos = mCheckedIdStates.valueAt(checkedIndex)
			val lastPosId = mAdapter.getItemId(lastPos)
			if (id != lastPosId) {
				val start = Math.max(0, lastPos - CHECK_POSITION_SEARCH_DISTANCE)
				val end = Math.min(lastPos + CHECK_POSITION_SEARCH_DISTANCE, oldItemCount)
				var found = false
				for (searchPos in start until end) {
					val searchId = mAdapter.getItemId(searchPos)
					if (id == searchId) {
						found = true
						mCheckStates.put(searchPos, true)
						mCheckedIdStates.setValueAt(checkedIndex, searchPos)
						break
					}
				}
				if (!found) {
					mCheckedIdStates.delete(id)
					checkedIndex--
				}
			} else {
				mCheckStates.put(lastPos, true)
			}
			checkedIndex++
		}
	}

	fun onBindViewHolder(vh: RecyclerView.ViewHolder, position: Int) {
		val checked = isItemChecked(position)
		if (vh.itemView is Checkable) {
			(vh.itemView as Checkable).isChecked = checked
		}
		ViewCompat.setActivated(vh.itemView, checked)
	}

	fun onRestoreInstanceState(savedInstanceState: Bundle) {
		val states = savedInstanceState.getByteArray(selectedItemsKey)
		if (null != states) {
			val inParcel = Parcel.obtain()
			inParcel.unmarshall(states, 0, states.size)
			inParcel.setDataPosition(0)
			mCheckStates = inParcel.readSparseBooleanArray()
			val numStates = inParcel.readInt()
			mCheckedIdStates.clear()
			for (i in 0 until numStates) {
				val key = inParcel.readLong()
				val value = inParcel.readInt()
				mCheckedIdStates.put(key, value)
			}
			inParcel.recycle()
		}
	}

	fun onSaveInstanceState(outState: Bundle) {
		val outParcel = Parcel.obtain()
		outParcel.writeSparseBooleanArray(mCheckStates)
		val numStates = mCheckedIdStates.size()
		outParcel.writeInt(numStates)
		for (i in 0 until numStates) {
			outParcel.writeLong(mCheckedIdStates.keyAt(i))
			outParcel.writeInt(mCheckedIdStates.valueAt(i))
		}
		val states = outParcel.marshall()
		outState.putByteArray(selectedItemsKey, states)
		outParcel.recycle()
	}
}
