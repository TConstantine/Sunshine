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

package constantine.theodoridis.app.sunshine.di

import android.content.ContentResolver
import android.content.SharedPreferences
import android.content.res.Resources
import constantine.theodoridis.app.sunshine.data.database.DatabaseDataSource
import constantine.theodoridis.app.sunshine.data.database.SQLiteDatabaseDataSource
import constantine.theodoridis.app.sunshine.data.datasource.PreferenceDataSource
import constantine.theodoridis.app.sunshine.data.datasource.ResourceDataSource
import constantine.theodoridis.app.sunshine.data.datasource.ResourcesDataSource
import constantine.theodoridis.app.sunshine.data.datasource.SharedPreferencesDataSource
import dagger.Module
import dagger.Provides

@Module
class DataSourceModule {
	@Provides
	fun providePreferenceDataSource(sharedPreferences: SharedPreferences): PreferenceDataSource {
		return SharedPreferencesDataSource(sharedPreferences)
	}

	@Provides
	fun provideResourceDataSource(resources: Resources): ResourceDataSource {
		return ResourcesDataSource(resources)
	}

	@Provides
	fun provideDatabaseDataSource(
			contentResolver: ContentResolver
	): DatabaseDataSource {
		return SQLiteDatabaseDataSource(contentResolver)
	}
}