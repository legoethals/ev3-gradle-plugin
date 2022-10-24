package com.legoethals.ev3

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.the

class Ev3Plugin : Plugin<Project> {

    override fun apply(project: Project) {

        //Add an extension object and make it available to the project
        val ev3config = project.extensions.create<Ev3PluginExtension>("ev3")

        val customFile = project.objects.fileProperty()

        project.tasks.register("customBrol", Ev3CustomTask::class.java){
            customFile.set(project.layout.buildDirectory.file("hello.txt"))
//            println(ev3config.getResources())
//            inputBrol.set(ev3Extension.getResources().get())
            destination.set(customFile)
        }

        project.task("brol") {//ad hoc task (not-so-custom)
            doLast {
                val message = ev3config.customData.welcomeMessage.get()
                println("$message, Joris")
            }
        }
    }

}

//To get config support when using buildSrc...
fun Project.ev3Config(config: Ev3PluginExtension.() -> Unit): Ev3PluginExtension {
    val the = (this as ExtensionAware).the<Ev3PluginExtension>()
    the.config()
    return the
}