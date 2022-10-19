package com.legoethals.ev3

import org.gradle.api.provider.Property

abstract class Ev3PluginExtension {

    abstract val message: Property<String>

    init {
        message.convention("Hello there!")
    }
}