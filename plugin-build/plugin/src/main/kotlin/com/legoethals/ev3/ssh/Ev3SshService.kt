package com.legoethals.ev3.ssh

import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.common.IOUtils
import net.schmizz.sshj.connection.channel.direct.Parameters
import net.schmizz.sshj.transport.verification.PromiscuousVerifier
import net.schmizz.sshj.xfer.FileSystemFile
import org.gradle.internal.impldep.org.eclipse.jgit.errors.NotSupportedException
import java.io.File
import java.io.IOException
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean


class Ev3SshService(
    private val hostname: String,
    private val port: Int,
    private val username: String,
    private val password: String?,
    private val identity_path: String?,
) : SshService {

    private val ssh = SSHClient()
    private val connectRan: AtomicBoolean = AtomicBoolean(false)
    private val serverSockets: MutableList<ServerSocket> = Collections.synchronizedList(mutableListOf())

    init {
        printConfig()
    }

    private fun connectIfNotConnected() {
        if (connectRan.get()) {
            return
        }
        ssh.addHostKeyVerifier(PromiscuousVerifier())
        ssh.connect(hostname, port)
        if (password == null) {
            throw NotSupportedException("Password should be given and will be used. Identity_path is not yet supported")
        }
        ssh.authPassword(username, password)
        connectRan.set(true)
    }

    private fun printConfig() {
        println("Ev3SshService created with:")
        println("hostname: '$hostname'")
        println("port: '$port'")
        println("username: '$username'")
        println("password: '$password'")
        println("identity_path: '$identity_path'")
    }

    override fun mkdirs(directory: String) {
        executeCommand("mkdir -p $directory")
    }

    override fun downloadFileContents(path: String): String? {
        connectIfNotConnected()
        val targetFile =
            FileSystemFile(File.createTempFile("ev3-gradle-plugin-fileContents", UUID.randomUUID().toString()))
        try {
            ssh.newSCPFileTransfer().download(path, targetFile)
        } catch (e: IOException) {
            println("Problem downloading file: ${e.message}")
            return null
        }
        return targetFile.file.readText()
    }

    override fun upload(directoryPath: String, file: File) {
        connectIfNotConnected()
        val filePath = directoryPath.trimEnd('/') + "/" + file.name
        ssh.newSCPFileTransfer().upload(FileSystemFile(file), filePath)
    }

    override fun executeCommand(command: String) {
        connectIfNotConnected()
        ssh.startSession().use {
            println("Executing command '$command'")
            val cmd = it!!.exec(command)
            println(IOUtils.readFully(cmd.inputStream).toString())
            cmd.join(1, TimeUnit.SECONDS)
            println("Exit status: ${cmd.exitStatus}")
        }
    }

    override fun localPortForward(localPort: Int, ev3Port: Int) {
        connectIfNotConnected()
        Thread {
            val serverSocket = ServerSocket()
            serverSockets.add(serverSocket)
            serverSocket.use {
                val params = Parameters("0.0.0.0", localPort, "localhost", ev3Port)
                serverSocket.reuseAddress = true
                serverSocket.bind(InetSocketAddress(params.localHost, params.localPort))
                println("Setting up local port forward")
                ssh.newLocalPortForwarder(params, serverSocket).listen()
            }
        }.start()
    }

    override fun close() {
        serverSockets.forEach {
            try {
                if (!it.isClosed) {
                    println("Closing server socket")
                    it.close()
                }
            } catch (e: IOException) {
                println("Could not close the serversocket (port forwarding)")
            }
        }
        println("Closing ssh connection")
        if (ssh.isConnected) {
            ssh.disconnect()
        }
    }

}