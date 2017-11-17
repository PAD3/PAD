package pad.service


import pad.Runner
import pad.dao.BookDao
import pad.dao.StudentDao
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

    fun getStudent(id: String): ServiceResponse<Student?> {
        try {
            return ServiceResponse(studentDao.queryForId(id))
        } catch (e: SQLException) {
            return ServiceResponse()
        }
    }

    fun getStudents(): ServiceResponse<List<Student>> {
        try {
            return ServiceResponse(studentDao.queryForAll())
        } catch (e: SQLException) {
            return ServiceResponse(null, e.message)
        }

    }

    fun createStudent(name: String, phone : String, year: Int): ServiceResponse<Student> {
        try {
            val student = Student()
            student.name = name
            student.year = year
            student.phone = phone
            studentDao.create(student)
            return ServiceResponse(student,link = "http://localhost:4567/students/${student.id}")
        } catch (e: SQLException) {
            return ServiceResponse(null, e.message)
        }
    }

    fun createBook(idStudent: String, title: String, author: String, year: Int, desc: String?): ServiceResponse<Book> {
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
            return ServiceResponse(null, e.message,null)
        }
        return ServiceResponse(book, link = "http://localhost:4567/students/${student.id}/books/${book.id}")
    }

    fun getBooks(idStudent: String) : ServiceResponse<List<Book>> {
        try {
            return ServiceResponse(studentDao.queryForId(idStudent).books.toList())
        } catch (e: SQLException) {
            return ServiceResponse(null, e.message,null)
        }
    }


}