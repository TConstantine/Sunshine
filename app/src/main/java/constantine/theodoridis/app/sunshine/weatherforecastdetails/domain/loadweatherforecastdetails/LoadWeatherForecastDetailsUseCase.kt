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
  companion object {
    private const val CELSIUS_TO_FAHRENHEIT_MULTIPLIER = 1.8F
    private const val CELSIUS_TO_FAHRENHEIT_DIFFERENCE = 32
    private const val METRIC_TO_IMPERIAL_WIND_SPEED_MODIFIER = .621371192237334F
    private const val WIND_DEGREES_22_AND_HALF = 22.5
    private const val WIND_DEGREES_67_AND_HALF = 67.5
    private const val WIND_DEGREES_112_AND_HALF = 112.5
    private const val WIND_DEGREES_157_AND_HALF = 157.5
    private const val WIND_DEGREES_202_AND_HALF = 202.5
    private const val WIND_DEGREES_247_AND_HALF = 247.5
    private const val WIND_DEGREES_292_AND_HALF = 292.5
    private const val WIND_DEGREES_337_AND_HALF = 337.5
  }
  
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
      else -> temperature * CELSIUS_TO_FAHRENHEIT_MULTIPLIER + CELSIUS_TO_FAHRENHEIT_DIFFERENCE
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
      else -> String.format("%1.0f", windSpeed * METRIC_TO_IMPERIAL_WIND_SPEED_MODIFIER).toFloat()
    }
  }
  
  private fun transformWindDirection(windDegrees: Float): String {
    return when {
      windDegrees >= WIND_DEGREES_22_AND_HALF && windDegrees < WIND_DEGREES_67_AND_HALF -> Direction.NE.toString()
      windDegrees >= WIND_DEGREES_67_AND_HALF && windDegrees < WIND_DEGREES_112_AND_HALF -> Direction.E.toString()
      windDegrees >= WIND_DEGREES_112_AND_HALF && windDegrees < WIND_DEGREES_157_AND_HALF -> Direction.SE.toString()
      windDegrees >= WIND_DEGREES_157_AND_HALF && windDegrees < WIND_DEGREES_202_AND_HALF -> Direction.S.toString()
      windDegrees >= WIND_DEGREES_202_AND_HALF && windDegrees < WIND_DEGREES_247_AND_HALF -> Direction.SW.toString()
      windDegrees >= WIND_DEGREES_247_AND_HALF && windDegrees < WIND_DEGREES_292_AND_HALF -> Direction.W.toString()
      windDegrees >= WIND_DEGREES_292_AND_HALF && windDegrees < WIND_DEGREES_337_AND_HALF -> Direction.NW.toString()
      else -> Direction.N.toString()
    }
  }
}
