import com.github.javaparser.ast.Modifier
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.type.PrimitiveType
import org.objectweb.asm.Opcodes
import java.io.File


object Utils {
    private const val SUFFIX_CLASS = ".class"
    private val REPLACE_REGEX = "[/$]".toRegex()
    fun isClass(name: String): Boolean {
        return name.endsWith(SUFFIX_CLASS)
    }

    fun retrieveClassNameForJarClass(jarClass: String): String {
        val pre: String = jarClass.replace(File.separator, ".").replace(SUFFIX_CLASS, "")
        return pre.replace(REPLACE_REGEX, ".")
    }

    fun modifierToAccess(modifier: List<Modifier>): Int {
        var access = 0
        modifier.forEach {
            when (it.keyword.ordinal) {
                Modifier.Keyword.PUBLIC.ordinal -> {
                    access = access.or(Opcodes.ACC_PUBLIC)
                }
                Modifier.Keyword.PROTECTED.ordinal -> {
                    access = access.or(Opcodes.ACC_PROTECTED)
                }
                Modifier.Keyword.PRIVATE.ordinal -> {
                    access = access.or(Opcodes.ACC_PRIVATE)
                }
                Modifier.Keyword.ABSTRACT.ordinal -> {
                    access = access.or(Opcodes.ACC_ABSTRACT)
                }
                Modifier.Keyword.STATIC.ordinal -> {
                    access = access.or(Opcodes.ACC_STATIC)
                }
                Modifier.Keyword.FINAL.ordinal -> {
                    access = access.or(Opcodes.ACC_FINAL)
                }
                Modifier.Keyword.TRANSIENT.ordinal -> {
                    access = access.or(Opcodes.ACC_TRANSIENT)
                }
                Modifier.Keyword.VOLATILE.ordinal -> {
                    access = access.or(Opcodes.ACC_VOLATILE)
                }
                Modifier.Keyword.SYNCHRONIZED.ordinal -> {
                    access = access.or(Opcodes.ACC_SYNCHRONIZED)
                }
                Modifier.Keyword.NATIVE.ordinal -> {
                    access = access.or(Opcodes.ACC_NATIVE)
                }
                Modifier.Keyword.TRANSITIVE.ordinal -> {
                    access = access.or(Opcodes.ACC_TRANSITIVE)
                }
            }
        }
        return access
    }

    fun typeToDescriptor(
        clazz: com.github.javaparser.ast.type.Type,
        fullNameSupport: ((String) -> String)
    ): String {
        val stringBuilder: StringBuilder = java.lang.StringBuilder()
        var currentClass = clazz
        while (currentClass.isArrayType) {
            stringBuilder.append('[')
            currentClass = currentClass.asArrayType().componentType
        }
        if (currentClass.isPrimitiveType) {
            val descriptor: Char = when (currentClass.asPrimitiveType().type) {
                PrimitiveType.Primitive.INT -> {
                    'I'
                }
                PrimitiveType.Primitive.BOOLEAN -> {
                    'Z'
                }
                PrimitiveType.Primitive.BYTE -> {
                    'B'
                }
                PrimitiveType.Primitive.CHAR -> {
                    'C'
                }
                PrimitiveType.Primitive.SHORT -> {
                    'S'
                }
                PrimitiveType.Primitive.DOUBLE -> {
                    'D'
                }
                PrimitiveType.Primitive.FLOAT -> {
                    'F'
                }
                PrimitiveType.Primitive.LONG -> {
                    'J'
                }
                else -> {
                    throw AssertionError()
                }
            }
            stringBuilder.append(descriptor)
        } else if (currentClass.isVoidType) {
            stringBuilder.append('V')
        } else {
            stringBuilder.append('L')
                .append(getInternalName(fullNameSupport.invoke(currentClass.asString())))
                .append(';')
        }
        return stringBuilder.toString()
    }

    private fun getInternalName(clazz: String): String {
        return clazz.replace('.', '/')
    }

    fun getMethodDescriptor(
        method: MethodDeclaration,
        fullNameSupport: ((String) -> String)
    ): String {
        val stringBuilder = java.lang.StringBuilder()
        stringBuilder.append('(')
        val parameters = method.parameters
        for (parameter in parameters) {
            stringBuilder.append(typeToDescriptor(parameter.type, fullNameSupport))
        }
        stringBuilder.append(')')
        stringBuilder.append(typeToDescriptor(method.type, fullNameSupport))
        return stringBuilder.toString()
    }
}