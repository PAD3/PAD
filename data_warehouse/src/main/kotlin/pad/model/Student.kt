package pad.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.field.ForeignCollectionField
import com.j256.ormlite.table.DatabaseTable
import pad.dao.StudentDao
import pad.hateoas.Link
import pad.util.UIDUtil

@DatabaseTable(daoClass = StudentDao::class)
class Student {
    @DatabaseField(id = true)
    lateinit var id: String

    @DatabaseField
    var name: String? = null

    @DatabaseField
    var year: Int = 0

    @DatabaseField
    var phone: String? = null

    @ForeignCollectionField
    @JsonIgnore
    var books: Collection<Book> = mutableListOf()
}
