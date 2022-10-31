package com.legoethals.ev3.ssh

import java.io.File

class Ev3SshService: SshService {

    override fun mkdirs(directory: String) {
        TODO("Not yet implemented 1")
    }

    override fun downloadFileContents(path: String): String {
        TODO("Not yet implemented 2")
    }

    override fun upload(path: String, file: File) {
        TODO("Not yet implemented 3")
    }

    override fun executeCommand() {
        TODO("Not yet implemented 4")
    }

    override fun portforward(localPort: Int, ev3Port: Int) {
        TODO("Not yet implemented 5")
    }

}