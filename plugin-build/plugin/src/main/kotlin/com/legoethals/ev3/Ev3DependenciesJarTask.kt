package com.legoethals.ev3

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.OutputFile
import org.gradle.kotlin.dsl.withGroovyBuilder

abstract class Ev3DependenciesJarTask : ShadowJar() {
    init {
        val objectFactory = project.objects
        description = "Creates a jar with implementation dependencies for the app"
        mergeServiceFiles()
        archiveVersion.set("")
        archiveClassifier.set("dependencies")
        //Lookup these excludes
        exclude("META-INF/INDEX.LIST", "META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA", "module-info.class")
        //All because the output directory is not protected :/
        archiveMd5File.convention(project.provider {
            val archiveMd5FileName = archiveFileName.get() + ".MD5"
            val archiveDirectoryFile = archiveFile.get().asFile.parentFile
            val archiveDirectory = objectFactory.directoryProperty()
            archiveDirectory.set(archiveDirectoryFile)
            archiveDirectory.file(archiveMd5FileName).get()
        })
    }

    override fun copy() {
        super.copy()
        ant.withGroovyBuilder {
            "checksum"("file" to archiveFile.get())
        }
    }

    @get:OutputFile
    abstract val archiveMd5File: RegularFileProperty
}