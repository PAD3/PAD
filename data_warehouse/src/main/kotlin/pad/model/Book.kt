package pad.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import pad.dao.BookDao
import pad.hateoas.Link

@DatabaseTable(daoClass = BookDao::class)
class Book {
    @DatabaseField(generatedId = true)
    var id : Int = 0

    @DatabaseField
    var title : String? = null

    @DatabaseField
    var author : String? = null

    @DatabaseField
    var year : Int = 0

    @DatabaseField(canBeNull = true)
    var desc : String? = null

    @DatabaseField(foreign = true)
    @JsonIgnore
    lateinit var student : Student

    val links : List<Link>
        get() = listOf(Link("self", "http://localhost:4567/students/${student.id}/books/id"),
                Link("student.books", "http://localhost:4567/students/${student.id}/books"),
                Link("student", "http://localhost:4567/students/${student.id}"))
}