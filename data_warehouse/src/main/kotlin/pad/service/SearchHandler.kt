package pad.service

import com.j256.ormlite.dao.Dao

class SearchHandler {
    companion object {
        val criterias : List<String> = SearchCriteria.values().map { it.toString().toLowerCase() }
    }

    fun<T> query(dao : Dao<T,String>, criteries : Map<String,String>, offset : Int, limit : Int) : List<T>{
        val where = dao.queryBuilder().offset(offset).limit(limit).where()
        val scopes : List<Criteria> = criteries.map { parseCriteria(it.key,it.value) }.filterNotNull()
        for (index in scopes.indices){
            val scope = scopes[index]
            when (scope.searchCriteria) {
                SearchCriteria.STARTS_WITH -> {
                    where.like(scope.field, "${scope.value}%")
                }
                SearchCriteria.ENDS_WITH -> where.like(scope.field, "%${scope.value}")
                SearchCriteria.EQUALS -> where.eq(scope.field, scope.value)
                SearchCriteria.BIGGER_THAN -> where.gt(scope.field,scope.value)
                SearchCriteria.SMALLER_THAN -> where.lt(scope.field,scope.value)
            }
            if (index < scopes.size - 1)
                where.and()
        }
        return where.query()
    }

    fun parseCriteria(key : String, value : String) : Criteria? {
        val searchCriteria = criterias.find { key.contains(it) }?: return null
        val field : String = key.replace(searchCriteria,"").removePrefix("_").removeSuffix("_")
        return Criteria(SearchCriteria.valueOf(searchCriteria.toUpperCase()),field,value)
    }
}

data class Criteria(val searchCriteria: SearchCriteria, val field : String, val value : String)

enum class SearchCriteria{
    STARTS_WITH, ENDS_WITH, EQUALS, BIGGER_THAN, SMALLER_THAN;
}

annotation class SearchField