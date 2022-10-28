package com.legoethals.ev3

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ApplicationPlugin
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.JavaApplication
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.the
import org.gradle.plugins.ide.idea.IdeaPlugin
import org.gradle.plugins.ide.idea.model.IdeaLanguageLevel
import org.jetbrains.gradle.ext.runConfigurations
import org.jetbrains.gradle.ext.settings


class Ev3Plugin : Plugin<Project> {

    override fun apply(project: Project) {

        project.plugins.apply(ApplicationPlugin::class.java)

        //Add an extension object and make it available to the project
        val ev3config = project.extensions.create<Ev3PluginExtension>("ev3")

        val customFile = project.objects.fileProperty()

        project.configurations.getByName("compileOnly") {
            dependencies.add(project.dependencies.create("com.github.bdeneuter:lejos-ev3-api:0.9.1-beta"))
        }

        //TODO Only apply if jar task is available

        val appJar = project.tasks.register("ev3AppJar", ShadowJar::class.java) {
            mergeServiceFiles()
            archiveClassifier.set("app")
            val sourceSets = project.extensions.getByType(SourceSetContainer::class.java)

            val main: SourceSet = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
            from(main.output)

//            doLast { //TODO Check setup (version and mainclass must be set before this code is executed
            manifest {
                attributes["Implementation-Version"] = project.version.toString()
                attributes["Main-Class"] = ev3config.mainClass.get()
                //!! As a side note, when you specify the classpath by using -jar, -cp, or -classpath, you override the system variable CLASSPATH.
                // -> https://docs.oracle.com/javase/7/docs/technotes/tools/findingclasses.html
//        Class path has to contain
                attributes["Class-Path"] = "libs/gradletest-1.0.0-dependencies.jar" //TODO Take from dependencyShadowJar output
            }
//            }
        }

        val dependenciesJar = project.tasks.register("ev3DependenciesJar", ShadowJar::class.java) {
            dependsOn(appJar)
            mergeServiceFiles()
            archiveClassifier.set("dependencies")
            //    archiveClassifier.set("dependencies") //-> classifier vs appendix?
            val implementationConfig = project.configurations.getByName("implementation")
            implementationConfig.isCanBeResolved = true //https://github.com/johnrengelman/shadow/issues/448
            configurations = listOf(implementationConfig)
            //Lookup these excludes
            exclude("META-INF/INDEX.LIST", "META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA", "module-info.class")
        }

        val splitShadowJar = project.tasks.create("splitShadowJar") {
            dependsOn(dependenciesJar)

            doLast {
                println("Building separate src and dependency shadowjars")
            }
        }

        project.tasks.register("customBrol", Ev3CustomTask::class.java) {
            customFile.set(project.layout.buildDirectory.file("hello.txt"))
//            println(ev3config.getResources())
//            inputBrol.set(ev3Extension.getResources().get())
            destination.set(customFile)
        }

        //TODO Extend plugin for kotlin?
//        project.tasks.withType<KotlinCompile>() {
//            kotlinOptions {
//                jvmTarget = "1.8"
//            }
//        }


        //TODO Extend plugin for idea?
        project.plugins.withType(IdeaPlugin::class.java) {
            if (this.model.project != null) { //idea project is only available if plugin is applied on root project
                this.model.project {
                    jdkName = "1.8"
                    languageLevel = IdeaLanguageLevel("1.8")
                    vcs = "Git"
                    settings {
                        runConfigurations {
                            create("YieldApp [Dev]", org.jetbrains.gradle.ext.Application::class.java)
                        }
                    }
                }
            }
        }

        project.task("brol") {//ad hoc task (not-so-custom)
            doLast {
                val hostname = ev3config.sshConfig.hostname.get()
                println("Your ev3 hostname is $hostname, Joris")
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