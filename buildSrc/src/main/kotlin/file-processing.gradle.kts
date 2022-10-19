//plugins {
//    base //Bare minimum
//}
//
//val fileProcessing = extensions.create<FileProcessingExtension>("fileProcessing")
//
//tasks.register<FileProcessingTask>("processFiles") {
//    processing.set(fileProcessing.processing)
//    inputDirectory.set(layout.projectDirectory.dir("input"))
//    outputDirectory.set(layout.projectDirectory.dir("output"))
//}