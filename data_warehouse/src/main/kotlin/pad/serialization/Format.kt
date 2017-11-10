package pad.serialization

import pad.serialization.Format.Constants.HTML_NAME
import pad.serialization.Format.Constants.JSON_NAME
import pad.serialization.Format.Constants.XML_NAME

enum class Format private constructor(private val customName: String) {

    JSON(JSON_NAME), XML(XML_NAME), HTML(HTML_NAME);

    override fun toString(): String {
        return this.name
    }

    object Constants {
        internal val JSON_NAME = "application/json"
        internal val XML_NAME = "application/xml"
        internal val HTML_NAME = "text/html"
    }

    companion object {

        fun fromString(name: String): Format {
            when (name) {
                XML_NAME -> return XML
                HTML_NAME -> return HTML
                else -> return JSON
            }
        }
    }

}
