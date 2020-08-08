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

import constantine.theodoridis.app.sunshine.domain.transform.Transformer
import constantine.theodoridis.app.sunshine.weatherforecastdetails.domain.loadweatherforecastdetails.LoadWeatherForecastDetailsResponse

class WeatherForecastDetailsTransformer :
  Transformer<LoadWeatherForecastDetailsResponse, WeatherForecastDetailsViewModel> {
  override fun transform(input: LoadWeatherForecastDetailsResponse): WeatherForecastDetailsViewModel {
    val transformedMinTemperature = transformTemperature(input.minTemperature)
    val transformedMaxTemperature = transformTemperature(input.maxTemperature)
    return WeatherForecastDetailsViewModel(
      date = transformDate(
        input.dayOfWeek,
        input.month,
        input.dayOfMonth
      ),
      iconId = input.iconId,
      iconDescription = transformIconDescription(
        input.iconDescriptionLabel,
        input.forecast
      ),
      forecast = input.forecast,
      forecastDescription = transformForecastDescription(
        input.forecastDescriptionLabel,
        input.forecast
      ),
      maxTemperature = transformedMaxTemperature,
      maxTemperatureDescription = transformTemperatureDescription(
        input.maxTemperatureDescriptionLabel,
        transformedMaxTemperature
      ),
      minTemperature = transformedMinTemperature,
      minTemperatureDescription = transformTemperatureDescription(
        input.minTemperatureDescriptionLabel,
        transformedMinTemperature
      ),
      humidity = transformHumidity(
        input.humidity
      ),
      pressure = transformPressure(
        input.pressure,
        input.pressureUnit
      ),
      wind = transformWind(
        input.windSpeed,
        input.windSpeedUnit,
        input.windDirection
      )
    )
  }
  
  private fun transformDate(dayOfWeek: String, month: String, dayOfMonth: Int): String {
    return String.format("$dayOfWeek, $month %02d", dayOfMonth)
  }
  
  private fun transformIconDescription(iconDescriptionLabel: String, forecast: String): String {
    return "$iconDescriptionLabel $forecast"
  }
  
  private fun transformForecastDescription(forecastDescriptionLabel: String, forecast: String): String {
    return "$forecastDescriptionLabel $forecast"
  }
  
  private fun transformTemperature(temperature: Float): String {
    return String.format("%1.0f\u00B0", temperature)
  }
  
  private fun transformTemperatureDescription(
    temperatureDescriptionLabel: String,
    transformedTemperature: String): String {
    return "$temperatureDescriptionLabel $transformedTemperature"
  }
  
  private fun transformHumidity(humidity: Float): String {
    return String.format("%1.0f %%", humidity)
  }
  
  private fun transformPressure(pressure: Float, pressureUnit: String): String {
    return String.format("%1.0f $pressureUnit", pressure)
  }
  
  private fun transformWind(windSpeed: Float, windSpeedUnit: String, windDirection: String): String {
    return String.format("%1\$1.0f $windSpeedUnit %2\$s", windSpeed, windDirection)
  }
}
