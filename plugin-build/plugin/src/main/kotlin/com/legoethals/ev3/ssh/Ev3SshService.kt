package com.legoethals.ev3.ssh

import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.common.IOUtils
import net.schmizz.sshj.connection.channel.direct.Parameters
import net.schmizz.sshj.connection.channel.direct.Session
import net.schmizz.sshj.transport.verification.HostKeyVerifier
import net.schmizz.sshj.xfer.FileSystemFile
import java.io.File
import java.io.IOException
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.security.PublicKey
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean


class Ev3SshService(
    private val hostname: String,
    private val port: Int,
    private val username: String,
    private val password: String?,
    private val identity_path: String?,
): SshService {

    private val ssh = SSHClient()
    private var session: Session? = null
    private val connectRan: AtomicBoolean = AtomicBoolean(false)

    init {
        printConfig()
    }

    private fun connectIfNotConnected() {
        if(connectRan.get()){
            return
        }
        //https://github.com/hierynomus/sshj/blob/master/examples/src/main/java/net/schmizz/sshj/examples/InMemoryKnownHosts.java -> Do verify the hostkeys :)
        ssh.addHostKeyVerifier(object : HostKeyVerifier {
            override fun verify(p0: String?, p1: Int, p2: PublicKey?): Boolean {
                return true
            }

            override fun findExistingAlgorithms(p0: String?, p1: Int): MutableList<String> {
                return mutableListOf()
            }
        })
        ssh.connect(hostname, port)
        ssh.authPassword(username, password)
        session = ssh.startSession()
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
        executeCommand("mkdirs -p $directory")
    }

    override fun downloadFileContents(path: String): String {
        connectIfNotConnected()
        val fileName = path.split("/").last()
        ssh.newSCPFileTransfer().download(path, FileSystemFile("/tmp/"))
        return FileSystemFile("/tmp/$fileName").file.readText()
    }

    override fun upload(path: String, file: File) {
        connectIfNotConnected()
        ssh.newSCPFileTransfer().upload(FileSystemFile(file), path)
    }

    override fun executeCommand(command: String) {
        connectIfNotConnected()
        println("Executing command '$command'")
        val cmd = session!!.exec(command)
        println(IOUtils.readFully(cmd.inputStream).toString())
        cmd.join(10, TimeUnit.SECONDS)
        println("\nExit status: ${cmd.exitStatus}")
    }

    override fun localPortForward(localPort: Int, ev3Port: Int) {
        connectIfNotConnected()
        val params = Parameters("0.0.0.0", localPort, "localhost", ev3Port)
        val serverSocket = ServerSocket()
        serverSocket.reuseAddress = true
        serverSocket.bind(InetSocketAddress(params.localHost, params.localPort))
        ssh.newLocalPortForwarder(params, serverSocket).listen()
    }

    override fun close() {
        println("Closing connection")
        try {
            session?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        if(ssh.isConnected){
            ssh.disconnect()
        }
    }

}