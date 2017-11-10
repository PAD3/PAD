package pad.serialization

import pad.serialization.Format.Constants.YAML_NAME
import pad.serialization.Format.Constants.JSON_NAME
import pad.serialization.Format.Constants.XML_NAME

enum class Format constructor(private val customName: String) {

    JSON(JSON_NAME), XML(XML_NAME), YAML(YAML_NAME);

    override fun toString(): String {
        return this.name
    }

    object Constants {
        internal val JSON_NAME = "application/json"
        internal val XML_NAME = "application/xml"
        internal val YAML_NAME = "application/x-yaml"
    }

    companion object {

        fun fromString(name: String): Format {
            when (name) {
                XML_NAME -> return XML
                YAML_NAME -> return YAML
                else -> return JSON
            }
        }
    }

}
