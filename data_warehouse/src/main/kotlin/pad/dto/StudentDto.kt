package pad.dto

import pad.hateoas.HateoasParam
import pad.hateoas.HateoasProvider
import pad.hateoas.Link
import pad.model.Student

class StudentDto(student: Student) : Dto {
    @HateoasParam("studentId")
    val id: String = student.id

    val name: String? = student.name
    val year: Int = student.year
    val phone: String? = student.phone
    override var links : List<Link> = listOf()
}