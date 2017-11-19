package pad.controller

import com.github.kittinunf.fuel.httpGet
import org.eclipse.jetty.http.HttpStatus
import org.junit.jupiter.api.*

import org.junit.jupiter.api.Assertions.*
import pad.Runner
import pad.hateoas.Hateoas
import pad.hateoas.HateoasNode
import pad.http.HttpChecker
import pad.http.check

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class DataControllerTest {


    @AfterAll()
    fun teardown() {

    }

    @Test
    fun testAnnotations() {
        val dataController = DataController()
        println(dataController.javaClass.methods
                .map { it.getAnnotationsByType(Hateoas::class.java) }
                .filter { it.isNotEmpty() }
                .map {
                    val annotation = it[0]
                    HateoasNode(annotation.rel, annotation.linkFormat, annotation.params.toList())
                }
                .toList())
    }

}