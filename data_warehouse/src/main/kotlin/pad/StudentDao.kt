package pad

import com.j256.ormlite.dao.BaseDaoImpl
import com.j256.ormlite.support.ConnectionSource
import pad.model.Student

import java.sql.SQLException

class StudentDao @Throws(SQLException::class)
constructor(connectionSource: ConnectionSource, dataClass: Class<Student>) : BaseDaoImpl<Student, String>(connectionSource, dataClass)
