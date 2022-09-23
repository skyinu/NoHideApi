import com.github.javaparser.StaticJavaParser
import com.github.javaparser.symbolsolver.JavaSymbolSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver
import java.io.File
import kotlin.streams.asStream


class HideApiCollector {
    private val hideApiClassModelMap = mutableMapOf<String, HideApiClassModel>()
    fun collect(sourceDir: List<String>): Map<String, HideApiClassModel> {
        val typeSolver = CombinedTypeSolver(ReflectionTypeSolver(false))
        sourceDir.forEach {
            typeSolver.add(JavaParserTypeSolver(it))
        }
        val symbolSolver = JavaSymbolSolver(typeSolver)
        StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver)
        sourceDir.stream().sequential().forEach {
            val sourceDirFile = File(it)
            println("start collect in ${sourceDirFile.absolutePath}")
            sourceDirFile.walkBottomUp().asStream().parallel()
                .filter { item -> item.isFile }
                .forEach { javaFile ->
                    if (javaFile.name.endsWith(".java")) {
                        kotlin.runCatching {
                            val cu = StaticJavaParser.parse(javaFile)
                            val javaSourceParser = JavaSourceParser(cu)
                            hideApiClassModelMap.putAll(javaSourceParser.parse())
                        }.onFailure {
                            println("parse failed:${javaFile.absolutePath}")
                        }
                    }
                }
        }
        return hideApiClassModelMap
    }
}