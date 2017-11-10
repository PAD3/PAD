package pad.injection

import com.j256.ormlite.dao.DaoManager
import com.j256.ormlite.jdbc.JdbcConnectionSource
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.TableUtils
import dagger.Module
import dagger.Provides
import pad.StudentDao
import pad.model.Student

import javax.inject.Singleton
import java.sql.SQLException

@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideConnectionSource(): ConnectionSource {
        val databaseUrl = "jdbc:mysql://localhost/pad"
        try {
            val connectionSource = JdbcConnectionSource(databaseUrl)
            connectionSource.setUsername("pad_user")
            connectionSource.setPassword("i_love_pad")
            return connectionSource
        } catch (e: SQLException) {
            throw RuntimeException("Cannot access database!")
        }

    }

    @Singleton
    @Provides
    fun provideStudentDao(connectionSource: ConnectionSource): StudentDao {
        try {
            TableUtils.createTableIfNotExists<Student>(connectionSource, Student::class.java)
            return DaoManager.createDao<StudentDao, Student>(connectionSource, Student::class.java)
        } catch (e: SQLException) {
            throw RuntimeException("Cannot init StudentDao!")
        }

    }

}
