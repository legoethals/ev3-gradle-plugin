package com.legoethals.ev3.ssh

import com.legoethals.ev3.gradle.testsupport.KGenericContainer
import com.legoethals.ev3.gradle.testsupport.TestMarkers.EXPECT
import com.legoethals.ev3.gradle.testsupport.TestMarkers.GIVEN
import com.legoethals.ev3.gradle.testsupport.TestMarkers.THEN
import com.legoethals.ev3.gradle.testsupport.TestMarkers.WHEN
import com.legoethals.ev3.gradle.testsupport.shouldBe
import com.legoethals.ev3.gradle.testsupport.shouldNotThrowAnyException
import com.legoethals.ev3.ssh.SshServiceTest.Companion.SSHD_CONTAINER
import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.common.IOUtils
import net.schmizz.sshj.connection.channel.direct.Session
import net.schmizz.sshj.transport.verification.HostKeyVerifier
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.slf4j.LoggerFactory
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.io.File
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

    lateinit var service: Ev3SshService

    @BeforeEach
    fun setup() {
        service = Ev3SshService(SSHD_CONTAINER.host, SSHD_CONTAINER.firstMappedPort, "root", "root", null)
    }

    @AfterEach
    fun tearDown() {
        service.close()
    }

    @Test
    fun `the container should be running`() {
        SSHD_CONTAINER.isRunning shouldBe true
    }

    @Test
    fun `the ssh client can connect to the container`() {
        GIVEN
        val ssh = SSHClient()
        //https://github.com/hierynomus/sshj/blob/master/examples/src/main/java/net/schmizz/sshj/examples/InMemoryKnownHosts.java -> Do verify the hostkeys :)
        ssh.addHostKeyVerifier(object : HostKeyVerifier {
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

    @Test
    fun `the ev3 service can connect to the container and execute a command`() {
        { service.executeCommand("echo hello; echo brol") }.shouldNotThrowAnyException()
    }

    @Test
    fun `the ev3 service can connect to the container and execute multiple commands`() {
        EXPECT
        { service.executeCommand("ls /tmp") }.shouldNotThrowAnyException();
        { service.executeCommand("echo hello") }.shouldNotThrowAnyException();
        { service.executeCommand("echo brol") }.shouldNotThrowAnyException();
    }

    @Test
    fun `the ev3 service can make dirs on the container`() {
        EXPECT
        { service.mkdirs("/tmp/brol") }.shouldNotThrowAnyException()
    }

    @Test
    fun `the ev3 service can make dirs on the container and upload a file`() {
        GIVEN
        service.mkdirs("/tmp/brol3/")
        service.executeCommand("ls -al /tmp")
        val file = File.createTempFile("test1", ".MD5")
        file.writeText("auehtaethsuaeostauoetsauoesth")

        EXPECT
        service.upload("/tmp/brol3", file)
        service.executeCommand("ls -al /tmp/brol3/")
    }

    @Test
    fun `the ev3 service can make dirs on the container and upload and download a file`() {
        GIVEN
        service.mkdirs("/tmp/brol4/")
        service.executeCommand("ls -al /tmp")
        service.executeCommand("cat /tmp/brol4/")
        val file = File.createTempFile("test2", ".MD5")
        val fileContents = "auehtaethsuaeostauoetsauoesth"
        file.writeText(fileContents)
        val fileName = file.name;
        service.upload("/tmp/brol4", file)
        service.executeCommand("ls -al /tmp/brol4/")

        WHEN
        val downloadedContents = service.downloadFileContents("/tmp/brol4/$fileName")

        THEN
        downloadedContents shouldBe fileContents
    }

    @Test
    fun `the ev3 service returns a null string when trying to download non-existent file contents`() {
        WHEN
        val downloadedContents = service.downloadFileContents("/tmp/brol4/doesnotexist")

        THEN
        downloadedContents shouldBe null
    }

    @Test
    fun `the ev3 service can setup local port forwarding`() {
        service.localPortForward(5005, 5005)
        Thread.sleep(2000)
    }

    @Test
    fun `the ev3 service can setup local port forwarding, execute a command and close the forwarding`() {
        service.localPortForward(5005, 5005)
        service.executeCommand("sleep 2")
    }
}