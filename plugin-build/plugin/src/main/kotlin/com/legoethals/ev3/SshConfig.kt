package com.legoethals.ev3

import org.gradle.api.provider.Property

abstract class SshConfig {

    abstract val hostname: Property<String>
    abstract val username: Property<String>
    abstract val password: Property<String>
    abstract val identity_path: Property<String>

    init {
        username.convention("root")
        password.convention("")
        identity_path.convention("id_rsa")
    }

}