package pad.service


import pad.Runner
import pad.dao.BookDao
import pad.dao.StudentDao
import pad.model.Book
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

    fun getStudent(id: String): Student? {
        try {
            return studentDao.queryForId(id.toString())
        } catch (e: SQLException) {
            halt(500)
            return null
        }
    }

    fun getStudents(): List<Student> {
        try {
            return studentDao.queryForAll()
        } catch (e: SQLException) {
            return ArrayList()
        }

    }

    fun createStudent(name: String, phone : String, year: Int): Student? {
        try {
            val student = Student()
            student.name = name
            student.year = year
            student.phone = phone
            studentDao.create(student)
            return student
        } catch (e: SQLException) {
            halt(500)
            return null
        }
    }

    fun createBook(idStudent: String, title: String, author: String, year: Int, desc: String?): Book? {
        val student = studentDao.queryForId(idStudent) ?: return null
        val book = Book()
        book.author = author
        book.title = title
        book.year = year
        book.desc = desc
        book.student = student
        try {
            bookDao.create(book)
        } catch (e: SQLException) {
            return null
        }
        return book
    }

    fun getBooks(idStudent: String) : List<Book> {
        try {
            return studentDao.queryForId(idStudent).books.toList()
        } catch (e: SQLException) {
            return ArrayList()
        }
    }


}