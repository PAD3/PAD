package pad.dto

import pad.hateoas.Link

interface Dto {
    var links : List<Link>
}