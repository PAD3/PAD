package pad.controller

import com.baidu.unbiz.fluentvalidator.FluentValidator
import com.baidu.unbiz.fluentvalidator.ResultCollectors.toSimple
import pad.Runner
import pad.model.ErrorMessage
import pad.serialization.DefaultTemplateEngine
import pad.service.DataService
import pad.validator.NameValidator
import pad.validator.PhoneValidator
import spark.ModelAndView
import spark.Request
import spark.Response
import spark.Spark.*
import javax.inject.Inject

class DefaultDataController : AbstractController(), DataController {
    @Inject
    lateinit var dataService: DataService

    override fun init() {
        Runner.mainComponent.inject(this)
        val templateEngine = DefaultTemplateEngine()
        get("/users", { req, res -> this.getData(req, res) }, templateEngine)
        post("/add", { req, res -> this.createUser(req, res) }, templateEngine)
    }

    fun getData(req: Request, res: Response): ModelAndView {
        return ModelAndView(dataService.students, req.headers("Accept"))
    }

    fun createUser(req: Request, res: Response): ModelAndView {
        val result = FluentValidator.checkAll()
                .on(req.queryParams("name"), NameValidator())
                .on(req.queryParams("phone"), PhoneValidator())
                .doValidate()
                .result(toSimple())
        var response: Any?
        if (result.isSuccess) {
            response = dataService.createStudent(req.queryParams("name"))
        } else
            response = ErrorMessage(400, result.errors)
        res.status(400)
        return ModelAndView(response, req.headers("Accept"))
    }

}
