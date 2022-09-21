import org.gradle.api.artifacts.transform.InputArtifact
import org.gradle.api.artifacts.transform.TransformAction
import org.gradle.api.artifacts.transform.TransformOutputs
import org.gradle.api.artifacts.transform.TransformParameters
import org.gradle.api.file.FileSystemLocation
import org.gradle.api.provider.Provider
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * https://docs.gradle.org/current/dsl/org.gradle.api.artifacts.transform.TransformAction.html
 */
abstract class NoHideApiAction : TransformAction<TransformParameters.None> {

    @InputArtifact
    abstract fun getInputArtifact(): Provider<FileSystemLocation?>

    override fun transform(outputs: TransformOutputs) {
        val input: File = getInputArtifact().get().asFile
        if (input.name.equals(PlatformUtils.ANDROID_JAR, true)) {
            val output = outputs.file(input.name + ".transformed")
            println("start process ${input.absolutePath} to ${output.absolutePath}")
            val hideApiCollector = HideApiCollector()
            val hideApiClassModelMap =
                hideApiCollector.collect(PlatformUtils.findAndroidSourceDirByAndroidJar(input))
            processAndroidJar(
                hideApiClassModelMap,
                input,
                output
            )
        } else {
            outputs.file(getInputArtifact())
        }
    }

    private fun processAndroidJar(
        hideApiClassModelMap: Map<String, HideApiClassModel>,
        input: File,
        output: File
    ) {
        val inputZip = ZipFile(input)
        val inputZipStream = ZipInputStream(FileInputStream(input))
        val outputStream = ZipOutputStream(FileOutputStream(output))
        do {
            val nextZip = inputZipStream.nextEntry ?: break
            outputStream.putNextEntry(ZipEntry(nextZip.name))
            val itemData = inputZip.getInputStream(nextZip).readAllBytes()
            if (!Utils.isClass(nextZip.name) || !hideApiClassModelMap.contains(
                    Utils.retrieveClassNameForJarClass(
                        nextZip.name
                    )
                )
            ) {
                outputStream.write(itemData)
                continue
            }
            val classReader = ClassReader(itemData)
            val classWrite = ClassWriter(classReader, 0)
            classReader.accept(NoHideClassVisitor(hideApiClassModelMap, classWrite), 0)
            outputStream.write(classWrite.toByteArray())
        } while (true)
        inputZipStream.close()
        outputStream.close()
    }

}