package pad.controller

import pad.Runner
import pad.serialization.DefaultTemplateEngine
import pad.service.DataService
import spark.ModelAndView
import spark.Request
import spark.Response

import javax.inject.Inject

import spark.Spark.get
import spark.Spark.post
import spark.TemplateViewRoute

class DefaultDataController : AbstractController(), DataController {
    @Inject
    lateinit var dataService: DataService

    override fun init() {
        Runner.mainComponent.inject(this)
        val templateEngine = DefaultTemplateEngine()
        get("/users", TemplateViewRoute { req, res -> this.getData(req, res) }, templateEngine)
        post("/add", TemplateViewRoute { req, res -> this.createUser(req, res) }, templateEngine)
    }

    fun getData(req: Request, res: Response): ModelAndView {
        return ModelAndView(dataService!!.students, req.headers("Accept"))
    }

    fun createUser(req: Request, res: Response): ModelAndView {
        return ModelAndView(dataService!!.createStudent(req.queryParams("name")), req.headers("Accept"))
    }

}
