package com.legoethals.ev3

import com.legoethals.ev3.ssh.SshService
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

abstract class Ev3RunTask @Inject constructor(private val sshService: SshService) : DefaultTask() {

    init {
        debug.convention(false)
    }

    @get:Input
    abstract val debug: Property<Boolean>

    @TaskAction
    fun run(){
        if(debug.get()){
            println("Running in debug mode, forwarding port")
        } else {
            println("Running")
        }
    }
}