package pad

import pad.controller.DefaultDataController
import pad.injection.DaggerMainComponent

object Runner {
    var mainComponent = DaggerMainComponent.builder()
            .build()

    @JvmStatic
    fun main(args: Array<String>) {
        val dataController = DefaultDataController()
        dataController.init()
    }

}
