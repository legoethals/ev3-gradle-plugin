plugins {
    `kotlin-dsl`
//    id("org.jetbrains.kotlin.jvm") version "1.6.21"
    kotlin("jvm").version("1.6.21") //TODO Check toml linking
    id("maven-publish")
    id("com.gradle.plugin-publish") version "1.0.0"
//    id ("com.github.johnrengelman.shadow") version "7.1.2"
//    id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.6"
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(gradleApi())
    implementation("gradle.plugin.com.github.johnrengelman:shadow:7.1.2")
    implementation("gradle.plugin.org.jetbrains.gradle.plugin.idea-ext:gradle-idea-ext:1.1.6")

    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

publishing {
    repositories {
        mavenLocal() //Use with gw publishToMavenLocal
    }
}

group = "com.legoethals"
version = "1.0.0"

//Add metadata to plugin
pluginBundle {
    website = property("WEBSITE").toString()
    vcsUrl = property("VCS_URL").toString()
    description = property("DESCRIPTION").toString()
    tags = listOf("lego", "ev3", "ssh", "mindstorms")
}

//Define the plugin for publishing
gradlePlugin {
    plugins {
        create(property("ID").toString()) {
            id = property("ID").toString()
            implementationClass = property("IMPLEMENTATION_CLASS").toString()
            version = property("VERSION").toString()
            displayName = property("DISPLAY_NAME").toString()
        }
    }
}

tasks.create("setupPluginUploadFromEnvironment") {
    doLast {
        val key = System.getenv("GRADLE_PUBLISH_KEY")
        val secret = System.getenv("GRADLE_PUBLISH_SECRET")

        if (key == null || secret == null) {
            throw GradleException("gradlePublishKey and/or gradlePublishSecret are not defined environment variables")
        }

        System.setProperty("gradle.publish.key", key)
        System.setProperty("gradle.publish.secret", secret)
    }
}