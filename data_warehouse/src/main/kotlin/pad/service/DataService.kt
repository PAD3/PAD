package pad.service


import pad.Runner
import pad.dao.BookDao
import pad.dao.StudentDao
import pad.dto.BookDto
import pad.dto.StudentDto
import pad.hateoas.HateoasProvider
import pad.model.Book
import pad.model.ServiceResponse
import pad.model.Student
import spark.Spark.halt
import javax.inject.Inject
import javax.inject.Singleton
import java.sql.SQLException
import java.util.ArrayList

@Singleton
class DataService @Inject constructor() {

    @Inject
    lateinit var studentDao: StudentDao
    @Inject
    lateinit var bookDao: BookDao

    init {
        Runner.mainComponent.inject(this)
    }

    fun getStudent(id: String): ServiceResponse<StudentDto?> {
        try {
            return ServiceResponse(StudentDto(studentDao.queryForId(id)))
        } catch (e: SQLException) {
            return ServiceResponse()
        }
    }

    fun getStudents(): ServiceResponse<List<StudentDto>> {
        try {
            return ServiceResponse(studentDao.queryForAll().map { StudentDto(it) })
        } catch (e: SQLException) {
            return ServiceResponse(null, e.message)
        }

    }

    fun createStudent(name: String, phone: String, year: Int): ServiceResponse<StudentDto> {
        try {
            val student = Student()
            student.name = name
            student.year = year
            student.phone = phone
            studentDao.create(student)
            return ServiceResponse(StudentDto(student))
        } catch (e: SQLException) {
            throw e
            return ServiceResponse(null, e.message)
        }
    }

    fun createBook(idStudent: String, title: String, author: String, year: Int, desc: String?): ServiceResponse<BookDto> {
        val student = studentDao.queryForId(idStudent) ?: return ServiceResponse(null, "Student not found")
        val book = Book()
        book.author = author
        book.title = title
        book.year = year
        book.desc = desc
        book.student = student
        try {
            bookDao.create(book)
        } catch (e: SQLException) {
            return ServiceResponse(null, e.message)
        }
        return ServiceResponse(BookDto(book))
    }

    fun getBooks(idStudent: String): ServiceResponse<List<BookDto>> {
        try {
            return ServiceResponse(studentDao.queryForId(idStudent).books.map { BookDto(it) }.toList())
        } catch (e: SQLException) {
            return ServiceResponse(null, e.message)
        }
    }

    fun getBook(idStudent: String, idBook: String): ServiceResponse<BookDto?> {
        try {
            val book = bookDao.queryBuilder()
                    .limit(1)
                    .where()
                    .eq("student_id", idStudent)
                    .and()
                    .eq("id", idBook)
                    .query().firstOrNull()
            if (book != null)
                return ServiceResponse(BookDto(book))
            else
                return ServiceResponse(null,"Book not found!")
        } catch (e: SQLException) {
            return ServiceResponse(null, e.message)
        }
    }


}