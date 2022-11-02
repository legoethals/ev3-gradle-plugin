# EV3 d3v Gradle plugin

This project contains the code for a Gradle plugin for LEGO® EV3 development.

## Features
* Adds compileOnly dependency on `com.github.bdeneuter:lejos-ev3-api:0.9.1-beta`  
  (Thanks to [Bart De Neuter](https://github.com/bdeneuter) for making this available)
* Creates separate jars for dependencies and code to speed up development and deployment
* Incremental build: no unnecessary packaging and deployment
* Sets up port forwarding to attach a jdwp debugger
* Allows in-IDE debugging
* [In progress] Sets up debug configuration in IntelliJ IDEA, when the org.jetbrains.gradle.plugin.idea-ext plugin is applied
* [TODO] Runs the program using ssh with PTY
* [TODO] Supports key-based authentication

## Using the plugin
### Using the plugin api
```kotlin
plugins {
    id("com.legoethals.ev3.d3v")
}
```
### Configuration
Defaults:
```kotlin
ev3 {
    mainClass.set("com.legoethals.ev3.TestProgramKt")
    jarDestinationDir.set("/home/lejos/programs")
    jarLibsRelativeDir.set("libs")
    ssh {
        hostname.set("10.0.1.1")
        port.set(22)
        username.set("root")
        password.set("")
    }
    debug {
        localPort.set(5005)
        remotePort.set(5005)
    }
}
```
## Gradle tasks
* **ev3AppJar**: builds [projectname]-app.jar and .md5 checksum 
* **ev3DependenciesJar**: builds [projectname]-dependencies.jar and .md5 checksum
* **ev3Deploy**: deploys [projectname]-app.jar and checksum to the ev3 if checksum doesn't match 
* **ev3DeployDependencies**: deploys [projectname]-dependencies.jar and checksum to the ev3 if checksum doesn't match
* **ev3Run**: runs the app using the jrun script on the ev3
* **ev3RunDebug**
  * sets up local port forwarding for debugging 
  * runs the app using the jrun script on the ev3, with jdwp agent attached  
  *ev3 image must be built with jdwp agent for this to work!*

## Develop/try out

The project structure is based on composite builds for plugin development. 
See https://github.com/cortinico/kotlin-gradle-plugin-template for the used template.



### Project structure:
* example: the example project using the plugin
* plugin-build: the project to build the plugin
  * plugin: the plugin code

### Try it out:
Plugin your ev3 with lejos installed and run `./gradlew :example:ev3Run`, which packages, deploys and runs the example code on the ev3. 


# Additional installation and dev information (Not part of plugin)
## LeJOS Api
http://www.lejos.org/ev3/docs/

## First time installation
Download and extract the oracle eJDK 1.8

Download and extract the LeJOS API from https://sourceforge.net/projects/ev3.lejos.p/files/0.9.1-beta/  
`tar -xzvf ~/Downloads/leJOS_EV3_0.9.1-beta.tar.gz -C /opt/lejos`

This directory contains:
* /bin -> Some utility programs to interact with the ev3
* /docs -> The Lejos api documentation
* /lib/ev3 -> The jars needed for ev3 development: dbusjava for the JNI Layer, ev3classes for the API
* /lib/pc -> The jars supporting the utilities
* /sd500.zip -> The zip containing the base image with the kernel to deploy on the SD Card
* /lejosimage.zip -> The zip that needs to be unzipped on the sd card


Set some environment variables in your ~/.profile (and logout and login again if necessary):
```bash
export LEJOS_EV3_JAVA_HOME=/opt/java/jdk1.8.0_112
export EV3_HOME=/opt/lejos/leJOS_EV3_0.9.1-beta
export PATH=/opt/lejos/leJOS_EV3_0.9.1-beta/bin:$PATH
```
-> Setting them in ~/.zshrc will make them accessible to a terminal, but not to Intellij when run from a desktop shortcut.

## Installation on SD Card
Read https://sourceforge.net/p/lejos/wiki/Installing%20leJOS/ and read it again and again...
It boils down to something like this:
```bash
unzip sd500.zip 
cat sd500.img >/dev/sdb
cd /run/media/*/SD500/
unzip /home/*/ev3/leJOS_EV3_0.9.1-beta/lejosimage.zip
cp -af /home/*/ev3/ejre-7u75-fcs-b13-linux-arm-sflt-headless-18_dec_2014
```

## Setup ssh configuration
Place the following in ~/.ssh/config
```
Host ev2_usb
    User root
    HostName 10.0.1.1
    KexAlgorithms +diffie-hellman-group1-sha1
    HostKeyAlgorithms +ssh-rsa
    Ciphers aes128-cbc 
```

## Connecting through usb on linux
* On the EV3, pan mode must be `access point` with an address like 10.0.1.1. Plug out the usb cable and plug it back in again if needed.
* After this, a ping to 10.0.1.1 should be possible

## Setup Wifi
-> `wpa_supplicant.conf` -> Nice to have task to setup?
