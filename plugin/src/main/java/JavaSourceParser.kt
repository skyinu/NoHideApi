import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.Node
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.body.MethodDeclaration

class JavaSourceParser(private val compilationUnit: CompilationUnit) {
    private val hideApiClassModelMap = mutableMapOf<String, HideApiClassModel>()

    fun parse(): Map<String, HideApiClassModel> {
        if (compilationUnit.packageDeclaration.isEmpty) {
            return hideApiClassModelMap
        }
        val packageName = compilationUnit.packageDeclaration.get().name
        compilationUnit.childNodes.filterIsInstance<ClassOrInterfaceDeclaration>().forEach {
            parseClassNode(packageName.asString(), it)
        }
        return hideApiClassModelMap
    }

    private fun parseClassNode(
        hostName: String,
        classOrInterfaceDeclaration: ClassOrInterfaceDeclaration
    ) {
        val className = kotlin.runCatching { classOrInterfaceDeclaration.resolve().qualifiedName }
            .getOrDefault("$hostName.${classOrInterfaceDeclaration.name}")
        var superClassName: String? = null
        if (classOrInterfaceDeclaration.extendedTypes.isNonEmpty) {
            kotlin.runCatching {
                superClassName =
                    findReferenceWithImport(classOrInterfaceDeclaration.extendedTypes[0].nameAsString)
            }
        }
        val implementInterfaceName = arrayListOf<String>()
        classOrInterfaceDeclaration.implementedTypes.forEach {
            kotlin.runCatching {
                implementInterfaceName.add(findReferenceWithImport(it.nameAsString))
            }
        }
        val access = Utils.modifierToAccess(classOrInterfaceDeclaration.modifiers)
        val hideApiClassModel =
            HideApiClassModel(className, access, superClassName, implementInterfaceName)
        hideApiClassModelMap[className] = hideApiClassModel
        classOrInterfaceDeclaration.childNodes.forEach {
            when (it) {
                is ClassOrInterfaceDeclaration -> {
                    parseClassNode(className, it)
                }
                is FieldDeclaration -> {
                    parseField(hideApiClassModel, it)
                }
                is MethodDeclaration -> {
                    parseMethod(hideApiClassModel, it)
                }
            }
        }
        if (hideApiClassModel.fieldModels.isEmpty() && hideApiClassModel.methodModels.isEmpty()) {
            hideApiClassModelMap.remove(className)
        }
    }

    private fun parseField(
        hideApiClassModel: HideApiClassModel,
        fieldDeclaration: FieldDeclaration
    ) {
        if (!isHideApi(fieldDeclaration)) {
            return
        }
        val access = Utils.modifierToAccess(fieldDeclaration.modifiers)
        fieldDeclaration.variables.forEach {
            kotlin.runCatching {
                val name = it.name.asString()
                val fieldModel =
                    FieldModel(access, name, Utils.typeToDescriptor(it.type) { simpleName ->
                        return@typeToDescriptor findReferenceWithImport(simpleName)
                    })
                hideApiClassModel.fieldModels.add(fieldModel)
            }
        }
    }

    private fun parseMethod(
        hideApiClassModel: HideApiClassModel,
        methodDeclaration: MethodDeclaration
    ) {
        if (!isHideApi(methodDeclaration)) {
            return
        }
        val access = Utils.modifierToAccess(methodDeclaration.modifiers)
        val name = methodDeclaration.nameAsString
        val desc = kotlin.runCatching {
            Utils.getMethodDescriptor(methodDeclaration) {
                return@getMethodDescriptor findReferenceWithImport(it)
            }
        }.onFailure {
        }.getOrNull() ?: return
        val methodModel = MethodModel(access, name, desc, null)
        hideApiClassModel.methodModels.add(methodModel)
    }

    private fun isHideApi(node: Node): Boolean {
        if (node.comment.isEmpty) {
            return false
        }
        return node.comment.get().content?.contains("@hide") == true
    }

    private fun findReferenceWithImport(name: String): String {
        compilationUnit.imports.forEach {
            if (it.name.asString().endsWith(name)) {
                return it.name.asString()
            }
        }
        val packageName = compilationUnit.packageDeclaration.get().name.asString()
        if (name.startsWith(packageName)) {
            return name
        }
        throw RuntimeException("can't find ${name}'s  reference in $packageName")
    }
}