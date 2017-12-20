package pad.dao

import com.j256.ormlite.dao.BaseDaoImpl
import com.j256.ormlite.support.ConnectionSource
import pad.model.Book
import java.sql.SQLException

class BookDao @Throws(SQLException::class)
constructor(connectionSource: ConnectionSource, dataClass: Class<Book>) : BaseDaoImpl<Book, String>(connectionSource, dataClass)