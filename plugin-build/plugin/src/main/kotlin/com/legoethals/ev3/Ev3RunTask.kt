package com.legoethals.ev3

import com.legoethals.ev3.ssh.SshServiceProvider
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

abstract class Ev3RunTask @Inject constructor(private val sshServiceProvider: SshServiceProvider) : DefaultTask() {

    init {
        debug.convention(false)
    }

    @get:Input
    abstract val debug: Property<Boolean>

    @get:Nested
    abstract val debugConfig: DebugConfig

    @get:Input
    abstract val jarDestinationDir: Property<String>

    @get:Input
    abstract val jarFileName: Property<String>

    @get:Input
    abstract val mainClass: Property<String>

    @TaskAction
    fun run(){
        sshServiceProvider.create().use {
            if(debug.get()){
//                #To debug, the jre must be built with the jdwp agent included!
                //TODO Auto Attach intellij?
                it.localPortForward(debugConfig.localPort.get(), debugConfig.remotePort.get())
                it.executeCommand("jrun -cp ${jarDestinationDir.get()}/${jarFileName.get()} -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=${debugConfig.remotePort.get()} ${mainClass.get()}")
            } else {
                it.executeCommand("jrun -cp ${jarDestinationDir.get()}/${jarFileName.get()} ${mainClass.get()}")
            }
        }
    }
}