package pad.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.field.ForeignCollectionField
import com.j256.ormlite.table.DatabaseTable
import pad.dao.StudentDao
import pad.hateoas.Link

@DatabaseTable(daoClass = StudentDao::class)
class Student {
    @DatabaseField(generatedId = true)
    var id: Int = 0

    @DatabaseField
    var name: String? = null

    @DatabaseField
    var year: Int = 0

    @DatabaseField
    var phone: String? = null

    @ForeignCollectionField
    @JsonIgnore
    var books: Collection<Book> = mutableListOf()

    val links : List<Link>
    get() = listOf(Link("self", "http://localhost:4567/students/$id"))
}
