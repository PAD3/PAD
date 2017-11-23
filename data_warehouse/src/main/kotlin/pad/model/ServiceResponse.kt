package pad.model

class ServiceResponse<out T, out D>(val body : T? = null, val errorMessage : String? = null, val param : D? = null)