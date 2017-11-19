package pad.hateoas

import pad.dto.Dto
import kotlin.reflect.KClass

data class HateoasNode(val rel: String, val linkFormat: String, val params: List<String> = listOf(), val dtos: List<KClass<out Dto>>) {
    val linkRegex: Regex = formatToRegex(linkFormat, params)

    private fun formatToRegex(linkFormat: String, params: List<String>): Regex {
        var result: String = linkFormat
        for (name in params) {
            result = result.replace(":$name", "\\w+")
        }
        return result.toRegex()
    }
}