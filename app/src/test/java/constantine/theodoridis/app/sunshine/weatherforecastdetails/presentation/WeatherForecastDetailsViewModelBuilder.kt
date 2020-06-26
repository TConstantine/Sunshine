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

package constantine.theodoridis.app.sunshine.weatherforecastdetails.presentation

class WeatherForecastDetailsViewModelBuilder {
	private var date = ""
	private var iconId = 0
	private var iconLabel = ""
	private var forecast = ""
	private var forecastDescription = ""
	private var maxTemperature = ""
	private var maxTemperatureLabel = ""
	private var minTemperature = ""
	private var minTemperatureLabel = ""
	private var humidity = ""
	private var pressure = ""
	private var wind = ""

	fun withDate(date: String): WeatherForecastDetailsViewModelBuilder {
		this.date = date
		return this
	}

	fun withIconId(iconId: Int): WeatherForecastDetailsViewModelBuilder {
		this.iconId = iconId
		return this
	}

	fun withIconDescription(iconLabel: String): WeatherForecastDetailsViewModelBuilder {
		this.iconLabel = iconLabel
		return this
	}

	fun withForecast(forecast: String): WeatherForecastDetailsViewModelBuilder {
		this.forecast = forecast
		return this
	}

	fun withForecastDescription(forecastDescription: String): WeatherForecastDetailsViewModelBuilder {
		this.forecastDescription = forecastDescription
		return this
	}

	fun withMaxTemperature(maxTemperature: String): WeatherForecastDetailsViewModelBuilder {
		this.maxTemperature = maxTemperature
		return this
	}

	fun withMaxTemperatureDescription(maxTemperatureLabel: String): WeatherForecastDetailsViewModelBuilder {
		this.maxTemperatureLabel = maxTemperatureLabel
		return this
	}

	fun withMinTemperature(minTemperature: String): WeatherForecastDetailsViewModelBuilder {
		this.minTemperature = minTemperature
		return this
	}

	fun withMinTemperatureDescription(minTemperatureLabel: String): WeatherForecastDetailsViewModelBuilder {
		this.minTemperatureLabel = minTemperatureLabel
		return this
	}

	fun withHumidity(humidity: String): WeatherForecastDetailsViewModelBuilder {
		this.humidity = humidity
		return this
	}

	fun withPressure(pressure: String): WeatherForecastDetailsViewModelBuilder {
		this.pressure = pressure
		return this
	}

	fun withWind(wind: String): WeatherForecastDetailsViewModelBuilder {
		this.wind = wind
		return this
	}

	fun build(): WeatherForecastDetailsViewModel {
		return WeatherForecastDetailsViewModel(
				date = date,
				iconId = iconId,
				iconDescription = iconLabel,
				forecast = forecast,
				forecastDescription = forecastDescription,
				maxTemperature = maxTemperature,
				maxTemperatureDescription = maxTemperatureLabel,
				minTemperature = minTemperature,
				minTemperatureDescription = minTemperatureLabel,
				humidity = humidity,
				pressure = pressure,
				wind = wind
		)
	}
}