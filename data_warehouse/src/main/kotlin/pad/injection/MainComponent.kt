package pad.injection

import dagger.Component
import pad.controller.DataController
import pad.service.DataService

import javax.inject.Singleton


@Singleton
@Component(modules = arrayOf(DatabaseModule::class))
interface MainComponent {
    fun inject(controller: DataController)
    fun inject(dataService: DataService)
}
