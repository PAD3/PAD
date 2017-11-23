package pad.controller

import com.baidu.unbiz.fluentvalidator.FluentValidator
import com.baidu.unbiz.fluentvalidator.ResultCollectors.toSimple
import org.eclipse.jetty.http.HttpStatus
import pad.Runner
import pad.dto.BookDto
import pad.dto.StudentDto
import pad.hateoas.Hateoas
import pad.serialization.DefaultTemplateEngine
import pad.serialization.ResponseBuilder
import pad.service.DataService
import pad.validator.*
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
        get("/students", this::getStudents, templateEngine)
        get("/students/:studentId", this::getStudent, templateEngine)
        get("/students/:studentId/books", this::getBooks, templateEngine)
        get("/students/:studentId/books/:bookId", this::getBook, templateEngine)
        put("/students/:studentId", this::putStudent, templateEngine)
        post("/students", this::postStudent, templateEngine)
        post("/students/:studentId/books", this::postBook, templateEngine)
        post("/*", this::notFound, templateEngine)

        get("/*", this::notFound, templateEngine)
        put("/*", this::notFound, templateEngine)
    }

    fun notFound(req: Request, res: Response): ModelAndView {
        return ResponseBuilder(req, res)
                .code(HttpStatus.NOT_FOUND_404)
                .error("Route not found!")
                .getModel()
    }

    @Hateoas(rel = "student.books.list", linkFormat = "/students/:studentId/books")
    fun getBooks(req: Request, res: Response): ModelAndView {
        val responseBuilder = ResponseBuilder(req, res)
        val response = dataService.getBooks(req.params("studentId")).body
        responseBuilder.response(response)
        return responseBuilder.getModel()
    }


    fun postBook(req: Request, res: Response): ModelAndView {
        val idStudent = req.params("studentId")?.toIntOrNull()
        val title = req.queryParams("title")
        val author = req.queryParams("author")
        val desc = req.queryParams("desc")
        val year = req.queryParams("year")?.toIntOrNull()
        val responseBuilder = ResponseBuilder(req, res)
        val result = FluentValidator.checkAll()
                .setIsFailFast(false)
                .on(idStudent, IdValidator("studentId"))
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
                        .code(HttpStatus.CREATED_201)
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

    @Hateoas(rel = "students.books.get", linkFormat = "/students/:studentId/books/:bookId",
            rootForDto = arrayOf(BookDto::class))
    fun getBook(req: Request, res: Response): ModelAndView {
        val responseBuilder = ResponseBuilder(req, res)
        val response = dataService.getBook(req.params("studentId"), req.params("bookId"))
        if (response.body == null)
            responseBuilder
                    .code(HttpStatus.NOT_FOUND_404)
                    .error("Book not found!")
        responseBuilder.response(response.body)
        return responseBuilder.getModel()
    }


    @Hateoas(rel = "students.list", linkFormat = "/students")
    fun getStudents(req: Request, res: Response): ModelAndView {
        val responseBuilder = ResponseBuilder(req, res)
        val response = dataService.getStudents()
        responseBuilder.response(response.body)
        return responseBuilder.getModel()
    }

    @Hateoas(rel = "students.get", linkFormat = "/students/:studentId",
            rootForDto = arrayOf(StudentDto::class))
    fun getStudent(req: Request, res: Response): ModelAndView {
        val responseBuilder = ResponseBuilder(req, res)
        val response = dataService.getStudent(req.params("studentId"))
        responseBuilder.response(response.body)
        if (response.body == null)
            responseBuilder
                    .code(HttpStatus.NOT_FOUND_404)
                    .error("Student not found!")
        return responseBuilder.getModel()
    }

    fun postStudent(req: Request, res: Response): ModelAndView {
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
        } else
            responseBuilder
                    .error(result.errors)
                    .code(HttpStatus.BAD_REQUEST_400)
        return responseBuilder.getModel()
    }

    fun putStudent(req: Request, res: Response): ModelAndView {
        val studentId = req.params("studentId")
        val studentName = req.queryParams("name")
        val studentPhone = req.queryParams("phone")
        val studentYear = req.queryParams("year")?.toIntOrNull()
        val responseBuilder = ResponseBuilder(req, res)
        val result = FluentValidator.checkAll()
                .setIsFailFast(false)
                .on(studentId, UUIDValidator("studentId"))
                .on(studentName, NameValidator("Name"))
                .on(studentPhone, PhoneValidator())
                .on(studentYear, YearValidator(1900, 2000))
                .doValidate()
                .result(toSimple())
        Runner.logger.debug("studentId = $studentId")
        if (result.isSuccess) {
            val response = dataService.putStudent(studentId, studentName, studentPhone, studentYear!!)
            responseBuilder
                    .code(if (response.param == true) HttpStatus.CREATED_201 else HttpStatus.OK_200)
                    .response(response.body)
        } else
            responseBuilder
                    .error(result.errors)
                    .code(HttpStatus.BAD_REQUEST_400)
        return responseBuilder.getModel()
    }

}
