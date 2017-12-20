package pad

import spark.servlet.SparkApplication


/**
 * Application class for servlet (used in deployment scope)
 */
class PadApplication : SparkApplication {
    override fun init() {
        Runner.main(arrayOf())
    }
}