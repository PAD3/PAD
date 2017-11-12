package pad.controller

import com.baidu.unbiz.fluentvalidator.FluentValidator
import com.baidu.unbiz.fluentvalidator.ResultCollectors.toSimple
import pad.Runner
import pad.model.ErrorMessage
import pad.serialization.DefaultTemplateEngine
import pad.serialization.Format
import pad.service.DataService
import pad.validator.NameValidator
import pad.validator.PhoneValidator
import pad.validator.YearValidator
import spark.Filter
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
        after(Filter { req, res ->
            res.header("Content-Type", Format.fromString(req.headers("Accept")).toString())
        })
        get("/students", this::getStudents, templateEngine)
        get("/students/:id", this::getStudent, templateEngine)
        post("/students", this::addStudent, templateEngine)
    }

    fun getStudents(req: Request, res: Response): ModelAndView {
        return ModelAndView(dataService.getStudents(), req.headers("Accept"))
    }

    fun getStudent(req: Request, res: Response) : ModelAndView {
        return ModelAndView(dataService.getStudent(req.params("id")), req.headers("Accept"))
    }

    fun addStudent(req: Request, res: Response): ModelAndView {
        val response: Any?
        val studentName = req.queryParams("name")
        val studentPhone = req.queryParams("name")
        val studentYear = req.queryParams("name").toIntOrNull() ?: 0
        val result = FluentValidator.checkAll()
                .setIsFailFast(false)
                .on(studentName, NameValidator())
                .on(studentPhone, PhoneValidator())
                .on(studentYear, YearValidator())
                .doValidate()
                .result(toSimple())
        response = if (result.isSuccess) {
            dataService.createStudent(studentName, studentYear)
        } else
            ErrorMessage(400, result.errors)

        return ModelAndView(response, req.headers("Accept"))
    }

}
