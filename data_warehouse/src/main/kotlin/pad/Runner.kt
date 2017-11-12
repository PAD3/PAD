package pad

import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Logger
import pad.controller.DefaultDataController
import pad.injection.DaggerMainComponent

object Runner {
    var mainComponent = DaggerMainComponent.builder()
            .build()

    val logger = Logger.getLogger(Logger::class.java)

    @JvmStatic
    fun main(args: Array<String>) {
        BasicConfigurator.configure()
        /*logger.level = Level.DEBUG*/
        val dataController = DefaultDataController()
        dataController.init()
        logger.debug("HEY!")
    }

}
