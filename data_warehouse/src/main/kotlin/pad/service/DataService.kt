package pad.service


import pad.Runner
import pad.StudentDao
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

    init {
        Runner.mainComponent.inject(this)
    }

    fun getStudent(id : String) : Student? {
        try {
            return studentDao.queryForId(id.toString())
        } catch (e : SQLException) {
            halt(500)
            return null
        }
    }

    fun getStudents(): List<Student> {
        try {
            return studentDao.queryForAll()
        } catch (e: SQLException) {
            halt(500)
            return ArrayList()
        }

    }

    fun createStudent(name: String, year: Int): Student? {
        try {
            val student = Student()
            student.name = name
            student.year = year
            studentDao.create(student)
            return student
        } catch (e: SQLException) {
            halt(500)
            return null
        }

    }


}