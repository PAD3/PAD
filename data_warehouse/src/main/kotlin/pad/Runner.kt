package pad

import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Logger
import pad.controller.DataController
import pad.injection.DaggerMainComponent
import spark.Spark.port

object Runner {
    var mainComponent = DaggerMainComponent.builder()
            .build()

    val logger = Logger.getLogger(Logger::class.java)

    @JvmStatic
    fun main(args: Array<String>) {
        if (args.isNotEmpty()) {
            val port = args[0].toIntOrNull()
            port?.let {
                port(port)
            }
        }
        BasicConfigurator.configure()
        val dataController = DataController()
        dataController.init()
        logger.debug("HEY!")
    }

}
