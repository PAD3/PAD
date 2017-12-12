package pad.cache

import org.apache.http.HttpHost
import org.apache.http.HttpRequest
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicHttpRequest
import pad.Runner

object CacheValidator {
    fun invalidate(url : String){
        try {
            val httpclient = DefaultHttpClient()
            val httpPurge: HttpRequest = BasicHttpRequest("PURGE", "${Runner.baseUrl}$url")
            val result = httpclient.execute(HttpHost(Runner.proxyHost), httpPurge)
            println(httpPurge.requestLine)
            println(result.allHeaders.asList())
            println(result.statusLine)
        } catch (e : Exception){
            Runner.logger.error(e.message)
        }
    }
}