package pad.hateoas

data class HateoasNode(val rel : String, val linkFormat : String, val params : List<String> = listOf())