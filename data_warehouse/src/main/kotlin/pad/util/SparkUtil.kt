package pad.util

import pad.Runner
import pad.serialization.Format
import spark.Request
import java.net.URLDecoder
import java.util.LinkedHashMap

fun Request.bodyParams(): BodyParams {
    if (Format.fromString(contentType()) != Format.URL_FORM_ENCODED) {
        return BodyParams(mapOf())
    }
    val query = body()
    val queryPairs = LinkedHashMap<String, String>()
    val pairs = query.split("&".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
    for (pair in pairs) {
        val idx = pair.indexOf("=")
        queryPairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
                URLDecoder.decode(pair.substring(idx + 1), "UTF-8"))
    }
    return BodyParams(queryPairs)
}

class BodyParams(private val params: Map<String, String>) {
    fun int(name: String): Int {
        return params[name]?.toIntOrNull() ?: Int.MIN_VALUE
    }

    fun string(name: String): String {
        return params[name] ?: ""
    }
}