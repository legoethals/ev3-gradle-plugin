package com.legoethals.ev3

import org.gradle.api.provider.Property

abstract class DebugConfig {
    abstract val remotePort: Property<Int>
    abstract val localPort: Property<Int>

    init {
        remotePort.convention(8000)
        localPort.convention(5005)
    }
}