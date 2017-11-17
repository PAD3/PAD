package pad.serialization

enum class Format constructor(private val customName: String) {

    JSON("application/json"), XML("application/xml"), YAML("application/x-yaml"), INVALID("invalid");

    override fun toString(): String = this.customName

    companion object {

        fun fromString(name: String): Format = when (name) {
            XML.toString() -> XML
            YAML.toString() -> YAML
            JSON.toString() -> JSON
            else -> INVALID
        }
    }

}
