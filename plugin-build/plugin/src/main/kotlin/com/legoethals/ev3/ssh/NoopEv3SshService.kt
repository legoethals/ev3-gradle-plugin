package com.legoethals.ev3.ssh

import java.io.File

class NoopEv3SshService(
    private val hostname: String,
    private val port: Int,
    private val username: String,
    private val password: String?,
    private val identity_path: String?,
): SshService {

    init {
        println("Ev3SshService created with:")
        println("hostname: '$hostname'")
        println("username: '$username'")
        println("password: '$password'")
        println("identity_path: '$identity_path'")
    }

    override fun mkdirs(directory: String) {
        println("Mkdirs for path '$directory'")
    }

    override fun downloadFileContents(path: String): String? {
        println("Downloading filecontents on path $path, returning static response brol")
        return "brol"
    }

    override fun upload(directoryPath: String, file: File) {
        println("Uploading file with name ${file.name} to path $directoryPath")
    }

    override fun executeCommand(command: String) {
        println("Executing command '$command'")
    }

    override fun localPortForward(localPort: Int, ev3Port: Int) {
        println("Forwarding localport $localPort to remote port $ev3Port")
    }

    override fun close() {
        println("Closing...")
    }
}