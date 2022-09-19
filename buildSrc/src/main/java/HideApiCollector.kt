import java.io.File
import kotlin.streams.asStream

class HideApiCollector {
    fun collect(sourceDir: List<String>) {
        sourceDir.stream().sequential().forEach {
            println("start collect in $it")
            val sourceDirFile = File(it)
            sourceDirFile.walkBottomUp().asStream().sequential().forEach {

            }
        }
    }
}