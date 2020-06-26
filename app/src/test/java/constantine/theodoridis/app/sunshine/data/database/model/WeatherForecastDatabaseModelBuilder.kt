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

package constantine.theodoridis.app.sunshine.data.database.model

object WeatherForecastDatabaseModelBuilder {
	private var locationSettingId = 0
	private var dateInMilliseconds = 0L
	private var weatherId = 0
	private var maxTemperature = 0.0F
	private var minTemperature = 0.0F
	private var humidity = 0.0F
	private var pressure = 0.0F
	private var windSpeed = 0.0F
	private var windDegrees = 0.0F

	fun withDate(dateInMilliseconds: Long): WeatherForecastDatabaseModelBuilder {
		this.dateInMilliseconds = dateInMilliseconds
		return this
	}

	fun withWeatherId(weatherId: Int): WeatherForecastDatabaseModelBuilder {
		this.weatherId = weatherId
		return this
	}

	fun withHumidity(humidity: Float): WeatherForecastDatabaseModelBuilder {
		this.humidity = humidity
		return this
	}

	fun withPressure(pressure: Float): WeatherForecastDatabaseModelBuilder {
		this.pressure = pressure
		return this
	}

	fun withMaxTemperature(maxTemperature: Float): WeatherForecastDatabaseModelBuilder {
		this.maxTemperature = maxTemperature
		return this
	}

	fun withMinTemperature(minTemperature: Float): WeatherForecastDatabaseModelBuilder {
		this.minTemperature = minTemperature
		return this
	}

	fun withWindSpeed(windSpeed: Float): WeatherForecastDatabaseModelBuilder {
		this.windSpeed = windSpeed
		return this
	}

	fun withWindDegrees(windDegrees: Float): WeatherForecastDatabaseModelBuilder {
		this.windDegrees = windDegrees
		return this
	}

	fun build(): WeatherForecastDatabaseModel {
		return WeatherForecastDatabaseModel(
				locationSettingId = locationSettingId,
				dateInMilliseconds = dateInMilliseconds,
				weatherId = weatherId,
				maxTemperature = maxTemperature,
				minTemperature = minTemperature,
				humidity = humidity,
				pressure = pressure,
				windSpeed = windSpeed,
				windDegrees = windDegrees
		)
	}
}