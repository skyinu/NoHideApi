import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class NoHideClassVisitor(
    private val hideApiClassModelMap: Map<String, HideApiClassModel>,
    classVisitor: ClassVisitor?
) : ClassVisitor(Opcodes.ASM9, classVisitor) {
    companion object {
        private val REPLACE_REGEX = "[/$]".toRegex()
    }

    private var hideApiClassModel: HideApiClassModel? = null
    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
        val fullName = name?.replace(REPLACE_REGEX, ".") ?: ""
        hideApiClassModel = hideApiClassModelMap[fullName]
        hideApiClassModel?.let {
            println("$fullName field count ${it.fieldModels.size} method count ${it.methodModels.size}")
        }
    }

    override fun visitField(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        value: Any?
    ): FieldVisitor {
        return super.visitField(access, name, descriptor, signature, value)
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        return super.visitMethod(access, name, descriptor, signature, exceptions)
    }


    override fun visitEnd() {
        super.visitEnd()
        if (hideApiClassModel == null) {
            return
        }
        hideApiClassModel?.methodModels?.forEach {
            val methodVisitor = cv.visitMethod(it.access, it.name, it.descriptor, null, null)
            methodVisitor.visitMaxs(0, 0)
            methodVisitor.visitEnd()
        }
    }
}