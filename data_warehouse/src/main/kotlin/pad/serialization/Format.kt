package pad.serialization

import pad.Runner

enum class Format constructor(private val customName: String) {

    JSON("application/json"), XML("application/xml"), YAML("application/x-yaml"), INVALID("invalid");

    override fun toString(): String = this.customName

    companion object {

        val mediaTypeRegex = "(\\*/\\*)|([a-z-]+/\\*)|([a-z-]+/[a-z-]+)".toRegex()
        val qRegex = "q=[01]?(.\\d+)?".toRegex()

        fun fromString(name: String?): Format = when (name) {
            XML.toString() -> XML
            YAML.toString() -> YAML
            JSON.toString(), "application/*", null, "", "*/*" -> JSON
            else -> INVALID
        }

        fun parseHeader(header: String): Format {
            val resultHeader = header.replace(" ", "")
                    .split(",").asSequence()
                    .filter { mediaTypeRegex.find(it) != null }
                    .map {
                        val mediaType = mediaTypeRegex.find(it)!!.groupValues.first()
                        val (type, subtype) = mediaType.split("/")
                        val q = qRegex.find(it)?.groupValues?.first()?.split("=")?.get(1)?.toFloatOrNull() ?: 1f
                        return@map FormatVariant(type, subtype, q)
                    }
                    .filter { it.mime != INVALID }
                    .sortedWith(Comparator { o1, o2 ->
                        val compResult = o2.q.compareTo(o1.q)
                        if (compResult != 0) {
                            return@Comparator compResult
                        } else {
                            return@Comparator if (o2.hasSpecialSubType)
                                if (o1.hasSpecialSubType) 0 else
                                    1 else if (o1.hasSpecialSubType) -1 else 0
                        }
                    }).firstOrNull()?.mime
            return resultHeader ?: INVALID
        }
    }

    private data class FormatVariant(val type: String, val subType: String, val q: Float) {
        override fun toString(): String = "$type/$subType"
        val hasSpecialSubType = subType != "*"
        val mime = Format.fromString(toString())
    }

}
