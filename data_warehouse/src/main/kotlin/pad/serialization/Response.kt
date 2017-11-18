package pad.serialization

import org.eclipse.jetty.http.HttpStatus
import pad.Runner
import spark.ModelAndView
import spark.Request
import spark.Response
import spark.Spark.halt

data class ResponseMessage(val code: Int, val error: List<String>,
                           val response: Any? = null)

class ResponseBuilder(req: Request, private val res: Response) {
    var code: Int = HttpStatus.OK_200
        private set
    var error: MutableList<String> = mutableListOf()
        private set
    var header: Format? = null
        private set
    var response: Any? = null
        private set

    init {
        this.header = Format.parseHeader(req.headers("Accept"))
        Runner.logger.error("HEAD::: ${req.headers("Accept")} ~~~  ${this.header}")
        if (this.header == Format.INVALID) {
            halt(HttpStatus.NOT_ACCEPTABLE_406)
        } else
            res.header("Content-Type", this.header.toString())
    }

    fun header(header: Pair<String, String?>): ResponseBuilder {
        this.res.header(header.first, header.second)
        return this
    }

    fun code(code: Int): ResponseBuilder {
        this.code = code
        return this
    }

    fun error(error: List<String>): ResponseBuilder {
        this.error.addAll(error)
        return this
    }

    fun error(error: String?): ResponseBuilder {
        error?.apply { this@ResponseBuilder.error.add(this) }
        return this
    }

    fun response(response: Any?): ResponseBuilder {
        this.response = response
        return this
    }

    fun getModel() = ModelAndView(ResponseMessage(code, error, response), header.toString())

    fun getResponseMessage() = ResponseMessage(code, error, response)

}