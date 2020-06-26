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

import constantine.theodoridis.app.sunshine.domain.entity.Direction
import constantine.theodoridis.app.sunshine.domain.entity.TemperatureUnit
import constantine.theodoridis.app.sunshine.domain.repository.WeatherForecastRepository
import constantine.theodoridis.app.sunshine.domain.usecase.UseCase
import constantine.theodoridis.app.sunshine.weatherforecastdetails.domain.repository.StringRepository
import constantine.theodoridis.app.sunshine.weatherforecastdetails.domain.repository.TemperatureUnitRepository
import java.util.*

class LoadWeatherForecastDetailsUseCase(
		private val weatherForecastRepository: WeatherForecastRepository,
		private val temperatureUnitRepository: TemperatureUnitRepository,
		private val stringRepository: StringRepository
) : UseCase<LoadWeatherForecastDetailsRequest, LoadWeatherForecastDetailsResponse> {
	override fun execute(request: LoadWeatherForecastDetailsRequest): LoadWeatherForecastDetailsResponse {
		val weatherForecast = weatherForecastRepository.getWeatherForecast(request.location, request.dateInMilliseconds)
		val temperatureUnit = temperatureUnitRepository.getTemperatureUnit()
		val calendar = Calendar.getInstance()
		calendar.timeInMillis = weatherForecast.dateInMilliseconds
		return LoadWeatherForecastDetailsResponse(
				dayOfWeek = transformDayOfWeek(calendar),
				month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()),
				dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH),
				iconId = weatherForecast.iconId,
				iconDescriptionLabel = stringRepository.getIconDescriptionLabel(),
				forecast = weatherForecast.forecast,
				forecastDescriptionLabel = stringRepository.getForecastDescriptionLabel(),
				maxTemperature = transformTemperature(temperatureUnit, weatherForecast.maxTemperature),
				maxTemperatureDescriptionLabel = stringRepository.getMaxTemperatureDescriptionLabel(),
				minTemperature = transformTemperature(temperatureUnit, weatherForecast.minTemperature),
				minTemperatureDescriptionLabel = stringRepository.getMinTemperatureDescriptionLabel(),
				humidity = weatherForecast.humidity,
				pressure = weatherForecast.pressure,
				pressureUnit = stringRepository.getPressureUnit(),
				windSpeedUnit = getWindSpeedUnit(temperatureUnit),
				windSpeed = transformWindSpeed(temperatureUnit, weatherForecast.windSpeed),
				windDirection = transformWindDirection(weatherForecast.windDegrees)
		)
	}

	private fun transformDayOfWeek(calendar: Calendar): String {
		return when {
			isToday(calendar) -> {
				stringRepository.getToday()
			}
			isTomorrow(calendar) -> {
				stringRepository.getTomorrow()
			}
			else -> {
				calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
			}
		}
	}

	private fun isToday(calendar: Calendar): Boolean {
		val currentCalendar = Calendar.getInstance()
		return currentCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
				&& currentCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
				&& currentCalendar.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)
	}

	private fun isTomorrow(calendar: Calendar): Boolean {
		val currentCalendar = Calendar.getInstance()
		return currentCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
				&& currentCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
				&& currentCalendar.get(Calendar.DAY_OF_MONTH) + 1 == calendar.get(Calendar.DAY_OF_MONTH)
	}

	private fun transformTemperature(temperatureUnit: TemperatureUnit, temperature: Float): Float {
		return when (temperatureUnit) {
			TemperatureUnit.METRIC -> temperature
			else -> temperature * 1.8F + 32
		}
	}

	private fun getWindSpeedUnit(temperatureUnit: TemperatureUnit): String {
		return when (temperatureUnit) {
			TemperatureUnit.METRIC -> stringRepository.getWindSpeedUnitMetric()
			else -> stringRepository.getWindSpeedUnitImperial()
		}
	}

	private fun transformWindSpeed(temperatureUnit: TemperatureUnit, windSpeed: Float): Float {
		return when (temperatureUnit) {
			TemperatureUnit.METRIC -> windSpeed
			else -> String.format("%1.0f", windSpeed * .621371192237334F).toFloat()
		}
	}

	private fun transformWindDirection(windDegrees: Float): String {
		return when {
			windDegrees >= 22.5 && windDegrees < 67.5 -> Direction.NE.toString()
			windDegrees >= 67.5 && windDegrees < 112.5 -> Direction.E.toString()
			windDegrees >= 112.5 && windDegrees < 157.5 -> Direction.SE.toString()
			windDegrees >= 157.5 && windDegrees < 202.5 -> Direction.S.toString()
			windDegrees >= 202.5 && windDegrees < 247.5 -> Direction.SW.toString()
			windDegrees >= 247.5 && windDegrees < 292.5 -> Direction.W.toString()
			windDegrees >= 292.5 && windDegrees < 337.5 -> Direction.NW.toString()
			else -> Direction.N.toString()
		}
	}
}