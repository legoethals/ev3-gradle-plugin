package com.legoethals.ev3

import com.legoethals.ev3.ssh.Ev3SshServiceProvider
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.plugins.ApplicationPlugin
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.*
import org.gradle.plugins.ide.idea.IdeaPlugin
import org.gradle.plugins.ide.idea.model.IdeaLanguageLevel
import org.jetbrains.gradle.ext.runConfigurations
import org.jetbrains.gradle.ext.settings


class Ev3Plugin : Plugin<Project> {

    override fun apply(project: Project) {

        project.plugins.apply(ApplicationPlugin::class.java)

        val ev3config = project.extensions.create<Ev3PluginExtension>("ev3")

        project.configurations.getByName("compileOnly") {
            dependencies.add(project.dependencies.create("com.github.bdeneuter:lejos-ev3-api:0.9.1-beta"))
        }

        val ev3DependenciesConfiguration = project.configurations.register("ev3Dependencies") {
            extendsFrom(project.configurations.getByName("implementation"))
            isCanBeResolved = true
        }

        val ev3DependenciesJar by project.tasks.registering(Ev3DependenciesJarTask::class) {
            configurations = listOf(ev3DependenciesConfiguration.get())
        }

        val ev3AppJar by project.tasks.registering(Ev3JarTask::class) {
            dependenciesFile.set(ev3DependenciesJar.flatMap { it.archiveFile })
            jarLibsRelativeDir.set(ev3config.jarLibsRelativeDir)
            mainClassName.set(ev3config.mainClass)
        }

        project.tasks.register<Ev3DeployTask>("ev3Deploy", Ev3SshServiceProvider(ev3config.sshConfig)).configure {
            doFirst {logger.log(LogLevel.LIFECYCLE, "Deploying code version ${project.version}")}
            doLast { logger.log(LogLevel.LIFECYCLE, "Code deployed")}
            inputArtifact.set(ev3AppJar.flatMap { it.archiveFile })
            inputArtifactMd5.set(ev3AppJar.flatMap { it.archiveMd5File })
            artifactDestinationDir.set(ev3config.jarDestinationDir)
            artifactChecksumDestinationDir.set(ev3config.jarDestinationDir)
        }

        project.tasks.register<Ev3DeployTask>("ev3DeployDependencies", Ev3SshServiceProvider(ev3config.sshConfig)).configure {
            doFirst {logger.log(LogLevel.LIFECYCLE, "Deploying dependencies...")}
            doLast { logger.log(LogLevel.LIFECYCLE, "Dependencies deployed")}
            inputArtifact.set(ev3DependenciesJar.flatMap { it.archiveFile })
            inputArtifactMd5.set(ev3DependenciesJar.flatMap { it.archiveMd5File })
            artifactDestinationDir.set(ev3config.getJarLibsAbsoluteDir())
            artifactChecksumDestinationDir.set(ev3config.getJarLibsAbsoluteDir())
        }

        project.tasks.register<Ev3RunTask>("ev3Run", Ev3SshServiceProvider(ev3config.sshConfig)).configure {
            dependsOn(project.tasks.withType(Ev3DeployTask::class))
            jarDestinationDir.set(ev3config.jarDestinationDir)
            jarFileName.set(ev3AppJar.flatMap { project.provider { it.archiveFile.get().asFile.name} })
            mainClass.set(ev3config.mainClass)
        }

        project.tasks.register<Ev3RunTask>("ev3RunDebug", Ev3SshServiceProvider(ev3config.sshConfig)).configure {
            dependsOn(project.tasks.withType(Ev3DeployTask::class))
            debug.set(true)
            debugConfig.apply {
                localPort.set(ev3config.debugConfig.localPort)
                remotePort.set(ev3config.debugConfig.remotePort)
            }
            jarDestinationDir.set(ev3config.jarDestinationDir)
            jarFileName.set(ev3AppJar.flatMap { project.provider { it.archiveFile.get().asFile.name} })
            mainClass.set(ev3config.mainClass)
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
                            create("Remote debug [${ev3config.debugConfig.localPort}]", org.jetbrains.gradle.ext.Application::class.java)
                        }
                    }
                }
            }
        }

    }


}