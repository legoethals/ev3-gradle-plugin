package com.legoethals.ev3

import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input

abstract class DebugConfig {
    @get:Input
    abstract val remotePort: Property<Int>

    @get:Input
    abstract val localPort: Property<Int>

    init {
        remotePort.convention(8000)
        localPort.convention(5005)
    }
}