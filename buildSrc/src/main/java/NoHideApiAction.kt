class NoHideApiAction : TransformAction<TransformParameters.None> {
    companion object {
        private val ANDROID_JAR = "android.jar"
    }

    @InputArtifact
    abstract fun getInputArtifact(): Provider<FileSystemLocation?>

    override fun transform(outputs: TransformOutputs) {
        val input: File = getInputArtifact().get().asFile
        if (input.name.equals(ANDROID_JAR, true)) {
            
        } else {
            outputs.file(getInputArtifact())
        }
    }
}