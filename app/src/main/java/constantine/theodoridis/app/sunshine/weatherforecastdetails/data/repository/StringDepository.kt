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

package constantine.theodoridis.app.sunshine.weatherforecastdetails.data.repository

import constantine.theodoridis.app.sunshine.R
import constantine.theodoridis.app.sunshine.data.datasource.ResourceDataSource
import constantine.theodoridis.app.sunshine.weatherforecastdetails.domain.repository.StringRepository

class StringDepository(private val resourceDataSource: ResourceDataSource) : StringRepository {
  override fun getIconDescriptionLabel(): String {
    return resourceDataSource.getString(R.string.icon_description_label)
  }
  
  override fun getForecastDescriptionLabel(): String {
    return resourceDataSource.getString(R.string.forecast_description_label)
  }
  
  override fun getMaxTemperatureDescriptionLabel(): String {
    return resourceDataSource.getString(R.string.max_temperature_description_label)
  }
  
  override fun getMinTemperatureDescriptionLabel(): String {
    return resourceDataSource.getString(R.string.min_temperature_description_label)
  }
  
  override fun getPressureUnit(): String {
    return resourceDataSource.getString(R.string.pressure_unit)
  }
  
  override fun getToday(): String {
    return resourceDataSource.getString(R.string.today)
  }
  
  override fun getTomorrow(): String {
    return resourceDataSource.getString(R.string.tomorrow)
  }
  
  override fun getWindSpeedUnitImperial(): String {
    return resourceDataSource.getString(R.string.wind_speed_unit_imperial)
  }
  
  override fun getWindSpeedUnitMetric(): String {
    return resourceDataSource.getString(R.string.wind_speed_unit_metric)
  }
}
