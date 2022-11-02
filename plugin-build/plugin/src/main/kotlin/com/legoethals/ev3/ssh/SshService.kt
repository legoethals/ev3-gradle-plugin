package com.legoethals.ev3.ssh

import java.io.Closeable
import java.io.File

interface SshService : Closeable {
    fun mkdirs(directory: String)
    fun downloadFileContents(path: String): String?
    fun localPortForward(localPort: Int, ev3Port: Int)
    fun upload(directoryPath: String, file: File)
    fun executeCommand(command: String)
}