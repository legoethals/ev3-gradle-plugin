package com.legoethals.ev3

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.withGroovyBuilder

abstract class Ev3JarTask : ShadowJar() {

    init {
        description = "Creates a jar with classes from the ${SourceSet.MAIN_SOURCE_SET_NAME} sourceset"
        mergeServiceFiles()
        archiveClassifier.set("app")

        val sourceSets = project.extensions.getByType(SourceSetContainer::class.java)
        from(sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME).output)

        archiveMd5File.convention(project.provider {
            val archiveMd5FileName = archiveFileName.get() + ".MD5"
            val archiveDirectoryFile = archiveFile.get().asFile.parentFile
            val archiveDirectory = objectFactory.directoryProperty()
            archiveDirectory.set(archiveDirectoryFile)
            archiveDirectory.file(archiveMd5FileName).get()
        })
    }

    @TaskAction
    override fun copy() {
        val dependenciesFileName = dependenciesFile.asFile.get().name
//        logger.warn("Writing manifest with dependenciesfilename $dependenciesFileName")
        manifest {
            attributes["Implementation-Version"] = project.version.toString()
            attributes["Main-Class"] = mainClassName.get()
            //!! As a side note, when you specify the classpath by using -jar, -cp, or -classpath, you override the system variable CLASSPATH.
            // -> https://docs.oracle.com/javase/7/docs/technotes/tools/findingclasses.html
            attributes["Class-Path"] = "${jarLibsRelativeDir.get().trimStart('/').trimEnd('/')}/${dependenciesFileName}"
        }

        super.copy()
        ant.withGroovyBuilder {
            "checksum"("file" to archiveFile.get())
        }
    }

    @get:InputFile
    abstract val dependenciesFile: RegularFileProperty

    @get:Input
    abstract val jarLibsRelativeDir: Property<String>

    @get:Input
    abstract val mainClassName: Property<String>

    @get:OutputFile
    abstract val archiveMd5File: RegularFileProperty

}