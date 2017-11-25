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
import java.net.URI

import javax.inject.Singleton
import java.sql.SQLException

@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideConnectionSource(): ConnectionSource {
        val connectionSource : JdbcPooledConnectionSource
        val dbUrl : String
        val username : String
        val password : String
        if (System.getenv("CLEARDB_DATABASE_URL") != null){
            val dbUri = URI(System.getenv("CLEARDB_DATABASE_URL"))
            username = dbUri.userInfo.split(":")[0]
            password = dbUri.userInfo.split(":")[1]
            dbUrl = "jdbc:mysql://${dbUri.host}${dbUri.path}"
        }else {
            dbUrl = "jdbc:mysql://localhost:3306/pad"
            username = "pad_user"
            password = "i_love_pad"
        }
        try {
            connectionSource = JdbcPooledConnectionSource(dbUrl)
            connectionSource.setUsername(username)
            connectionSource.setPassword(password)
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
