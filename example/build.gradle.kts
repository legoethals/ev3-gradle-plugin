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
        hostname.set("ev2_usb")

    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.projectreactor:reactor-core:3.4.14")

}

val hello by tasks.registering {
    doLast {
        println("hello there!")
    }
}
//
//templateExampleConfig {
//    message.set("Just trying this gradle plugin...")
//}
//
//buildscript {
//    repositories {
//
//    }
//    dependencies {
//        classpath("com.legoethals:ev3-gradle-plugin:1.0.0")
////        'com.equeo.gradle.plugins:gradle-plugins:1.0-SNAPSHOT'
//    }
//}

//apply plugin: 'com.equeo.gradle.plugins'

//plugins {
//    id("com.legoethals.ev3.d3v")
//}
//
//apply<Ev3
//ev3Config {
//
//}

//ev3Config {
//    customData {
//        welcomeMessage.set("Hi folks!")
//    }
//}
