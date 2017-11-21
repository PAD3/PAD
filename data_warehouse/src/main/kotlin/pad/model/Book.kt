package pad.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import pad.dao.BookDao
import pad.hateoas.Link

@DatabaseTable(daoClass = BookDao::class)
class Book {
    @DatabaseField(id = true)
    var id : Int = 0

    @DatabaseField
    var title : String? = null

    @DatabaseField
    var author : String? = null

    @DatabaseField
    var year : Int = 0

    @DatabaseField(canBeNull = true)
    var desc : String? = null

    @DatabaseField(foreign = true, columnName = "student_id")
    @JsonIgnore
    lateinit var student : Student
}