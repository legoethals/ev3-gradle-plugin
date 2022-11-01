package com.legoethals.ev3

import org.gradle.api.provider.Property

abstract class SshConfig {

    abstract val hostname: Property<String>
    abstract val port: Property<Int>
    abstract val username: Property<String>
    abstract val password: Property<String>
    abstract val identity_path: Property<String>

    init {
        hostname.convention("10.0.1.1")
        port.convention(22)
        username.convention("root")
        password.convention("")
        identity_path.convention("id_rsa")
    }

}