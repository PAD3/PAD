package pad.controller

import com.github.kittinunf.fuel.httpGet
import org.eclipse.jetty.http.HttpStatus
import org.junit.jupiter.api.*

import org.junit.jupiter.api.Assertions.*
import pad.Runner
import pad.http.HttpChecker
import pad.http.check

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class DataControllerTest {


    @BeforeAll
    fun setup() {
        Runner.main(arrayOf("8080"))
    }

    @AfterAll()
    fun teardown() {

    }

    @Test
    fun getBooks() {
        val result = "http://localhost:8080/students/1/books".httpGet().check(object : HttpChecker {
            override fun checkStatusCode(statusCode: Int) {
                assertEquals(statusCode, HttpStatus.OK_200)
            }

            override fun checkHeaders(headers: Map<String, List<String>>) {

            }

            override fun checkBody(body: String) {

            }
        })
    }

}