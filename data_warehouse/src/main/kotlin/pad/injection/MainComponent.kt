package pad.injection

import dagger.Component
import pad.controller.DefaultDataController
import pad.service.DataService

import javax.inject.Singleton


@Singleton
@Component(modules = arrayOf(DatabaseModule::class))
interface MainComponent {
    fun inject(controller: DefaultDataController)
    fun inject(dataService: DataService)
}
