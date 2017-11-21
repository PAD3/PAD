package pad.hateoas

import pad.Runner
import pad.dto.Dto
import spark.Request
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

object HateoasProvider {
    val nodes: MutableList<HateoasNode> = mutableListOf()
    private val restCache: MutableMap<HateoasNode, List<HateoasNode>> = mutableMapOf()
    fun inspect(subject: Any) {
        nodes.addAll(subject.javaClass.methods
                .map { it.getAnnotationsByType(Hateoas::class.java) }
                .filter { it.isNotEmpty() }
                .map {
                    val annotation = it[0]
                    HateoasNode(annotation.rel, annotation.linkFormat, getParamsFromLinkFormat(annotation.linkFormat), annotation.rootForDto.toList())
                }
                .toList())
    }

    fun getLinks(req: Request): List<Link> {
        Runner.logger.debug("req params = ${req.params()}")
        val params = req.params()
        val pathInfo = req.pathInfo()
        val node = nodes.firstOrNull { pathInfo.matches(it.linkRegex) } ?: return listOf()
        if (restCache[node] == null)
            restCache[node] = nodes.asSequence().filter { it != node && node.params.containsAll(it.params) }.toList()
        val links = restCache[node]?.map { Link(it.rel, makeLink(it.linkFormat, params)) }?.toMutableList() ?: return listOf()
        links.add(Link("self", makeLink(node.linkFormat, params)))
        println(node)
        return links
    }

    fun getLinks(dto: Dto, topLinks: List<Link>): List<Link> {
        val alreadyLinks = topLinks.map { it.href }
        Runner.logger.debug("already get = $topLinks ${dto.javaClass.name}")
        val params = getParamsFromDto(dto)
        Runner.logger.debug("params = $params")
        val node = nodes.firstOrNull { it.dtos.contains(dto::class) } ?: return listOf()
        var links = nodes.asSequence().filter { it != node && node.params.containsAll(it.params) }
                .map { Link(it.rel, makeLink(it.linkFormat, params)) }.toMutableList()
        links = links.filter { !alreadyLinks.contains(it.href) }.toMutableList()
        links.add(Link("self", makeLink(node.linkFormat, params)))
        return links
    }

    fun getParamsFromLinkFormat(linkFormat: String): List<String> {
        return "/:\\w+(/|$)".toRegex().findAll(linkFormat)
                .filter { it.groupValues.isNotEmpty() }
                .map {
                    it.groupValues.toList()
                }
                .flatten()
                .map {
                    it.replace("[/:]".toRegex(), "")
                }
                .filter { it.isNotEmpty() }
                .toList()
    }


    private fun getParamsFromDto(dto: Any): Map<String, String> {
        val params = mutableMapOf<String, String>()
        dto::class.memberProperties.forEach {
            val annotation = it.findAnnotation<HateoasParam>()
            if (annotation != null) {
                val field = dto.javaClass.getDeclaredField(it.name)
                field.isAccessible = true
                params.put(if (annotation.name == "default") it.name else annotation.name, field.get(dto).toString())
            }
        }
        return params
    }

    private fun makeLink(linkFormat: String, params: Map<String, String>): String {
        var result: String = linkFormat.replace(":", "")
        Runner.logger.debug("making link $linkFormat    $params")
        for ((name, value) in params) {
            Runner.logger.debug("replacing $name with $value")
            result = result.replace(name.removePrefix(":"), value, ignoreCase = true)
        }
        return "${Runner.baseUrl}$result"
    }
}