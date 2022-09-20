import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.Node
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.body.MethodDeclaration

class JavaSourceParser(private val compilationUnit: CompilationUnit) {

    fun parse() {
        if (compilationUnit.packageDeclaration.isEmpty) {
            return
        }
        val packageName = compilationUnit.packageDeclaration.get().name
        compilationUnit.childNodes.filterIsInstance<ClassOrInterfaceDeclaration>().forEach {
            parseClassNode(packageName.asString(), it)
        }
    }

    private fun parseClassNode(
        hostName: String,
        classOrInterfaceDeclaration: ClassOrInterfaceDeclaration
    ) {
        val className = "$hostName.${classOrInterfaceDeclaration.name}"
        var superClassName = ""
        if (classOrInterfaceDeclaration.extendedTypes.isNonEmpty) {
            superClassName =
                findReferenceWithImport(classOrInterfaceDeclaration.extendedTypes[0].nameAsString)
        }
        val implementInterfaceName = arrayListOf<String>()
        classOrInterfaceDeclaration.implementedTypes.forEach {
            implementInterfaceName.add(findReferenceWithImport(it.nameAsString))
        }
        classOrInterfaceDeclaration.childNodes.forEach {
            when (it) {
                is ClassOrInterfaceDeclaration -> {
                    parseClassNode(className, it)
                }
                is FieldDeclaration -> {
                    parseField(it)
                }
                is MethodDeclaration -> {
                    parseMethod(it)
                }
            }
        }
    }

    private fun parseField(fieldDeclaration: FieldDeclaration) {
        if (!isHideApi(fieldDeclaration)) {
            return
        }
        fieldDeclaration.modifiers.forEach {
        }
        fieldDeclaration.variables.forEach {
        }
    }

    private fun parseMethod(methodDeclaration: MethodDeclaration) {
        if (!isHideApi(methodDeclaration)) {
            return
        }
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
        return "${compilationUnit.packageDeclaration.get().name}.$name"
    }
}