package pad.util

import java.util.*


object UIDUtil {
    fun generateUID() = UUID.randomUUID().toString().replace("-","")
}