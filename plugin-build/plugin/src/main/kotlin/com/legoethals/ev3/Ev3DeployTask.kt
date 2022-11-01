package com.legoethals.ev3

import com.legoethals.ev3.ssh.SshServiceProvider
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.impldep.com.google.common.collect.TreeTraverser.using
import javax.inject.Inject

abstract class Ev3DeployTask @Inject constructor(private val sshServiceProvider: SshServiceProvider) : DefaultTask() {

    init {
        outputs.upToDateWhen { checkDeployedArtifactUpToDate() }
    }

    private fun checkDeployedArtifactUpToDate(): Boolean {
        val sshService = sshServiceProvider.create()
        val currentMd5File = inputArtifactMd5.get().asFile
        val currentArtifactMd5 = currentMd5File.readText()
        val remoteArtifactMd5FilePath = artifactChecksumDestinationDir.get().trimEnd('/') + "/" + currentMd5File.name
        val remoteArtifactMd5 = sshService.downloadFileContents(remoteArtifactMd5FilePath)
        logger.debug("Comparing local hash: '$currentArtifactMd5' to remote hash '$remoteArtifactMd5' fetched from location '$remoteArtifactMd5FilePath'" )
        return currentArtifactMd5 == remoteArtifactMd5
    }

    @get:InputFile
    abstract val inputArtifact: RegularFileProperty

    @get:InputFile
    abstract val inputArtifactMd5: RegularFileProperty

    @get:Input
    abstract val artifactDestinationDir: Property<String>

    @get:Input
    abstract val artifactChecksumDestinationDir: Property<String>

    @TaskAction
    fun deploy() {
        sshServiceProvider.create().use {
            it.mkdirs(artifactDestinationDir.get())
            it.upload(artifactDestinationDir.get(), inputArtifact.get().asFile)
            it.mkdirs(artifactChecksumDestinationDir.get())
            it.upload(artifactChecksumDestinationDir.get(), inputArtifactMd5.get().asFile)
        }
    }
}