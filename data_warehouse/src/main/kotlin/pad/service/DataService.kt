package pad.service


import pad.Runner
import pad.StudentDao
import pad.model.Student
import javax.inject.Inject
import javax.inject.Singleton
import java.sql.SQLException
import java.util.ArrayList

@Singleton
class DataService @Inject constructor() {

    @Inject
    lateinit var studentDao: StudentDao

    val students: List<Student>
        get() {
            try {
                return studentDao.queryForAll()
            } catch (e: SQLException) {
                return ArrayList()
            }

        }

    init {
        Runner.mainComponent.inject(this)
    }

    fun createStudent(name: String): Student? {
        try {
            val student = Student()
            student.name = name
            studentDao.create(student)
            return student
        } catch (e: SQLException) {
            return null
        }

    }


}