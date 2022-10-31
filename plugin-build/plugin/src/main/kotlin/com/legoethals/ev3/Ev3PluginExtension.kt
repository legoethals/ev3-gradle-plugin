package com.legoethals.ev3

import org.gradle.api.provider.Property
import org.gradle.api.tasks.Nested


//Managed types = abstract class or interface with no fields and whose properties are all managed
//A named managed type is a managed type that additionally has an abstract property "name" of type String. Named managed types are especially useful as the element type of NamedDomainObjectContainer (see below).
abstract class Ev3PluginExtension {

//    abstract val outputDir: RegularFileProperty

    @get:Nested
    abstract val sshConfig: SshConfig

    @get:Nested
    abstract val debugConfig: DebugConfig

    abstract val jarDestinationDir: Property<String>
    abstract val jarLibsRelativeDir: Property<String>

    abstract val mainClass: Property<String>

    fun getJarLibsAbsoluteDir(): String = jarDestinationDir.get().trimEnd('/') + "/" + jarLibsRelativeDir.get().trimStart('/').trimEnd('/') + "/"

    fun ssh(config: SshConfig.() -> Unit) {
        sshConfig.config()
    }

    fun debug(config: DebugConfig.() -> Unit) {
        debugConfig.config()
    }

    init {
        jarDestinationDir.convention("/home/lejos/programs")
        jarLibsRelativeDir.convention("libs/")
    }
}