package pad.hateoas

import com.fasterxml.jackson.annotation.JsonIgnore
import pad.Runner

data class Link(val rel: String, @JsonIgnore val linkFormat: String, private var params: Map<String, String>) {
    val href: String

    init {
        var result: String = linkFormat.replace(":", "")
        params = params.mapKeys { it.key.replace(":", "").toLowerCase() }
        val mutableParams = params.toMutableMap()
        val iterator = mutableParams.iterator()
        while (iterator.hasNext()) {
            val pair = iterator.next()
            val newResult = result.replace(pair.key, pair.value, ignoreCase = true)
            if (result == newResult)
                iterator.remove()
            result = newResult
        }
        href = "${Runner.baseUrl}$result"
        params = mutableParams
    }

    fun hasParam(param: String?): Boolean {
        if (param == null)
            return false
        return params.containsKey(param.toLowerCase())
    }
}