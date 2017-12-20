package pad.serialization

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class FormatTest {
    @Test
    fun parse() {
        val str = "application/xml,application/json,application/*"
        println(Format.parseHeader(str))
    }

}