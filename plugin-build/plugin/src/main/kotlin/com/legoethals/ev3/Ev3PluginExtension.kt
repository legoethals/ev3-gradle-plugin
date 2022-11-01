package com.legoethals.ev3

import org.gradle.api.provider.Property
import org.gradle.api.tasks.Nested


abstract class Ev3PluginExtension {

    @get:Nested
    abstract val sshConfig: SshConfig

    @get:Nested
    abstract val debugConfig: DebugConfig

    abstract val jarDestinationDir: Property<String>
    abstract val jarLibsRelativeDir: Property<String>

    //Do not forget the Kt at the end of classname if needed!
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