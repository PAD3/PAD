package pad.controller

import com.baidu.unbiz.fluentvalidator.FluentValidator
import com.baidu.unbiz.fluentvalidator.ResultCollectors.toSimple
import org.eclipse.jetty.http.HttpStatus
import pad.Runner
import pad.serialization.DefaultTemplateEngine
import pad.serialization.Format
import pad.serialization.ResponseBuilder
import pad.service.DataService
import pad.validator.*
import spark.Filter
import spark.ModelAndView
import spark.Request
import spark.Response
import spark.Spark.*
import javax.inject.Inject

class DataController : AbstractController() {
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
        get("/students/:id/books", this::getBooks, templateEngine)
        post("/students", this::addStudent, templateEngine)
        post("/students/:id/books", this::addBook, templateEngine)
    }

    fun getBooks(req: Request, res: Response): ModelAndView {
        val responseBuilder = ResponseBuilder(req, res)
        val response = dataService.getBooks(req.params("id"))
        responseBuilder.response(response.body)
        return responseBuilder.getModel()
    }


    fun addBook(req: Request, res: Response): ModelAndView {
        val idStudent = req.params("id")?.toIntOrNull()
        val title = req.queryParams("title")
        val author = req.queryParams("author")
        val desc = req.queryParams("desc")
        val year = req.queryParams("year")?.toIntOrNull()
        val responseBuilder = ResponseBuilder(req, res)
        val result = FluentValidator.checkAll()
                .setIsFailFast(false)
                .on(idStudent, IdValidator("idStudent"))
                .on(title, NameValidator("Title"))
                .on(author, NameValidator("Author"))
                .on(desc, DescValidator())
                .on(year, YearValidator(1000, 2017))
                .doValidate()
                .result(toSimple())
        if (result.isSuccess) {
            val response = dataService.createBook(idStudent.toString(), title, author, year!!, desc)
            if (response.body != null) {
                responseBuilder
                        .response(response.body)
                        .code(HttpStatus.OK_200)
            } else {
                responseBuilder
                        .code(HttpStatus.BAD_REQUEST_400)
                        .error(response.errorMessage)
            }
        } else {
            responseBuilder
                    .error(result.errors)
        }
        return responseBuilder.getModel()
    }

    fun getStudents(req: Request, res: Response): ModelAndView {
        val responseBuilder = ResponseBuilder(req, res)
        val response = dataService.getStudents()
        responseBuilder.response(response.body)
        return responseBuilder.getModel()
    }

    fun getStudent(req: Request, res: Response): ModelAndView {
        val responseBuilder = ResponseBuilder(req, res)
        val response = dataService.getStudent(req.params("id"))
        responseBuilder.response(response.body)
        if (response.body == null)
            responseBuilder
                    .code(HttpStatus.NOT_FOUND_404)
                    .error("Student not found!")
        return responseBuilder.getModel()
    }

    fun addStudent(req: Request, res: Response): ModelAndView {
        val studentName = req.queryParams("name")
        val studentPhone = req.queryParams("phone")
        val studentYear = req.queryParams("year")?.toIntOrNull()
        val responseBuilder = ResponseBuilder(req, res)
        val result = FluentValidator.checkAll()
                .setIsFailFast(false)
                .on(studentName, NameValidator("Name"))
                .on(studentPhone, PhoneValidator())
                .on(studentYear, YearValidator(1900, 2000))
                .doValidate()
                .result(toSimple())
        if (result.isSuccess) {
            val response = dataService.createStudent(studentName, studentPhone, studentYear!!)
            responseBuilder
                    .code(HttpStatus.CREATED_201)
                    .response(response.body)
                    .header(Pair("Location", response.link))
        } else
            responseBuilder
                    .error(result.errors)
                    .code(HttpStatus.BAD_REQUEST_400)
        return responseBuilder.getModel()
    }

}
