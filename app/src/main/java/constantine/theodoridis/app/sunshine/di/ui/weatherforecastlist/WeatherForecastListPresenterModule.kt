package constantine.theodoridis.app.sunshine.di.ui.weatherforecastlist

import constantine.theodoridis.app.sunshine.domain.usecase.GetGeoLocationUseCase
import constantine.theodoridis.app.sunshine.presentation.weatherforecastlist.MapLocationPresenter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import io.reactivex.Scheduler

@Module
@InstallIn(ActivityComponent::class)
class WeatherForecastListPresenterModule {
  @Provides
  fun provideMapLocationPresenter(
    useCase: GetGeoLocationUseCase,
    scheduler: Scheduler
  ): MapLocationPresenter {
    return MapLocationPresenter(useCase, scheduler)
  }
}
