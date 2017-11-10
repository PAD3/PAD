package pad.serialization

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import spark.ModelAndView
import spark.TemplateEngine

class DefaultTemplateEngine : TemplateEngine() {

    override fun render(modelAndView: ModelAndView): String {
        when (Format.fromString(modelAndView.viewName)) {
            Format.XML ->
                    return XmlMapper().writeValueAsString(modelAndView.model)
            Format.YAML ->
                    return YAMLMapper().writeValueAsString(modelAndView.model)
            else -> {
                return ObjectMapper().writeValueAsString(modelAndView.model)

            }
        }
    }
}
