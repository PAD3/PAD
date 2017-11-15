package pad.serialization

import org.eclipse.jetty.http.HttpStatus
import spark.ModelAndView
import spark.Spark.halt

data class ResponseMessage(val code: Int, val error: List<String>,
                           val response: Any? = null)

class ResponseBuilder {
    var code: Int = HttpStatus.OK_200
        private set
    var error: MutableList<String> = mutableListOf()
        private set
    var header : String? = null
        private set
    var response: Any? = null
        private set

    fun code(code: Int): ResponseBuilder {
        this.code = code
        return this
    }

    fun error(error: List<String>): ResponseBuilder {
        this.error.addAll(error)
        return this
    }

    fun error(error : String) : ResponseBuilder {
        this.error.add(error)
        return this
    }

    fun header(header : String): ResponseBuilder {
        this.header = header
        if (header == "application/jora")
            halt(HttpStatus.NOT_ACCEPTABLE_406)
        return this
    }

    fun response(response: Any?): ResponseBuilder {
        this.response = response
        return this
    }

    fun getModel() = ModelAndView(ResponseMessage(code, error, response),header)

    fun getResponseMessage() = ResponseMessage(code, error, response)

}