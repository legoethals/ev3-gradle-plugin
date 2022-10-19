plugins {
    base
    `kotlin-dsl`
    id("org.jetbrains.kotlin.jvm") version "1.6.10"
    id("maven-publish")
    id("com.gradle.plugin-publish") version "1.0.0-rc-1"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.junit.jupiter:junit-jupiter:5.8.1")
}


//testing {
//    suites {
//        val test by getting(JvmTestSuite::class) {
//            useKotlinTest()
//        }
//
//        val functionalTest by registering(JvmTestSuite::class) {
//            useKotlinTest()
//
//            dependencies {
//                implementation(project)
//            }
//
//            targets {
//                all {
//                    testTask.configure { shouldRunAfter(test) }
//                }
//            }
//        }
//
//
//    }
//}


pluginBundle {
    website = "www.legoethals.com/ev3"
    vcsUrl = ""
}

gradlePlugin {
    val greeting by plugins.creating {
        id = "com.example.plugin.greeting"
//        id = "com.legoethals.ev3"
        implementationClass = "com.legoethals.ev3.Ev3Plugin"
    }
}

//gradlePlugin.testSourceSets(sourceSets["functionalTest"])
//
//tasks.named<Task>("check") {
//    dependsOn(testing.suites.named("functionalTest"))
//}
