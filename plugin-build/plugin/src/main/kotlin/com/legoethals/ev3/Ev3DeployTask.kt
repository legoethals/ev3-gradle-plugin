package com.legoethals.ev3

import com.legoethals.ev3.ssh.SshService
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.StopExecutionException
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

abstract class Ev3DeployTask @Inject constructor(private val sshService: SshService) : DefaultTask() {

    init {
        outputs.upToDateWhen { checkDeployedArtifactUpToDate() }
    }

    private fun checkDeployedArtifactUpToDate(): Boolean {
        return true
        TODO("Not yet implemented")
    }

    @get:InputFile
    abstract val inputArtifact: RegularFileProperty

    @get:InputFile
    abstract val inputArtifactMd5: RegularFileProperty

    @get:Input
    abstract val artifactDestination: Property<String>

    @get:Input
    abstract val artifactChecksumDestination: Property<String>

    @TaskAction
    fun deploy() {
//        val artifactMd5 = inputArtifactMd5.get().asFile.readText()
//        val remoteMd5: String = sshService.downloadFileContents(artifactChecksumDestination.get())
//        if(artifactMd5 == remoteMd5) {
////            throw StopExecutionException()
//            return;
//        }
//
//        sshService.mkdirs(artifactDestination.get())
//        sshService.mkdirs(artifactChecksumDestination.get())
//
//        sshService.upload(artifactDestination.get(), inputArtifact.get().asFile)
//        sshService.upload(artifactChecksumDestination.get(), inputArtifactMd5.get().asFile)

        //TODO Write to output
    }
}