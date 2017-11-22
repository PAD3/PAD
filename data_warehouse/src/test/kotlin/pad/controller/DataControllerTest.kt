package pad.controller

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import pad.hateoas.HateoasProvider
import pad.serialization.Format

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class DataControllerTest {


    @AfterAll()
    fun teardown() {

    }

    @Test
    fun testAnnotations() {
        HateoasProvider.inspect(DataController())
        println(HateoasProvider.nodes)
        println()
    }

    @Test
    fun testParse(){
        val header = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8";
        println("RESULT = "+Format.parseHeader(header))
    }

    @Test
    fun testPathProcess() {
        println(HateoasProvider.getParamsFromLinkFormat("/students/:studentId/books/:bookId"))
    }

}