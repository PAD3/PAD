package pad.http

import com.github.kittinunf.fuel.core.Request

/*val result = "http://localhost:8080/students/1/books".httpGet().response().second
Runner.logger.debug("RESULT = ${String(result.data)}")*/

interface HttpChecker {
    fun checkStatusCode(statusCode : Int)
    fun checkHeaders(headers : Map<String,List<String>>)
    fun checkBody(body : String)
}

fun Request.check(checker : HttpChecker){
    val response = this.response().second
    checker.checkStatusCode(response.statusCode)
    checker.checkHeaders(response.headers)
    checker.checkBody(String(response.data))
}

