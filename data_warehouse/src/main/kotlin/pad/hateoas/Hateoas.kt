package pad.hateoas

annotation class Hateoas (val rel : String, val linkFormat : String, val params : Array<String> = arrayOf())