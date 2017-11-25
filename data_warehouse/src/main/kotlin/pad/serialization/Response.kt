package pad.serialization

import com.sun.org.apache.xpath.internal.operations.Mod
import org.eclipse.jetty.http.HttpStatus
import pad.Runner
import pad.dto.Dto
import pad.hateoas.HateoasProvider
import pad.hateoas.Link
import spark.ModelAndView
import spark.Request
import spark.Response
import spark.Spark.halt

data class ResponseMessage(val success: Boolean, val error: List<String>,
                           val payload: Any? = null, val links: List<Link> = listOf())

class ResponseBuilder(req: Request, private val res: Response) {
    var code: Int = HttpStatus.OK_200
        private set
    var error: MutableList<String> = mutableListOf()
        private set
    var header: Format? = null
        private set
    var response: Any? = null
        private set
    private val globalLinks: MutableList<Link> = HateoasProvider.getLinks(req).toMutableList()

    init {
        val acceptHeader = req.headers("Accept")
        this.header = if (acceptHeader == null) Format.JSON else Format.parseHeader(req.headers("Accept"))
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

    fun ignoreParam(param: String): ResponseBuilder {
        Runner.logger.debug(" param = $param   BEFORE IGNORE: $globalLinks")
        globalLinks.removeAll { it.hasParam(param) }
        Runner.logger.debug("AFTER IGNORE: $globalLinks")
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

    fun response(response: Dto?): ResponseBuilder {
        response?.apply {
            this.links = HateoasProvider.getLinks(this, globalLinks)
        }
        this.response = response
        return this
    }

    fun response(response: List<Dto>?): ResponseBuilder {
        response?.apply {
            this.forEach { it.links = HateoasProvider.getLinks(it, globalLinks) }
        }
        this.response = response
        return this
    }

    fun getModel(): ModelAndView {
        res.status(code)
        val isSuccess = isSuccessCode(code)
        if (code == HttpStatus.NO_CONTENT_204)
            halt(HttpStatus.NO_CONTENT_204)
        if (code == HttpStatus.CREATED_201)
            res.header("Location", (response as Dto).links.firstOrNull { it.rel == "self" }?.href)
        return ModelAndView(ResponseMessage(isSuccess, error, response, if (isSuccess) globalLinks else listOf()), header.toString())
    }

    private fun isSuccessCode(code: Int): Boolean = code < 400


}