package pad.service


import com.j256.ormlite.stmt.PreparedDelete
import pad.Runner
import pad.dao.BookDao
import pad.dao.StudentDao
import pad.dto.BookDto
import pad.dto.StudentDto
import pad.hateoas.HateoasProvider
import pad.model.Book
import pad.model.ServiceResponse
import pad.model.Student
import pad.util.UIDUtil
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

    fun getStudent(id: String): ServiceResponse<StudentDto?, Unit> {
        try {
            return ServiceResponse(StudentDto(studentDao.queryForId(id)))
        } catch (e: SQLException) {
            return ServiceResponse()
        }
    }

    fun getStudents(): ServiceResponse<List<StudentDto>, Unit> {
        try {
            return ServiceResponse(studentDao.queryForAll().map { StudentDto(it) })
        } catch (e: SQLException) {
            return ServiceResponse(null, e.message)
        }

    }

    fun createStudent(name: String, phone: String, year: Int): ServiceResponse<StudentDto, Unit> {
        return try {
            val student = Student()
            student.id = UIDUtil.generateUID()
            student.name = name
            student.year = year
            student.phone = phone
            studentDao.create(student)
            ServiceResponse(StudentDto(student))
        } catch (e: SQLException) {
            ServiceResponse(null, e.message)
        }
    }

    fun deleteStudent(id: String) : ServiceResponse<Void,Void>{
        return try {
            val deleteBuilder = bookDao.deleteBuilder()
            deleteBuilder.where().eq("student_id",id)
            bookDao.delete(deleteBuilder.prepare())
            studentDao.deleteIds(listOf(id))
            ServiceResponse(null)
        } catch (e : SQLException){
            ServiceResponse(null,e.message)
        }

    }

    fun putStudent(id: String, name: String, phone: String, year: Int): ServiceResponse<StudentDto, Boolean> {
        var student = studentDao.queryForId(id)
        var createdNewUser = false
        return try {
            if (student == null) {
                student = Student()
                student.id = id
                createdNewUser = true
            }
            student.name = name
            student.year = year
            student.phone = phone
            if (createdNewUser)
                studentDao.create(student)
            else
                studentDao.update(student)
            ServiceResponse(StudentDto(student), param = createdNewUser)
        } catch (e: SQLException) {
            ServiceResponse(null, e.message, false)
        }
    }

    fun createBook(idStudent: String, title: String, author: String, year: Int, desc: String?): ServiceResponse<BookDto, Unit> {
        val student = studentDao.queryForId(idStudent) ?: return ServiceResponse(null, "Student not found")
        val book = Book()
        book.id = UIDUtil.generateUID()
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

    fun getBooks(idStudent: String): ServiceResponse<List<BookDto>, Unit> {
        try {
            return ServiceResponse(studentDao.queryForId(idStudent).books.map { BookDto(it) }.toList())
        } catch (e: SQLException) {
            return ServiceResponse(null, e.message)
        }
    }

    fun getBook(idStudent: String, idBook: String): ServiceResponse<BookDto?, Unit> {
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
                return ServiceResponse(null, "Book not found!")
        } catch (e: SQLException) {
            return ServiceResponse(null, e.message)
        }
    }


}