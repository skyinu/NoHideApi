import java.io.File

object PlatformUtils {
    const val ANDROID_JAR = "android.jar"

    /**
     * sdk\platforms\android-32\android.jar
     */
    fun findAndroidSourceDirByAndroidJar(jar: File): List<String> {
        val platformVersion = jar.parentFile.name
        val baseDir = jar.parentFile.parentFile.absolutePath +
                "${File.separator}sources${File.separator}$platformVersion"
        return arrayListOf("$baseDir${File.separator}android")
    }
}