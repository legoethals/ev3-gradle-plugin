package com.legoethals.ev3.ssh

import com.legoethals.ev3.SshConfig

interface SshServiceProvider {
    fun create(): SshService
}

class Ev3SshServiceProvider(private val sshConfig: SshConfig) : SshServiceProvider {
    override fun create(): SshService {
        return Ev3SshService(
            hostname = sshConfig.hostname.get(),
            port = sshConfig.port.get(),
            username = sshConfig.username.get(),
            password = sshConfig.password.orNull,
            identity_path = sshConfig.password.orNull,
        )
    }
}