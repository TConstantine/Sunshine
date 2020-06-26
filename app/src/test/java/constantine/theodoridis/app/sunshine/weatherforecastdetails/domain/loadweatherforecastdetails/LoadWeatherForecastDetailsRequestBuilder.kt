/*
 *  Copyright (C) 2020 Constantine Theodoridis
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

package constantine.theodoridis.app.sunshine.weatherforecastdetails.domain.loadweatherforecastdetails

object LoadWeatherForecastDetailsRequestBuilder {
	private var location = ""
	private var dateInMilliseconds = 0L

	fun withLocation(location: String): LoadWeatherForecastDetailsRequestBuilder {
		LoadWeatherForecastDetailsRequestBuilder.location = location
		return this
	}

	fun withDate(dateInMilliseconds: Long): LoadWeatherForecastDetailsRequestBuilder {
		LoadWeatherForecastDetailsRequestBuilder.dateInMilliseconds = dateInMilliseconds
		return this
	}

	fun build(): LoadWeatherForecastDetailsRequest {
		return LoadWeatherForecastDetailsRequest(
				location = location,
				dateInMilliseconds = dateInMilliseconds
		)
	}
}