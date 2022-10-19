package com.legoethals.ev3

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

class Ev3Plugin : Plugin<Project> {

    override fun apply(project: Project) {

        //Add an extension object and make it available to the project
        val ev3Extension = project.extensions.create<Ev3PluginExtension>("ev3")

        val customFile = project.objects.fileProperty()

        project.tasks.register("customBrol", Ev3CustomTask::class.java){
            customFile.set(project.layout.buildDirectory.file("hello.txt"))
            inputBrol.set(ev3Extension.message)
            destination.set(customFile)
        }

        project.task("brol") {//ad hoc task (not-so-custom)
            doLast {
                val message = ev3Extension.message.get()
                println(message)
            }
        }
    }

}