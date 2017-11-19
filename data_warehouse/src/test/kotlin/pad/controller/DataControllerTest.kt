package pad.controller

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import pad.hateoas.HateoasProvider

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
    fun testPathProcess() {
        println(HateoasProvider.getParamsFromLinkFormat("/students/:studentId/books/:bookId"))
    }

}