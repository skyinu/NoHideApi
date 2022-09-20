import com.github.javaparser.StaticJavaParser
import java.io.File
import kotlin.streams.asStream

class HideApiCollector {
    fun collect(sourceDir: List<String>) {
        sourceDir.stream().sequential().forEach {
            val sourceDirFile = File(it)
            println("start collect in ${sourceDirFile.absolutePath}")
            sourceDirFile.walkBottomUp().asStream().sequential()
                .filter { item -> item.isFile }
                .forEach { javaFile ->
                    val cu = StaticJavaParser.parse(javaFile)
                    val javaSourceParser = JavaSourceParser(cu)
                    javaSourceParser.parse()
                }
        }
    }
}