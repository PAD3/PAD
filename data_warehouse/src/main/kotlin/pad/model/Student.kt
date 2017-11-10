package pad.model

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import pad.StudentDao

@DatabaseTable(daoClass = StudentDao::class)
class Student {
    @DatabaseField(generatedId = true)
    var id: Int = 0

    @DatabaseField
    var name: String? = null
}
