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
    private var methodList = arrayListOf<String>()
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
        methodList.add(name ?: "")
        return super.visitMethod(access, name, descriptor, signature, exceptions)
    }


    override fun visitEnd() {
        super.visitEnd()
        if (hideApiClassModel == null) {
            return
        }
        hideApiClassModel?.fieldModels?.forEach {
            addField(it)
        }
        hideApiClassModel?.methodModels?.forEach {
            addMethod(it)
        }
    }

    private fun addField(fieldModel: FieldModel) {
    }

    private fun addMethod(methodModel: MethodModel) {
        if (methodList.contains(methodModel.name)) {
            println("interesting method ${methodModel.name} of ${hideApiClassModel?.className}")
            return
        }
        val methodVisitor =
            cv.visitMethod(methodModel.access, methodModel.name, methodModel.descriptor, null, null)
        methodVisitor.visitCode()
        methodVisitor.visitTypeInsn(Opcodes.NEW, "java/lang/RuntimeException")
        methodVisitor.visitInsn(Opcodes.DUP)
        methodVisitor.visitLdcInsn("hide stub")
        methodVisitor.visitMethodInsn(
            Opcodes.INVOKESPECIAL,
            "java/lang/RuntimeException",
            "<init>",
            "(Ljava/lang/String;)V",
            false
        )
        methodVisitor.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Throwable")
        methodVisitor.visitInsn(Opcodes.ATHROW)
        methodVisitor.visitMaxs(3, 1)
        methodVisitor.visitEnd()
    }
}