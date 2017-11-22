package pad.injection

import com.j256.ormlite.dao.DaoManager
import com.j256.ormlite.jdbc.JdbcConnectionSource
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.TableUtils
import dagger.Module
import dagger.Provides
import pad.dao.BookDao
import pad.dao.StudentDao
import pad.model.Book
import pad.model.Student

import javax.inject.Singleton
import java.sql.SQLException

@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideConnectionSource(): ConnectionSource {
        val databaseUrl = "jdbc:mysql://localhost:3306/pad"
        try {
            val connectionSource = JdbcPooledConnectionSource(databaseUrl)
            connectionSource.setUsername("pad_user")
            connectionSource.setPassword("i_love_pad")
            connectionSource.setCheckConnectionsEveryMillis(5000)
            connectionSource.setMaxConnectionsFree(5)
            connectionSource.initialize()
            return connectionSource
        } catch (e: SQLException) {
            throw RuntimeException("Cannot access database!",e)
        }

    }

    @Singleton
    @Provides
    fun provideStudentDao(connectionSource: ConnectionSource): StudentDao {
        try {
            TableUtils.createTableIfNotExists<Student>(connectionSource, Student::class.java)
            return DaoManager.createDao<StudentDao, Student>(connectionSource, Student::class.java)
        } catch (e: SQLException) {
            System.out.println(e)
            throw RuntimeException("Cannot init StudentDao!")
        }
    }

    @Singleton
    @Provides
    fun provideBookDao(connectionSource: ConnectionSource): BookDao {
        try {
            TableUtils.createTableIfNotExists<Book>(connectionSource, Book::class.java)
            return DaoManager.createDao<BookDao, Book>(connectionSource, Book::class.java)
        } catch (e: SQLException) {
            System.out.println(e)
            throw RuntimeException("Cannot init BookDao!")
        }
    }

}
