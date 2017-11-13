package pad.controller

import com.baidu.unbiz.fluentvalidator.FluentValidator
import com.baidu.unbiz.fluentvalidator.ResultCollectors.toSimple
import pad.Runner
import pad.model.ErrorMessage
import pad.serialization.DefaultTemplateEngine
import pad.serialization.Format
import pad.service.DataService
import pad.validator.*
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
        get("/students/:id/books", this::getBooks, templateEngine)
        post("/students", this::addStudent, templateEngine)
        post("/students/:id/books", this::addBook, templateEngine)
    }

    fun getBooks(req : Request, res : Response) : ModelAndView {
        return ModelAndView(dataService.getBooks(req.params("id")), req.headers("Accept"))
    }

    fun addBook(req: Request, res: Response): ModelAndView {
        val idStudent = req.params("id")?.toIntOrNull()
        val title = req.queryParams("title")
        val author = req.queryParams("author")
        val desc = req.queryParams("desc")
        val year = req.queryParams("year")?.toIntOrNull()
        val response: Any?
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
            val book = dataService.createBook(idStudent.toString(), title, author, year!!, desc)
            if (book != null)
                response = book
            else
                response = ErrorMessage(400, mutableListOf("Student not found!"))
        } else
            response = ErrorMessage(400, result.errors)
        return ModelAndView(response,req.headers("Accept"))
    }

    fun getStudents(req: Request, res: Response): ModelAndView {
        return ModelAndView(dataService.getStudents(), req.headers("Accept"))
    }

    fun getStudent(req: Request, res: Response): ModelAndView {
        return ModelAndView(dataService.getStudent(req.params("id")), req.headers("Accept"))
    }

    fun addStudent(req: Request, res: Response): ModelAndView {
        val response: Any?
        val studentName = req.queryParams("name")
        val studentPhone = req.queryParams("phone")
        val studentYear = req.queryParams("year")?.toIntOrNull()
        val result = FluentValidator.checkAll()
                .setIsFailFast(false)
                .on(studentName, NameValidator("Name"))
                .on(studentPhone, PhoneValidator())
                .on(studentYear, YearValidator(1900, 2000))
                .doValidate()
                .result(toSimple())
        response = if (result.isSuccess) {
            dataService.createStudent(studentName, studentPhone, studentYear!!)
        } else
            ErrorMessage(400, result.errors)

        return ModelAndView(response, req.headers("Accept"))
    }

}
