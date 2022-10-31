package com.legoethals.ev3.ssh

import java.io.File

interface SshService {
    fun mkdirs(directory: String)
    fun downloadFileContents(path: String): String
    fun executeCommand()
    fun portforward(localPort: Int, ev3Port: Int)
    fun upload(path: String, file: File)
}