package pad.util

import java.util.UUID
import java.nio.LongBuffer
import javax.crypto.KeyGenerator
import com.sun.tools.jdi.Packet.fromByteArray
import spark.utils.IOUtils.toByteArray


object UIDUtil {
    fun generateUID() = UUID.randomUUID().toString().replace("-","")
}