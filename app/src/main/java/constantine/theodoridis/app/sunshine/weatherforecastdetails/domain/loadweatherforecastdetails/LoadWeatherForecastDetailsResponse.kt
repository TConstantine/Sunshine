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

data class LoadWeatherForecastDetailsResponse(
		val dayOfWeek: String,
		val month: String,
		val dayOfMonth: Int,
		val iconId: Int,
		val iconDescriptionLabel: String,
		val forecast: String,
		val forecastDescriptionLabel: String,
		val maxTemperature: Float,
		val maxTemperatureDescriptionLabel: String,
		val minTemperature: Float,
		val minTemperatureDescriptionLabel: String,
		val humidity: Float,
		val pressure: Float,
		val pressureUnit: String,
		val windSpeed: Float,
		val windSpeedUnit: String,
		val windDirection: String
)