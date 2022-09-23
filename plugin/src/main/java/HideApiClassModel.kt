data class HideApiClassModel(
    val className: String,
    val access: Int,
    val superClassName: String?,
    val implementClassName: ArrayList<String> = arrayListOf(),
    val fieldModels: ArrayList<FieldModel> = arrayListOf(),
    val methodModels: ArrayList<MethodModel> = arrayListOf()
)

data class FieldModel(val access: Int, val name: String, val type: String)

data class MethodModel(
    val access: Int,
    val name: String,
    val descriptor: String?,
    val signature: String?
)