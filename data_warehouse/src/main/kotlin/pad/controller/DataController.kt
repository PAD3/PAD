package pad.controller

import com.baidu.unbiz.fluentvalidator.FluentValidator
import com.baidu.unbiz.fluentvalidator.ResultCollectors.toSimple
import org.eclipse.jetty.http.HttpStatus
import pad.Runner
import pad.dto.BookDto
import pad.dto.StudentDto
import pad.hateoas.Hateoas
import pad.model.ServiceResponse
import pad.serialization.DefaultTemplateEngine
import pad.serialization.ResponseBuilder
import pad.service.DataService
import pad.cache.CacheValidator
import pad.util.bodyParams
import pad.util.hasSearchParams
import pad.util.searchParams
import pad.validator.*
import spark.ModelAndView
import spark.Request
import spark.Response
import spark.Spark.*
import javax.inject.Inject

class DataController : AbstractController() {
    @Inject
    lateinit var dataService: DataService

    companion object {
        const val DEFAULT_OFFSET: Int = 0
        const val DEFAULT_LIMIT: Int = 25
        const val MAX_LIMIT = 10000
    }

    override fun init() {
        Runner.mainComponent.inject(this)
        val templateEngine = DefaultTemplateEngine()
        get("/students", this::getStudents, templateEngine)
        get("/students/:studentId", this::getStudent, templateEngine)
        get("/students/:studentId/books", this::getBooks, templateEngine)
        get("/students/:studentId/books/:bookId", this::getBook, templateEngine)
        put("/students/:studentId", this::putStudent, templateEngine)
        delete("/students/:studentId", this::deleteStudent, templateEngine)
        delete("/students/:studentId/books/:bookId", this::deleteBook, templateEngine)
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
        val studentId = req.params("studentId")
        var q = req.queryParams("q")
        if (!NameValidator("query").validate(null, q))
            q = null
        var offset = req.queryParams("offset")?.toIntOrNull() ?: DEFAULT_OFFSET
        var limit = req.queryParams("limit")?.toIntOrNull() ?: DEFAULT_LIMIT
        val response: ServiceResponse<List<BookDto>, Unit>
        if (!IntValidator(0, Int.MAX_VALUE).validate(null, offset))
            offset = DEFAULT_OFFSET
        if (!IntValidator(0, MAX_LIMIT).validate(null, limit))
            limit = DEFAULT_LIMIT
        response = if (q == null) {
            if (req.hasSearchParams())
                dataService.searchMulticriteriaBooks(studentId,req.searchParams(), offset, limit)
            else
                dataService.getBooks(studentId,offset, limit)
        } else {
            dataService.searchRelevantBooks(studentId, q, offset, limit)
        }
        responseBuilder.response(response.body)
        return responseBuilder.getModel()
    }


    fun postBook(req: Request, res: Response): ModelAndView {
        val bodyParams = req.bodyParams()
        val idStudent = req.params("studentId")
        val title = bodyParams.string("title")
        val author = bodyParams.string("author")
        val desc = bodyParams.string("desc")
        val year = bodyParams.int("year")
        val responseBuilder = ResponseBuilder(req, res)
        val result = FluentValidator.checkAll()
                .setIsFailFast(false)
                .on(idStudent, UUIDValidator("studentId"))
                .on(title, NameValidator("Title"))
                .on(author, NameValidator("Author"))
                .on(desc, DescValidator())
                .on(year, IntValidator(1000, 2017))
                .doValidate()
                .result(toSimple())
        if (result.isSuccess) {
            val response = dataService.createBook(idStudent.toString(), title, author, year, desc)
            if (response.body != null) {
                CacheValidator.invalidate("/students/$idStudent/books")
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

    fun putBook(req: Request, res: Response): ModelAndView {
        val bodyParams = req.bodyParams()
        val idStudent = req.params("studentId")
        val title = bodyParams.string("title")
        val author = bodyParams.string("author")
        val desc = bodyParams.string("desc")
        val year = bodyParams.int("year")
        val responseBuilder = ResponseBuilder(req, res)
        val result = FluentValidator.checkAll()
                .setIsFailFast(false)
                .on(idStudent, UUIDValidator("studentId"))
                .on(title, NameValidator("Title"))
                .on(author, NameValidator("Author"))
                .on(desc, DescValidator())
                .on(year, IntValidator(1000, 2017))
                .doValidate()
                .result(toSimple())
        if (result.isSuccess) {
            val response = dataService.createBook(idStudent.toString(), title, author, year, desc)
            if (response.body != null) {
                CacheValidator.invalidate("/students/$idStudent/books")
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
            rootForDto = [(BookDto::class)])
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
        var q = req.queryParams("q")
        if (!NameValidator("query").validate(null, q))
            q = null
        var offset = req.queryParams("offset")?.toIntOrNull() ?: DEFAULT_OFFSET
        var limit = req.queryParams("limit")?.toIntOrNull() ?: DEFAULT_LIMIT
        val response: ServiceResponse<List<StudentDto>, Unit>
        if (!IntValidator(0, Int.MAX_VALUE).validate(null, offset))
            offset = DEFAULT_OFFSET
        if (!IntValidator(0, MAX_LIMIT).validate(null, limit))
            limit = DEFAULT_LIMIT
        response = if (q == null) {
            if (req.hasSearchParams())
                dataService.searchMulticriteriaStudents(req.searchParams(), offset, limit)
            else
                dataService.getStudents(offset, limit)
        } else {
            dataService.searchRelevantStudents(q, offset, limit)
        }
        responseBuilder.response(response.body)
        return responseBuilder.getModel()
    }

    @Hateoas(rel = "students.get", linkFormat = "/students/:studentId",
            rootForDto = [(StudentDto::class)])
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

    fun deleteStudent(req: Request, res: Response): ModelAndView {
        val studentId = req.params("studentId")
        val responseBuilder = ResponseBuilder(req, res)
        responseBuilder.ignoreParam("studentId")
        val check = FluentValidator.checkAll()
                .on(studentId, UUIDValidator("studentId"))
                .result(toSimple())
        if (check.isSuccess) {
            val result = dataService.deleteStudent(studentId)
            CacheValidator.invalidate("/students")
            CacheValidator.invalidate("/students/$studentId")
            if (result.errorMessage != null)
                responseBuilder
                        .code(HttpStatus.INTERNAL_SERVER_ERROR_500)
                        .error(result.errorMessage)
            else
                responseBuilder
                        .code(HttpStatus.OK_200)
        } else
            responseBuilder
                    .code(HttpStatus.BAD_REQUEST_400)
                    .error(check.errors)
        return responseBuilder.getModel()
    }

    fun deleteBook(req: Request, res: Response): ModelAndView {
        val studentId = req.params("studentId")
        val bookId = req.params("bookId")
        val responseBuilder = ResponseBuilder(req, res)
        responseBuilder.ignoreParam("bookId")
        val check = FluentValidator.checkAll()
                .on(studentId, UUIDValidator("studentId"))
                .on(bookId, UUIDValidator("bookId"))
                .result(toSimple())
        if (check.isSuccess) {
            CacheValidator.invalidate("/students/$studentId/books")
            CacheValidator.invalidate("/students/$studentId/books/$bookId")
            val result = dataService.deleteBook(bookId, studentId)
            if (result.errorMessage != null)
                responseBuilder
                        .code(HttpStatus.INTERNAL_SERVER_ERROR_500)
                        .error(result.errorMessage)
            else
                responseBuilder
                        .code(HttpStatus.OK_200)
        } else
            responseBuilder
                    .code(HttpStatus.BAD_REQUEST_400)
                    .error(check.errors)
        return responseBuilder.getModel()
    }

    fun postStudent(req: Request, res: Response): ModelAndView {
        val bodyParams = req.bodyParams()
        val studentName = bodyParams.string("name")
        val studentPhone = bodyParams.string("phone")
        val studentYear = bodyParams.int("year")
        val responseBuilder = ResponseBuilder(req, res)
        val check = FluentValidator.checkAll()
                .setIsFailFast(false)
                .on(studentName, NameValidator("Name"))
                .on(studentPhone, PhoneValidator())
                .on(studentYear, IntValidator(1900, 2000))
                .doValidate()
                .result(toSimple())
        if (check.isSuccess) {
            CacheValidator.invalidate("/students")
            val result = dataService.createStudent(studentName, studentPhone, studentYear)
            responseBuilder
                    .code(HttpStatus.CREATED_201)
                    .response(result.body)
        } else
            responseBuilder
                    .error(check.errors)
                    .code(HttpStatus.BAD_REQUEST_400)
        return responseBuilder.getModel()
    }

    fun putStudent(req: Request, res: Response): ModelAndView {
        val bodyParams = req.bodyParams()
        val studentId = req.params("studentId")
        val studentName = bodyParams.string("name")
        val studentPhone = bodyParams.string("phone")
        val studentYear = bodyParams.int("year")
        val responseBuilder = ResponseBuilder(req, res)
        val check = FluentValidator.checkAll()
                .setIsFailFast(false)
                .on(studentId, UUIDValidator("studentId"))
                .on(studentName, NameValidator("Name"))
                .on(studentPhone, PhoneValidator())
                .on(studentYear, IntValidator(1900, 2000))
                .doValidate()
                .result(toSimple())
        Runner.logger.debug("studentId = $studentId")
        if (check.isSuccess) {
            CacheValidator.invalidate("/students/$studentId")
            CacheValidator.invalidate("/students")
            val result = dataService.putStudent(req.params("studentId"), studentName, studentPhone, studentYear)
            responseBuilder
                    .code(if (result.param == true) HttpStatus.CREATED_201 else HttpStatus.OK_200)
                    .response(result.body)
        } else
            responseBuilder
                    .error(check.errors)
                    .code(HttpStatus.BAD_REQUEST_400)
        return responseBuilder.getModel()
    }

}
