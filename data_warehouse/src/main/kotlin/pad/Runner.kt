package pad

import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Logger
import pad.controller.DataController
import pad.hateoas.HateoasProvider
import pad.injection.DaggerMainComponent
import pad.serialization.Format
import spark.Spark.port


/**
 * Runner for debug and tests
 */
object Runner {
    var mainComponent = DaggerMainComponent.builder()
            .build()

    val logger = Logger.getLogger(Logger::class.java)
    val baseUrl = if (System.getenv("BASE_URL") != null) System.getenv("BASE_URL") else "http://localhost:4567"
    val proxyHost = if (System.getenv("PROXY_HOST") != null) System.getenv("PROXY_HOST") else "localhost:4567"
    val supportedMimeTypes = setOf(Format.JSON, Format.XML, Format.YAML)

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
        HateoasProvider.inspect(dataController)
        dataController.init()
        logger.debug("HEY!")
    }

}
