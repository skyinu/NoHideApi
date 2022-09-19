import org.gradle.api.artifacts.transform.InputArtifact
import org.gradle.api.artifacts.transform.TransformAction
import org.gradle.api.artifacts.transform.TransformOutputs
import org.gradle.api.artifacts.transform.TransformParameters
import org.gradle.api.file.FileSystemLocation
import org.gradle.api.provider.Provider
import java.io.File

abstract class NoHideApiAction : TransformAction<TransformParameters.None> {

    @InputArtifact
    abstract fun getInputArtifact(): Provider<FileSystemLocation?>

    override fun transform(outputs: TransformOutputs) {
        val input: File = getInputArtifact().get().asFile
        if (input.name.equals(PlatformUtils.ANDROID_JAR, true)) {
            println("start process ${input.absolutePath}")
            val hideApiCollector = HideApiCollector()
            hideApiCollector.collect(PlatformUtils.findAndroidSourceDirByAndroidJar(input))
        } else {
            outputs.file(getInputArtifact())
        }
        outputs.file(getInputArtifact())
    }

}