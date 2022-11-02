plugins {
    kotlin("jvm") version "1.6.0"
    id("com.legoethals.ev3.d3v")
    id("com.palantir.git-version") version "0.15.0"
}

val gitVersion: groovy.lang.Closure<String> by extra
version = gitVersion()

ev3 {
    mainClass.set("com.legoethals.ev3.TestisKt")
    ssh {
        hostname.set("10.0.1.1")
        username.set("root")
        password.set("")
    }
    debug {
        localPort.set(5005)
        remotePort.set(5005)
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.projectreactor:reactor-core:3.4.14")
}
