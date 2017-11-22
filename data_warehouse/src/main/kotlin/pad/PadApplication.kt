package pad

import spark.servlet.SparkApplication

class PadApplication : SparkApplication {
    override fun init() {
        Runner.main(arrayOf())
    }
}