package pad.model

class ServiceResponse<out T>(val body : T? = null, val errorMessage : String? = null)