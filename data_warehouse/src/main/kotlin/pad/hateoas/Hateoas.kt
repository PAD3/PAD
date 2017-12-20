package pad.hateoas

import pad.dto.Dto
import kotlin.reflect.KClass

annotation class Hateoas(val rel: String = "self", val linkFormat: String = "",
                         val rootForDto : Array<KClass< out Dto>> = arrayOf())