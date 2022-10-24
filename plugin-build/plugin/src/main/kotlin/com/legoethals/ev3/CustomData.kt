package com.legoethals.ev3

import org.gradle.api.provider.Property

abstract class CustomData {

    abstract val welcomeMessage: Property<String>
    abstract val vcsUrl: Property<String>

    init {
        welcomeMessage.convention("No hello for you!")
    }

}