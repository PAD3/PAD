package pad.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import pad.hateoas.HateoasParam
import pad.hateoas.HateoasProvider
import pad.hateoas.Link
import pad.model.Book

class BookDto(book: Book) : Dto {
    @HateoasParam(name = "bookId")
    val id: Int = book.id

    @HateoasParam(name = "studentId")
    @JsonIgnore
    val studentId = book.student.id

    val title: String? = book.title
    val author: String? = book.desc
    val year: Int = book.year
    val desc: String? = book.desc
    override var links : List<Link> = listOf()

}