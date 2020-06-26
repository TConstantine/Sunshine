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

class LoadWeatherForecastDetailsResponseBuilder {
	private var dayOfWeek = ""
	private var month = ""
	private var dateOfMonth = 0
	private var iconId = 0
	private var iconDescriptionLabel = ""
	private var forecast = ""
	private var forecastDescriptionLabel = ""
	private var maxTemperature = 0.0F
	private var maxTemperatureDescriptionLabel = ""
	private var minTemperature = 0.0F
	private var minTemperatureDescriptionLabel = ""
	private var humidity = 0.0F
	private var pressure = 0.0F
	private var pressureUnit = ""
	private var windSpeed = 0.0F
	private var windSpeedUnit = ""
	private var windDirection = ""

	fun withDayOfWeek(dayOfWeek: String): LoadWeatherForecastDetailsResponseBuilder {
		this.dayOfWeek = dayOfWeek
		return this
	}

	fun withMonth(month: String): LoadWeatherForecastDetailsResponseBuilder {
		this.month = month
		return this
	}

	fun withDateOfMonth(dateOfMonth: Int): LoadWeatherForecastDetailsResponseBuilder {
		this.dateOfMonth = dateOfMonth
		return this
	}

	fun withIconId(iconId: Int): LoadWeatherForecastDetailsResponseBuilder {
		this.iconId = iconId
		return this
	}

	fun withIconDescriptionLabel(iconDescriptionLabel: String): LoadWeatherForecastDetailsResponseBuilder {
		this.iconDescriptionLabel = iconDescriptionLabel
		return this
	}

	fun withForecast(forecast: String): LoadWeatherForecastDetailsResponseBuilder {
		this.forecast = forecast
		return this
	}

	fun withForecastDescriptionLabel(forecastDescriptionLabel: String): LoadWeatherForecastDetailsResponseBuilder {
		this.forecastDescriptionLabel = forecastDescriptionLabel
		return this
	}

	fun withTemperature(temperature: Float): LoadWeatherForecastDetailsResponseBuilder {
		maxTemperature = temperature
		minTemperature = temperature
		return this
	}

	fun withTemperatureDescriptionLabel(temperatureDescriptionLabel: String): LoadWeatherForecastDetailsResponseBuilder {
		maxTemperatureDescriptionLabel = temperatureDescriptionLabel
		minTemperatureDescriptionLabel = temperatureDescriptionLabel
		return this
	}

	fun withHumidity(humidity: Float): LoadWeatherForecastDetailsResponseBuilder {
		this.humidity = humidity
		return this
	}

	fun withPressure(pressure: Float): LoadWeatherForecastDetailsResponseBuilder {
		this.pressure = pressure
		return this
	}

	fun withPressureUnit(pressureUnit: String): LoadWeatherForecastDetailsResponseBuilder {
		this.pressureUnit = pressureUnit
		return this
	}

	fun withWindSpeed(windSpeed: Float): LoadWeatherForecastDetailsResponseBuilder {
		this.windSpeed = windSpeed
		return this
	}

	fun withWindSpeedUnit(windSpeedUnit: String): LoadWeatherForecastDetailsResponseBuilder {
		this.windSpeedUnit = windSpeedUnit
		return this
	}

	fun withWindDirection(windDirection: String): LoadWeatherForecastDetailsResponseBuilder {
		this.windDirection = windDirection
		return this
	}

	fun build(): LoadWeatherForecastDetailsResponse {
		return LoadWeatherForecastDetailsResponse(
				dayOfWeek = dayOfWeek,
				month = month,
				dayOfMonth = dateOfMonth,
				iconId = iconId,
				iconDescriptionLabel = iconDescriptionLabel,
				forecast = forecast,
				forecastDescriptionLabel = forecastDescriptionLabel,
				maxTemperature = maxTemperature,
				maxTemperatureDescriptionLabel = maxTemperatureDescriptionLabel,
				minTemperature = minTemperature,
				minTemperatureDescriptionLabel = minTemperatureDescriptionLabel,
				humidity = humidity,
				pressure = pressure,
				pressureUnit = pressureUnit,
				windSpeedUnit = windSpeedUnit,
				windSpeed = windSpeed,
				windDirection = windDirection
		)
	}
}