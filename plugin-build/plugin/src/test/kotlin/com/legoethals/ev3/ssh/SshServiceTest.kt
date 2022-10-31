package com.legoethals.ev3.ssh

import com.legoethals.ev3.gradle.testsupport.KGenericContainer
import com.legoethals.ev3.gradle.testsupport.TestMarkers.EXPECT
import com.legoethals.ev3.gradle.testsupport.TestMarkers.GIVEN
import com.legoethals.ev3.gradle.testsupport.shouldBe
import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.common.IOUtils
import net.schmizz.sshj.connection.channel.direct.LocalPortForwarder
import net.schmizz.sshj.connection.channel.direct.Session
import net.schmizz.sshj.connection.channel.forwarded.RemotePortForwarder
import net.schmizz.sshj.transport.verification.HostKeyVerifier
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.slf4j.LoggerFactory
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.io.IOException
import java.security.PublicKey
import java.time.Duration
import java.util.concurrent.TimeUnit

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Testcontainers
class SshServiceTest {

    companion object {
        @Container
        val SSHD_CONTAINER: KGenericContainer = KGenericContainer("sickp/alpine-sshd:7.5-r2")
            .withExposedPorts(22)
            .withLogConsumer(Slf4jLogConsumer(LoggerFactory.getLogger(SshServiceTest::class.java)))
            .withStartupTimeout(Duration.ofSeconds(30))
    }

    @Test
    fun `the container should be running`() {
        SSHD_CONTAINER.isRunning shouldBe true
    }

    @Test
    fun `the service can connect to the container`() {
        GIVEN
        val ssh = SSHClient()
        //https://github.com/hierynomus/sshj/blob/master/examples/src/main/java/net/schmizz/sshj/examples/InMemoryKnownHosts.java -> Do verify the hostkeys :)
        ssh.addHostKeyVerifier(object: HostKeyVerifier {
            override fun verify(p0: String?, p1: Int, p2: PublicKey?): Boolean {
                return true
            }

            override fun findExistingAlgorithms(p0: String?, p1: Int): MutableList<String> {
                return mutableListOf()
            }
        })
        ssh.connect(SSHD_CONTAINER.host, SSHD_CONTAINER.firstMappedPort)
        var session: Session? = null
        val console = System.console()
        try {
            ssh.authPassword("root", "root")
            //val forwarder = ssh.newLocalPortForwarder()
            session = ssh.startSession()
            val cmd = session.exec("echo hello;echo brol")
            println(IOUtils.readFully(cmd.inputStream).toString())
            cmd.join(5, TimeUnit.SECONDS)
            println("\nExit status: ${cmd.exitStatus}")
        } finally {
            try {
                session?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            ssh.disconnect()
        }

    }
}